package com.jdcloud.gardener.camellia.uac.application.defaults.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.criteria.DefaultApplicationCriteria;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.entity.DefaultApplicationEntity;
import com.jdcloud.gardener.camellia.uac.application.defautls.schema.request.DefaultCreateApplicationParameter;
import com.jdcloud.gardener.camellia.uac.application.defautls.schema.request.DefaultSearchApplicationCriteriaParameter;
import com.jdcloud.gardener.camellia.uac.application.defautls.schema.request.DefaultUpdateApplicationParameter;
import com.jdcloud.gardener.camellia.uac.application.defautls.schema.response.DefaultApplicationAppearance;
import com.jdcloud.gardener.camellia.uac.application.service.ApplicationServiceTemplate;
import com.jdcloud.gardener.camellia.uac.common.utils.CriteriaParameterUtils;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghan30
 * @date 2022/11/9 13:44
 */
@ConditionalOnMissingBean(value = ApplicationServiceTemplate.class, ignored = DefaultApplicationService.class)
public class DefaultApplicationService extends ApplicationServiceTemplate<
        DefaultCreateApplicationParameter,
        DefaultSearchApplicationCriteriaParameter,
        DefaultUpdateApplicationParameter,
        DefaultApplicationAppearance,
        DefaultApplicationEntity,
        DefaultApplicationCriteria> {

    /**
     * 搜索条件的字段和trait类的对照关系表
     */
    private static final Map<String, Class<?>> CRITERIA_FILED_TO_TRAIT_MAPPING = new ConcurrentHashMap<>();

    static {
        Collection<Class<?>> traits = Arrays.asList(
                ApiStandardDataTraits.Id.class,
                GenericTraits.Name.class
        );
        //默认搜索参数实现的trait
        traits.forEach(
                clazz -> CRITERIA_FILED_TO_TRAIT_MAPPING.put(new FieldScanner().field(DefaultSearchApplicationCriteriaParameter.class, clazz), clazz)
        );
    }

    public DefaultApplicationService(ObjectMapper objectMapper) {
        super(
                source -> objectMapper.convertValue(source, DefaultApplicationEntity.class),
                source -> CriteriaParameterUtils.createDomainCriteriaWrapper(
                        DefaultApplicationCriteria.class,
                        source,
                        CRITERIA_FILED_TO_TRAIT_MAPPING,
                        objectMapper
                ),
                source -> objectMapper.convertValue(source, DefaultApplicationEntity.class),
                source -> objectMapper.convertValue(source, DefaultApplicationAppearance.class)
        );
    }
}
