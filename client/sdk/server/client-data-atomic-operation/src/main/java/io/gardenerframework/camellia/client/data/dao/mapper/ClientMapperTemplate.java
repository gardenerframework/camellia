package io.gardenerframework.camellia.client.data.dao.mapper;

import io.gardenerframework.camellia.client.data.dao.sql.ClientMapperSqlTemplate;
import io.gardenerframework.camellia.client.data.schema.criteria.ClientCriteriaTemplate;
import io.gardenerframework.camellia.client.data.schema.entity.ClientEntityTemplate;
import io.gardenerframework.fragrans.data.persistence.template.annotation.DomainDaoTemplate;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
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
    @SelectProvider(ClientMapperSqlTemplate.class)
    void createClient(@Param(ParameterNames.client) E client);

    /**
     * 读取客户端信息
     *
     * @param clientId     客户端id
     * @param showPassword 是否显示密码
     * @return 读取结果
     */
    @Nullable
    @InsertProvider(ClientMapperSqlTemplate.class)
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
    @SelectProvider(ClientMapperSqlTemplate.class)
    Collection<E> searchClient(
            @Param(ParameterNames.criteria) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            int pageNo,
            int pageSize
    );

    /**
     * 删除客户端
     *
     * @param clientId 客户端的记录id
     */
    @DeleteProvider(ClientMapperSqlTemplate.class)
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
