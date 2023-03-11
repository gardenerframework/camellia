package io.gardenerframework.camellia.authentication.server.main;

import lombok.*;

import java.util.Date;

public interface PasswordEncryptionService {
    /**
     * 创建加密秘钥
     *
     * @return 加密秘钥
     * @throws Exception 遇到问题
     */
    Key createKey() throws Exception;

    /**
     * 执行密码解密
     *
     * @param id     秘钥id
     * @param cipher 密文
     * @return 解密后的密码
     * @throws Exception 遇到问题
     */
    String decrypt(@NonNull String id, @NonNull String cipher) throws Exception;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    class Key {
        /**
         * 秘钥id
         */
        @NonNull
        private String id;
        /**
         * 秘钥
         */
        @NonNull
        private String key;

        /**
         * 过期时间
         */
        @NonNull
        private Date expiryTime;
    }
}
