package com.jdcloud.gardener.camellia.authorization.authentication.main;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Base64PasswordEncryptionService implements PasswordEncryptionService {

    /**
     * 执行解密
     *
     * @param key      加密秘钥
     * @param password 密码字节流
     * @return 原文
     * @throws Exception 加解密问题
     */
    protected abstract String decryptInternally(String key, byte[] password) throws Exception;

    @Override
    public String decrypt(String key, String password) throws Exception {
        return decryptInternally(key, Base64.getDecoder().decode(password.getBytes(StandardCharsets.UTF_8)));
    }
}
