package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:41
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseVerificationResponse {
    private boolean verified = false;
}
