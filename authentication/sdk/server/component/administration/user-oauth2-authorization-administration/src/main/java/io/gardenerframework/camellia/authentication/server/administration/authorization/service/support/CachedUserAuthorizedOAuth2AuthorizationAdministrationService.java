package io.gardenerframework.camellia.authentication.server.administration.authorization.service.support;

import io.gardenerframework.camellia.authentication.server.administration.authorization.schema.request.RemoveUserAuthorizedAuthorizationRequest;
import io.gardenerframework.camellia.authentication.server.administration.authorization.service.UserAuthorizedOAuth2AuthorizationAdministrationService;
import io.gardenerframework.camellia.authentication.server.administration.configuration.UserAuthorizedOAuth2AuthorizationAdministrationComponent;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticatedAuthentication;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/24 15:31
 */
@RequiredArgsConstructor
@ConditionalOnMissingBean(
        value = UserAuthorizedOAuth2AuthorizationAdministrationService.class,
        ignored = CachedUserAuthorizedOAuth2AuthorizationAdministrationService.class
)
@ConditionalOnBean(CacheClient.class)
@Import(CachedUserAuthorizedOAuth2AuthorizationAdministrationService.OAuth2AuthorizationServiceInterceptor.class)
@UserAuthorizedOAuth2AuthorizationAdministrationComponent
public class CachedUserAuthorizedOAuth2AuthorizationAdministrationService
        implements UserAuthorizedOAuth2AuthorizationAdministrationService, InitializingBean {
    /**
     * 名称空间
     */
    private static final String[] NAMESPACE = new String[]{
            "camellia",
            "authentication",
            "server",
            "component",
            "administration",
            "oauth2-authorization",
    };
    /**
     * 后缀
     */
    private static final String SUFFIX = "command";

    private final CacheClient client;
    private BasicCacheManager<Date> commandCacheManager;

    @Override
    public void removeOAuth2Authorization(RemoveUserAuthorizedAuthorizationRequest request) throws Exception {
        commandCacheManager.set(NAMESPACE, buildRemoveOAuth2AuthorizationCommandId(
                        request.getUserId(),
                        request.getClientId(),
                        request.getDeviceId()), SUFFIX,
                new Date(),
                //todo 写死了一个月
                Duration.ofHours(24 * 30)
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.commandCacheManager = new BasicCacheManager<Date>(client) {
        };
    }


    /**
     * 基于用户id，客户端id，设备id算出来移除命令id
     *
     * @param userId   用户id
     * @param clientId 客户端id
     * @param deviceId 设备id
     * @return 命令id
     */
    private String buildRemoveOAuth2AuthorizationCommandId(
            @NonNull String userId,
            @Nullable String clientId,
            @Nullable String deviceId
    ) {
        List<String> ids = new LinkedList<>();
        ids.add(userId);
        if (StringUtils.hasText(clientId)) {
            ids.add(clientId);
        }
        if (StringUtils.hasText(deviceId)) {
            ids.add(deviceId);
        }
        return String.join(".", ids);
    }

    @Nullable
    private Date getCommandExpiryTime(
            @NonNull String userId,
            @Nullable String clientId,
            @Nullable String deviceId
    ) {
        return commandCacheManager.get(
                NAMESPACE,
                buildRemoveOAuth2AuthorizationCommandId(userId, clientId, deviceId),
                SUFFIX
        );
    }

    /**
     * @author zhanghan30
     * @date 2023/3/24 16:00
     */
    @ConditionalOnClass(OAuth2AuthorizationService.class)
    @ConditionalOnBean(CachedUserAuthorizedOAuth2AuthorizationAdministrationService.class)
    @RequiredArgsConstructor
    @Aspect
    public static class OAuth2AuthorizationServiceInterceptor {
        private final OAuth2AuthorizationService oAuth2AuthorizationService;
        private final CachedUserAuthorizedOAuth2AuthorizationAdministrationService userAuthorizedOAuth2AuthorizationAdministrationService;

        @Around("execution(* org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService.findByToken(..))" +
                "|| execution(* org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService.findById(..))")
        public Object onFind(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            OAuth2Authorization result = (OAuth2Authorization) proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            if (result == null) {
                //没有找到用户授权
                return null;
            }
            OAuth2Authorization.Token<OAuth2AccessToken> accessToken = result.getAccessToken();
            if (accessToken == null || accessToken.getToken() == null) {
                //没有访问令牌
                return result;
            }
            //访问令牌的授权时间
            Instant issuedAt = accessToken.getToken().getIssuedAt();
            //获取用户和客户端id
            Map<String, Object> attributes = result.getAttributes();
            if (attributes == null) {
                //没有属性
                return result;
            }
            //获取用户信息
            UserAuthenticatedAuthentication userAuthenticatedAuthentication = (UserAuthenticatedAuthentication) attributes.get(Principal.class.getName());
            if (userAuthenticatedAuthentication == null) {
                //没有用户
                return result;
            }
            //用户id
            String userId = userAuthenticatedAuthentication.getUser().getId();
            //客户端id
            String clientId = result.getRegisteredClientId();
            //查看全渠道退出命令是否存在
            Date commandIssuedTime = userAuthorizedOAuth2AuthorizationAdministrationService.getCommandExpiryTime(userId, null, null);
            //全渠道退出
            if (commandIssuedTime != null && issuedAt != null &&
                    commandIssuedTime.after(Date.from(issuedAt))) {
                //删除
                oAuth2AuthorizationService.remove(result);
                return null;
            }
            //单一渠道退出
            commandIssuedTime = userAuthorizedOAuth2AuthorizationAdministrationService.getCommandExpiryTime(userId, clientId, null);
            if (commandIssuedTime != null && issuedAt != null
                    && commandIssuedTime.after(Date.from(issuedAt))) {
                oAuth2AuthorizationService.remove(result);
                return null;
            }
            //没有退出指令，返回正常结果
            return result;
        }
    }
}
