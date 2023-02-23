package io.gardenerframework.camellia.authentication.server.main.client;

import io.gardenerframework.camellia.authentication.server.main.client.schema.ProxiedRegisteredClient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.DefaultNamingPolicy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/5/21 14:19
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisteredClientProxy implements MethodInterceptor {
    private final RegisteredClient target;

    /**
     * 代理目标
     *
     * @param target 目标
     * @return 代理结果
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static RegisteredClient proxy(@Nullable RegisteredClient target) {
        if (target == null) {
            //没有目标代理什么劲头子
            return null;
        }
        //查看目标类型
        Class<? extends RegisteredClient> targetClass;
        if (RegisteredClient.class.equals(ClassUtils.getUserClass(target))) {
            //之所以代理一层是因为RegisteredClient没有public的构造方法
            targetClass = ProxiedRegisteredClient.class;
        } else {
            //被代理类必须有public的无参构造方法
            targetClass = (Class<? extends RegisteredClient>) ClassUtils.getUserClass(target.getClass());
            if (!Objects.equals(targetClass, target.getClass()) && target.getClass().getName().contains("byRegisteredClientProxy")) {
                //目标类型与用户类不符，且代理标签是本类生成的
                return target;
            }
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new RegisteredClientProxy(target));
        enhancer.setNamingPolicy(new DefaultNamingPolicy() {
            @Override
            protected String getTag() {
                return "byRegisteredClientProxy";
            }
        });
        return (RegisteredClient) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("getClientSecret")) {
            return null;
        }
        Method proxiedMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        return proxiedMethod.invoke(target, objects);
    }
}
