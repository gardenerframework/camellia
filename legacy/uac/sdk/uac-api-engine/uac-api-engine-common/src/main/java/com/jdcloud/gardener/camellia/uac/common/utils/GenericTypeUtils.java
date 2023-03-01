package com.jdcloud.gardener.camellia.uac.common.utils;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;

/**
 * @author zhanghan30
 * @date 2022/10/10 19:52
 */
public abstract class GenericTypeUtils {
    private GenericTypeUtils() {
    }

    /**
     * 将一个约定了范型上限的转换器转换为支持父类型的转换器
     *
     * @param converter 转换器
     * @param source    目标
     * @param <S>       原类型的父类型
     * @param <T>       目标转换器
     * @return 目标转换器
     */
    @SuppressWarnings("unchecked")
    public static <S, T> T convert(@NonNull Converter<? extends S, T> converter, @NonNull S source) {
        return ((Converter<S, T>) converter).convert(source);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object target) {
        return (T) target;
    }
}
