package io.gardenerframework.camellia.authentication.infra.challenge.engine.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import javax.validation.constraints.NotNull;

/**
 * @author zhanghan30
 * @date 2023/2/28 12:12
 */
public class ChallengeAuthenticatorUtils {
    /**
     * 将认证器的名字注入到给定的挑战中
     *
     * @param challenge         挑战
     * @param authenticatorName 认证器名称
     * @return 完成注入后的挑战数据
     */
    @SuppressWarnings("unchecked")
    public static <C extends Challenge> C injectChallengeAuthenticatorName(
            @NotNull C challenge,
            @NotNull String authenticatorName
    ) {
        if (!(challenge instanceof ChallengeAuthenticatorNameProvider)) {
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
                            return authenticatorName;
                        } catch (NoSuchMethodException e) {
                            //要求访问的不是接口的方法
                            return method.invoke(challenge, objects);
                        }
                    }
            );
            try {
                return (C) enhancer.create();
            } catch (Exception e) {
                //创建失败，无法代理，拉吹
                return challenge;
            }
        }
        //challenge已经具备ChallengeAuthenticatorNameProvider接口，不需要重新proxy
        return challenge;
    }
}
