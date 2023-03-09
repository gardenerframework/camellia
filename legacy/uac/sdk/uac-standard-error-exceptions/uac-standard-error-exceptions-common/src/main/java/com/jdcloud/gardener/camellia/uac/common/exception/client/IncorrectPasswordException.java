package com.jdcloud.gardener.camellia.uac.common.exception.client;

import com.jdcloud.gardener.camellia.uac.common.exception.CommonExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 密码不正确
 *
 * @author zhanghan30
 * @date 2022/9/21 02:22
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectPasswordException extends CommonExceptions.ClientSideException {
}
