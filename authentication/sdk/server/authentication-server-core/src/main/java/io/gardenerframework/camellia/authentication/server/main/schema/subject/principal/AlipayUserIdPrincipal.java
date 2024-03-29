package io.gardenerframework.camellia.authentication.server.main.schema.subject.principal;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/1/2 23:23
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlipayUserIdPrincipal extends Principal {
    private static final long serialVersionUID = SerializationVersionNumber.version;

}
