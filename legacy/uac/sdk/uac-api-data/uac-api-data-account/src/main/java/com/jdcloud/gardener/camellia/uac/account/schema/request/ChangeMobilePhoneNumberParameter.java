package com.jdcloud.gardener.camellia.uac.account.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/11/15 19:59
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChangeMobilePhoneNumberParameter implements
        ContactTraits.MobilePhoneNumber,
        SecurityTraits.TuringTraits.CaptchaToken,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response {
    /**
     * 新的手机号码
     */
    private String mobilePhoneNumber;
    /**
     * 更改时使用的挑战token(或者id)
     * <p>
     * 当更新时，无论是后台管理人员更新，还是用户自己更新，都应当见检查更新操作是否符合预期
     * <p>
     * 比如验证邮箱是否归当前用户所有
     * <p>
     * 比如验证后台人员是否获得了某个审批流的结果
     * <p>
     * 这个参数就泛指执行操作前应当检查的某个挑战
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
