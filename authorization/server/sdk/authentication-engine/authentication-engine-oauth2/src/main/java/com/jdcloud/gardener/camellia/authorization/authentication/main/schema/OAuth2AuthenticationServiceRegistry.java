package com.jdcloud.gardener.camellia.authorization.authentication.main.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.OAuth2AuthenticationServiceBase;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:54
 */
@RequiredArgsConstructor
public class OAuth2AuthenticationServiceRegistry implements InitializingBean {
    private final Collection<OAuth2AuthenticationServiceBase> services;
    private final Map<String, OAuth2AuthenticationServiceBase> authenticationServices = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(services)) {
            services.forEach(
                    service -> authenticationServices.put(
                            Objects.requireNonNull(AnnotationUtils.findAnnotation(
                                            service.getClass(), AuthenticationType.class
                                    ))
                                    .value(),
                            service
                    )
            );
        }
    }

    /**
     * 获取给定{@link AuthenticationType}注解标记的sns认证服务
     *
     * @param type 类型
     * @return 服务
     */
    @Nullable
    public OAuth2AuthenticationServiceBase getService(String type) {
        return authenticationServices.get(type);
    }
}
