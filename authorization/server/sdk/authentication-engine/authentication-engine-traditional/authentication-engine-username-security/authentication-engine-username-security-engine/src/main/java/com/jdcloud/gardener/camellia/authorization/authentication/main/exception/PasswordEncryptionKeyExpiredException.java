package com.jdcloud.gardener.camellia.authorization.authentication.main.exception;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.BadAuthenticationRequestException;

public class PasswordEncryptionKeyExpiredException extends BadAuthenticationRequestException {
    public PasswordEncryptionKeyExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PasswordEncryptionKeyExpiredException(String msg) {
        super(msg);
    }
}
