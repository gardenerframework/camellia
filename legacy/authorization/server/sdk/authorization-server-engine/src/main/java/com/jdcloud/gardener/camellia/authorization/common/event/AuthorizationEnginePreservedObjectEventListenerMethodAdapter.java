package com.jdcloud.gardener.camellia.authorization.common.event;

import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation.CareForAuthorizationEnginePreservedException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation.CareForAuthorizationEnginePreservedPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AbstractAuthenticationEvent;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AuthenticationFailedEvent;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import com.jdcloud.gardener.fragrans.log.annotation.ReferLogTarget;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Send;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * @author ZhangHan
 * @date 2022/4/27 23:49
 */
@Slf4j
public class AuthorizationEnginePreservedObjectEventListenerMethodAdapter extends ApplicationListenerMethodAdapter {
    @Nullable
    private final CareForAuthorizationEnginePreservedPrincipal careForAuthorizationEnginePreservedPrincipal;
    @Nullable
    private final CareForAuthorizationEnginePreservedException careForAuthorizationEnginePreservedException;

    /**
     * Construct a new ApplicationListenerMethodAdapter.
     *
     * @param beanName    the name of the bean to invoke the listener method on
     * @param targetClass the target class that the method is declared on
     * @param method      the listener method to invoke
     */
    public AuthorizationEnginePreservedObjectEventListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        super(beanName, targetClass, method);
        this.careForAuthorizationEnginePreservedPrincipal = AnnotationUtils.findAnnotation(method, CareForAuthorizationEnginePreservedPrincipal.class);
        this.careForAuthorizationEnginePreservedException = AnnotationUtils.findAnnotation(method, CareForAuthorizationEnginePreservedException.class);
    }

    /**
     * 没有任何东西是保留的
     *
     * @param preservedObjectFlag 保留标志位
     * @return 是否有个什么玩意是保留的
     */
    private boolean nothingIsPreserved(boolean... preservedObjectFlag) {
        for (boolean flag : preservedObjectFlag) {
            if (flag) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断事件中携带的异常是不是保留异常
     *
     * @param event 事件
     * @return 是否保留
     */
    private boolean isExceptionPreserved(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailedEvent) {
            AuthenticationException exception = ((AuthenticationFailedEvent) event).getException();
            return AnnotationUtils.findAnnotation(exception.getClass(), AuthorizationEnginePreserved.class) != null;
        } else {
            return false;
        }
    }

    private String safeGetExceptionClass(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailedEvent) {
            return ((AuthenticationFailedEvent) event).getException().getClass().getCanonicalName();
        } else {
            return "";
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof PayloadApplicationEvent) {
            Object payload = ((PayloadApplicationEvent<?>) event).getPayload();
            if (payload instanceof AbstractAuthenticationEvent) {
                BasicPrincipal principal = ((AbstractAuthenticationEvent) payload).getPrincipal();
                Assert.notNull(principal, "authenticationRequestPrincipal must not be null");
                boolean principalIsPreserved = (AnnotationUtils.findAnnotation(principal.getClass(), AuthorizationEnginePreserved.class) != null);
                boolean exceptionIsPreserved = isExceptionPreserved((AbstractAuthenticationEvent) payload);
                if (nothingIsPreserved(principalIsPreserved, exceptionIsPreserved)) {
                    //当前事件中没有任何玩意是保留的
                    super.onApplicationEvent(event);
                } else if (
                    //登录名是保留的，且事件监听关注或者异常是保留的，且监听器关注
                        (principalIsPreserved && careForAuthorizationEnginePreservedPrincipal != null) ||
                                (exceptionIsPreserved && careForAuthorizationEnginePreservedException != null)
                ) {
                    super.onApplicationEvent(event);
                } else {
                    GenericLoggerStaticAccessor.operationLogger().debug(
                            log,
                            GenericOperationLogContent.builder()
                                    .what(Event.class)
                                    .operation(new Send())
                                    .state(new Word() {
                                        @Override
                                        public String toString() {
                                            return "忽略";
                                        }
                                    })
                                    .detail(new Detail() {
                                        private final String principalClass = principal.getClass().getCanonicalName();
                                        private final String exceptionClass = safeGetExceptionClass((AbstractAuthenticationEvent) payload);
                                        private final String method = getTargetMethod().getName();
                                    }).build(),
                            null
                    );
                }
            }
        }
    }

    /**
     * @author ZhangHan
     * @date 2022/4/28 0:06
     */
    @LogTarget("事件")
    private class Event {
    }

    /**
     * @author ZhangHan
     * @date 2022/4/28 0:06
     */
    @ReferLogTarget(value = Event.class, suffix = "载荷")
    private class EventPayload {
    }
}
