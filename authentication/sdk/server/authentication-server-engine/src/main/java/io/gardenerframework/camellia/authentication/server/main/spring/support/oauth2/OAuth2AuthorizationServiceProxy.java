package io.gardenerframework.camellia.authentication.server.main.spring.support.oauth2;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticatedAuthentication;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.common.utils.HttpRequestUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/5/21 2:47
 */
@Aspect
@AuthenticationServerEngineComponent
@AllArgsConstructor
@Slf4j
public class OAuth2AuthorizationServiceProxy {
    private final EnhancedOAuth2TokenCustomizer tokenCustomizer;
    private final OAuth2AuthorizationIdModifier idModifier;
    private final ClientGroupProvider clientGroupProvider;

    /**
     * 拦截{@link OAuth2AuthorizationService#save(OAuth2Authorization)}方法
     *
     * @param proceedingJoinPoint 切入点
     * @return 原来方法的执行结果
     * @throws Throwable 抛出问题
     */
    @Around("execution(* org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService.save(..))")
    public Object onSave(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        OAuth2Authorization authorization = (OAuth2Authorization) proceedingJoinPoint.getArgs()[0];
        if (authorization != null) {
            //判断是否已经折腾过了
            if (authorization.getAttribute(OAuth2AuthorizationServiceProxy.class.getName()) == null) {
                //还没折腾过
                //先看看 refresh token是否需要客制化
                OAuth2RefreshToken originalRefreshToken = null;
                OAuth2RefreshToken rebuiltRefreshToken = null;
                if (authorization.getRefreshToken() != null && authorization.getRefreshToken().getToken() != null) {
                    rebuiltRefreshToken = tokenCustomizer.customizeRefreshToken(originalRefreshToken = authorization.getRefreshToken().getToken());
                }
                //再看看id是否要修改
                String originalId = authorization.getId();
                UserAuthenticatedAuthentication authenticatedUser = authorization.getAttribute(Principal.class.getName());
                User user = authenticatedUser == null ? null : authenticatedUser.getUser();
                Client client = null;
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                RegisteredClient registeredClient = null;
                if (authentication instanceof OAuth2ClientAuthenticationToken) {
                    //是在token接口或有client的认证
                    registeredClient = ((OAuth2ClientAuthenticationToken) authentication).getRegisteredClient();
                    client = new Client(
                            Objects.requireNonNull(registeredClient).getClientId(),
                            authorization.getAuthorizationGrantType().getValue(),
                            null
                    );
                }
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                String modified = idModifier.modify(originalId, HttpRequestUtils.getSafeHttpHeaders(request), clientGroupProvider.getClientGroup(registeredClient), client, user);
                //总而言之重新弄一下并标记以下已经折腾过了
                OAuth2Authorization.Builder builder = OAuth2Authorization.from(authorization).attribute(OAuth2AuthorizationServiceProxy.class.getName(), true);
                if (!Objects.equals(modified, originalId)) {
                    builder.id(modified);
                }
                if (rebuiltRefreshToken != originalRefreshToken && rebuiltRefreshToken != null) {
                    builder.refreshToken(rebuiltRefreshToken);
                }
                authorization = builder.build();
            }
        }
        return proceedingJoinPoint.proceed(new Object[]{authorization});
    }
}
