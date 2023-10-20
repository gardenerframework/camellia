package io.gardenerframework.camellia.authentication.server.main.utils;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author zhanghan30
 * @date 2023/3/6 19:41
 */
@AuthenticationServerEngineComponent
@RequiredArgsConstructor
@Slf4j
public class DefaultUserAuthenticationServiceRegistry implements UserAuthenticationServiceRegistry, InitializingBean {
    private final Map<String, UserAuthenticationService> registry = new LinkedHashMap<>();
    private final Collection<UserAuthenticationService> services;
    private final GenericOperationLogger operationLogger = GenericLoggers.operationLogger();

    @Override
    public Collection<String> getUserAuthenticationServiceTypes(boolean ignorePreserved) {
        List<String> types = new LinkedList<>();
        registry.forEach(
                (type, service) -> {
                    if (AnnotationUtils.findAnnotation(service.getClass(), AuthenticationServerEnginePreserved.class) != null
                            && ignorePreserved) {
                        return;
                    }
                    types.add(type);
                }
        );
        return new ArrayList<>(types);
    }

    @Override
    public boolean hasUserAuthenticationService(@NonNull String type, boolean ignorePreserved) {
        return getUserAuthenticationServiceTypes(ignorePreserved).contains(type);
    }

    @Nullable
    @Override
    public UserAuthenticationService getUserAuthenticationService(@NonNull String type, boolean ignorePreserved) {
        return hasUserAuthenticationService(type, ignorePreserved) ? registry.get(type) : null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(services)) {
            services.forEach(
                    service -> {
                        String authenticationType = Objects.requireNonNull(AnnotationUtils.findAnnotation(service.getClass(), AuthenticationType.class)).value();
                        if (registry.get(authenticationType) != null) {
                            throw new IllegalStateException(authenticationType + " already registered with " + registry.get(authenticationType).getClass().getName());
                        }
                        registry.put(authenticationType, service);
                    }
            );
            operationLogger.info(
                    log,
                    GenericOperationLogContent.builder()
                            .what(UserAuthenticationService.class)
                            .operation(new Register())
                            .state(new Done())
                            .detail(new Detail() {
                                private final Collection<String> types = registry.keySet();
                            }).build(),
                    null
            );
        }
    }
}
