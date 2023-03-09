package io.gardenerframework.camellia.authentication.server.main.exception.client;

import io.gardenerframework.fragrans.messages.MessageArgumentsSupplier;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ZhangHan
 * @date 2022/4/26 16:27
 */
public class BadAuthenticationRequestParameterException extends BadAuthenticationRequestException
        implements MessageArgumentsSupplier {
    private final Collection<ConstraintViolation<Object>> violations;

    public BadAuthenticationRequestParameterException(Collection<ConstraintViolation<Object>> violations) {
        super("bad authentication request parameter found");
        this.violations = violations;
    }

    @Override
    public Object[] getMessageArguments() {
        if (CollectionUtils.isEmpty(violations)) {
            return new Object[0];
        } else {
            List<String> messages = new ArrayList<>(violations.size());
            violations.forEach(
                    constraintViolation -> messages.add(String.format("[%s]%s", constraintViolation.getPropertyPath(), constraintViolation.getMessage()))
            );
            return new Object[]{String.join(",", messages)};
        }
    }
}
