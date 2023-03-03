package io.gardenerframework.camellia.authentication.server.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.BadAuthenticationRequestParameterException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.UsernamePasswordAuthenticationParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.username.UsernameResolver;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;

/**
 * @author zhanghan30
 * @date 2021/12/27 12:44 下午
 */

@AuthenticationType("username")
public abstract class UsernamePasswordAuthenticationServiceBase implements UserAuthenticationService {
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private UsernameResolver resolver;
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private Validator validator;

    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private Collection<UsernamePasswordAuthenticationParameterPostProcessor> processors;

    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest httpServletRequest) throws AuthenticationException {
        UsernamePasswordAuthenticationParameter parameter = new UsernamePasswordAuthenticationParameter(
                httpServletRequest
        );
        Set<ConstraintViolation<Object>> violations = validator.validate(parameter);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
        if (!CollectionUtils.isEmpty(processors)) {
            //执行后置处理
            processors.forEach(
                    processor -> processor.afterParameterValidated(parameter)
            );
        }
        return new UserAuthenticationRequestToken(
                resolver.resolve(parameter.getUsername(), parameter.getPrincipalType()),
                //明确使用密码类型的凭据，要求走authenticate方法
                new PasswordCredentials(parameter.getPassword())
        );
    }

    /**
     * 进行密码之间的比较
     *
     * @param authenticationRequest 认证请求
     * @param user                  认证用户
     * @throws AuthenticationException 认证有问题
     */
    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {
        if (!compareCredentials(authenticationRequest.getCredentials(), user.getCredentials())) {
            throw new BadCredentialsException(user.getId());
        }
    }

    /**
     * 比较密码是不是一样
     *
     * @param request   请求中的密码
     * @param authority 用户存储的密码
     * @return 如果用户读取服务完成了认证，这个方法是可以直接返回true
     */
    protected abstract boolean compareCredentials(BasicCredentials request, BasicCredentials authority);
}
