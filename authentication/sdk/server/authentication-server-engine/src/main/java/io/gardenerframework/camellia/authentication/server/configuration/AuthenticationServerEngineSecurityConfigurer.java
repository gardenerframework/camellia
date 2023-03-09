package io.gardenerframework.camellia.authentication.server.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * @author zhanghan30
 * @date 2022/5/12 11:02 下午
 */
public abstract class AuthenticationServerEngineSecurityConfigurer extends AbstractHttpConfigurer<AuthenticationServerEngineSecurityConfigurer, HttpSecurity> {
}
