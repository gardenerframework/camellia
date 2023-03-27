package io.gardenerframework.camellia.authentication.server.main.mfa.schema.principal;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@AuthenticationServerEnginePreserved
@EqualsAndHashCode(callSuper = true)
@Getter
public class MfaAuthenticationPrincipal extends Principal {
    private static final long serialVersionUID = SerializationVersionNumber.version;
}
