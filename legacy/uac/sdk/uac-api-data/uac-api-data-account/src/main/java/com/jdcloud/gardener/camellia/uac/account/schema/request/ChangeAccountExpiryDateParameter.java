package com.jdcloud.gardener.camellia.uac.account.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Date;

/**
 * 修改账户过期时间
 *
 * @author zhanghan30
 * @date 2022/8/12 9:03 下午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChangeAccountExpiryDateParameter implements
        AccountTraits.AccountExpiryDate,
        SecurityTraits.TuringTraits.CaptchaToken,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response {
    /**
     * 账户过期时间
     */
    @Nullable
    private Date accountExpiryDate;
    /**
     * 挑战token
     */
    private String challengeId;
    /**
     * 挑战的应答，比如发送的验证码
     */
    private String response;
    /**
     * 人机检测
     */
    private String captchaToken;
}
