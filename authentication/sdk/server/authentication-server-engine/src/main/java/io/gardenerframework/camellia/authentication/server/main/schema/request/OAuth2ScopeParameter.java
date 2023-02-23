package io.gardenerframework.camellia.authentication.server.main.schema.request;

import lombok.Getter;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/5/31 16:00
 */
@Getter
public class OAuth2ScopeParameter extends AbstractAuthenticationRequestParameter {
    @Valid
    private final Collection<@NotBlank String> scopes;

    public OAuth2ScopeParameter(HttpServletRequest request) {
        super(request);
        scopes = parseClientRequestedScope(request);
    }

    /**
     * 获取令牌接口的所有参数
     *
     * @param request 请求
     * @return 参数
     */
    private MultiValueMap<String, String> getTokenEndpointParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            if (values.length > 0) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });
        return parameters;
    }

    /**
     * 解析oauth2请求的scope
     *
     * @param request 请求
     * @return scope
     */
    private Set<String> parseClientRequestedScope(HttpServletRequest request) {
        String scope = getTokenEndpointParameters(request).getFirst(OAuth2ParameterNames.SCOPE);
        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(
                    Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }
        return requestedScopes;
    }
}
