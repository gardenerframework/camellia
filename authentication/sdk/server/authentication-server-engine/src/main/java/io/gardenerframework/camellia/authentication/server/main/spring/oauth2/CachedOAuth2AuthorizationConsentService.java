package io.gardenerframework.camellia.authentication.server.main.spring.oauth2;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.configuration.OAuth2AuthorizationConsentOption;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * @author ZhangHan
 * @date 2022/1/8 3:31
 */
@AuthenticationServerEngineComponent
public class CachedOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {
    /**
     * 缓存名称空间
     */
    private static final String[] CONSENT_CACHE_NAMESPACES = new String[]{
            "camellia",
            "authentication:",
            "server",
            "engine",
            "consent"
    };
    private final BasicCacheManager<OAuth2AuthorizationConsent> cacheManager;
    private final OAuth2AuthorizationConsentOption options;

    public CachedOAuth2AuthorizationConsentService(CacheClient client, OAuth2AuthorizationConsentOption options) {
        this.cacheManager = new BasicCacheManager<OAuth2AuthorizationConsent>(client) {

        };
        this.options = options;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        cacheManager.set(CONSENT_CACHE_NAMESPACES, authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName(), authorizationConsent, Duration.ofSeconds(options.getConsentTll()));
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        cacheManager.delete(CONSENT_CACHE_NAMESPACES, authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        return cacheManager.get(CONSENT_CACHE_NAMESPACES, registeredClientId, principalName);
    }
}
