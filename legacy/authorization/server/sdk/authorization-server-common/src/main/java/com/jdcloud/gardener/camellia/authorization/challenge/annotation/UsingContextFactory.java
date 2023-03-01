package com.jdcloud.gardener.camellia.authorization.challenge.annotation;

import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextFactory;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在sendChallenge方法上，说明上下文的工厂类是什么
 *
 * @author ZhangHan
 * @date 2022/5/31 17:36
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsingContextFactory {
    Class<? extends ChallengeContextFactory<? extends ChallengeRequest, ? extends ChallengeContext>> value();
}
