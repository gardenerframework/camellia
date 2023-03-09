package com.jdcloud.gardener.camellia.uac.client.schema.criteria;

import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/11/12 0:14
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ClientCriteriaTemplate implements
        GenericTraits.Id<String>,
        GenericTraits.Name {
    /**
     * 按id搜索
     */
    private String id;
    /**
     * 应用名称 - 判等
     */
    private String name;
}
