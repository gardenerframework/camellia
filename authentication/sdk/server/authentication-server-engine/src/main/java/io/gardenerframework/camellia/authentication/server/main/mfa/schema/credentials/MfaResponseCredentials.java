package io.gardenerframework.camellia.authentication.server.main.mfa.schema.credentials;

import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.Credentials;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/5/12 9:26 下午
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Getter
public class MfaResponseCredentials extends Credentials {
    @NonNull
    private final String response;
}
