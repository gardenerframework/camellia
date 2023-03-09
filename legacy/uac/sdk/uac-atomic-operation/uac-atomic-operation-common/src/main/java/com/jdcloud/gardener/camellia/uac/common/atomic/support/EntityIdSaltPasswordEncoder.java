package com.jdcloud.gardener.camellia.uac.common.atomic.support;

import com.google.common.primitives.Bytes;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import org.bouncycastle.crypto.generators.BCrypt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * 使用账户id作为salt的密码编码器
 *
 * @author zhanghan30
 * @date 2022/9/21 14:27
 */
public class EntityIdSaltPasswordEncoder<A extends BasicEntity<String>> implements PasswordEncoder<A> {
    @Override
    public String encode(A entity, String password) {
        //以账户id作为salt，原因是这个id是随机生成的
        byte[] salt = Arrays.copyOfRange(
                Bytes.ensureCapacity(
                        entity.getId().getBytes(StandardCharsets.UTF_8),
                        16, 0
                ),
                0,
                16
        );
        return Base64.getEncoder()
                .encodeToString(
                        BCrypt.generate(
                                password.getBytes(StandardCharsets.UTF_8),
                                salt,
                                10
                        )
                );
    }
}
