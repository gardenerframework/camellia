package io.gardenerframework.camellia.authentication.server.security.encryption;

import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/3/11 18:39
 */
public interface EncryptionService {
    /**
     * 产生一个key
     *
     * @return 可用的key
     * @throws Exception 发生问题
     */
    EncryptionKey createKey() throws Exception;

    /**
     * 解密
     *
     * @param id     密钥id
     * @param cipher 密文
     * @return 解密后的结果
     * @throws Exception 遇到问题
     */
    byte[] decrypt(@NonNull String id, @NonNull byte[] cipher) throws Exception;
}
