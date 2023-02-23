package com.jdcloud.gardener.camellia.authorization.authentication.main.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationEndpoint;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.AlreadyExisted;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.NotFound;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Register;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Update;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
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
@Component
@Slf4j
@RequiredArgsConstructor
public class UserAuthenticationServiceRegistry implements InitializingBean {
    private final Map<String, UserAuthenticationServiceRegistryItem> registry = new ConcurrentHashMap<>();
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
}
