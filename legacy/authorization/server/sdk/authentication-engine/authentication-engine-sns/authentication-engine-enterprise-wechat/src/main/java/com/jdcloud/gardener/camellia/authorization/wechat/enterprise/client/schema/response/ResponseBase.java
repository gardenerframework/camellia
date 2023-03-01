package com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client.schema.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

/**
 * @author ZhangHan
 * @date 2022/11/8 21:07
 */
@NoArgsConstructor
@Getter
@Setter
public abstract class ResponseBase {
    private int errcode;
    private String errmsg;

    public void assertSuccess() throws AuthenticationException {
        if (errcode != 0) {
            throw new AuthenticationServiceException(errmsg);
        }
    }
}
