package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.BadAuthenticationRequestParameterException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.QrCodeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.qrcode.service.QrCodeService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 转请求 + 认证一体化
 *
 * @author zhanghan30
 * @date 2021/12/31 10:15 下午
 */
@AllArgsConstructor
@AuthenticationType("qrcode")
@Component
public class QrCodeAuthenticationService implements UserAuthenticationService {
    private final QrCodeService qrCodeService;
    private final Validator validator;

    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) {
        QrCodeParameter parameter = new QrCodeParameter(request);
        Set<ConstraintViolation<Object>> violations = validator.validate(parameter);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
        return new UserAuthenticationRequestToken(qrCodeService.getPrincipal(parameter.getToken()));

    }

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {
        //基于token能正常读出来用户就通过了，因此在加载和检查用户状态时没有发生问题就等价于通过
    }
}
