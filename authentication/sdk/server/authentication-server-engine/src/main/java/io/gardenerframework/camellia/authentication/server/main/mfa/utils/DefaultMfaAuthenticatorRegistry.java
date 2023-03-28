package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticator;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.reason.AlreadyExisted;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
public class DefaultMfaAuthenticatorRegistry implements MfaAuthenticatorRegistry, InitializingBean {
    private final GenericBasicLogger basicLogger;
    private final GenericOperationLogger operationLogger;
    /**
     * key = 认证器名称
     * <p>
     * value = 服务和激活标记
     */
    private final Map<String, MfaAuthenticator<? extends ChallengeRequest, ? extends Challenge, ? extends ChallengeContext>> registry = new HashMap<>(10);
    /**
     * 所有mfa挑战应答服务类
     */
    @NonNull
    private final Collection<MfaAuthenticator<? extends ChallengeRequest, ? extends Challenge, ? extends ChallengeContext>> services;

    @Nullable
    private String parseName(MfaAuthenticator<? extends ChallengeRequest, ? extends Challenge, ? extends ChallengeContext> service) {
        if (service instanceof ChallengeAuthenticatorNameProvider) {
            return ((ChallengeAuthenticatorNameProvider) service).getChallengeAuthenticatorName();
        } else {
            ChallengeAuthenticator annotation = AnnotationUtils.findAnnotation(
                    service.getClass(),
                    ChallengeAuthenticator.class
            );
            if (annotation == null) {
                basicLogger.error(
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
                return null;
            } else {
                return annotation.value();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(services)) {
            basicLogger.error(
                    log,
                    GenericBasicLogContent.builder()
                            .what(ChallengeResponseService.class)
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
                    if (!StringUtils.hasText(name)) {
                        //没有名称，不是目标挑战服务
                        return;
                    }
                    if (registry.get(name) == null) {
                        registry.put(
                                name,
                                service
                        );
                    } else {
                        basicLogger.error(
                                log,
                                GenericBasicLogContent.builder()
                                        .what(MfaAuthenticationChallengeResponseService.class)
                                        .how(new AlreadyExisted())
                                        .detail(new Detail() {
                                            private final String serviceClass = service.getClass().getCanonicalName();
                                            private final String authenticator = name;
                                        })
                                        .build(),
                                null
                        );
                        throw new IllegalStateException("fail to start due to duplicated challenge authenticator name");
                    }
                }
        );
        operationLogger.info(
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
    public <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext> MfaAuthenticator<R, C, X> getAuthenticator(@NonNull String name) {
        return (MfaAuthenticator<R, C, X>) registry.get(name);
    }

}
