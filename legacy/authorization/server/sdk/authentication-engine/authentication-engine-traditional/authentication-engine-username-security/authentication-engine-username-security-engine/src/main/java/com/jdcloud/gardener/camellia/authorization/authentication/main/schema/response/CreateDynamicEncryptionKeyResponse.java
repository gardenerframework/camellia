package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateDynamicEncryptionKeyResponse {
    /**
     * 使用的加密算法
     * <p>
     * 为空标识不加密
     */
    private String algorithm;
    /**
     * 使用的key
     * <p>
     * 为空标识不加密
     */
    private String key;
    /**
     * key的有效期
     * <p>
     * 为空标识不加密
     */
    private Long ttl;
}
