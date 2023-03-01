package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 密码形式的登录凭据
 *
 * @author zhanghan30
 * @date 2022/5/12 4:23 下午
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class PasswordCredentials extends BasicCredentials {
    private final String password;
}
