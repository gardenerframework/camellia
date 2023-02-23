package com.jdcloud.gardener.camellia.authorization.authentication.main.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.main.OAuth2StateService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.OAuth2AuthenticationServiceRegistry;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.CreateOAuth2StateRequest;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response.CreateOAuth2StateResponse;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:11
 */
@RestController
@RequestMapping("/authentication/oauth2/state")
@RequiredArgsConstructor
@Slf4j
@AuthorizationServerRestController
public class OAuth2StateEndpoint {
    private final OAuth2AuthenticationServiceRegistry oAuth2AuthenticationServiceRegistry;
    private final OAuth2StateService oAuth2StateService;

    @PostMapping
    public CreateOAuth2StateResponse createState(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody CreateOAuth2StateRequest parameter
    ) {
        String state = oAuth2StateService.createState(
                oAuth2AuthenticationServiceRegistry.getService(parameter.getType()),
                httpServletRequest
        );
        Assert.hasText(state, "empty state created");
        return new CreateOAuth2StateResponse(state);
    }
}
