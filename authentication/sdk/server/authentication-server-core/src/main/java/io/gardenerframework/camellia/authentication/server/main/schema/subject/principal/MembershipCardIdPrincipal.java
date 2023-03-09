package io.gardenerframework.camellia.authentication.server.main.schema.subject.principal;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/4/25 5:04 下午
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MembershipCardIdPrincipal extends Principal {
    private static final long serialVersionUID = SerializationVersionNumber.version;
}
