package com.jdcloud.gardener.camellia.uac.client.schema.entity;

import com.jdcloud.gardener.camellia.uac.client.schema.trait.*;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.persistence.template.annotation.DomainObjectTemplate;
import com.jdcloud.gardener.fragrans.data.schema.annotation.UpdateBySpecificOperation;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicOperationTraceableEntity;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/12 0:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("client")
@LogTarget("客户端")
@SuperBuilder
@DomainObjectTemplate
public class ClientEntityTemplate extends BasicOperationTraceableEntity<String> implements
        AccountTraits.Credentials,
        BasicClientInformation,
        GenericTraits.StatusTraits.EnableFlag,
        GrantType,
        RedirectUri,
        RequireConsentFlag,
        Scope {
    /**
     * 密码肯定不是随着更新请求一起处理的
     */
    @UpdateBySpecificOperation
    private String password;
    /**
     * 客户端名称
     */
    private String name;
    /**
     * 客户端的描述
     */
    private String description;
    /**
     * 是否激活
     */
    @UpdateBySpecificOperation
    private boolean enabled;
    /**
     * 许可的类型
     * <p>
     * 这个属性一般是服务端授予给客户端的，不能随便去覆盖和更新
     */
    @UpdateBySpecificOperation
    private Collection<String> grantType;
    /**
     * 重定向的uri
     * <p>
     * 回调地址也一般是要认证的
     */
    @UpdateBySpecificOperation
    private Collection<String> redirectUri;
    /**
     * 许可的范围
     * <p>
     * 这个属性一般是服务端授予给客户端的，不能随便去覆盖和更新
     */
    @UpdateBySpecificOperation
    private Collection<String> scope;
    /**
     * 是否必须要求批准
     * <p>
     * 这个属性一般是服务端授予给客户端的，不能随便去覆盖和更新
     */
    @UpdateBySpecificOperation
    private boolean requireConsent;
}
