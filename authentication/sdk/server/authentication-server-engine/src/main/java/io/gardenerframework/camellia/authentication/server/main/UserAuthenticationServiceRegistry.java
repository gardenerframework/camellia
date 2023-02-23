package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthorizationEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationEndpoint;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.reason.AlreadyExisted;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.common.schema.verb.Update;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户认证服务注册表
 *
 * @author ZhangHan
 * @date 2022/5/12 0:39
 */
@Slf4j
@RequiredArgsConstructor
public class UserAuthenticationServiceRegistry implements InitializingBean {
    private final Map<String, UserAuthenticationServiceRegistryItem> registry = new ConcurrentHashMap<>();
    @NonNull
    private final Collection<UserAuthenticationService> services;

    /**
     * 获取注册的认证请求类型
     *
     * @param showPreserved 是否返回保留的
     * @param showDisabled  是否返回非激活的
     * @return 类型清单
     */
    public Collection<String> getRegisteredAuthenticationTypes(boolean showPreserved, boolean showDisabled) {
        Collection<String> registered = new ArrayList<>(registry.keySet().size());
        registry.forEach(
                (type, item) -> {
                    if (item.isPreserved() && !showPreserved) {
                        return;
                    }
                    if (!item.isEnabled() && !showDisabled) {
                        return;
                    }
                    registered.add(type);
                }
        );
        return registered;
    }

    /**
     * 转换器启用状态更新
     *
     * @param type    类型
     * @param enabled 是否启用
     */
    public void changeEnabledState(String type, boolean enabled) {
        UserAuthenticationServiceRegistryItem item = registry.get(type);
        if (item != null) {
            boolean enabledForLog = enabled;
            GenericLoggerStaticAccessor.operationLogger().info(
                    log,
                    GenericOperationLogContent.builder()
                            .what(UserAuthenticationService.class)
                            .operation(new Update())
                            .state(new Done())
                            .detail(new UserAuthenticationServiceDetail(type) {
                                private final boolean enabled = enabledForLog;
                            }).build(),
                    null
            );
            item.setEnabled(enabled);
        }
    }

    /**
     * 返回指定类型的条目
     *
     * @param type 类型
     * @return 条目
     */
    @Nullable
    public UserAuthenticationServiceRegistryItem getItem(String type) {
        return registry.get(type);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(services)) {
            GenericLoggerStaticAccessor.basicLogger().error(
                    log,
                    GenericBasicLogContent.builder()
                            .what(UserAuthenticationService.class)
                            .how(new NotFound())
                            .build(),
                    null
            );
            throw new IllegalStateException("empty services");
        }
        services.forEach(
                service -> {
                    //查看注册的认证类型
                    AuthenticationType annotation = AnnotationUtils.findAnnotation(service.getClass(), AuthenticationType.class);
                    if (annotation != null) {
                        if (registry.get(annotation.value()) == null) {
                            registry.put(
                                    annotation.value(),
                                    new UserAuthenticationServiceRegistryItem(
                                            service,
                                            annotation,
                                            AnnotationUtils.findAnnotation(ClassUtils.getUserClass(service), AuthenticationEndpoint.class),
                                            AnnotationUtils.findAnnotation(service.getClass(), AuthorizationEnginePreserved.class) != null,
                                            true)
                            );
                        } else {
                            GenericLoggerStaticAccessor.basicLogger().error(
                                    log,
                                    GenericBasicLogContent.builder()
                                            .what(UserAuthenticationService.class)
                                            .how(new AlreadyExisted())
                                            .detail(new UserAuthenticationServiceDetail(annotation.value()))
                                            .build(),
                                    null
                            );
                            throw new IllegalStateException("fail to start due to duplicated authentication request type");
                        }
                    } else {
                        GenericLoggerStaticAccessor.basicLogger().error(
                                log,
                                GenericBasicLogContent.builder()
                                        .what(AuthenticationType.class)
                                        .how(new NotFound())
                                        .detail(new Detail() {
                                            private final String serviceClass = service.getClass().getCanonicalName();
                                        }).build(),
                                null
                        );
                        throw new IllegalStateException("no AuthenticationType annotation found on " + service.getClass().getCanonicalName());
                    }
                }
        );
        GenericLoggerStaticAccessor.operationLogger().info(
                log,
                GenericOperationLogContent.builder()
                        .what(UserAuthenticationService.class)
                        .operation(new Register())
                        .state(new Done())
                        .detail(new Detail() {
                            private final Collection<UserAuthenticationServiceRegistryItem> services = registry.values();
                        }).build(),
                null
        );
    }

    /**
     * @author zhanghan30
     * @date 2022/4/25 1:24 下午
     */
    @AllArgsConstructor
    private class UserAuthenticationServiceDetail implements Detail {
        private final String type;
    }

    /**
     * @author ZhangHan
     * @date 2022/5/12 0:41
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserAuthenticationServiceRegistryItem {
        /**
         * 转换器
         */
        @NonNull
        private final UserAuthenticationService service;
        /**
         * 注解
         */
        @NonNull
        private final AuthenticationType authenticationType;
        /**
         * 支持的端点类型
         */
        @Nullable
        private final AuthenticationEndpoint authenticationEndpoint;
        /**
         * 是否是工程自保留的
         */
        private final boolean preserved;
        /**
         * 是否激活的标记
         */
        private boolean enabled;
    }
}
