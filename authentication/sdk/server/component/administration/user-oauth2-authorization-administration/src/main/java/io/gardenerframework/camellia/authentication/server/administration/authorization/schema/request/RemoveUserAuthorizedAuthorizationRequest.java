package io.gardenerframework.camellia.authentication.server.administration.authorization.schema.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/3/23 10:40
 */
@NoArgsConstructor
@Getter
@Setter
public class RemoveUserAuthorizedAuthorizationRequest {
    @NotBlank
    private String userId;
    @Nullable
    private String clientId;
    @Nullable
    private String deviceId;
}
