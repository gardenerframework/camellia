package io.gardenerframework.camellia.authentication.server.main.schema.subject.principal;

import io.gardenerframework.camellia.authentication.server.common.Version;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/4/25 5:04 下午
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MembershipCardIdPrincipal extends Principal {
    private static final long serialVersionUID = Version.current;
}
