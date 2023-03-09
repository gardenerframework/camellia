package com.jdcloud.gardener.camellia.uac.common.atomic;

import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;

/**
 * @author zhanghan30
 * @date 2022/9/21 14:26
 */
@FunctionalInterface
public interface PasswordEncoder<E extends BasicEntity<String>> {
    /**
     * 执行编码
     *
     * @param entity   要编码的记录
     * @param password 密码
     * @return 编码后的密码
     */
    String encode(E entity, String password);
}
