package io.gardenerframework.camellia.authentication.server.main.configuration;

import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationRequestParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.request.UsernamePasswordAuthenticationParameter;
import io.gardenerframework.camellia.authentication.server.security.encryption.EncryptionService;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Base64;
import java.util.function.BiConsumer;

@Configuration
public class PasswordEncryptionServiceConfiguration {
    @Bean
    @ConditionalOnBean({EncryptionService.class, Validator.class})
    public BiConsumer<
            HttpServletRequest,
            UsernamePasswordAuthenticationParameter> passwordDecryptHelper(
            Validator validator,
            EncryptionService encryptionService
    ) {
        return (request, usernamePasswordAuthenticationParameter) -> {
            //取id
            PasswordEncryptionKeyIdParameter passwordEncryptionKeyIdParameter = new PasswordEncryptionKeyIdParameter(request);
            //执行验证
            passwordEncryptionKeyIdParameter.validate(validator);
            //把解密后的密码放进去
            usernamePasswordAuthenticationParameter.setPassword(
                    new String(
                            encryptionService.decrypt(
                                    passwordEncryptionKeyIdParameter.getPasswordEncryptionKeyId(),
                                    Base64.getDecoder().decode(usernamePasswordAuthenticationParameter.getPassword()))
                    ));

        };
    }

    private static class PasswordEncryptionKeyIdParameter extends AuthenticationRequestParameter {
        @NotBlank
        @Getter
        private final String passwordEncryptionKeyId;

        protected PasswordEncryptionKeyIdParameter(HttpServletRequest request) {
            super(request);
            passwordEncryptionKeyId = request.getParameter("passwordEncryptionKeyId");

        }
    }
}