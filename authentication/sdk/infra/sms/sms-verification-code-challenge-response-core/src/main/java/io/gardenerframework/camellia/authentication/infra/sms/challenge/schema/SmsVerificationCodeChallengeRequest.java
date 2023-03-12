package io.gardenerframework.camellia.authentication.infra.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.fragrans.data.trait.mankind.MankindTraits;

public interface SmsVerificationCodeChallengeRequest extends ChallengeRequest,
        MankindTraits.ContactTraits.MobilePhoneNumber {
}
