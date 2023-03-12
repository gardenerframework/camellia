package io.gardenerframework.camellia.authentication.server.main.schema.credentials;

import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.Credentials;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/5/15 14:01
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class SmsVerificationCodeCredentials extends Credentials implements
        GenericTraits.IdentifierTraits.Code<String> {
    @NonNull
    private String code;
}
