package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.bean;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/3/29 18:12
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestChallengeRequest implements ChallengeRequest {
    @NonNull
    @NotBlank
    private String any = "";
}
