package com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.CasExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 票据不正确，如已经过期等服务端返回的与票据不正确有关的信息
 *
 * @author zhanghan30
 * @date 2022/8/23 4:13 下午
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTicketException extends CasExceptions.ClientSideException {
    public InvalidTicketException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidTicketException(String msg) {
        super(msg);
    }
}
