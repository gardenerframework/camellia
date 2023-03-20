package io.gardenerframework.camellia.authentication.server.main.schema.subject.principal;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JdXIdPrincipal extends Principal {
    private static final long serialVersionUID = SerializationVersionNumber.version;
}
