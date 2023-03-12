package io.gardenerframework.camellia.authentication.infra.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;

public interface SmsVerificationCodeChallengeContext extends ChallengeContext,
        GenericTraits.IdentifierTraits.Code<String> {
}
