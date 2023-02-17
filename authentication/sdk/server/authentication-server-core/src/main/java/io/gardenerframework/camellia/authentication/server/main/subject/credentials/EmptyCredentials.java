package io.gardenerframework.camellia.authentication.server.main.subject.credentials;

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
public class EmptyCredentials extends Credentials {
}
