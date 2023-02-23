package com.jdcloud.gardener.camellia.uac.common.dao.utils;

import com.jdcloud.gardener.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;

import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/11/7 11:15
 */
public abstract class SqlProviderUtils {
    private SqlProviderUtils() {

    }

    /**
     * 基于映射类型获取sql provider
     *
     * @param mapperType mapper 类型
     * @param <P>        需要的provider类型
     * @return provider 对象
     */
    @SuppressWarnings("unchecked")
    public static <P> P getActiveProvider(Class<?> mapperType) {
        try {
            return (P) Objects.requireNonNull(mapperType.getAnnotation(OverrideSqlProviderAnnotation.class))
                    .value()
                    .newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
