package com.jdcloud.gardener.camellia.authorization.challenge;

import com.jdcloud.gardener.fragrans.data.cache.client.CacheClient;
import com.jdcloud.gardener.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/6/1 17:17
 */
@Component
public class ChallengeCooldownCacheManager extends AbstractChallengeCacheManager<Long> {

    public ChallengeCooldownCacheManager(CacheClient cacheClient) {
        super(new BasicCacheManager<Long>(cacheClient) {
        });
    }

    @Override
    protected String getSuffix() {
        return "cooldown";
    }
}
