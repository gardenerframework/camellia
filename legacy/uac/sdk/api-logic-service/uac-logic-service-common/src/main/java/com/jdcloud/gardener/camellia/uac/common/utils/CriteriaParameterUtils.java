package com.jdcloud.gardener.camellia.uac.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SearchCriteriaParameterBase;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghan30
 * @date 2022/11/9 14:32
 */
public abstract class CriteriaParameterUtils {
    /**
     * 部分搜索条件实现的trait和实体实现的trait字段名称相同但类型不同，需要转换
     */
    private static final Map<Class<?>, Class<?>> CRITERIA_TRAIT_CLASS_MAPPING = new ConcurrentHashMap<>();

    static {
        //两个id trait之间需要转换
        CRITERIA_TRAIT_CLASS_MAPPING.put(ApiStandardDataTraits.Id.class, GenericTraits.Id.class);
    }

    private CriteriaParameterUtils() {

    }

    /**
     * 创建查询条件wrapper
     *
     * @param daoCriteriaType             dao层的查询条件类型
     * @param searchCriteriaParameter     controller层的搜索参数
     * @param criteriaFiledToTraitMapping controller层的搜索参数的字段名与dao层要求的trait类型间的映射
     * @param objectMapper                对象转换器
     * @param <C>                         dao层的搜索条件类型
     * @param <S>                         搜索参数类型
     * @return 查询条件wrapper
     */
    public static <C, S extends SearchCriteriaParameterBase> DomainCriteriaWrapper<C> createDomainCriteriaWrapper(
            Class<C> daoCriteriaType,
            S searchCriteriaParameter,
            Map<String, Class<?>> criteriaFiledToTraitMapping,
            ObjectMapper objectMapper
    ) {
        DomainCriteriaWrapper<C> result = new DomainCriteriaWrapper<>();
        result.setCriteria(objectMapper.convertValue(searchCriteriaParameter, daoCriteriaType));
        Collection<String> must = searchCriteriaParameter.getMust();
        Collection<String> should = searchCriteriaParameter.getShould();
        if (!CollectionUtils.isEmpty(must)) {
            result.setMust(new LinkedList<>());
            must.forEach(
                    filed -> {
                        if (criteriaFiledToTraitMapping.get(filed) != null) {
                            Class<?> targetType = criteriaFiledToTraitMapping.get(filed);
                            targetType = (CRITERIA_TRAIT_CLASS_MAPPING.get(targetType) == null ? targetType : CRITERIA_TRAIT_CLASS_MAPPING.get(targetType));
                            result.getMust().add(targetType);
                        }
                    }
            );
        }
        if (!CollectionUtils.isEmpty(should)) {
            result.setShould(new LinkedList<>());
            should.forEach(
                    filed -> {
                        if (criteriaFiledToTraitMapping.get(filed) != null) {
                            Class<?> targetType = criteriaFiledToTraitMapping.get(filed);
                            targetType = (CRITERIA_TRAIT_CLASS_MAPPING.get(targetType) == null ? targetType : CRITERIA_TRAIT_CLASS_MAPPING.get(targetType));
                            result.getMust().add(targetType);
                        }
                    }
            );
        }
        return result;
    }
}
