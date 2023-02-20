package io.gardenerframework.camellia.authentication.infra.challenge.core.schema;

import io.gardenerframework.camellia.authentication.infra.common.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 挑战的缓存上下文
 *
 * @author ZhangHan
 * @date 2022/5/15 22:52
 */
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ChallengeContext implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * 是否通过了验证
     */
    @Builder.Default
    private boolean verified = false;
}
