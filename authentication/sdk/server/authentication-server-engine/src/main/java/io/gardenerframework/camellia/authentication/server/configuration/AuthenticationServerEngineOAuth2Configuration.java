package io.gardenerframework.camellia.authentication.server.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.camellia.authentication.server.main.support.oauth2.OAuth2AuthorizationIdModifier;
import lombok.AllArgsConstructor;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

/**
 * 生成spring oauth2认证服务器所需的若干bean
 *
 * @author ZhangHan
 * @date 2022/5/11 14:19
 */
@Configuration
@AllArgsConstructor
@Import({
        OAuth2AuthorizationConsentOption.class
})
public class AuthenticationServerEngineOAuth2Configuration {
    private final AuthenticationServerPathOption authenticationServerPathOption;

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .authorizationEndpoint(authenticationServerPathOption.getOAuth2AuthorizationEndpoint())
                .tokenEndpoint(authenticationServerPathOption.getOAuth2TokenEndpoint())
                .oidcUserInfoEndpoint(authenticationServerPathOption.getOidcUserInfoEndpoint())
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);

    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationIdModifier.class)
    public OAuth2AuthorizationIdModifier defaultOAuth2AuthorizationIdModifier() {
        return (originalId, headers, client, user) -> originalId;
    }

    @Bean
    @Conditional(MissingJWKSourceBeanAndKeyPemProvided.class)
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAPrivateKey rsaPrivateKey = readPrivateKey();
        RSAPublicKey rsaPublicKey = readPublicKey();
        //检查读取出来的pk/sk是否没有问题
        validateKey(rsaPublicKey, rsaPrivateKey);
        JWKSet jwkSet = new JWKSet(
                new RSAKey.Builder(rsaPublicKey)
                        .privateKey(rsaPrivateKey)
                        .keyID(UUID.randomUUID().toString())
                        .build());
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    private RSAPrivateKey readPrivateKey() throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) factory.generatePrivate(
                new PKCS8EncodedKeySpec(
                        new PemReader(
                                new InputStreamReader(
                                        new ClassPathResource("authentication-server-engine/pki/private.pem")
                                                .getInputStream()
                                )
                        ).readPemObject().getContent()
                ));
    }

    private RSAPublicKey readPublicKey() throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) factory.generatePublic(
                new X509EncodedKeySpec(
                        new PemReader(
                                new InputStreamReader(
                                        new ClassPathResource("authentication-server-engine/pki/public.pem")
                                                .getInputStream()
                                )
                        ).readPemObject().getContent()
                ));
    }

    private void validateKey(RSAPublicKey publicKey, RSAPrivateKey privateKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        //公钥加密
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String test = UUID.randomUUID().toString();
        byte[] secretMessageBytes = test.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        //私钥解密
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        //解出来应该一样
        Assert.isTrue(new String(decryptedMessageBytes).equals(test), "key validation failed");
    }

    /**
     * 检查是否应当创建一个默认的JWKSource
     */
    public static class MissingJWKSourceBeanAndKeyPemProvided implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            //检查内容包含没有其它bean且类路径下存在两个pem文件
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            Assert.notNull(beanFactory, "beanFactory must not be null");
            String[] beanNames = beanFactory.getBeanNamesForType(JWKSource.class);
            if (beanNames.length >= 1) {
                //已经有其它的bean定义
                return false;
            }
            return new ClassPathResource("authentication-server-engine/pki/private.pem").exists()
                    && new ClassPathResource("authentication-server-engine/pki/public.pem").exists();
        }
    }
}
