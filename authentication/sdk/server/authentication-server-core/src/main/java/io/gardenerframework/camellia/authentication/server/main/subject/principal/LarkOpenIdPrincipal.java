package io.gardenerframework.camellia.authentication.server.main.subject.principal;

import io.gardenerframework.camellia.authentication.server.common.Version;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/1/2 23:23
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LarkOpenIdPrincipal extends Principal {
    private static final long serialVersionUID = Version.current;
}
