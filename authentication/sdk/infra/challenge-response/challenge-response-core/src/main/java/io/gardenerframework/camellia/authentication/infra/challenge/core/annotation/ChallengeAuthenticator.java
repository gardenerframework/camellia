package io.gardenerframework.camellia.authentication.infra.challenge.core.annotation;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于{@link ChallengeResponseService}，表达服务对应的一个名称
 *
 * @author zhanghan30
 * @date 2023/2/28 11:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChallengeAuthenticator {
    String value();
}
