package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.configuration.MfaServerEngineComponent;
import io.gardenerframework.fragrans.api.validation.HandlerMethodArgumentBeanValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/29 17:44
 */
@MfaServerEngineComponent
@RequiredArgsConstructor
@Order
public class OAuth2RequestingClientDeserializer implements Converter<Map<String, Object>, OAuth2RequestingClient> {
    private final ObjectMapper objectMapper;
    private final HandlerMethodArgumentBeanValidator beanValidator;

    @Nullable
    @Override
    public OAuth2RequestingClient convert(@NonNull Map<String, Object> source) {
        OAuth2RequestingClient oAuth2RequestingClient;
        try {
            oAuth2RequestingClient = objectMapper.convertValue(source, OAuth2RequestingClient.class);
            beanValidator.validate(oAuth2RequestingClient);
        } catch (Exception e) {
            //转换能出错必然不是这个类型或者验证失败
            return null;
        }
        return oAuth2RequestingClient;
    }
}
