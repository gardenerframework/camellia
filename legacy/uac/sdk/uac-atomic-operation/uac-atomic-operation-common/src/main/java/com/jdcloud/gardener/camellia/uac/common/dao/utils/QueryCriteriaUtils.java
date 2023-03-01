package com.jdcloud.gardener.camellia.uac.common.dao.utils;

import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.MatchAnyCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.FieldNameValue;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ZhangHan
 * @date 2022/11/5 10:25
 */
public abstract class QueryCriteriaUtils {
    private QueryCriteriaUtils() {

    }

    /**
     * 添加一个判等条件
     *
     * @param mappings              对外输出的字段与条件的映射
     * @param entityType            实体类型(用于字段扫描)
     * @param criteriaParameterName 条件参数名称
     * @param trait                 字段trait
     */
    public static void addEqualsCriteria(Map<Class<?>, BasicCriteria> mappings, Class<?> entityType, String criteriaParameterName, Class<?> trait) {
        mappings.put(trait, new EqualsCriteria(
                FieldScannerStaticAccessor.scanner().column(entityType, trait),
                new FieldNameValue(criteriaParameterName, FieldScannerStaticAccessor.scanner().field(entityType, trait))
        ));
    }

    /**
     * 创建一个基于must和should的组合条件
     *
     * @param mappingFactory 条件和字段的映射关系工厂
     * @param must           要求必须符合的字段
     * @param should         要求可选符合的字段
     * @return 组合条件
     */
    public static MatchAllCriteria createQueryCriteria(
            Supplier<Map<Class<?>, BasicCriteria>> mappingFactory,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    ) {
        Map<Class<?>, BasicCriteria> mappings = mappingFactory.get();
        MatchAllCriteria queryCriteria = new MatchAllCriteria();
        if (!CollectionUtils.isEmpty(must)) {
            MatchAllCriteria mustCriteria = new MatchAllCriteria();
            must.forEach(
                    trait -> {
                        BasicCriteria element = mappings.get(trait);
                        if (element != null) {
                            mustCriteria.and(element);
                        }
                    }
            );
            if (!mustCriteria.isEmpty()) {
                queryCriteria.and(mustCriteria);
            }
        }
        if (!CollectionUtils.isEmpty(should)) {
            MatchAnyCriteria shouldCriteria = new MatchAnyCriteria();
            should.forEach(
                    trait -> {
                        BasicCriteria element = mappings.get(trait);
                        if (element != null) {
                            shouldCriteria.or(element);
                        }
                    }
            );
            if (!shouldCriteria.isEmpty()) {
                queryCriteria.and(shouldCriteria);
            }
        }
        return queryCriteria;
    }
}
