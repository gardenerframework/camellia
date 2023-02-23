package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal;

import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
import com.jdcloud.gardener.fragrans.log.annotation.ReferLogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 这是一种特殊的登录名，实际上提交的是mfa认证发送的挑战id并将挑战应答作为登录凭据使用
 * <p>
 * mfa的登录名是不会调用{@link UserService}去加载用户的
 *
 * @author ZhangHan
 * @date 2022/5/11 10:15
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ReferLogTarget(value = BasicPrincipal.class, prefix = "mfa挑战id类型的")
@AuthorizationEnginePreserved
public class MfaChallengeIdPrincipal extends BasicPrincipal {
    private static final long serialVersionUID = Version.current;

    public MfaChallengeIdPrincipal(String name) {
        super(name);
    }
}
