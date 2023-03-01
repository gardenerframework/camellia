package com.jdcloud.gardener.camellia.uac.common.schema.criteria;

import lombok.*;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/7 10:53
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DomainCriteriaWrapper<C> {
    /**
     * 条件字段名称，用于搜索时指定搜索语句中的字段名称
     * <p>
     * 比如关系搜索中，账户关系的参数名叫"accountCriteria"，则最终使用的搜索参数名是"accountCriteria.criteria"
     */
    private final String criteriaFieldName = "criteria";
    private C criteria;
    @Nullable
    private Collection<Class<?>> must;
    @Nullable
    private Collection<Class<?>> should;
}
