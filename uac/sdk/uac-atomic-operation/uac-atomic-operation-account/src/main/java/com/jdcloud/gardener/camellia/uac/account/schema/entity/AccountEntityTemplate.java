package com.jdcloud.gardener.camellia.uac.account.schema.entity;

import com.jdcloud.gardener.camellia.uac.account.schema.trait.BasicPersonalInformation;
import com.jdcloud.gardener.camellia.uac.account.schema.trait.Contact;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.persistence.template.annotation.DomainObjectTemplate;
import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.annotation.ReadBySpecificOperation;
import com.jdcloud.gardener.fragrans.data.schema.annotation.UpdateBySpecificOperation;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicOperationTraceableEntity;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.BioTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.SnsTraits;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Date;

/**
 * 账户的实体记录定义
 *
 * @author zhanghan30
 * @date 2022/9/9 5:46 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("account")
@LogTarget("账户")
@SuperBuilder
@DomainObjectTemplate
@ToString(callSuper = true, exclude = {"password", "passwordExpiryDate"})
public class AccountEntityTemplate extends BasicOperationTraceableEntity<String> implements
        AccountTraits.Username,
        SnsTraits.WeChatOpenId,
        SnsTraits.AlipayOpenId,
        SnsTraits.DingTalkOpenId,
        SnsTraits.EnterpriseWeChatOpenId,
        SnsTraits.LarkOpenId,
        AccountTraits.Avatar,
        AccountTraits.Nickname,
        AccountTraits.Credentials,
        AccountTraits.CredentialsExpiryDate,
        GenericTraits.StatusTraits.EnableFlag,
        GenericTraits.StatusTraits.LockFlag,
        AccountTraits.AccountExpiryDate,
        BasicPersonalInformation,
        GenericTraits.Name,
        BioTraits.FaceId,
        Contact {
    /**
     * 用户名
     */
    @ImmutableField
    private String username;
    /**
     * 微信号
     * <p>
     * 允许变但是要专门的操作
     */
    @UpdateBySpecificOperation
    @ReadBySpecificOperation
    private String weChatOpenId;
    /**
     * 支付宝账号
     * <p>
     * 可以变但是要专门的操作
     */
    @UpdateBySpecificOperation
    @ReadBySpecificOperation
    private String alipayOpenId;
    /**
     * 企业微信id
     */
    @UpdateBySpecificOperation
    @ReadBySpecificOperation
    private String enterpriseWeChatOpenId;
    /**
     * 钉钉id
     */
    @UpdateBySpecificOperation
    @ReadBySpecificOperation
    private String dingTalkOpenId;
    /**
     * 飞书id
     */
    @UpdateBySpecificOperation
    @ReadBySpecificOperation
    private String larkOpenId;
    /**
     * 人脸id
     */
    @UpdateBySpecificOperation
    @ReadBySpecificOperation
    private String faceId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 密码
     */
    @UpdateBySpecificOperation
    private String password;
    /**
     * 密码过期时期
     */
    @Nullable
    @UpdateBySpecificOperation
    private Date passwordExpiryDate;
    /**
     * 是否激活
     */
    @UpdateBySpecificOperation
    private boolean enabled;
    /**
     * 是否锁定
     */
    @UpdateBySpecificOperation
    private boolean locked;
    /**
     * 账户过期日期
     */
    @Nullable
    @UpdateBySpecificOperation
    private Date accountExpiryDate;
    /**
     * 姓
     */
    private String surname;
    /**
     * 名
     */
    private String givenName;
    /**
     * 全名
     */
    private String name;
    /**
     * 姓别编码
     */
    private Integer gender;
    /**
     * 出生日期
     */
    private Date dateOfBirth;
    /**
     * 民族
     */
    private String ethnicGroup;
    /**
     * 手机号
     */
    @UpdateBySpecificOperation
    private String mobilePhoneNumber;
    /**
     * 座机号
     */
    private String officeTelephoneNumber;
    /**
     * 传真号
     */
    private String fax;
    /**
     * 电子邮箱
     */
    @UpdateBySpecificOperation
    private String email;

    public AccountEntityTemplate(Date createdTime, Date lastUpdateTime, String id, String creator, String updater, String surname, String givenName, Integer gender, Date dateOfBirth, String ethnicGroup, String mobilePhoneNumber, String officeTelephoneNumber, String fax, String email) {
        super(createdTime, lastUpdateTime, id, creator, updater);
        this.surname = surname;
        this.givenName = givenName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.ethnicGroup = ethnicGroup;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.officeTelephoneNumber = officeTelephoneNumber;
        this.fax = fax;
        this.email = email;
    }
}
