package com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记接口，说明这是一个管理接口
 *
 * @author zhanghan30
 * @date 2022/8/13 10:58 上午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagementApi {
}
