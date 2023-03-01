package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.OAuth2ClientUserAuthenticationToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticatedAuthentication;
import com.jdcloud.gardener.camellia.authorization.authentication.main.support.oauth2.OAuth2AccessTokenGranter;
import com.jdcloud.gardener.camellia.authorization.authentication.utils.AuthenticationEndpointMatcher;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 不同的认证接口有不同的要求类型
 * {@link OAuth2TokenEndpointFilter}要求{@link OAuth2AccessTokenAuthenticationToken}
 * 网页接口则只需要{@link Authentication}
 * <p>
 * 因此类似使用适配器的模式进行适配
 *
 * @author zhanghan30
 * @date 2022/5/12 5:21 下午
 */
@Component
@AllArgsConstructor
public class AuthenticationEndpointAuthenticatedAuthenticationAdapter {
    private final AuthenticationEndpointMatcher authenticationEndpointMatcher;
    private final OAuth2AccessTokenGranter oAuth2AccessTokenGranter;

    /**
     * 进行适配
     *
     * @param request                  http 请求，用来判断路径
     * @param userAuthentication       完成认证的用户
     * @param clientUserAuthentication 完成认证的客户端
     * @return 适配结果
     */
    public Authentication adapt(HttpServletRequest request, UserAuthenticatedAuthentication userAuthentication, OAuth2ClientUserAuthenticationToken clientUserAuthentication) {
        if (authenticationEndpointMatcher.isTokenEndpoint(request)) {
            return oAuth2AccessTokenGranter.createOAuth2AccessTokenAuthenticationToken(
                    clientUserAuthentication,
                    userAuthentication
            );
        } else {
            return userAuthentication;
        }
    }
}
