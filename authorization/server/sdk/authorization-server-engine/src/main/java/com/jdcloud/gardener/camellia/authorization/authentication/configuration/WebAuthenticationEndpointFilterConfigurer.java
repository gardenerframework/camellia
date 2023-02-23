package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.AuthenticationEndpointAuthenticationFailureHandler;
import com.jdcloud.gardener.camellia.authorization.authentication.main.LoginAuthenticationRequestConverter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.WebAuthenticationEntryProcessingFilter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.WebAuthenticationSuccessHandler;
import com.jdcloud.gardener.camellia.authorization.common.configuration.AuthorizationServerPathOption;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/4/26 21:51
 */
@AllArgsConstructor
@Component
public class WebAuthenticationEndpointFilterConfigurer extends AuthorizationServerEngineSecurityConfigurer {
    private final AuthorizationServerPathOption authorizationServerPathOption;
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
        return new AntPathRequestMatcher(authorizationServerPathOption.getWebAuthenticationEndpoint(), HttpMethod.POST.name());
    }
}
