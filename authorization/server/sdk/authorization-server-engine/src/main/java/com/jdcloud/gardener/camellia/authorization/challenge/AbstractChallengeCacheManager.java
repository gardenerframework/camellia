package com.jdcloud.gardener.camellia.authorization.challenge;

import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import com.jdcloud.gardener.fragrans.data.cache.manager.BasicCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.time.Duration;

/**
 * @author ZhangHan
 * @date 2022/6/1 16:38
 */
@RequiredArgsConstructor
public abstract class AbstractChallengeCacheManager<T> {
    private final BasicCacheManager<T> cacheManager;

    /**
     * 缓存对应的后缀
     *
     * @return 后缀
     */
    protected abstract String getSuffix();

    /**
     * 构建缓存名称空间
     *
     * @param serviceClass 服务类
     * @return 名称空间
     */
    private String[] buildNamespace(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> serviceClass) {
        return new String[]{
                "camellia",
                "authorization",
                "engine",
                "challenge",
                ClassUtils.getUserClass(serviceClass).getCanonicalName()
        };
    }


    /**
     * 当不存在时设置缓存
     *
     * @param serviceClass 服务类
     * @param key          缓存key
     * @param object       要缓存的数据
     * @param ttl          有效期
     * @return 是否缓存成功
     */
    public boolean setCacheIfNotPresents(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> serviceClass, String key, T object, @Nullable Duration ttl) {
        return cacheManager.setIfNotPresents(buildNamespace(serviceClass), key, getSuffix(), object, ttl);
    }

    /**
     * 使用服务对象来设置缓存
     *
     * @param service 服务对象
     * @param key     缓存key
     * @param object  要缓存的数据
     * @param ttl     有效期
     * @return 是否缓存成功
     */
    @SuppressWarnings("unchecked")
    public boolean setCacheIfNotPresents(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> service, String key, T object, @Nullable Duration ttl) {
        return setCacheIfNotPresents((Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>>) service.getClass(), key, object, ttl);
    }

    /**
     * 当存在时覆盖
     *
     * @param serviceClass 服务类
     * @param key          缓存key
     * @param object       要缓存的数据
     * @param ttl          有效期
     * @return 是否换粗成功
     */
    public boolean setCacheIfPresents(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> serviceClass, String key, T object, @Nullable Duration ttl) {
        return cacheManager.setIfPresents(buildNamespace(serviceClass), key, getSuffix(), object, ttl);
    }

    /**
     * 使用服务对象来设置缓存
     *
     * @param service 服务对象
     * @param key     缓存key
     * @param object  要缓存的数据
     * @param ttl     有效期
     * @return 是否缓存成功
     */
    @SuppressWarnings("unchecked")
    public boolean setCacheIfPresents(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> service, String key, T object, @Nullable Duration ttl) {
        return setCacheIfPresents((Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>>) service.getClass(), key, object, ttl);
    }

    /**
     * 删除缓存key
     *
     * @param serviceClass 服务类
     * @param key          缓存key
     */
    public void delete(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> serviceClass, String key) {
        cacheManager.delete(buildNamespace(serviceClass), key, getSuffix());
    }

    /**
     * 删除缓存
     *
     * @param service 服务对象
     * @param key     缓存key
     */
    @SuppressWarnings("unchecked")
    public void delete(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> service, String key) {
        delete((Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>>) service.getClass(), key);
    }

    /**
     * 获取缓存
     *
     * @param serviceClass 服务类
     * @param key          缓存key
     * @return 缓存值
     */
    @Nullable
    public T get(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> serviceClass, String key) {
        return cacheManager.get(buildNamespace(serviceClass), key, getSuffix());
    }

    /**
     * 返回ttl
     *
     * @param serviceClass 服务类
     * @param key          缓存key
     * @return ttl
     */
    @Nullable
    public Duration ttl(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> serviceClass, String key) {
        return cacheManager.ttl(buildNamespace(serviceClass), key, getSuffix());
    }

    /**
     * 返回ttl
     *
     * @param service 服务对象
     * @param key     缓存key
     * @return ttl
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public Duration ttl(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> service, String key) {
        return ttl((Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>>) service.getClass(), key);
    }
}
