package com.jdcloud.gardener.camellia.uac.joint.dao.sql;

import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.application.dao.sql.ApplicationSqlApi;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.trait.ApplicationRelation;
import com.jdcloud.gardener.camellia.uac.client.dao.mapper.ClientMapperTemplate;
import com.jdcloud.gardener.camellia.uac.client.dao.sql.ClientSqlApi;
import com.jdcloud.gardener.camellia.uac.client.schema.criteria.ClientCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.ClientRelation;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.PaginationUtils;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.SqlProviderUtils;
import com.jdcloud.gardener.camellia.uac.joint.dao.mapper.ApplicationClientRelationMapper;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelationDetail;
import com.jdcloud.gardener.camellia.uac.joint.schema.trait.ApplicationClientJointTraits;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainDaoTemplateRegistry;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

import java.util.Collection;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/11/24 17:11
 */
public class ApplicationClientRelationSqlProvider implements ProviderMethodResolver {

    /**
     * ????????????
     *
     * @param relation ??????
     */
    public String createRelation(
            @Param(ApplicationClientRelationMapper.APPLICATION_CLIENT_RELATION_PARAMETER_NAME) ApplicationClientRelation relation
    ) {
        return StatementBuilderStaticAccessor.builder().insert(
                ApplicationClientRelation.class,
                new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(),
                ApplicationClientRelationMapper.APPLICATION_CLIENT_RELATION_PARAMETER_NAME
        ).build();
    }

    /**
     * ??????????????????
     *
     * @param applicationCriteria ????????????????????????
     * @param applicationMust     ????????????????????????
     * @param applicationShould   ????????????????????????
     * @param clientCriteria      ????????????????????????
     * @param clientMust          ????????????????????????
     * @param clientShould        ????????????????????????
     * @param pageNo              ??????
     * @param pageSize            ?????????
     * @return ????????????
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String searchRelation(
            @Param(ApplicationClientRelationMapper.APPLICATION_CRITERIA_PARAMETER_NAME) ApplicationCriteriaTemplate applicationCriteria,
            Collection<Class<?>> applicationMust,
            Collection<Class<?>> applicationShould,
            @Param(ApplicationClientRelationMapper.CLIENT_CRITERIA_PARAMETER_NAME) ClientCriteriaTemplate clientCriteria,
            Collection<Class<?>> clientMust,
            Collection<Class<?>> clientShould,
            long pageNo,
            long pageSize
    ) {
        Class<? extends ApplicationMapperTemplate> activeApplicationMapper = Objects.requireNonNull(DomainDaoTemplateRegistry.getItem(ApplicationMapperTemplate.class)).getActiveImplementation();
        Class<? extends ClientMapperTemplate> activeClientMapper = Objects.requireNonNull(DomainDaoTemplateRegistry.getItem(ClientMapperTemplate.class)).getActiveImplementation();
        //???????????????
        ApplicationSqlApi applicationSqlApi = SqlProviderUtils.getActiveProvider(activeApplicationMapper);
        SelectStatement searchApplicationStatement = applicationSqlApi.createSearchApplicationStatement(
                activeApplicationMapper,
                applicationCriteria,
                ApplicationClientRelationMapper.APPLICATION_CRITERIA_PARAMETER_NAME,
                applicationMust, applicationShould
        );
        ClientSqlApi clientSqlApi = SqlProviderUtils.getActiveProvider(activeClientMapper);
        //??????????????????
        SelectStatement searchClientStatement = clientSqlApi.createSearchClientStatement(
                activeClientMapper,
                clientCriteria,
                ApplicationClientRelationMapper.CLIENT_CRITERIA_PARAMETER_NAME,
                clientMust, clientShould
        );
        String mainTableName = TableNameUtils.getTableName(ApplicationClientRelation.class);
        String applicationSubQueryName = "application";
        String clientSubQueryName = "client";
        return PaginationUtils.appendPagination(
                StatementBuilderStaticAccessor.builder().select(
                        ApplicationClientRelation.class,
                        new CommonScannerCallbacks.SelectStatementIgnoredAnnotations(),
                        mainTableName
                ).join(
                        searchApplicationStatement,
                        applicationSubQueryName
                ).on(
                        new EqualsCriteria(
                                new Column(
                                        mainTableName,
                                        FieldScannerStaticAccessor.scanner().column(
                                                ApplicationClientRelation.class,
                                                ApplicationRelation.class
                                        )
                                ),
                                applicationSqlApi.createIdColumn(
                                        activeApplicationMapper,
                                        applicationSubQueryName
                                )
                        )
                ).join(
                        searchClientStatement,
                        clientSubQueryName
                ).on(
                        new EqualsCriteria(
                                new Column(
                                        mainTableName,
                                        FieldScannerStaticAccessor.scanner().column(
                                                ApplicationClientRelation.class,
                                                ClientRelation.class
                                        )
                                ),
                                clientSqlApi.createIdColumn(
                                        activeClientMapper,
                                        clientSubQueryName
                                )
                        )
                ).column(
                        applicationSqlApi.aggregateSelectColumnsToJson(
                                activeApplicationMapper,
                                applicationSubQueryName,
                                FieldScannerStaticAccessor.scanner().column(ApplicationClientRelationDetail.class, ApplicationClientJointTraits.Application.class)
                        )
                ).column(
                        clientSqlApi.aggregateSelectColumnsToJson(
                                activeClientMapper,
                                clientSubQueryName,
                                FieldScannerStaticAccessor.scanner().column(ApplicationClientRelationDetail.class, ApplicationClientJointTraits.Client.class)
                        )
                ), pageNo, pageSize).build();
    }
}
