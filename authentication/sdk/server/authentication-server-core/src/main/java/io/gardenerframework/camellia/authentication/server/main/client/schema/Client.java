package io.gardenerframework.camellia.authentication.server.main.client.schema;

import io.gardenerframework.camellia.authentication.server.common.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端信息
 * <p>
 * 这个客户端信息是认证过程中用到的，仅限认证服务器内使用
 * <p>
 * 如果资源服务器想要获取客户端的详细信息，可以调用客户端管理服务器的接口
 * <p>
 * 原则上只提供客户端的id对外
 *
 * @author zhanghan30
 * @date 2022/4/19 9:22 下午
 */
@AllArgsConstructor
@Getter
public class Client implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * client id
     */
    private final String clientId;
    /**
     * 访问的授权类型
     */
    private final String grantType;
    /**
     * 对用户信息的访问范围
     */
    private final Set<String> scopes;
    /**
     * 客户端元数据，用于开发人员在客户端内保存自己的一些所需数据
     * <p>
     * 第一级key是provider的类路径，值是提供的元数据
     */
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Map<String, String>> metadata = new ConcurrentHashMap<>();

    /**
     * 获取某个provider给出的元数据
     *
     * @param providerType 类型
     * @return 元数据
     */
    public Map<String, String> getMetadata(@NonNull String providerType) {
        return metadata.get(providerType);
    }

    /**
     * 基于provider和key获取值
     *
     * @param providerType provider类型
     * @param key          key
     * @return 对应的值
     */
    @Nullable
    public String getMetadataValue(@NonNull String providerType, @NonNull String key) {
        Map<String, String> metadataPiece = metadata.get(providerType);
        if (metadataPiece == null) {
            return null;
        }
        return metadataPiece.get(key);
    }
}
