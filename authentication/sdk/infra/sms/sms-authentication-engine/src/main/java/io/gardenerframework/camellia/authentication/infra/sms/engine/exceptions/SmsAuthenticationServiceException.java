package io.gardenerframework.camellia.authentication.infra.sms.engine.exceptions;

/**
 * @author zhanghan30
 * @date 2023/2/14 19:10
 */
public class SmsAuthenticationServiceException extends RuntimeException {
    public SmsAuthenticationServiceException(Throwable cause) {
        super(cause);
    }
}
