package com.jdcloud.gardener.camellia.authorization.common.api.security;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.RegisteredClientProxy;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticatedAuthentication;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenProtectedEndpointSetting;
import com.jdcloud.gardener.fragrans.api.advice.engine.EndpointHandlerMethodAdvice;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.UnauthorizedException;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.Inactive;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.Mismatch;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.NotFound;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 切上所有RestController方法
 *
 * @author ZhangHan
 * @date 2022/5/14 1:41
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessTokenProtectedEndpointHandlerMethodAdvice implements EndpointHandlerMethodAdvice, PriorityOrdered {
    private final Map<Class<?>, AccessTokenProtectedEndpointSetting> providedAccessTokenProtectedEndpoints = new ConcurrentHashMap<>();
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final RegisteredClientRepository registeredClientRepository;
    //请求范围的bean
    private final AccessTokenDetails accessTokenDetails;

    public AccessTokenProtectedEndpointHandlerMethodAdvice(OAuth2AuthorizationService oAuth2AuthorizationService, RegisteredClientRepository registeredClientRepository, AccessTokenDetails accessTokenDetails, Collection<AccessTokenProtectedEndpointSupplier> accessTokenProtectedEndpointSuppliers) {
        this.oAuth2AuthorizationService = oAuth2AuthorizationService;
        this.registeredClientRepository = registeredClientRepository;
        this.accessTokenDetails = accessTokenDetails;
        if (!CollectionUtils.isEmpty(accessTokenProtectedEndpointSuppliers)) {
            accessTokenProtectedEndpointSuppliers.forEach(
                    accessTokenProtectedEndpointSupplier -> {
                        AccessTokenProtectedEndpointSetting accessTokenProtectedEndpointSetting = accessTokenProtectedEndpointSupplier.getAccessTokenProtectedEndpoint();
                        providedAccessTokenProtectedEndpoints.put(accessTokenProtectedEndpointSetting.getEndpoint(), accessTokenProtectedEndpointSetting);
                    }
            );
        }
    }

    @Override
    public void before(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
        AccessTokenProtectedEndpointSetting setting = getSetting(ClassUtils.getUserClass(target), methodSignature);
        if (setting == null) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AbstractOAuth2TokenAuthenticationToken)) {
            //不是access token
            handleAccessTokenNotFound(setting, authentication);
        } else {
            handleAccessTokenFound(setting, (AbstractOAuth2TokenAuthenticationToken<?>) authentication);
        }
    }

    private void handleAccessTokenNotFound(AccessTokenProtectedEndpointSetting setting, Authentication authentication) {
        GenericLoggerStaticAccessor.basicLogger().debug(
                log,
                GenericBasicLogContent.builder()
                        .what(authentication == null ? Authentication.class : authentication.getClass())
                        .how(authentication == null ? new NotFound() : new Mismatch()).build(),
                null
        );
        justFailIfAccessTokenIsRequired(setting);
    }

    private void handleAccessTokenFound(AccessTokenProtectedEndpointSetting setting, AbstractOAuth2TokenAuthenticationToken<?> authentication) {
        String tokenValue = authentication.getToken().getTokenValue();
        OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findByToken(tokenValue, OAuth2TokenType.ACCESS_TOKEN);
        if (oAuth2Authorization == null) {
            //token不正确
            GenericLoggerStaticAccessor.basicLogger().debug(
                    log,
                    GenericBasicLogContent.builder()
                            .what(AccessTokenTarget.class)
                            .how(new NotFound())
                            .detail(new Detail() {
                                private final String token = tokenValue;
                            }).build(),
                    null
            );
            justFailIfAccessTokenIsRequired(setting);
        } else {
            OAuth2Authorization.Token<OAuth2AccessToken> authorizedAccessToken = oAuth2Authorization.getAccessToken();
            if (!authorizedAccessToken.isActive()) {
                //token不正确
                GenericLoggerStaticAccessor.basicLogger().debug(
                        log,
                        GenericBasicLogContent.builder()
                                .what(AccessTokenTarget.class)
                                .how(new Inactive())
                                .detail(new Detail() {
                                    private final String token = tokenValue;
                                }).build(),
                        null
                );
                justFailIfAccessTokenIsRequired(setting);
            }
            //fixed 获取授权中的客户端id对应的client id
            RegisteredClient registeredClient = registeredClientRepository.findById(oAuth2Authorization.getRegisteredClientId());
            String clientId = null;
            Client client = null;
            if (registeredClient != null) {
                AuthorizationGrantType authorizationGrantType = oAuth2Authorization.getAuthorizationGrantType();
                Set<String> scopes = authorizedAccessToken.getToken().getScopes();
                //todo 空逻辑可见要改
                clientId = registeredClient.getClientId();
                client = new Client(clientId, authorizationGrantType.getValue(), scopes);
            } else {
                GenericLoggerStaticAccessor.basicLogger().debug(
                        log,
                        GenericBasicLogContent.builder()
                                .what(RegisteredClient.class)
                                .how(new NotFound())
                                .detail(new Detail() {
                                    private final String id = oAuth2Authorization.getRegisteredClientId();
                                })
                                .build(),
                        null
                );
                throw new UnauthorizedException(oAuth2Authorization.getRegisteredClientId());
            }
            UserAuthenticatedAuthentication authenticatedUser = oAuth2Authorization.getAttribute(Principal.class.getName());
            accessTokenDetails.setClient(client);
            //使用代理类，禁止获取client secret
            accessTokenDetails.setRegisteredClient(RegisteredClientProxy.proxy(registeredClient));
            accessTokenDetails.setUser(authenticatedUser == null ? null : authenticatedUser.getUser());
            GenericLoggerStaticAccessor.operationLogger().debug(
                    log,
                    GenericOperationLogContent.builder()
                            .what(AccessTokenDetails.class)
                            .operation(new Create())
                            .state(new Done())
                            .detail(new Detail() {
                                private final String clientId = registeredClient.getClientId();
                                private final String userId = authenticatedUser == null ? null : authenticatedUser.getUser().getId();
                            }).build(),
                    null
            );
        }
    }

    private void justFailIfAccessTokenIsRequired(AccessTokenProtectedEndpointSetting setting) {
        clearAccessTokenDetails();
        if (!setting.isOptional()) {
            throw new UnauthorizedException();
        }
    }

    private void clearAccessTokenDetails() {
        this.accessTokenDetails.setUser(null);
        this.accessTokenDetails.setClient(null);
    }

    @Nullable
    private AccessTokenProtectedEndpointSetting getSetting(Class<?> endpointClass, MethodSignature methodSignature) {
        //优先服从方法级别安全
        AccessTokenProtectedEndpoint annotation = AnnotationUtils.findAnnotation(methodSignature.getMethod(), AccessTokenProtectedEndpoint.class);
        if (annotation != null) {
            return new AccessTokenProtectedEndpointSetting(endpointClass, annotation.optional());
        }
        //没有服从类级别
        annotation = AnnotationUtils.findAnnotation(endpointClass, AccessTokenProtectedEndpoint.class);
        if (annotation != null) {
            return new AccessTokenProtectedEndpointSetting(endpointClass, annotation.optional());
        }
        return providedAccessTokenProtectedEndpoints.get(endpointClass);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    /**
     * @author ZhangHan
     * @date 2022/5/14 2:21
     */
    @LogTarget("access token")
    public class AccessTokenTarget {
    }
}
