package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * @author zhanghan30
 * @date 2022/5/12 11:02 下午
 */
public abstract class AuthorizationServerEngineSecurityConfigurer extends AbstractHttpConfigurer<AuthorizationServerEngineSecurityConfigurer, HttpSecurity> {
}
