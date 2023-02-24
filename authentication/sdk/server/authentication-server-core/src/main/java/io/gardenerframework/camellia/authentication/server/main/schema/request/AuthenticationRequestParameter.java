package io.gardenerframework.camellia.authentication.server.main.schema.request;

import io.gardenerframework.camellia.authentication.server.main.exception.client.BadAuthenticationRequestParameterException;
import lombok.NonNull;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 从http参数中读取认证请求并转为目标类型的基类
 *
 * @author ZhangHan
 * @date 2022/4/26 16:58
 */
public abstract class AuthenticationRequestParameter {
    protected AuthenticationRequestParameter(HttpServletRequest request) {
    }

    /**
     * 执行参数验证
     *
     * @param validator 验证
     * @throws BadAuthenticationRequestParameterException 抛出错误参数异常
     */
    public void validate(@NonNull Validator validator) throws BadAuthenticationRequestParameterException {
        Set<ConstraintViolation<Object>> violations = validator.validate(this);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
    }
}
