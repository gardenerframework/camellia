package com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.CasExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 非法用户，指用户已经删除或被禁用等
 *
 * @author zhanghan30
 * @date 2022/8/23 4:25 下午
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidUserException extends CasExceptions.ClientSideException {
    public InvalidUserException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidUserException(String msg) {
        super(msg);
    }
}
