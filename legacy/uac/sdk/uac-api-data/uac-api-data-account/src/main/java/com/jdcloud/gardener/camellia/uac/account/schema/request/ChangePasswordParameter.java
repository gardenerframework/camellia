package com.jdcloud.gardener.camellia.uac.account.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.request.constraints.StrongPassword;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * 修改密码参数
 *
 * @author zhanghan30
 * @date 2022/8/12 10:43 上午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChangePasswordParameter implements
        AccountTraits.Credentials,
        SecurityTraits.TuringTraits.CaptchaToken,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response {
    /**
     * 原始密码
     * <p>
     * 如果不为空则触发密码校验
     */
    @Nullable
    private String originalPassword;
    /**
     * 新密码
     */
    @StrongPassword
    @Nullable
    private String password;
    /**
     * 更改密码时使用的挑战token(或者id)
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
     * 人机检测token
     */
    private String captchaToken;
}
