package com.jdcloud.gardener.camellia.authorization.authentication.main.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EncryptionAlgorithm {
    /**
     * 加密算法类型
     *
     * @return 类型
     */
    String value();
}
