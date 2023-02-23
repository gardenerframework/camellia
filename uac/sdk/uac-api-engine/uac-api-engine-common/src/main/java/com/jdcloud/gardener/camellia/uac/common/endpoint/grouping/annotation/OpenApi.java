package com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明接口是一个openApi(给应用对接0的接口
 *
 * @author zhanghan30
 * @date 2022/8/13 11:00 上午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenApi {
}
