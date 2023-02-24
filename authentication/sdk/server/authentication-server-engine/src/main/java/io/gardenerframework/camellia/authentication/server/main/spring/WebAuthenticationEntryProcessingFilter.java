package io.gardenerframework.camellia.authentication.server.main.spring;

import io.gardenerframework.camellia.authentication.server.main.schema.LoginAuthenticationRequestToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 负责web端的/login地址的认证
 * <p>
 * token的不用我管，由{@link OAuth2TokenEndpointFilter}负责
 * <p>
 * 也就是spring自己负责
 *
 * @author zhanghan30
 * @date 2021/12/27 12:35 下午
 */
@Slf4j
public class WebAuthenticationEntryProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final LoginAuthenticationRequestConverter loginAuthenticationRequestConverter;

    public WebAuthenticationEntryProcessingFilter(
            RequestMatcher requestMatcher,
            LoginAuthenticationRequestConverter loginAuthenticationRequestConverter,
            AuthenticationEndpointAuthenticationFailureHandler authenticationFailureHandler,
            WebAuthenticationSuccessHandler webAuthenticationSuccessHandler,
            AuthenticationManager authenticationManager

    ) {
        super(requestMatcher, authenticationManager);
        this.loginAuthenticationRequestConverter = loginAuthenticationRequestConverter;
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
        this.setAuthenticationSuccessHandler(webAuthenticationSuccessHandler);
    }

    /**
     * 尝试执行认证
     *
     * @param request  http请求
     * @param response 响应
     * @return 认证结果
     * @throws AuthenticationException 如果认证失败就交给失败处理器
     * @throws IOException             Io有问题
     * @throws ServletException        基本的Servlet问题
     * @see AuthenticationEndpointAuthenticationFailureHandler
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //由转换器抛出的AuthenticationException都会被转为错误码并转到统一报错页面
        LoginAuthenticationRequestToken loginAuthenticationRequestToken = loginAuthenticationRequestConverter.convert(request);
        //获取用户认证信息
        return getAuthenticationManager().authenticate(loginAuthenticationRequestToken);
    }
}
