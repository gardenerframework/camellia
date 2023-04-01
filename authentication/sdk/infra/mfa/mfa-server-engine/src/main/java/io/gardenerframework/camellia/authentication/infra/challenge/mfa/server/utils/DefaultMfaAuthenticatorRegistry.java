package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.configuration.MfaServerEngineComponent;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.common.schema.reason.AlreadyExisted;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
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
 * @date 2023/3/29 14:22
 */
@RequiredArgsConstructor
@Slf4j
@MfaServerEngineComponent
public class DefaultMfaAuthenticatorRegistry implements MfaAuthenticatorRegistry, InitializingBean {
    private final Collection<MfaAuthenticator<? extends ChallengeRequest, ? extends Challenge, ? extends ChallengeContext>> authenticators;
    private final GenericBasicLogger basicLogger;

    private final Map<String, MfaAuthenticator<? extends ChallengeRequest, ? extends Challenge, ? extends ChallengeContext>> registry = new HashMap<>();

    @Override
    public Collection<String> getAuthenticatorNames() {
        return registry.keySet();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext, T extends MfaAuthenticator<R, C, X>> T getAuthenticator(@NonNull String name) {
        return (T) registry.get(name);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(authenticators)) {
            //没有认证器
            return;
        }
        authenticators.forEach(
                mfaAuthenticator -> {
                    String name = parseName(mfaAuthenticator);
                    if (!StringUtils.hasText(name)) {
                        //没有名称，不是目标挑战服务
                        return;
                    }
                    if (registry.get(name) == null) {
                        registry.put(
                                name,
                                mfaAuthenticator
                        );
                    } else {
                        basicLogger.error(
                                log,
                                GenericBasicLogContent.builder()
                                        .what(MfaAuthenticator.class)
                                        .how(new AlreadyExisted())
                                        .detail(new Detail() {
                                            private final String serviceClass = mfaAuthenticator.getClass().getCanonicalName();
                                            private final String authenticator = name;
                                        })
                                        .build(),
                                null
                        );
                        throw new IllegalStateException("fail to start due to duplicated mfa authenticator name");
                    }
                }
        );
    }

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
}
