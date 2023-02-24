package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.camellia.authentication.server.main.spring.AuthenticationEndpointAuthenticationFailureHandler;
import io.gardenerframework.camellia.authentication.server.main.spring.LoginAuthenticationRequestConverter;
import io.gardenerframework.camellia.authentication.server.main.spring.WebAuthenticationEntryProcessingFilter;
import io.gardenerframework.camellia.authentication.server.main.spring.WebAuthenticationSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author ZhangHan
 * @date 2022/4/26 21:51
 */
@AllArgsConstructor
public class WebAuthenticationEndpointFilterConfigurer extends AuthenticationServerEngineSecurityConfigurer {
    private final AuthenticationServerPathOption authenticationServerPathOption;
    private final LoginAuthenticationRequestConverter loginAuthenticationRequestConverter;
    private final AuthenticationEndpointAuthenticationFailureHandler authenticationEndpointAuthenticationFailureHandler;
    private final WebAuthenticationSuccessHandler webAuthenticationSuccessHandler;
    private final ObjectPostProcessor<Object> postProcessor;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        //登录接口不需要csrf
        builder.csrf().ignoringRequestMatchers(getEndpointMatcher());

        builder.addFilterBefore(this.postProcessor.postProcess(new WebAuthenticationEntryProcessingFilter(
                getEndpointMatcher(),
                loginAuthenticationRequestConverter,
                authenticationEndpointAuthenticationFailureHandler,
                webAuthenticationSuccessHandler,
                builder.getSharedObject(AuthenticationManager.class)
        )), UsernamePasswordAuthenticationFilter.class);
    }

    public RequestMatcher getEndpointMatcher() {
        return new AntPathRequestMatcher(authenticationServerPathOption.getWebAuthenticationEndpoint(), HttpMethod.POST.name());
    }
}
