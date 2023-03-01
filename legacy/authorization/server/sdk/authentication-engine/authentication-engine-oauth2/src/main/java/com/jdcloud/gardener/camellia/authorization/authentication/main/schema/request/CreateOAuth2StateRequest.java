package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import com.jdcloud.gardener.camellia.authorization.authentication.main.OAuth2AuthenticationServiceBase;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.constraits.SupportedOAuth2AuthenticationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOAuth2StateRequest {
    /**
     * 和{@link OAuth2AuthenticationServiceBase}上的{@link AuthenticationType}的类型保持一致
     */
    @NotBlank
    @SupportedOAuth2AuthenticationType
    private String type;
}
