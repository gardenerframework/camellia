package io.gardenerframework.camellia.authentication.common.client;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 提供客户端所需的元数据
 */
@FunctionalInterface
public interface RequestingClientMetadataProvider<M extends Serializable> {
    /**
     * 返回元数据的部分，多个provider完全提供完所有碎片后整合为最终的元数据
     *
     * @param clientId 客户端id
     * @return 元数据，如果为空就不会加入到{@link RequestingClient}中
     */
    @Nullable
    M getMetadataPiece(@NonNull String clientId);
}
