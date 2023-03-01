package com.jdcloud.gardener.camellia.uac.application.dao.mapper;

import com.jdcloud.gardener.camellia.uac.application.dao.sql.ApplicationSqlTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.fragrans.data.persistence.template.annotation.DomainDaoTemplate;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:29
 */
@DomainDaoTemplate
public interface ApplicationMapperTemplate<
        A extends ApplicationEntityTemplate,
        C extends ApplicationCriteriaTemplate
        > {
    String APPLICATION_ENTITY_PARAMETER_NAME = "application";
    String APPLICATION_ID_PARAMETER_NAME = "applicationId";
    String APPLICATION_CRITERIA_PARAMETER_NAME = "criteria";

    /**
     * 创建应用
     *
     * @param application 应用
     */
    @InsertProvider(ApplicationSqlTemplate.class)
    void createApplication(@Param(APPLICATION_ENTITY_PARAMETER_NAME) A application);

    /**
     * 读取指定应用信息
     *
     * @param applicationId 应用id
     */
    @SelectProvider(ApplicationSqlTemplate.class)
    A readApplication(@Param(APPLICATION_ID_PARAMETER_NAME) String applicationId);

    /**
     * 搜索应用
     *
     * @param criteria 搜索条件
     * @param must     必须包含的
     * @param should   可选包含的
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    @SelectProvider(ApplicationSqlTemplate.class)
    Collection<A> searchApplication(
            @Param(APPLICATION_CRITERIA_PARAMETER_NAME) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            long pageNo,
            long pageSize
    );

    /**
     * 更新指定应用信息
     *
     * @param application 应用
     */
    @UpdateProvider(ApplicationSqlTemplate.class)
    void updateApplication(@Param(APPLICATION_ENTITY_PARAMETER_NAME) A application);

    /**
     * 更新启用状态
     *
     * @param applicationId 应用id
     * @param status        状态
     */
    @UpdateProvider(ApplicationSqlTemplate.class)
    void changeApplicationEnableStatus(@Param(APPLICATION_ID_PARAMETER_NAME) String applicationId, boolean status);
}
