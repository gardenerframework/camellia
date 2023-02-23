package io.gardenerframework.camellia.authentication.server.main.schema.principal;

import io.gardenerframework.camellia.authentication.server.common.Version;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthorizationEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import lombok.experimental.SuperBuilder;

/**
 * 这是一种特殊的登录名，实际上提交的是mfa认证发送的挑战id并将挑战应答作为登录凭据使用
 * <p>
 * mfa的登录名是不会调用{@link UserService}去加载用户的
 *
 * @author ZhangHan
 * @date 2022/5/11 10:15
 */
@SuperBuilder
@AuthorizationEnginePreserved
public class MfaChallengeIdPrincipal extends Principal {
    private static final long serialVersionUID = Version.current;
}
