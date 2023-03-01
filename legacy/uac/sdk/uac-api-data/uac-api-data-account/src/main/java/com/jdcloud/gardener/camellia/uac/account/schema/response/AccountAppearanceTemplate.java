package com.jdcloud.gardener.camellia.uac.account.schema.response;

import com.jdcloud.gardener.camellia.uac.account.schema.trait.BasicPersonalInformation;
import com.jdcloud.gardener.camellia.uac.account.schema.trait.Contact;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 基本账户信息，主要用于列表展示和详细信息的展示
 * <p>
 * 注意用户表现数据必然没有密码
 * <p>
 * 如果用户有扩展字段，现场需要继承这个类
 * <p>
 * 为啥这里没有组织的信息、角色的信息呢，因为这些都是到对应领域的接口，拿着用户id去查
 *
 * @author zhanghan30
 * @date 2022/8/11 1:04 下午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountAppearanceTemplate implements
        ApiStandardDataTraits.Id<String>,
        BasicPersonalInformation,
        Contact,
        GenericTraits.StatusTraits.EnableFlag,
        AccountTraits.AccountExpiryDate,
        GenericTraits.Creator {
    /**
     * 用户账户id
     */
    private String id;
    /**
     * 姓
     */
    private String surname;
    /**
     * 名
     */
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
    private String email;
    /**
     * 联系用的手机号
     */
    private String mobilePhoneNumber;
    /**
     * 座机号
     */
    private String officeTelephoneNumber;
    /**
     * 传真
     */
    private String fax;
    /**
     * 锁定状态
     */
    private boolean locked;
    /**
     * 激活状态
     */
    private boolean enabled;
    /**
     * 账户过期时间
     */
    private Date accountExpiryDate;
    /**
     * 创建人
     */
    private String creator;
}
