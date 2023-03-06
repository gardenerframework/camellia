package io.gardenerframework.camellia.authentication.server.test.utils;

import io.gardenerframework.camellia.authentication.server.main.OAuth2BaseUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.OAuth2BasedIamUserReader;
import io.gardenerframework.camellia.authentication.server.main.OAuth2StateStore;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2023/3/6 19:09
 */
@Component
@AuthenticationType("test")
public class TestOAuth2BaseUserAuthenticationService extends OAuth2BaseUserAuthenticationService {
    public TestOAuth2BaseUserAuthenticationService(@NonNull Validator validator, OAuth2BasedIamUserReader oAuth2BasedIamUserReader, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2BasedIamUserReader, oAuth2StateStore);
    }
}
