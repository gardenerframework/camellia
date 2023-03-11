package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.main.configuration.PasswordEncryptionServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.exception.InvalidKeyException;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@PasswordEncryptionServiceComponent
@ConditionalOnClass(BasicCacheManager.class)
@ConditionalOnMissingBean(value = PasswordEncryptionService.class, ignored = RsaPasswordEncryptionService.class)
public class RsaPasswordEncryptionService implements PasswordEncryptionService {
    private final String[] NAMESPACE = new String[]{
            "authentication",
            "server",
            "component",
            "user-authentication-service",
            "username-password",
            "encrypt"
    };

    private final String SUFFIX = "key";

    private final BasicCacheManager<String> cacheManager;

    public RsaPasswordEncryptionService(CacheClient client) {
        this.cacheManager = new BasicCacheManager<String>(client) {
        };
    }

    @Override
    public Key createKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = generator.generateKeyPair();
        String rawPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String keyId = UUID.randomUUID().toString();
        Duration ttl = Duration.ofSeconds(30);
        cacheManager.set(NAMESPACE, keyId, SUFFIX, rawPrivateKey, ttl);
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
        return new Key(
                keyId,
                Base64.getEncoder().encodeToString(publicKey.getEncoded()),
                Date.from(Instant.now().plus(ttl))
        );
    }

    @Override
    public String decrypt(@NonNull String id, @NonNull String cipher) throws Exception {
        String privateKeySaved = cacheManager.get(NAMESPACE, id, SUFFIX);
        if (privateKeySaved == null) {
            throw new InvalidKeyException(id);
        } else {
            //保证秘钥的一次性有效
            cacheManager.delete(NAMESPACE, id, SUFFIX);
            byte[] decode = Base64.getDecoder().decode(privateKeySaved);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decode));
            Cipher rasCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rasCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rasCipher.doFinal(Base64.getDecoder().decode(cipher)));
        }
    }
}
