package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.reason.AlreadyExisted;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/2/27 14:28
 */
@RequiredArgsConstructor
@Slf4j
@AuthenticationServerEngineComponent
public class DefaultMfaAuthenticationChallengeResponseServiceRegistry implements MfaAuthenticationChallengeResponseServiceRegistry, InitializingBean {
    /**
     * key = 认证器名称
     * <p>
     * value = 服务和激活标记
     */
    private final Map<String, MfaAuthenticationChallengeResponseService<? extends MfaAuthenticationChallengeRequest, ? extends MfaAuthenticationChallengeContext>> registry = new HashMap<>();
    /**
     * 所有mfa挑战应答服务类
     */
    @NonNull
    private final Collection<MfaAuthenticationChallengeResponseService<? extends MfaAuthenticationChallengeRequest, ? extends MfaAuthenticationChallengeContext>> services;

    @NonNull
    private String parseName(MfaAuthenticationChallengeResponseService<? extends MfaAuthenticationChallengeRequest, ? extends MfaAuthenticationChallengeContext> service) {
        if (service instanceof ChallengeAuthenticatorNameProvider) {
            return ((ChallengeAuthenticatorNameProvider) service).getChallengeAuthenticatorName();
        } else {
            ChallengeAuthenticator annotation = AnnotationUtils.findAnnotation(
                    service.getClass(),
                    ChallengeAuthenticator.class
            );
            if (annotation == null) {
                GenericLoggerStaticAccessor.basicLogger().error(
                        log,
                        GenericBasicLogContent.builder()
                                .what(ChallengeAuthenticator.class)
                                .how(new NotFound())
                                .detail(new Detail() {
                                    private final Class<?> serviceClass = service.getClass();
                                })
                                .build(),
                        null
                );
                throw new IllegalStateException(services.getClass().getCanonicalName() + " needs to be a ChallengeAuthenticatorNameProvider or annotates with ChallengeAuthenticator");
            } else {
                return annotation.value();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(services)) {
            GenericLoggerStaticAccessor.basicLogger().error(
                    log,
                    GenericBasicLogContent.builder()
                            .what(MfaAuthenticationChallengeResponseService.class)
                            .how(new NotFound())
                            .build(),
                    null
            );
            //返回
            return;
        }
        services.forEach(
                service -> {
                    String name = parseName(service);
                    if (registry.get(name) == null) {
                        registry.put(
                                name,
                                service
                        );
                    } else {
                        GenericLoggerStaticAccessor.basicLogger().error(
                                log,
                                GenericBasicLogContent.builder()
                                        .what(MfaAuthenticationChallengeResponseService.class)
                                        .how(new AlreadyExisted())
                                        .detail(new DefaultMfaAuthenticationChallengeResponseServiceRegistry.MfaAuthenticationChallengeResponseServiceDetail(name))
                                        .build(),
                                null
                        );
                        throw new IllegalStateException("fail to start due to duplicated challenge authenticator name");
                    }
                }
        );
        GenericLoggerStaticAccessor.operationLogger().info(
                log,
                GenericOperationLogContent.builder()
                        .what(MfaAuthenticationChallengeResponseService.class)
                        .operation(new Register())
                        .state(new Done())
                        .detail(new Detail() {
                            private final Collection<String> services = registry.keySet();
                        }).build(),
                null
        );
    }

    @Override
    public Collection<String> getAuthenticatorNames() {
        return registry.keySet();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <R extends MfaAuthenticationChallengeRequest, X extends MfaAuthenticationChallengeContext> MfaAuthenticationChallengeResponseService<R, X> getMfaAuthenticationChallengeResponseService(@NonNull String name) {
        return (MfaAuthenticationChallengeResponseService<R, X>) registry.get(name);
    }

    /**
     * @author zhanghan30
     * @date 2022/4/25 1:24 下午
     */
    @AllArgsConstructor
    private static class MfaAuthenticationChallengeResponseServiceDetail implements Detail {
        private final String name;
    }
}
