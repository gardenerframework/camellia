package com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/17 19:32
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRoleProperty implements
        ApiStandardDataTraits.Id<String>,
        GenericTraits.Code {
    /**
     * 角色id
     */
    @NotBlank
    private String id;
    /**
     * 应用侧认为唯一识别角色代码
     */
    @NotBlank
    private String code;
}
