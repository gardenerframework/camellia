package com.jdcloud.gardener.camellia.uac.client.schema.response;

import com.jdcloud.gardener.camellia.uac.client.schema.trait.*;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/8 16:01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClientAppearanceTemplate implements
        ApiStandardDataTraits.Id<String>,
        BasicClientInformation,
        GenericTraits.StatusTraits.EnableFlag,
        RequireConsentFlag,
        GrantType,
        RedirectUri,
        Scope,
        GenericTraits.Creator {
    /**
     * 授权类型
     */
    Collection<String> grantType;
    private String id;
    /**
     * 应用的名称
     */
    private String name;
    /**
     * 剪短的一些描述
     */
    private String description;
    /**
     * 启用/停用
     */
    private boolean enabled;
    /**
     * 是否自动批准
     */
    private boolean requireConsent;
    /**
     * 重定向地址
     */
    private Collection<String> redirectUri;
    /**
     * 授权范围
     */
    private Collection<String> scope;
    /**
     * 创建人
     */
    private String creator;
}
