package com.jdcloud.gardener.camellia.uac.account.exception.client;

import com.jdcloud.gardener.camellia.uac.account.exception.AccountExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 创建账户时，基于程序逻辑查找用户名、手机号等发现已有账户存在
 *
 * @author zhanghan30
 * @date 2022/9/20 17:49
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountPropertyUniqueConstraintsViolationException extends AccountExceptions.ClientSideException {
}
