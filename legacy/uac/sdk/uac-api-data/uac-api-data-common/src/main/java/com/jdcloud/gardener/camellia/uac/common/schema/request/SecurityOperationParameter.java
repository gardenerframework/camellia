package com.jdcloud.gardener.camellia.uac.common.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/11/16 16:57
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SecurityOperationParameter implements
        SecurityTraits.TuringTraits.CaptchaToken,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response {
    /**
     * 挑战
     */
    private String challengeId;
    /**
     * 挑战的应答，比如发送的验证码
     */
    private String response;
    /**
     * 人机验证码
     */
    private String captchaToken;
}
