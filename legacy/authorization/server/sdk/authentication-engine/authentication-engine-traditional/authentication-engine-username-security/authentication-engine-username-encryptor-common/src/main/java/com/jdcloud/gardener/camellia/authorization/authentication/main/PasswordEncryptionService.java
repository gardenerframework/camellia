package com.jdcloud.gardener.camellia.authorization.authentication.main;

public interface PasswordEncryptionService {
    /**
     * 生成一个动态的对称加密秘钥
     *
     * @return 加密key
     * @throws Exception 生成过程中遇到了问题
     */
    String generateKey() throws Exception;

    /**
     * 执行解密
     *
     * @param key key
     * @return 解密结果
     * @throws Exception 加密过程中的问题
     */
    String decrypt(String key, String password) throws Exception;
}
