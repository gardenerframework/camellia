package io.gardenerframework.camellia.authentication.server.main.spring;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 负责网页认证的成功处理
 *
 * @author zhanghan30
 * @date 2021/12/27 10:37 下午
 */
@AuthenticationServerEngineComponent
public class WebAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public WebAuthenticationSuccessHandler(AuthenticationServerPathOption authenticationServerPathOption) {
        super();
        //设置为登录成功地址
        this.setDefaultTargetUrl(authenticationServerPathOption.getWebLoginSuccessPage());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
