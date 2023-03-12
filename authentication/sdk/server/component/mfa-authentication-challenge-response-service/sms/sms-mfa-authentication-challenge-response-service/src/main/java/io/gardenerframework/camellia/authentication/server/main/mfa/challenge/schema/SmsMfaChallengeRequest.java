package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.MobilePhoneNumberPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collection;


@SuperBuilder
public class SmsMfaChallengeRequest extends MfaAuthenticationChallengeRequest implements SmsVerificationCodeChallengeRequest {
    @Override
    public String getMobilePhoneNumber() {
        User user = this.getUser();
        Collection<@NonNull Principal> principals = user.getPrincipals();
        if (!CollectionUtils.isEmpty(principals)) {
            for (Principal principal : principals) {
                if (principal instanceof MobilePhoneNumberPrincipal) {
                    return principal.getName();
                }
            }
        }
        throw new UnsupportedOperationException(user.getId() + " does not have a mobile phone.");
    }

    @Override
    public void setMobilePhoneNumber(String mobilePhoneNumber) {

    }
}
