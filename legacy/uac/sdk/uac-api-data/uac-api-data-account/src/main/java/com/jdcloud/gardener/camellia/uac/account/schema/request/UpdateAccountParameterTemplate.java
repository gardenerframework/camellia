package com.jdcloud.gardener.camellia.uac.account.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.trait.BasicPersonalInformation;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 用户数据覆盖参数
 *
 * @author zhanghan30
 * @date 2022/8/12 9:29 上午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateAccountParameterTemplate implements
        AccountTraits.Avatar,
        AccountTraits.Nickname,
        BasicPersonalInformation,
        ContactTraits.OfficeTelephoneNumber,
        ContactTraits.Fax,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response,
        SecurityTraits.TuringTraits.CaptchaToken {
    /**
     * 个人头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 姓
     */
    @NotBlank
    private String surname;
    /**
     * 名
     */
    @NotBlank
    private String givenName;
    /**
     * 出生日期
     */
    private Date dateOfBirth;
    /**
     * 性别
     * <p>
     * 0 = 不知道(无法辨别)
     * <p>
     * 1 = 男
     * <p>
     * 2 = 女
     * <p>
     * 9 = 未说明
     */
    private Integer gender;
    /**
     * 民族二字码
     */
    private String ethnicGroup;
    /**
     * 座机号
     */
    private String officeTelephoneNumber;
    /**
     * 传真
     */
    private String fax;
    /**
     * 挑战token
     */
    private String challengeId;
    /**
     * 应答
     */
    private String response;
    /**
     * 人机测试验证码
     */
    private String captchaToken;
}
