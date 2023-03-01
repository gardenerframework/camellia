package com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.response;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/11/17 19:17
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RoleAppearance implements
        ApiStandardDataTraits.Id<String>,
        GenericTraits.Code,
        GenericTraits.Name {
    /**
     * 角色id
     */
    private String id;
    /**
     * 应用侧认为唯一识别角色代码
     */
    private String code;
    /**
     * 角色名称
     */
    private String name;
}
