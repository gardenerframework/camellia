package com.jdcloud.gardener.camellia.uac.client.atomic;

import com.jdcloud.gardener.camellia.uac.client.atomic.verifier.CLintMustExistVerifier;
import com.jdcloud.gardener.camellia.uac.client.atomic.verifier.ClientMustNotExistedVerifier;
import com.jdcloud.gardener.camellia.uac.client.dao.mapper.ClientMapperTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.criteria.ClientCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.atomic.verifier.PasswordVerifier;
import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordChecker;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author ZhangHan
 * @date 2022/11/12 9:31
 */
@RequiredArgsConstructor
public class ClientAtomicOperationTemplate<T extends ClientEntityTemplate, C extends ClientCriteriaTemplate> {
    private final ClientMapperTemplate<T, C> clientMapper;
    private final CommonOperations commonOperations;

    /**
     * 创建客户端
     *
     * @param client          客户端
     * @param passwordEncoder 客户端密码编码器
     */
    public void createClient(@NonNull T client, PasswordEncoder<? super T> passwordEncoder) {
        readClient(
                client.getId(),
                false,
                ClientMustNotExistedVerifier.builder().recordId(client.getId()).build()
        );
        //编码密码
        client.setPassword(passwordEncoder.encode(client, client.getPassword()));
        clientMapper.createClient(client);
    }

    /**
     * 读取客户端
     *
     * @param clientId     客户端id
     * @param showPassword 是否显示密码
     * @param checkers     检查器
     * @return 客户端信息
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public T readClient(@NonNull String clientId, boolean showPassword, RecordChecker<? super T>... checkers) {
        return commonOperations.readThenCheck().single(
                () -> clientMapper.readClient(clientId, showPassword),
                checkers
        );
    }

    /**
     * 读取客户端，不存在则报错
     *
     * @param clientId     客户端id
     * @param showPassword 是否显示密码
     * @param checkers     检查器
     * @return 客户端信息
     */
    @SuppressWarnings("unchecked")
    public T safeReadClient(@NonNull String clientId, boolean showPassword, RecordChecker<? super T>... checkers) {
        Collection<RecordChecker<? super T>> checkerList = new LinkedList<>();
        checkerList.add(
                CLintMustExistVerifier.builder().recordId(clientId).build()
        );
        if (checkers != null && checkers.length > 0) {
            checkerList.addAll(Arrays.asList(checkers));
        }
        RecordChecker<ClientEntityTemplate>[] array = checkerList.toArray(new RecordChecker[]{});
        return readClient(clientId, showPassword, array);
    }


    /**
     * 更改激活状态
     *
     * @param clientId 客户端id
     * @param status   客户端状态
     * @return 之前的客户端激活状态
     */
    public boolean changeClientEnableStatus(@NonNull String clientId, boolean status) {
        ClientEntityTemplate clientEntityTemplate = safeReadClient(clientId, false);
        clientMapper.changeClientEnableStatus(clientId, status);
        return clientEntityTemplate.isEnabled();
    }

    /**
     * 更改是否进行自动批准的状态
     *
     * @param clientId 客户端id
     * @param flag     状态
     * @return 之前的客户端激活状态
     */
    public boolean changeClientRequireConsentFlag(String clientId, boolean flag) {
        ClientEntityTemplate clientEntityTemplate = safeReadClient(clientId, false);
        clientMapper.changeClientRequireConsentFlag(clientId, flag);
        return clientEntityTemplate.isRequireConsent();
    }

    /**
     * 更新客户端
     *
     * @param client 客户端
     */
    @SuppressWarnings({"unchecked"})
    public T updateClient(
            @NonNull T client
    ) {
        T before = safeReadClient(client.getId(), false);
        //确保擦除密码
        before.setPassword(null);
        //设置名字
        clientMapper.updateClient(client);
        return before;
    }

    /**
     * 搜索客户端
     *
     * @param criteria 搜索条件
     * @param must     必须具备的属性
     * @param should   可以具备的属性
     * @param pageNo   页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    public GenericQueryResult<T> searchClient(
            @NonNull C criteria,
            Collection<Class<?>> must,
            Collection<Class<?>> should,
            long pageNo,
            long pageSize
    ) {
        return new GenericQueryResult<>(clientMapper.searchClient(
                criteria,
                must,
                should,
                pageNo, pageSize
        ), commonOperations.getFoundRows());
    }

    /**
     * 认证客户端
     *
     * @param clientId        客户端id
     * @param password        密码
     * @param passwordEncoder 密码编码器
     */
    public T authenticate(
            @NonNull String clientId,
            @NonNull String password,
            @NonNull PasswordEncoder<? super T> passwordEncoder
    ) {
        return safeReadClient(
                clientId,
                true,
                PasswordVerifier.<T>builder()
                        .password(password)
                        .passwordEncoder(passwordEncoder)
                        .build()
        );
    }

    /**
     * 变更客户端的scope
     *
     * @param clientId 客户端id
     * @param scope    范围
     * @return scope
     */
    public Collection<String> changeClientScope(
            @NonNull String clientId,
            Collection<String> scope
    ) {
        T client = safeReadClient(clientId, false);
        clientMapper.changeClientScope(clientId, scope);
        return client.getScope();
    }

    /**
     * 变更客户端的scope
     *
     * @param clientId  客户端id
     * @param grantType 授权类型
     * @return grantType
     */
    public Collection<String> changeClientGrantType(
            @NonNull String clientId,
            Collection<String> grantType
    ) {
        T client = safeReadClient(clientId, false);
        clientMapper.changeClientGrantType(clientId, grantType);
        return client.getGrantType();
    }

    /**
     * 变更客户端的scope
     *
     * @param clientId    客户端id
     * @param redirectUri 回调地址
     * @return 回调地址
     */
    public Collection<String> changeClientRedirectUri(
            @NonNull String clientId,
            Collection<String> redirectUri
    ) {
        T client = safeReadClient(clientId, false);
        clientMapper.changeClientRedirectUri(clientId, redirectUri);
        return client.getRedirectUri();
    }
}
