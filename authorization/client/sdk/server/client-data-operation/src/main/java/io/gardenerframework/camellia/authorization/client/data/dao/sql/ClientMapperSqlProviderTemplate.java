package io.gardenerframework.camellia.authorization.client.data.dao.sql;

import io.gardenerframework.camellia.authorization.client.data.dao.mapper.ClientMapperTemplate;
import io.gardenerframework.camellia.authorization.client.data.schema.criteria.ClientCriteriaTemplate;
import io.gardenerframework.camellia.authorization.client.data.schema.entity.ClientEntityTemplate;
import io.gardenerframework.fragrans.data.persistence.criteria.support.CriteriaBuilder;
import io.gardenerframework.fragrans.data.persistence.orm.statement.StatementBuilder;
import io.gardenerframework.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import io.gardenerframework.fragrans.data.persistence.template.sql.DomainSqlTemplateBase;
import io.gardenerframework.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import io.gardenerframework.fragrans.data.practice.persistence.orm.statement.schema.criteria.CommonCriteria;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * sql操作模板
 *
 * @author chris
 * @date 2023/10/23
 */
public class ClientMapperSqlProviderTemplate<E extends ClientEntityTemplate, C extends ClientCriteriaTemplate>
        extends DomainSqlTemplateBase implements ProviderMethodResolver {
    public ClientMapperSqlProviderTemplate() {
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

    /**
     * 执行客户端的搜索
     *
     * @param context  上下文
     * @param criteria 搜索条件
     * @param must     必须要求符合的条件
     * @param should   可以要求符合的条件
     * @param pageNo   要求的页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    public String searchClient(
            ProviderContext context,
            C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            int pageNo,
            int pageSize
    ) {
        Class<?> entityType = getDomainObjectType(context);
        return buildSearchClientStatement(entityType, criteria, must, should)
                .pagination(pageNo, pageSize).build();
    }

    /**
     * 计算查到的行数
     *
     * @param context  上下文
     * @param criteria 查询条件
     * @param must     and 条件
     * @param should   or 条件
     * @return 语句
     */
    public String countFoundRows(
            ProviderContext context,
            C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    ) {
        Class<?> entityType = getDomainObjectType(context);
        return buildSearchClientStatement(entityType, criteria, must, should).countFoundRows(true).build();
    }

    /**
     * 生成语句供查询和查询所有行数使用
     *
     * @param entityType 实体类型
     * @param criteria   条件
     * @param must       and
     * @param should     or
     * @return 语句
     */
    private SelectStatement buildSearchClientStatement(
            Class<?> entityType,
            C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    ) {
        return StatementBuilder.getInstance().select(
                entityType,
                new CommonScannerCallbacks
                        .CompositeCallbacks()
                        //包含所有字段(除去注解要去掉的字段)
                        .include(new CommonScannerCallbacks.SelectStatementIgnoredAnnotations())
                        //去掉密码
                        .exclude(new CommonScannerCallbacks.UsingTraits(Collections.singletonList(SecurityTraits.SecretTraits.Password.class)))
        ).where(CriteriaBuilder.getInstance().createCriteria(
                TableNameUtils.getTableName(entityType),
                entityType,
                criteria,
                ClientMapperTemplate.ParameterNames.criteria,
                must,
                should
        ));
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
}
