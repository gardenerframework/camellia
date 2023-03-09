package com.jdcloud.gardener.camellia.authorization.authentication.main.exception.server;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.CasExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 表示这是服务器的问题
 *
 * @author zhanghan30
 * @date 2022/8/23 4:12 下午
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class CasServerException extends CasExceptions.ServerSideException {
    public CasServerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CasServerException(String msg) {
        super(msg);
    }
}
