package io.gardenerframework.camellia.authentication.server.main.subject.credentials;

import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 密码形式的登录凭据
 *
 * @author zhanghan30
 * @date 2022/5/12 4:23 下午
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class PasswordCredentials extends Credentials implements SecurityTraits.SecretTraits.Password {
    /**
     * 密码
     */
    @NonNull
    private String password;
}
