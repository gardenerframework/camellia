package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.bean;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2023/3/29 18:12
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TestChallenge extends Challenge {
    private String field;
}
