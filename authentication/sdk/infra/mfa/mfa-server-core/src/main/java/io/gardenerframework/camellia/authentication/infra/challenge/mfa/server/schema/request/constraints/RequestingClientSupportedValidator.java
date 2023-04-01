package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.constraints;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/29 15:03
 */
public class RequestingClientSupportedValidator extends AbstractConstraintValidator<RequestingClientSupported, Map<String, Object>> {
    @Autowired
    private Collection<Converter<Map<String, Object>, ? extends RequestingClient>> clientDataDeserializers;

    @Override
    protected boolean validate(Map<String, Object> value, ConstraintValidatorContext context, Map<String, Object> data) {
        //为空可以
        if (value == null) {
            return true;
        }
        for (Converter<Map<String, Object>, ? extends RequestingClient> clientDataDeserializer : clientDataDeserializers) {
            if (clientDataDeserializer.convert(value) != null) {
                return true;
            }
        }
        return false;
    }
}