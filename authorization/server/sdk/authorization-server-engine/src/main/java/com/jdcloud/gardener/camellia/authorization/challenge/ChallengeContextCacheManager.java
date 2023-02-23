package com.jdcloud.gardener.camellia.authorization.challenge;

import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.fragrans.data.cache.client.CacheClient;
import com.jdcloud.gardener.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/6/1 17:13
 */
@Component
public class ChallengeContextCacheManager extends AbstractChallengeCacheManager<ChallengeContext> {

    public ChallengeContextCacheManager(CacheClient cacheClient) {
        super(new BasicCacheManager<ChallengeContext>(cacheClient) {
        });
    }

    @Override
    protected String getSuffix() {
        return "context";
    }
}
