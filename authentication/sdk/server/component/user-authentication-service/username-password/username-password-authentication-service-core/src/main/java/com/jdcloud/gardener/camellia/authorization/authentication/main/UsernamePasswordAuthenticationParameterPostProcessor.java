package com.jdcloud.gardener.camellia.authorization.authentication.main;

import io.gardenerframework.camellia.authentication.server.main.schema.request.UsernamePasswordAuthenticationParameter;
import org.springframework.security.core.AuthenticationException;

/**
 * @author zhanghan30
 * @date 2022/12/26 17:35
 */
@FunctionalInterface
public interface UsernamePasswordAuthenticationParameterPostProcessor {
    /**
     * 当参数被{@link javax.xml.validation.Validator}验证通过后进行后置处理
     *
     * @param parameter 认证参数
     * @throws AuthenticationException 如果在过程中认为认证应该失败，则抛出认证异常
     */
    void afterParameterValidated(UsernamePasswordAuthenticationParameter parameter) throws AuthenticationException;
}
