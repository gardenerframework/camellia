package com.jdcloud.gardener.camellia.uac.application.schema.request;

import com.jdcloud.gardener.camellia.uac.common.schema.request.SearchCriteriaParameterBase;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/8 16:10
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SearchApplicationCriteriaParameterTemplate extends SearchCriteriaParameterBase implements
        ApiStandardDataTraits.Id<String>,
        GenericTraits.Name {
    /**
     * 支持按照id
     */
    private String id;
    /**
     * 支持搜索名称
     */
    private String name;

    public SearchApplicationCriteriaParameterTemplate(Collection<String> must, Collection<String> should, String id, String name) {
        super(must, should);
        this.id = id;
        this.name = name;
    }
}
