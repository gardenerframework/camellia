package com.jdcloud.gardener.camellia.authorization.challenge.configuration;

import com.jdcloud.gardener.camellia.authorization.challenge.ChallengePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/5/16 6:30
 */
@Configuration
@ComponentScan(basePackageClasses = ChallengePackage.class)
public class AuthorizationEngineChallengeConfiguration {
}
