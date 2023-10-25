package io.gardenerframework.camellia.authorization.client.data.dao.mapper;

import io.gardenerframework.camellia.authorization.client.data.dao.sql.ClientMapperSqlProviderTemplate;
import io.gardenerframework.camellia.authorization.client.data.schema.criteria.ClientCriteriaTemplate;
import io.gardenerframework.camellia.authorization.client.data.schema.entity.ClientEntityTemplate;
import io.gardenerframework.fragrans.data.persistence.template.annotation.DomainDaoTemplate;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * 客户端dao对象模板
 *
 * @author chris
 * @date 2023/10/23
 */
@DomainDaoTemplate
public interface ClientMapperTemplate<E extends ClientEntityTemplate, C extends ClientCriteriaTemplate> {
    /**
     * 创建客户端
     *
     * @param client 客户端记录
     */
    @SelectProvider(ClientMapperSqlProviderTemplate.class)
    void createClient(@Param(ParameterNames.client) E client);

    /**
     * 读取客户端信息
     *
     * @param clientId     客户端id
     * @param showPassword 是否显示密码
     * @return 读取结果
     */
    @Nullable
    @SelectProvider(ClientMapperSqlProviderTemplate.class)
    E readClient(@Param(ParameterNames.clientId) String clientId, boolean showPassword);

    /**
     * 执行客户端的搜索指令
     * <p>
     * 查询出来的客户端默认不显示密码
     *
     * @param criteria 搜索条件
     * @param must     必须满足的条件
     * @param should   可选满足的条件
     * @param pageNo   从第几页
     * @param pageSize 页大小
     */
    @SelectProvider(ClientMapperSqlProviderTemplate.class)
    Collection<E> searchClient(
            @Param(ParameterNames.criteria) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            int pageNo,
            int pageSize
    );

    /**
     * 获取找到多少行数据
     *
     * @param criteria 条件
     * @param must     and 条件
     * @param should   or 条件
     * @return 记录数量
     */
    @SelectProvider(ClientMapperSqlProviderTemplate.class)
    long countFoundRows(
            @Param(ParameterNames.criteria) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    );

    /**
     * 修改客户端中允许直接覆盖更新的字段
     *
     * @param clientId 客户端id
     * @param client   客户端数据
     */
    @UpdateProvider(ClientMapperSqlProviderTemplate.class)
    void updateClient(@Param(ParameterNames.clientId) String clientId, @Param(ParameterNames.client) E client);

    /**
     * 按patch修改客户端的字段
     *
     * @param clientId 客户端 id
     * @param client   客户端
     * @param fields   要修改的字段
     */
    @UpdateProvider(ClientMapperSqlProviderTemplate.class)
    void patchClient(
            @Param(ParameterNames.clientId) String clientId,
            @Param(ParameterNames.client) E client,
            Collection<String> fields
    );

    /**
     * 删除客户端
     *
     * @param clientId 客户端的记录id
     */
    @DeleteProvider(ClientMapperSqlProviderTemplate.class)
    void deleteClient(@Param(ParameterNames.clientId) String clientId);

    /**
     * 参数名称
     */
    interface ParameterNames {
        String clientId = "clientId";
        String client = "client";
        String criteria = "criteria";
    }
}
