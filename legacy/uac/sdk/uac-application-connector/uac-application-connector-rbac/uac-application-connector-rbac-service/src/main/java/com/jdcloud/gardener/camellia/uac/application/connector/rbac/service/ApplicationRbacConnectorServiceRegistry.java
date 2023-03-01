package com.jdcloud.gardener.camellia.uac.application.connector.rbac.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghan30
 * @date 2022/11/17 20:21
 */
@RequiredArgsConstructor
public class ApplicationRbacConnectorServiceRegistry implements InitializingBean {
    /**
     * 服务清单
     */
    private final Collection<ApplicationRbacConnectorService> services;
    /**
     * 注册表
     */
    private final Map<String, ApplicationRbacConnectorService> applicationIdServiceMapping = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(services)) {
            services.forEach(
                    service -> {
                        Collection<String> ids = Objects.requireNonNull(service.supportApplicationIds());
                        Assert.notEmpty(ids, "no application id supported by " + service.getClass());
                        ids.forEach(
                                id -> {
                                    ApplicationRbacConnectorService serviceRegistered = applicationIdServiceMapping.putIfAbsent(id, service);
                                    Assert.isNull(serviceRegistered,
                                            "application " + id + " was supported by " +
                                                    Objects.requireNonNull(serviceRegistered).getClass() +
                                                    ", but " + service.getClass() + " also claimed");
                                }
                        );

                    }
            );
        }
    }

    /**
     * 获取给定应用的服务实例
     *
     * @param applicationId 应用id
     * @return 服务实例，找不到就返回{@code null}
     */
    @Nullable
    public ApplicationRbacConnectorService get(@NonNull String applicationId) {
        return this.applicationIdServiceMapping.get(applicationId);
    }
}
