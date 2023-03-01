package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ZhangHan
 * @date 2022/5/15 14:01
 */
@AllArgsConstructor
@Getter
public class SmsAuthenticationCodeCredentials extends BasicCredentials {
    private final String code;
}
