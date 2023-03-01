package com.jdcloud.gardener.camellia.uac.common.schema.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/7 20:34
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class SearchCriteriaParameterBase {
    /**
     * 必须具有的属性
     */
    private Collection<String> must;
    /**
     * 可选具有的属性
     */
    private Collection<String> should;
}
