package io.gardenerframework.camellia.authentication.server.test.utils;

import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationRequestParameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/2/17 16:37
 */
@Getter
@Setter
public class UsernamePasswordAuthenticationParameter extends AuthenticationRequestParameter {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    protected UsernamePasswordAuthenticationParameter() {
        super(null);
    }
}
