package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZhangHan
 * @date 2022/4/26 8:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsAuthenticationCodeResponse {
    private long cooldown;
}
