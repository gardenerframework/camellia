package com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * mfa认证过程中发生的挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 21:12
 */
@Getter
public class MfaAuthenticationChallengeRequest extends ChallengeRequest {
    /**
     * 发起挑战时，用户使用的登录名
     */
    private final BasicPrincipal principal;
    /**
     * 认证过程中使用的上下文
     */
    private final Map<String, Object> context;

    public MfaAuthenticationChallengeRequest(MultiValueMap<String, String> headers, String clientGroup, @Nullable Client client, User user, BasicPrincipal principal, Map<String, Object> context) {
        super(headers, clientGroup, client, user);
        this.principal = principal;
        this.context = context;
    }
}
