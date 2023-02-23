package com.jdcloud.gardener.camellia.authorization.username.recovery.schema.challenge;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/**
 * @author zhanghan30
 * @date 2022/12/26 20:45
 */
@Getter
public class PasswordRecoveryChallengeRequest extends ChallengeRequest {
    @Nullable
    private final String authenticator;

    public PasswordRecoveryChallengeRequest(MultiValueMap<String, String> headers, String clientGroup, @Nullable Client client, @Nullable User user, @Nullable String authenticator) {
        super(headers, clientGroup, client, user);
        this.authenticator = authenticator;
    }
}
