package io.gardenerframework.camellia.authentication.common.client.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 特指基于oauth2协议的，正在请求接口或服务或资源的客户端信息
 */
@SuperBuilder
@Getter
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class RequestingClient implements Serializable {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * client id
     */
    @NonNull
    private String clientId;
    /**
     * 访问的授权类型
     */
    @NonNull
    private String grantType;
    /**
     * 对用户信息的访问范围
     */
    @NonNull
    private Set<String> scopes;

    /**
     * 要求范围不能被改变
     *
     * @param scopes 范围
     */
    public void setScopes(@NonNull Set<String> scopes) {
        this.scopes = Collections.unmodifiableSet(scopes);
    }

    /**
     * 客户端元数据，用于开发人员在客户端内保存自己的一些所需数据
     * <p>
     * 第一级key是provider的类路径，值是提供的元数据
     * <p>
     * 屏蔽原始的getter
     */
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Serializable> metadata = new ConcurrentHashMap<>();

    /**
     * 设置元数据
     *
     * @param providerType 类型
     * @param metadata     元数据
     */
    public <M extends Serializable> void setMetadata(@NonNull String providerType, @NonNull M metadata) {
        this.metadata.put(providerType, metadata);
    }

    /**
     * 获取某个provider给出的元数据
     *
     * @param providerType 类型
     * @return 元数据
     */
    @Nullable
    @SuppressWarnings("unchecked")

    public <M extends Serializable> M getMetadata(@NonNull String providerType) {
        return (M) metadata.get(providerType);
    }
}
