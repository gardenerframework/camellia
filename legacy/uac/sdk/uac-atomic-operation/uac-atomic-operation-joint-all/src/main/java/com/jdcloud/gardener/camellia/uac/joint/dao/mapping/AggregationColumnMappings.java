package com.jdcloud.gardener.camellia.uac.joint.dao.mapping;

import com.jdcloud.gardener.camellia.uac.account.dao.mapper.AccountMapperTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.joint.schema.trait.JointTraits;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.handler.JsonTypeHandler;
import com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation.ColumnMapping;
import com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation.ColumnTypeHandlerProvider;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainDaoTemplateRegistry;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainObjectTemplateTypesResolver;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/11/7 12:34
 */
public interface AggregationColumnMappings {
    abstract class AggregationColumnMappingBase extends JsonTypeHandler implements ColumnTypeHandlerProvider {
        protected AggregationColumnMappingBase() {
            //设置为行json的模式
            super(RECORD_ROW_OBJECT_MAPPER);
        }

        protected Class<?> resolveAggregationEntityType(Class<?> mapperInterface, Method mapperMethod) {
            Type returnType = mapperMethod.getGenericReturnType();
            Class<?> aggregationEntityType = null;
            if (returnType instanceof Class) {
                aggregationEntityType = (Class<?>) returnType;
            }
            if (returnType instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) returnType).getRawType();
                if (Collection.class.isAssignableFrom((Class<?>) rawType)) {
                    aggregationEntityType = parseCollectionTypeParameter((ParameterizedType) returnType);
                }
            }
            Assert.notNull(aggregationEntityType, "cannot get aggregation type from " + returnType);
            return aggregationEntityType;
        }

        private Class<?> parseCollectionTypeParameter(ParameterizedType returnType) {
            Type actualTypeArgument = returnType.getActualTypeArguments()[0];
            if (actualTypeArgument instanceof Class) {
                return (Class<?>) actualTypeArgument;
            } else if (actualTypeArgument instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType();
            }
            throw new IllegalArgumentException("cannot determine type for " + returnType);
        }
    }

    class AccountColumnMapping extends AggregationColumnMappingBase implements ColumnTypeHandlerProvider {
        @Override
        @SuppressWarnings("unchecked")
        public Type getTypeReference() {
            Class<? extends AccountMapperTemplate<?, ?>> accountMapper = (Class<? extends AccountMapperTemplate<?, ?>>)
                    Objects.requireNonNull(DomainDaoTemplateRegistry.getItem(AccountMapperTemplate.class)).getActiveImplementation();
            return Objects.requireNonNull(DomainObjectTemplateTypesResolver.resolveTemplateImplementationTypeMappings(
                    accountMapper,
                    AccountMapperTemplate.class
            )).get(AccountEntityTemplate.class);
        }

        @Override
        public ColumnMapping provide(Class<?> mapperInterface, Method mapperMethod) {
            Class<?> aggregationEntityType = resolveAggregationEntityType(mapperInterface, mapperMethod);
            return new ColumnMapping(
                    FieldScannerStaticAccessor.scanner().column(aggregationEntityType, JointTraits.AggregationTraits.Account.class),
                    FieldScannerStaticAccessor.scanner().field(aggregationEntityType, JointTraits.AggregationTraits.Account.class),
                    this,
                    null
            );
        }
    }

    class ApplicationColumnMapping extends AggregationColumnMappingBase implements ColumnTypeHandlerProvider {
        @Override
        @SuppressWarnings("unchecked")
        public Type getTypeReference() {
            Class<? extends ApplicationMapperTemplate<?, ?>> applicationMapper = (Class<? extends ApplicationMapperTemplate<?, ?>>)
                    Objects.requireNonNull(DomainDaoTemplateRegistry.getItem(ApplicationMapperTemplate.class)).getActiveImplementation();
            return Objects.requireNonNull(DomainObjectTemplateTypesResolver.resolveTemplateImplementationTypeMappings(
                    applicationMapper,
                    ApplicationMapperTemplate.class
            )).get(ApplicationEntityTemplate.class);
        }


        @Override
        public ColumnMapping provide(Class<?> mapperInterface, Method mapperMethod) {
            Class<?> aggregationEntityType = resolveAggregationEntityType(mapperInterface, mapperMethod);
            return new ColumnMapping(
                    FieldScannerStaticAccessor.scanner().column(aggregationEntityType, JointTraits.AggregationTraits.Application.class),
                    FieldScannerStaticAccessor.scanner().field(aggregationEntityType, JointTraits.AggregationTraits.Application.class),
                    this,
                    null
            );
        }
    }
}
