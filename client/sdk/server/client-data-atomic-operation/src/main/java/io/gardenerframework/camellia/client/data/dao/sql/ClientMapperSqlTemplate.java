package io.gardenerframework.camellia.client.data.dao.sql;

import io.gardenerframework.camellia.client.data.dao.mapper.ClientMapperTemplate;
import io.gardenerframework.camellia.client.data.schema.criteria.ClientCriteriaTemplate;
import io.gardenerframework.camellia.client.data.schema.entity.ClientEntityTemplate;
import io.gardenerframework.fragrans.data.persistence.orm.statement.StatementBuilder;
import io.gardenerframework.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import io.gardenerframework.fragrans.data.persistence.template.sql.DomainSqlTemplateBase;
import io.gardenerframework.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import io.gardenerframework.fragrans.data.practice.persistence.orm.statement.schema.criteria.CommonCriteria;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * sql操作模板
 *
 * @author chris
 * @date 2023/10/23
 */
public class ClientMapperSqlTemplate<E extends ClientEntityTemplate, C extends ClientCriteriaTemplate> extends DomainSqlTemplateBase {
    public ClientMapperSqlTemplate() {
        super(ClientMapperTemplate.class, ClientEntityTemplate.class);
    }

    /**
     * 生成创建客户端的语句
     *
     * @param context 上下文
     * @param client  客户端记录
     * @return 语句
     */
    public String createClient(ProviderContext context, E client) {
        return StatementBuilder.getInstance().insert(
                getDomainObjectType(context),
                new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(),
                ClientMapperTemplate.ParameterNames.client).build();
    }

    /**
     * 构建读取客户端数据的语句
     *
     * @param context      上下文
     * @param clientId     客户端id
     * @param showPassword 是否显示密码
     * @return 语句
     */
    public String readClient(ProviderContext context, String clientId, boolean showPassword) {
        CommonScannerCallbacks.CompositeCallbacks compositeCallbacks = new CommonScannerCallbacks.CompositeCallbacks();
        //包含的列
        compositeCallbacks.include(new CommonScannerCallbacks.SelectStatementIgnoredAnnotations());
        if (!showPassword) {
            //包含基础上去掉的列
            compositeCallbacks.exclude(
                    new CommonScannerCallbacks.UsingTraits(Collections.singletonList(SecurityTraits.SecretTraits.Password.class))
            );
        }
        return StatementBuilder.getInstance().select(
                getDomainObjectType(context),
                compositeCallbacks
        ).where(new CommonCriteria.QueryByIdCriteria(
                ClientMapperTemplate.ParameterNames.clientId
        )).build();
    }

    public String searchClient(
            ProviderContext context,
            C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            int pageNo,
            int pageSize
    ) {
        return createSearchClientStatementInternally(
                (Class<? extends ClientMapperTemplate<?, ?>>) context.getMapperType(),
                criteria,
                ClientMapperTemplate.ParameterNames.criteria,
                must,
                should,
                pageNo,
                pageSize
        ).countFoundRows(true).build();
    }

    /**
     * 生成删除客户端的语句
     *
     * @param context  上下文
     * @param clientId 客户端id
     * @return 语句
     */
    public String deleteClient(ProviderContext context, String clientId) {
        return StatementBuilder.getInstance().delete(getDomainObjectType(context))
                .where(new CommonCriteria.QueryByIdCriteria(
                        ClientMapperTemplate.ParameterNames.clientId
                )).build();
    }

    private SelectStatement createSearchClientStatementInternally(
            Class<? extends ClientMapperTemplate<?, ?>> mapperType,
            C criteria, String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            long pageNo, long pageSize
    ) {
        Class<?> entityType = getDomainObjectType(mapperType);
        SelectStatement statement = StatementBuilder.getInstance().select()
                .table(TableNameUtils.getTableName(entityType))
                .columns(createSelectColumns(entityType));
        MatchAllCriteria queryCriteria = QueryCriteriaUtils.createQueryCriteria(
                () -> buildQueryCriteria(entityType, criteria, criteriaParameterName),
                must,
                should
        );
        if (!queryCriteria.isEmpty()) {
            statement.where(queryCriteria);
        }
        //附加分页参数
        PaginationUtils.appendPagination(statement, pageNo, pageSize);
        return statement;
    }
}
