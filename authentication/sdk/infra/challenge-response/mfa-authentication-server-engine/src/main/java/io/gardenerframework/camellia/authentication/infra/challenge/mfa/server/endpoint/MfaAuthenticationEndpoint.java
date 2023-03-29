package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.endpoint;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaAuthenticationServerScenario;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.configuration.MfaAuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.CloseChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.SendChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.VerifyResponseRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.constraints.MfaAuthenticatorSupported;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ListAuthenticatorsResponse;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ResponseVerificationResponse;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.utils.MfaAuthenticatorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:25
 */
@RequestMapping("/mfa")
@RequiredArgsConstructor
@MfaAuthenticationServerEngineComponent
@RestController
public class MfaAuthenticationEndpoint implements MfaAuthenticationEndpointSkeleton {
    /**
     * 注册表
     */
    private final MfaAuthenticatorRegistry registry;
    /**
     * 执行客户端翻序列化
     */
    private final Collection<Converter<Map<String, Object>, ? extends RequestingClient>> clientDataDeserializers;


    @GetMapping
    @Override
    public ListAuthenticatorsResponse listAuthenticators() throws Exception {
        return new ListAuthenticatorsResponse(registry.getAuthenticatorNames());
    }

    @PostMapping("/{authenticator}:send")
    @Override
    public Challenge sendChallenge(
            @Valid @PathVariable("authenticator") @MfaAuthenticatorSupported String authenticator,
            @Valid @RequestBody SendChallengeRequest request) throws Exception {
        RequestingClient client = deserializeRequestingClient(request.getRequestingClient());
        Class<? extends Scenario> scenario = deserializeScenario(request.getScenario());
        MfaAuthenticator<ChallengeRequest, Challenge, ChallengeContext> authenticatorInstance = Objects.requireNonNull(registry.getAuthenticator(authenticator));
        return authenticatorInstance.sendChallenge(
                client,
                scenario,
                authenticatorInstance.createChallengeRequest(request.getUser(), client, scenario)
        );
    }

    @PostMapping("/{authenticator}:verify")
    @Override
    public ResponseVerificationResponse verifyResponse(
            @Valid @PathVariable("authenticator") @MfaAuthenticatorSupported String authenticator,
            @Valid @RequestBody VerifyResponseRequest request
    ) throws Exception {
        RequestingClient client = deserializeRequestingClient(request.getRequestingClient());
        Class<? extends Scenario> scenario = deserializeScenario(request.getScenario());
        MfaAuthenticator<ChallengeRequest, Challenge, ChallengeContext> authenticatorInstance = Objects.requireNonNull(registry.getAuthenticator(authenticator));
        return new ResponseVerificationResponse(
                authenticatorInstance.verifyResponse(
                        client,
                        scenario,
                        request.getChallengeId(),
                        request.getResponse()
                ));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{authenticator}")
    @Override
    public void closeChallenge(
            @Valid @MfaAuthenticatorSupported String authenticator,
            @Valid @RequestBody CloseChallengeRequest request
    ) throws Exception {
        RequestingClient client = deserializeRequestingClient(request.getRequestingClient());
        Class<? extends Scenario> scenario = deserializeScenario(request.getScenario());
        MfaAuthenticator<ChallengeRequest, Challenge, ChallengeContext> authenticatorInstance = Objects.requireNonNull(registry.getAuthenticator(authenticator));
        authenticatorInstance.closeChallenge(
                client,
                scenario,
                request.getChallengeId()
        );
    }

    /**
     * 反序列化客户端
     *
     * @param data 数据
     * @return 反序列化完成的结果
     */
    @Nullable
    private RequestingClient deserializeRequestingClient(@Nullable Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        for (Converter<Map<String, Object>, ? extends RequestingClient> deserializer : clientDataDeserializers) {
            RequestingClient client = deserializer.convert(data);
            if (client != null) {
                return client;
            }
        }
        throw new IllegalArgumentException("cannot deserialize requesting client");
    }

    /**
     * 反序列化场景
     *
     * @param scenario 场景
     * @return 场景类
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Scenario> deserializeScenario(@Nullable String scenario) {
        try {
            if (StringUtils.hasText(scenario)) {
                //尝试完成类型转换
                Class<?> clazz = Class.forName(scenario);
                if (Scenario.class.isAssignableFrom(clazz)) {
                    return (Class<? extends Scenario>) clazz;
                }
            }
        } catch (ClassNotFoundException e) {
            //不需要处理什么，返回兜底类型
        }
        return MfaAuthenticationServerScenario.class;
    }
}
