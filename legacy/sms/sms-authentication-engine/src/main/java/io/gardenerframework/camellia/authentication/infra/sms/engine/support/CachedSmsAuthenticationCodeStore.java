package io.gardenerframework.camellia.authentication.infra.sms.engine.support;

import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationCodeStore;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.time.Duration;

/**
 * @author ZhangHan
 * @date 2022/4/25 22:39
 */
@Slf4j
@ConditionalOnMissingBean(value = SmsAuthenticationCodeStore.class, ignored = CachedSmsAuthenticationCodeStore.class)
public class CachedSmsAuthenticationCodeStore implements SmsAuthenticationCodeStore {

    private final BasicCacheManager<String> cacheManager;

    public CachedSmsAuthenticationCodeStore(CacheClient cacheClient) {
        cacheManager = new BasicCacheManager<String>(cacheClient) {
        };
    }

    /**
     * 给出缓存的命名空间
     *
     * @param applicationId 应用id
     * @return 命名空间
     */
    private String[] buildNamespaces(String applicationId) {
        return new String[]{
                "camellia",
                "authentication",
                "infra",
                "sms",
                applicationId
        };
    }

    @Override
    public boolean saveCodeIfAbsent(String applicationId, String mobilePhoneNumber, Class<? extends Scenario> scenario, String code, Duration ttl) throws Exception {
        return cacheManager.setIfNotPresents(buildNamespaces(applicationId), mobilePhoneNumber, scenario.getCanonicalName(), code, ttl);
    }

    @Override
    public Duration getTimeRemaining(String applicationId, String mobilePhoneNumber, Class<? extends Scenario> scenario) throws Exception {
        return cacheManager.ttl(buildNamespaces(applicationId), mobilePhoneNumber, scenario.getCanonicalName());
    }

    @Override
    public String getCode(String applicationId, String mobilePhoneNumber, Class<? extends Scenario> scenario) throws Exception {
        return cacheManager.get(buildNamespaces(applicationId), mobilePhoneNumber, scenario.getCanonicalName());
    }

    @Override
    public void removeCode(String applicationId, String mobilePhoneNumber, Class<? extends Scenario> scenario) throws Exception {
        cacheManager.delete(buildNamespaces(applicationId), mobilePhoneNumber, scenario.getCanonicalName());
    }
}