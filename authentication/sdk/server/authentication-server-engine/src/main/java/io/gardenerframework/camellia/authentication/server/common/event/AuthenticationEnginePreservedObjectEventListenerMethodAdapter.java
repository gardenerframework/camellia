package io.gardenerframework.camellia.authentication.server.common.event;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.event.listener.annotation.CareForAuthenticationServerEnginePreservedException;
import io.gardenerframework.camellia.authentication.server.main.event.listener.annotation.CareForAuthenticationServerEnginePreservedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationEvent;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationFailedEvent;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.verb.Send;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.word.Word;
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
public class AuthenticationEnginePreservedObjectEventListenerMethodAdapter extends ApplicationListenerMethodAdapter {
    @Nullable
    private final CareForAuthenticationServerEnginePreservedPrincipal careForAuthenticationServerEnginePreservedPrincipal;
    @Nullable
    private final CareForAuthenticationServerEnginePreservedException careForAuthenticationServerEnginePreservedException;

    /**
     * Construct a new ApplicationListenerMethodAdapter.
     *
     * @param beanName    the name of the bean to invoke the listener method on
     * @param targetClass the target class that the method is declared on
     * @param method      the listener method to invoke
     */
    public AuthenticationEnginePreservedObjectEventListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        super(beanName, targetClass, method);
        this.careForAuthenticationServerEnginePreservedPrincipal = AnnotationUtils.findAnnotation(method, CareForAuthenticationServerEnginePreservedPrincipal.class);
        this.careForAuthenticationServerEnginePreservedException = AnnotationUtils.findAnnotation(method, CareForAuthenticationServerEnginePreservedException.class);
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
    private boolean isExceptionPreserved(AuthenticationEvent event) {
        if (event instanceof AuthenticationFailedEvent) {
            AuthenticationException exception = ((AuthenticationFailedEvent) event).getException();
            return AnnotationUtils.findAnnotation(exception.getClass(), AuthenticationServerEnginePreserved.class) != null;
        } else {
            return false;
        }
    }

    private String safeGetExceptionClass(AuthenticationEvent event) {
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
            if (payload instanceof AuthenticationEvent) {
                Principal principal = ((AuthenticationEvent) payload).getPrincipal();
                Assert.notNull(principal, "authenticationRequestPrincipal must not be null");
                boolean principalIsPreserved = (AnnotationUtils.findAnnotation(principal.getClass(), AuthenticationServerEnginePreserved.class) != null);
                boolean exceptionIsPreserved = isExceptionPreserved((AuthenticationEvent) payload);
                if (nothingIsPreserved(principalIsPreserved, exceptionIsPreserved)) {
                    //当前事件中没有任何玩意是保留的
                    super.onApplicationEvent(event);
                } else if (
                    //登录名是保留的，且事件监听关注或者异常是保留的，且监听器关注
                        (principalIsPreserved && careForAuthenticationServerEnginePreservedPrincipal != null) ||
                                (exceptionIsPreserved && careForAuthenticationServerEnginePreservedException != null)
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
                                        private final String exceptionClass = safeGetExceptionClass((AuthenticationEvent) payload);
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
    private class Event {
    }

    /**
     * @author ZhangHan
     * @date 2022/4/28 0:06
     */
    private class EventPayload {
    }
}
