package io.gardenerframework.camellia.authentication.server.main.subject.principal;

import io.gardenerframework.camellia.authentication.server.common.Version;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/1/1 0:39
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UsernamePrincipal extends Principal {
    private static final long serialVersionUID = Version.current;
}
