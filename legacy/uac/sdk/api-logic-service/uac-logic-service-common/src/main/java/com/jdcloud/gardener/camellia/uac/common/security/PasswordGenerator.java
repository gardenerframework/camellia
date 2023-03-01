package com.jdcloud.gardener.camellia.uac.common.security;

/**
 * 要求生成一个密码
 *
 * @author zhanghan30
 * @date 2022/9/22 13:02
 */
@FunctionalInterface
public interface PasswordGenerator<E> {
    /**
     * 生成一个随机密码
     *
     * @param entity 当前正在要求生成密码的实体
     * @return 密码
     */
    String generate(E entity);
}
