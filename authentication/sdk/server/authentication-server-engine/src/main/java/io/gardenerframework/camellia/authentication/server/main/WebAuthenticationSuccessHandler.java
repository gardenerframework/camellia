package io.gardenerframework.camellia.authentication.server.main;

import com.jdcloud.gardener.camellia.authorization.common.configuration.AuthorizationServerPathOption;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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
@Data
@EqualsAndHashCode(callSuper = true)
@Component
public class WebAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public WebAuthenticationSuccessHandler(AuthorizationServerPathOption authorizationServerPathOption) {
        super();
        //设置为登录成功地址
        this.setDefaultTargetUrl(authorizationServerPathOption.getWebLoginSuccessPage());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
