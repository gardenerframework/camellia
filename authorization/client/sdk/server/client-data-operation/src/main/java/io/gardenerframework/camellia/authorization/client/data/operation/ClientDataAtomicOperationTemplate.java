package io.gardenerframework.camellia.authorization.client.data.operation;

import io.gardenerframework.camellia.authorization.client.data.dao.mapper.ClientMapperTemplate;
import io.gardenerframework.camellia.authorization.client.data.schema.criteria.ClientCriteriaTemplate;
import io.gardenerframework.camellia.authorization.client.data.schema.entity.ClientEntityTemplate;
import io.gardenerframework.fragrans.data.practice.operation.CommonOperations;
import io.gardenerframework.fragrans.data.practice.operation.checker.RecordChecker;
import io.gardenerframework.fragrans.data.schema.query.GenericQueryResult;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.common.schema.verb.Create;
import io.gardenerframework.fragrans.log.common.schema.verb.Update;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chris
 * @date 2023/10/24
 */
@Slf4j
@RequiredArgsConstructor
public class ClientDataAtomicOperationTemplate<E extends ClientEntityTemplate, C extends ClientCriteriaTemplate> {
    /**
     * 数据操作模板
     * <p>
     * 按类型加载
     */
    @NonNull
    private final ClientMapperTemplate<E, C> clientMapperTemplate;
    /**
     * 通用操作类
     */
    @NonNull
    private final CommonOperations commonOperations;

    /**
     * 创建客户端
     *
     * @param client 客户端记录
     * @throws Exception 遇到问题抛出异常
     */
    public String createClient(@NonNull E client) throws Exception {
        return createClient(client, null);
    }

    /**
     * 创建客户端
     *
     * @param client          客户端
     * @param passwordEncoder 客户端密码编码器
     * @throws Exception 遇到问题抛出异常
     */
    public String createClient(@NonNull E client, @Nullable Function<? super E, String> passwordEncoder) throws Exception {
        //生成客户端id
        String clientId = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        client.setId(clientId);
        //编码密码
        if (passwordEncoder != null) {
            client.setPassword(passwordEncoder.apply(client));
        }
        //执行插入操作
        clientMapperTemplate.createClient(client);
        //记录日志
        GenericLoggers.basicLogger().info(
                log,
                GenericBasicLogContent.builder()
                        .what(client.getClass())
                        .how(new Create())
                        .detail(new ClientLogDetail(clientId))
                        .build()
        );
        return clientId;
    }

    /**
     * 读取给定id的客户端
     *
     * @param clientId     客户端id
     * @param showPassword 是否返回密码
     * @return 客户端信息
     * @throws Exception 遇到问题抛出异常
     */
    @Nullable
    public E readClient(@NonNull String clientId, boolean showPassword) throws Exception {
        return commonOperations.readThenCheck().single(
                () -> clientMapperTemplate.readClient(clientId, showPassword)
        );
    }

    /**
     * 读取给定id的客户端
     *
     * @param clientId     客户端id
     * @param showPassword 是否返回密码
     * @param checkers     记录验证器清单
     * @return 客户端信息
     * @throws Exception 遇到问题抛出异常
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public E readClient(@NonNull String clientId, boolean showPassword, RecordChecker<? super E>... checkers) throws Exception {
        return commonOperations.readThenCheck().single(
                () -> clientMapperTemplate.readClient(clientId, showPassword),
                checkers
        );
    }

    /**
     * 搜索客户端
     *
     * @param criteria 条件
     * @param must     and 条件
     * @param should   or 条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 搜索结果
     * @throws Exception 遇到问题抛出异常
     */
    public GenericQueryResult<E> searchClient(
            @NonNull C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            int pageNo,
            int pageSize
    ) throws Exception {
        return GenericQueryResult.<E>builder().contents(clientMapperTemplate.searchClient(criteria, must, should, pageNo, pageSize))
                .total(clientMapperTemplate.countFoundRows(criteria, must, should))
                .build();
    }

    /**
     * 更新客户端
     *
     * @param clientId 客户端id
     * @param client   客户端数据
     * @throws Exception 遇到问题抛出异常
     */
    public void updateClient(@NonNull String clientId, @NonNull E client) throws Exception {
        clientMapperTemplate.updateClient(clientId, client);
        //记录日志
        GenericLoggers.basicLogger().info(
                log,
                GenericBasicLogContent.builder()
                        .what(client.getClass())
                        .how(new Update())
                        .detail(new ClientLogDetail(clientId))
                        .build()
        );
    }

    /**
     * 更新客户端，并在更新前执行记录检查，如是否存在，是否激活等
     *
     * @param clientId 客户端id
     * @param client   客户端
     * @param checkers 检查器
     * @throws Exception 遇到问题抛出异常
     */
    public void updateClient(@NonNull String clientId, @NonNull E client, RecordChecker<? super E>... checkers) throws Exception {
        E record = readClient(clientId, false, checkers);
        if (record != null) {
            updateClient(clientId, client);
        }
    }

    /**
     * 更新客户端
     *
     * @param clientId 客户端id
     * @param client   客户端
     * @param fields   字段集合
     * @throws Exception 遇到问题抛出异常
     */
    public void patchClient(@NonNull String clientId, @NonNull E client, Collection<String> fields) throws Exception {
        if (!CollectionUtils.isEmpty(fields)) {
            clientMapperTemplate.patchClient(clientId, client, fields);
        }
        //记录日志
        GenericLoggers.basicLogger().info(
                log,
                GenericBasicLogContent.builder()
                        .what(client.getClass())
                        .how(new Update())
                        .detail(new ClientLogDetail(clientId) {
                            private final Collection<String> patchedFields = fields;
                        })
                        .build()
        );
    }

    /**
     * 更新客户端，并在更新前执行记录检查，如是否存在，是否激活等
     *
     * @param clientId 客户端id
     * @param client   客户端
     * @param fields   字段集合
     * @param checkers 检查器
     * @throws Exception 遇到问题抛出异常
     */
    public void patchClient(@NonNull String clientId, @NonNull E client, Collection<String> fields, RecordChecker<? super E>... checkers) throws Exception {
        E record = readClient(clientId, false, checkers);
        if (record != null) {
            patchClient(clientId, client, fields);
        }
    }

    @AllArgsConstructor
    private static class ClientLogDetail implements Detail {
        private final String clientId;
    }
}
