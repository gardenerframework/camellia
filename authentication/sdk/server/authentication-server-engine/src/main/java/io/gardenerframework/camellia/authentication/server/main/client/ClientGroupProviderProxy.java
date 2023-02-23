package io.gardenerframework.camellia.authentication.server.main.client;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/5/21 13:33
 */
@Aspect
@Component
public class ClientGroupProviderProxy {
    @Around("execution(* com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider.getClientGroup(..))")
    public Object onGetClientGroup(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        RegisteredClient registeredClient = (RegisteredClient) proceedingJoinPoint.getArgs()[0];
        if (registeredClient != null) {
            registeredClient = RegisteredClientProxy.proxy(registeredClient);
        }
        return proceedingJoinPoint.proceed(new Object[]{registeredClient});
    }
}
