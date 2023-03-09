package com.jdcloud.gardener.camellia.uac.client.dao.mapper;

import com.jdcloud.gardener.camellia.uac.client.dao.sql.ClientSqlTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.criteria.ClientCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
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
public interface ClientMapperTemplate<
        T extends ClientEntityTemplate,
        C extends ClientCriteriaTemplate
        > {
    String CLIENT_ENTITY_PARAMETER_NAME = "client";
    String CLIENT_ID_PARAMETER_NAME = "clientId";
    String CLIENT_CRITERIA_PARAMETER_NAME = "criteria";
    String CLIENT_SCOPE_PARAMETER_NAME = "scope";
    String CLIENT_GRANT_TYPE_PARAMETER_NAME = "grantType";
    String CLIENT_REDIRECT_URI_PARAMETER_NAME = "redirectUri";

    /**
     * 创建客户端
     *
     * @param client 客户端
     */
    @InsertProvider(ClientSqlTemplate.class)
    void createClient(@Param(CLIENT_ENTITY_PARAMETER_NAME) T client);

    /**
     * 读取指定应用信息
     *
     * @param clientId id
     */
    @SelectProvider(ClientSqlTemplate.class)
    T readClient(@Param(CLIENT_ID_PARAMETER_NAME) String clientId, boolean showPassword);

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
    @SelectProvider(ClientSqlTemplate.class)
    Collection<T> searchClient(
            @Param(CLIENT_CRITERIA_PARAMETER_NAME) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            long pageNo,
            long pageSize
    );

    /**
     * 更新指定应用信息
     *
     * @param client 客户端
     */
    @UpdateProvider(ClientSqlTemplate.class)
    void updateClient(@Param(CLIENT_ENTITY_PARAMETER_NAME) T client);

    /**
     * 更新启用状态
     *
     * @param clientId id
     * @param status   状态
     */
    @UpdateProvider(ClientSqlTemplate.class)
    void changeClientEnableStatus(@Param(CLIENT_ID_PARAMETER_NAME) String clientId, boolean status);

    /**
     * 更新是否自动批准的状态
     *
     * @param clientId id
     * @param flag     状态
     */
    @UpdateProvider(ClientSqlTemplate.class)
    void changeClientRequireConsentFlag(@Param(CLIENT_ID_PARAMETER_NAME) String clientId, boolean flag);

    /**
     * 变更客户端的scope
     *
     * @param clientId 客户端id
     * @param scope    新的scope
     */
    @UpdateProvider(ClientSqlTemplate.class)
    void changeClientScope(@Param(CLIENT_ID_PARAMETER_NAME) String clientId, @Param(CLIENT_SCOPE_PARAMETER_NAME) Collection<String> scope);

    /**
     * 变更客户端的scope
     *
     * @param clientId  客户端id
     * @param grantType 授权类型
     */
    @UpdateProvider(ClientSqlTemplate.class)
    void changeClientGrantType(@Param(CLIENT_ID_PARAMETER_NAME) String clientId, @Param(CLIENT_GRANT_TYPE_PARAMETER_NAME) Collection<String> grantType);

    /**
     * 变更客户端的scope
     *
     * @param clientId    客户端id
     * @param redirectUri 重定向地址
     */
    @UpdateProvider(ClientSqlTemplate.class)
    void changeClientRedirectUri(@Param(CLIENT_ID_PARAMETER_NAME) String clientId, @Param(CLIENT_REDIRECT_URI_PARAMETER_NAME) Collection<String> redirectUri);
}
