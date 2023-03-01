package com.jdcloud.gardener.camellia.authorization.common.event;

import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation.CareForAuthorizationEnginePreservedObject;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AbstractAuthenticationEvent;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * @author ZhangHan
 * @date 2022/4/27 23:29
 */
public class AuthorizationServerEventListenerFactory implements EventListenerFactory, Ordered {
    /**
     * 支持带有{@link CareForAuthorizationEnginePreservedObject}注解的玩意
     *
     * @param method 方法
     * @return 支持与否
     */
    @Override
    public boolean supportsMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null) {
            for (Class<?> type : parameterTypes) {
                //只要有参数是目标事件类型就符合
                if (AbstractAuthenticationEvent.class.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 创建一个事件监听器的适配器
     *
     * @param beanName bean名称
     * @param type     目标类型
     * @param method   方法
     * @return 适配器
     */
    @Override
    public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
        return new AuthorizationEnginePreservedObjectEventListenerMethodAdapter(beanName, type, method);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
