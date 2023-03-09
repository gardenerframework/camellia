package io.gardenerframework.camellia.authentication.server.test.cases;


import io.gardenerframework.camellia.authentication.server.main.spring.oauth2.CachedOAuth2AuthorizationConsentService;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationServerEngineTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/5/20 2:21 下午
 */
@SpringBootTest(classes = AuthenticationServerEngineTestApplication.class)
@DisplayName("CachedOAuth2AuthorizationConsentService测试")
public class CachedOAuth2AuthorizationConsentServiceTest {
    @Autowired
    private CachedOAuth2AuthorizationConsentService oAuth2AuthorizationConsentService;

    @Test
    @DisplayName("冒烟测试")
    public void smokeTest() {
        OAuth2AuthorizationConsent consent = OAuth2AuthorizationConsent.withId(UUID.randomUUID().toString(),
                UUID.randomUUID().toString()).authority(new SimpleGrantedAuthority(UUID.randomUUID().toString())).scope(UUID.randomUUID().toString()).build();
        oAuth2AuthorizationConsentService.save(consent);
        OAuth2AuthorizationConsent consentLoaded = oAuth2AuthorizationConsentService.findById(consent.getRegisteredClientId(), consent.getPrincipalName());
        Assertions.assertEquals(consent.getRegisteredClientId(), consentLoaded.getRegisteredClientId());
        Assertions.assertEquals(consent.getPrincipalName(), consentLoaded.getPrincipalName());
        Assertions.assertTrue(consent.getScopes().containsAll(consentLoaded.getScopes()));
        Set<String> original = consent.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        Set<String> loaded = consentLoaded.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        original.removeAll(loaded);
        Assertions.assertTrue(original.isEmpty());
    }
}
