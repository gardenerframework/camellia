package io.gardenerframework.camellia.authentication.server.main.endpoint;

import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerRestController;
import io.gardenerframework.camellia.authentication.server.main.OAuth2BaseUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.schema.reponse.CreateOAuth2StateResponse;
import io.gardenerframework.camellia.authentication.server.main.schema.request.constraints.AuthenticationTypeSupported;
import io.gardenerframework.camellia.authentication.server.main.utils.UserAuthenticationServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:11
 */
@AuthenticationServerRestController
@RequestMapping("/authentication/state/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2StateEndpoint {
    private final UserAuthenticationServiceRegistry registry;

    @PostMapping("/{type}")
    public CreateOAuth2StateResponse createState(
            @AuthenticationTypeSupported(
                    //要求是oauth2 iam的认证服务
                    type = OAuth2BaseUserAuthenticationService.class,
                    //这种认证服务当然不是保留的
                    ignorePreserved = true,
                    //rest 接口用的
                    endpointType = AuthenticationTypeSupported.EndpointType.RestApi
            )
            @Valid @PathVariable("type") String type
    ) throws Exception {
        OAuth2BaseUserAuthenticationService service = (OAuth2BaseUserAuthenticationService) registry.getUserAuthenticationService(type);
        //这个取出来不可能是空的，因为注解已经验证过了
        return new CreateOAuth2StateResponse(Objects.requireNonNull(service).createState());
    }
}
