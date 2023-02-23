package com.jdcloud.gardener.camellia.uac.application.schema.entity;

import com.jdcloud.gardener.camellia.uac.application.schema.trait.BasicApplicationInformation;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.persistence.template.annotation.DomainObjectTemplate;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicOperationTraceableEntity;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("application")
@LogTarget("应用")
@SuperBuilder
@DomainObjectTemplate
public class ApplicationEntityTemplate extends BasicOperationTraceableEntity<String> implements
        BasicApplicationInformation,
        GenericTraits.StatusTraits.EnableFlag {
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
}
