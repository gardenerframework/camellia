package com.jdcloud.gardener.camellia.authorization.challenge.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明参数中哪个是挑战id
 *
 * @author zhanghan30
 * @date 2022/5/23 8:12 下午
 * @see ValidateChallengeEnvironment
 * @see ValidateChallenge
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChallengeId {
}
