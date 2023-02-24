package io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 空凭据，用来表达根本不需要什么登录凭据的认证模式，比如脸，比如二维码
 *
 * @author zhanghan30
 * @date 2022/9/5 5:18 下午
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmptyCredentials extends Credentials {
}
