package com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client.schema.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ZhangHan
 * @date 2022/11/8 21:07
 */
@Getter
@Setter
@NoArgsConstructor
public class AccessTokenResponse extends ResponseBase {
    private String access_token;
    private long expires_in;
}
