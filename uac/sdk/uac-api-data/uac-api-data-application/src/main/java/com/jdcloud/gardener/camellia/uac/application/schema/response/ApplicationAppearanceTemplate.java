package com.jdcloud.gardener.camellia.uac.application.schema.response;

import com.jdcloud.gardener.camellia.uac.application.schema.trait.BasicApplicationInformation;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/11/8 16:01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ApplicationAppearanceTemplate implements
        ApiStandardDataTraits.Id<String>,
        BasicApplicationInformation,
        GenericTraits.StatusTraits.EnableFlag,
        GenericTraits.Creator {
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
     * 主页地址
     */
    private String homepageUrl;
    /**
     * 图标
     */
    private String logo;
    /**
     * 启用/停用
     */
    private boolean enabled;
    /**
     * 创建人
     */
    private String creator;
}
