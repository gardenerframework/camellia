package io.gardenerframework.camellia.authentication.server.main.mfa.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author zhanghan30
 * @date 2023/2/28 12:12
 */
@Aspect
@AuthenticationServerEngineComponent
public class MfaAuthenticationChallengeAuthenticatorNameInjector {
    @Around("execution(* io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService.sendChallenge(..))")
    public Object injectChallengeAuthenticatorName(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        Object challenge = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        MfaAuthenticationChallengeRequest request = (MfaAuthenticationChallengeRequest) proceedingJoinPoint.getArgs()[3];
        String authenticatorName = request.getAuthenticatorName();
        if (challenge != null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(challenge.getClass());
            //整合名称接口
            enhancer.setInterfaces(new Class[]{ChallengeAuthenticatorNameProvider.class});
            enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> authenticatorName
            );
            return enhancer.create();
        } else {
            return null;
        }
    }
}
