package com.jdcloud.gardener.camellia.uac.account.exception.client;

import com.jdcloud.gardener.camellia.uac.account.exception.AccountExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当搜索时，希望找到唯一的账户但是没有找到或者找到的账户不唯一
 * <p>
 * 比如登录时使用了手机号，结果用手机号找到了2个账户
 *
 * @author zhanghan30
 * @date 2022/9/20 17:49
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountNotFoundOrNotUniqueException extends AccountExceptions.ClientSideException {
}
