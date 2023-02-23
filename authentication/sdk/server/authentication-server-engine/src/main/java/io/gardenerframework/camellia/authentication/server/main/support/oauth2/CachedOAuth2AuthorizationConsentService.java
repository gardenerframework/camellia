package io.gardenerframework.camellia.authentication.server.main.support.oauth2;

import com.jdcloud.gardener.camellia.authorization.authentication.configuration.OAuth2AuthorizationConsentOption;
import com.jdcloud.gardener.fragrans.data.cache.client.CacheClient;
import com.jdcloud.gardener.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * @author ZhangHan
 * @date 2022/1/8 3:31
 */
@Component
public class CachedOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {
    /**
     * 缓存名称空间
     */
    private static final String[] CONSENT_CACHE_NAMESPACES = new String[]{
            "camellia",
            "authorization:",
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
