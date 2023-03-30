package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.configuration.MfaAuthenticationServerEngineComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/29 17:44
 */
@MfaAuthenticationServerEngineComponent
@RequiredArgsConstructor
@Order
public class OAuth2RequestingClientDeserializer implements Converter<Map<String, Object>, OAuth2RequestingClient> {
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public OAuth2RequestingClient convert(Map<String, Object> source) {
        OAuth2RequestingClient oAuth2RequestingClient = objectMapper.convertValue(source, OAuth2RequestingClient.class);
        if (oAuth2RequestingClient != null && oAuth2RequestingClient.getClientId() != null && oAuth2RequestingClient.getGrantType() != null) {
            return oAuth2RequestingClient;
        }
        return null;
    }
}
