package com.jdcloud.gardener.camellia.authorization.demo.authenticattion.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.SmsAuthenticationCodeChallengeResponseServiceBase;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.SmsAuthenticationCodeChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeClient;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeEncoder;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/5/16 9:14
 */
@Component
@ConditionalOnClass(SmsAuthenticationCodeChallengeResponseServiceBase.class)
public class DemoSmsAuthenticationCodeService extends SmsAuthenticationCodeChallengeResponseServiceBase {
    public DemoSmsAuthenticationCodeService(SmsAuthenticationCodeEncoder smsAuthenticationCodeEncoder, SmsAuthenticationCodeStore smsAuthenticationCodeStore, SmsAuthenticationCodeClient smsAuthenticationCodeClient, ClientGroupProvider clientGroupProvider, AccessTokenDetails accessTokenDetails) {
        super(smsAuthenticationCodeEncoder, smsAuthenticationCodeStore, smsAuthenticationCodeClient, clientGroupProvider, accessTokenDetails);
    }

    @Override
    protected long getTtl() {
        return 300;
    }


    @Override
    public String getCooldownKey(SmsAuthenticationCodeChallengeRequest request) {
        return String.format("%s.%s", request.getUser().getId(), request.getClientGroup());
    }

    @Override
    public long getCooldown() {
        return 60;
    }
}
