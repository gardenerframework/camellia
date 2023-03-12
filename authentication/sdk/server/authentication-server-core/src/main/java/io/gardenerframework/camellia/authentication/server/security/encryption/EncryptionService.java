package io.gardenerframework.camellia.authentication.server.security.encryption;

import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import lombok.NonNull;

import java.security.InvalidKeyException;
import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/3/11 18:39
 */
public interface EncryptionService {
    /**
     * 产生一个key
     *
     * @param ttl key的生存时间
     * @return 可用的key
     * @throws Exception 发生问题
     */
    EncryptionKey createKey(@NonNull Duration ttl) throws Exception;

    /**
     * 执行加密
     *
     * @param id      密钥id
     * @param content 内容
     * @return 加密后的结果
     * @throws InvalidKeyException 当key id对应的key不存在或不合法
     * @throws Exception           发生问题
     */
    byte[] encrypt(@NonNull String id, @NonNull byte[] content) throws InvalidKeyException, Exception;

    /**
     * 解密
     *
     * @param id     密钥id
     * @param cipher 密文
     * @return 解密后的结果
     * @throws InvalidKeyException 当key id对应的key不存在或不合法
     * @throws Exception           遇到问题
     */
    byte[] decrypt(@NonNull String id, @NonNull byte[] cipher) throws InvalidKeyException, Exception;
}
