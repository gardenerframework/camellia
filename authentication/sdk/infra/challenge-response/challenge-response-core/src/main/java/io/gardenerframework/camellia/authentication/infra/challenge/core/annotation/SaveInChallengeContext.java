package io.gardenerframework.camellia.authentication.infra.challenge.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表当前属性要在context中存储
 *
 * @author zhanghan30
 * @date 2023/2/27 14:53
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SaveInChallengeContext {
}
