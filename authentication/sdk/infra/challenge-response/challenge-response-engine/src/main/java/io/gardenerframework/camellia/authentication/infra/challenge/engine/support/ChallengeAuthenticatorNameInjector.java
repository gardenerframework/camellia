package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

/**
 * @author zhanghan30
 * @date 2023/2/28 12:12
 */
@Aspect
public class ChallengeAuthenticatorNameInjector {
    @Around("execution(* io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService.sendChallenge(..))")
    public Object injectChallengeAuthenticatorName(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        Object challenge = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        Object service = proceedingJoinPoint.getTarget();
        //对于还没有被代理的挑战有效
        if (challenge != null && !ClassUtils.isCglibProxy(challenge)) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(challenge.getClass());
            //整合名称接口
            enhancer.setInterfaces(new Class[]{ChallengeAuthenticatorNameProvider.class});
            enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
                        try {
                            //当前对challenge的代理调用的是getName方法
                            ChallengeAuthenticatorNameProvider.class.getDeclaredMethod(
                                    method.getName(),
                                    method.getParameterTypes()
                            );
                            ChallengeAuthenticator annotation;
                            //首先检查服务是不是ChallengeAuthenticatorNameProvider类型
                            return service instanceof ChallengeAuthenticatorNameProvider ?
                                    //是就调用服务的方法返回名字
                                    ((ChallengeAuthenticatorNameProvider) service).getChallengeAuthenticatorName() :
                                    //不是找注解
                                    (annotation = AnnotationUtils.findAnnotation(service.getClass(), ChallengeAuthenticator.class)) == null ?
                                            //没注解返回空白字符串，不然返回注解的值
                                            "" : annotation.value();
                        } catch (NoSuchMethodException e) {
                            //要求访问的不是接口的方法
                            return method.invoke(challenge, objects);
                        }
                    }
            );
            return enhancer.create();
        } else {
            return challenge;
        }
    }
}
