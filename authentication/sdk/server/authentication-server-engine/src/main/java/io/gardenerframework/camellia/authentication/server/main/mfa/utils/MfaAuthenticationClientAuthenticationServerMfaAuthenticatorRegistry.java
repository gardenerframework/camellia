package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.client.MfaAuthenticationClientPrototype;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.CloseChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.SendChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.VerifyResponseRequest;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticator;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.common.schema.reason.AlreadyExisted;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/31 16:11
 */
@RequiredArgsConstructor
@Slf4j
@AuthenticationServerEngineComponent
@ConditionalOnClass(MfaAuthenticationClientPrototype.class)
public class MfaAuthenticationClientAuthenticationServerMfaAuthenticatorRegistry implements
        AuthenticationServerMfaAuthenticatorRegistry, InitializingBean {
    private final GenericBasicLogger basicLogger;
    private final ObjectMapper objectMapper;
    /**
     * 注册的所有客户端
     */
    private final Collection<MfaAuthenticationClientPrototype<? extends Challenge>> clients;
    /**
     * 那些客户毒案表达了自己和某个远程认证的名称绑定
     */
    private final Map<String, MfaAuthenticationClientPrototype<? extends Challenge>> knownRemoteAuthenticatorClientMappings;
    /**
     * 当名称没有找到绑定时使用的默认客户毒案
     */
    private MfaAuthenticationClientPrototype<? extends Challenge> defaultClient;

    @Nullable
    private String parseName(MfaAuthenticationClientPrototype<? extends Challenge> client) {
        if (client instanceof ChallengeAuthenticatorNameProvider) {
            return ((ChallengeAuthenticatorNameProvider) client).getChallengeAuthenticatorName();
        } else {
            ChallengeAuthenticator annotation = AnnotationUtils.findAnnotation(
                    client.getClass(),
                    ChallengeAuthenticator.class
            );
            if (annotation == null) {
                return null;
            } else {
                return annotation.value();
            }
        }
    }

    @Override
    public Collection<String> getAuthenticatorNames() {
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext> AuthenticationServerMfaAuthenticator<R, C, X> getAuthenticator(@NonNull String name) {
        MfaAuthenticationClientPrototype<? extends Challenge> client = knownRemoteAuthenticatorClientMappings.get(name);
        if (client == null && defaultClient == null) {
            //没有人负责这个认证名称
            return null;
        }
        return (AuthenticationServerMfaAuthenticator<R, C, X>) new AuthenticationServerMfaAuthenticatorAdapter(
                client == null ? defaultClient : client,
                name,
                objectMapper
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(clients)) {
            for (MfaAuthenticationClientPrototype<? extends Challenge> client : clients) {
                String name = parseName(client);
                if (StringUtils.hasText(name)) {
                    if (knownRemoteAuthenticatorClientMappings.get(name) != null) {
                        basicLogger.error(
                                log,
                                GenericBasicLogContent.builder()
                                        .what(MfaAuthenticationClientPrototype.class)
                                        .how(new AlreadyExisted())
                                        .detail(new Detail() {
                                            private final String clientClass = client.getClass().getCanonicalName();
                                            private final String authenticator = name;
                                        })
                                        .build(),
                                null
                        );
                        throw new IllegalStateException("fail to start due to duplicated challenge authenticator client name");
                    }
                    knownRemoteAuthenticatorClientMappings.put(name, client);
                } else {
                    if (defaultClient != null) {
                        basicLogger.error(
                                log,
                                GenericBasicLogContent.builder()
                                        .what(MfaAuthenticationClientPrototype.class)
                                        .how(new AlreadyExisted())
                                        .detail(new Detail() {
                                            private final String clientClass = client.getClass().getCanonicalName();
                                            private final String authenticator = name;
                                        })
                                        .build(),
                                null
                        );
                        throw new IllegalStateException("fail to start due to duplicated bottom challenge authenticator client");
                    }
                    defaultClient = client;
                }
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class MfaAuthenticationServerClientChallengeRequest implements ChallengeRequest {
        /**
         * 请求的用户
         */
        @NonNull
        private User user;
        /**
         * 请求的参数
         */
        @Nullable
        private Map<String, Object> additionalArguments;
    }

    @RequiredArgsConstructor
    private static class AuthenticationServerMfaAuthenticatorAdapter implements AuthenticationServerMfaAuthenticator<MfaAuthenticationServerClientChallengeRequest, Challenge, ChallengeContext> {
        private final MfaAuthenticationClientPrototype<? extends Challenge> mfaAuthenticationClient;
        private final String authenticatorName;

        private final ObjectMapper objectMapper;

        @Override
        public MfaAuthenticationServerClientChallengeRequest authenticationContextToChallengeRequest(
                @Nullable RequestingClient client,
                @NonNull Class<? extends Scenario> scenario,
                @NonNull Principal principal,
                @NonNull User user,
                @NonNull Map<String, Object> context
        ) throws Exception {
            return new MfaAuthenticationServerClientChallengeRequest(
                    user,
                    null
            );
        }

        @Override
        public Challenge sendChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationClientAuthenticationServerMfaAuthenticatorRegistry.MfaAuthenticationServerClientChallengeRequest request) throws ChallengeResponseServiceException, ChallengeInCooldownException {
            try {
                Challenge challenge = mfaAuthenticationClient.sendChallenge(
                        authenticatorName,
                        new SendChallengeRequest(
                                objectMapper.convertValue(request.getUser(), new TypeReference<Map<String, Object>>() {
                                }),
                                client == null ? null : objectMapper.convertValue(client, new TypeReference<Map<String, Object>>() {
                                }),
                                scenario.getName(),
                                request.getAdditionalArguments()
                        )
                );
                if (!(challenge instanceof ChallengeAuthenticatorNameProvider)) {
                    challenge = injectAuthenticatorName(challenge, authenticatorName);
                }
                return challenge;
            } catch (HttpClientErrorException e) {
                //todo
                throw new ChallengeResponseServiceException(e);
            } catch (Exception e) {
                throw new ChallengeResponseServiceException(e);
            }
        }

        private Challenge injectAuthenticatorName(@NonNull Challenge challenge, @NonNull String authenticator) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(challenge.getClass());
            //整合名称接口
            enhancer.setInterfaces(new Class[]{ChallengeAuthenticatorNameProvider.class});
            enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
                        try {
                            //当前对challenge的代理调用的是getName方法
                            ChallengeAuthenticatorNameProvider.class.getDeclaredMethod(
                                    method.getName(),
                                    method.getParameterTypes()
                            );
                            ChallengeAuthenticator annotation;
                            //首先检查服务是不是ChallengeAuthenticatorNameProvider类型
                            return authenticator;
                        } catch (NoSuchMethodException e) {
                            //要求访问的不是接口的方法
                            return method.invoke(challenge, objects);
                        }
                    }
            );
            return (Challenge) enhancer.create();
        }

        @Override
        public boolean verifyResponse(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull String response) throws ChallengeResponseServiceException {
            try {
                return mfaAuthenticationClient.verifyResponse(
                        authenticatorName,
                        new VerifyResponseRequest(
                                client == null ? null : objectMapper.convertValue(client, new TypeReference<Map<String, Object>>() {
                                }),
                                scenario.getName(),
                                challengeId,
                                response
                        )
                ).isVerified();
            } catch (Exception e) {
                throw new ChallengeResponseServiceException(e);
            }
        }

        @Nullable
        @Override
        public ChallengeContext getContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
            return null;
        }

        @Override
        public void closeChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
            try {
                mfaAuthenticationClient.closeChallenge(
                        authenticatorName,
                        new CloseChallengeRequest(
                                client == null ? null : objectMapper.convertValue(client, new TypeReference<Map<String, Object>>() {
                                }),
                                scenario.getName(),
                                challengeId
                        )
                );
            } catch (Exception e) {
                throw new ChallengeResponseServiceException(e);
            }
        }
    }
}
