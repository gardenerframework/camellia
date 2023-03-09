package com.jdcloud.gardener.camellia.authorization.authentication.main.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/**
 * @author ZhangHan
 * @date 2022/5/16 7:14
 */
@Getter
public class SmsAuthenticationCodeChallengeRequest extends ChallengeRequest {
    /**
     * 用户的凭据很多，不如直接记录发给哪个手机号
     */
    private final String mobilePhoneNumber;

    public SmsAuthenticationCodeChallengeRequest(MultiValueMap<String, String> headers, String clientGroup, @Nullable Client client, User user, String mobilePhoneNumber) {
        super(headers, clientGroup, client, user);
        this.mobilePhoneNumber = mobilePhoneNumber;
    }
}
