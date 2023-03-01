package com.jdcloud.gardener.camellia.uac.account.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.request.constraints.StrongPassword;
import com.jdcloud.gardener.camellia.uac.account.schema.trait.BasicPersonalInformation;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 创建用户参数
 * <p>
 * 创建参数仅适用于管理的api，也就是后台操作人员
 *
 * @author zhanghan30
 * @date 2022/8/12 9:29 上午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateAccountParameterTemplate implements
        AccountTraits.Username,
        AccountTraits.Avatar,
        AccountTraits.Nickname,
        AccountTraits.Credentials,
        BasicPersonalInformation,
        ContactTraits.OfficeTelephoneNumber,
        ContactTraits.Fax,
        AccountTraits.AccountExpiryDate,
        SecurityTraits.TuringTraits.CaptchaToken,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response {

    /**
     * 用户名
     * <p>
     * 要求不能为空 - 常规来说用户名都没有为空的
     */
    @NotBlank
    private String username;
    /**
     * 个人头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 密码
     */
    @StrongPassword
    @Nullable
    private String password;
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
     * 联系用的电子邮箱
     */
    @Valid
    private EmailProperty emailProperty;
    /**
     * 联系用的手机号
     */
    @Valid
    private MobilePhoneNumberProperty mobilePhoneNumberProperty;
    /**
     * 座机号
     */
    private String officeTelephoneNumber;
    /**
     * 传真
     */
    private String fax;
    /**
     * 账户过期时间
     * <p>
     * 过期账户无法登录
     */
    private Date accountExpiryDate;
    /**
     * 微信id
     */
    private String weChatOpenIdToken;
    /**
     * 企业微信id
     */
    private String enterpriseWeChatOpenIdToken;
    /**
     * 支付宝id
     */
    private String alipayOpenIdToken;
    /**
     * 钉钉id
     */
    private String dingTalkOpenIdToken;
    /**
     * 飞书id
     */
    private String larkOpenIdToken;
    /**
     * 图灵测试token
     */
    private String captchaToken;
    /**
     *
     */
    private String challengeId;
    /**
     * 应答
     */
    private String response;
}
