package io.gardenerframework.camellia.authentication.server.security.encryption.schema;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2023/3/11 18:39
 */
@Getter
@Setter
@SuperBuilder
public class EncryptionKey {
    /**
     * 加密id
     */
    @NonNull
    private String id;
    /**
     * 密钥
     */
    @NonNull
    private String key;
    /**
     * 密钥过期时间
     */
    @NonNull
    private Date expiryTime;
}
