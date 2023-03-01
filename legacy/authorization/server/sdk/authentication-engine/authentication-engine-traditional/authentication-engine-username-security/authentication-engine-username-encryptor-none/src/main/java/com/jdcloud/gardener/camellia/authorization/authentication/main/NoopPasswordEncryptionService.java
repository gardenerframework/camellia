package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.EncryptionAlgorithm;

@EncryptionAlgorithm("none")
public class NoopPasswordEncryptionService implements PasswordEncryptionService {
    @Override
    public String generateKey() throws Exception {
        return "";
    }

    @Override
    public String decrypt(String key, String password) throws Exception {
        return password;
    }
}
