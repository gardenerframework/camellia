package io.gardenerframework.camellia.authentication.server.main.schema;

import io.gardenerframework.camellia.authentication.server.main.LoginAuthenticationRequestAuthenticator;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter;

import java.util.Collection;
import java.util.Collections;

/**
 * 登录认证请求
 * <p>
 * 请求中包含了用户的登录申请
 * <p>
 * 还有要求用户登录的客户端
 *
 * @author zhanghan30
 * @date 2022/1/4 1:59 下午
 */
@AllArgsConstructor
@Getter
@Setter
public class LoginAuthenticationRequestToken implements Authentication {
    private final transient UserAuthenticationRequestToken userAuthenticationRequestToken;
    /**
     * 当前要进行用户访问的客户端
     * <p>
     * {@link OAuth2ClientAuthenticationFilter}认证了client id和密码
     * 别的没有认证
     */
    @Nullable
    private final transient OAuth2ClientUserAuthenticationToken clientUserAuthenticationRequestToken;
    /**
     * 客户端组
     */
    private final transient String clientGroup;
    /**
     * 认证请求的上下文，主要是给{@link LoginAuthenticationRequestAuthenticator}用的
     */
    private final transient LoginAuthenticationRequestContext context;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return userAuthenticationRequestToken.getCredentials();
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Principal getPrincipal() {
        return userAuthenticationRequestToken.getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        //没有认证当然返回false
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        //do nothing
    }

    @Override
    public String getName() {
        return getPrincipal().getName();
    }
}
