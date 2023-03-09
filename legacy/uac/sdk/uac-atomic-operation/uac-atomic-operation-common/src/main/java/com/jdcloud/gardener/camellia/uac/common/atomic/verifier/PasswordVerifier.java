package com.jdcloud.gardener.camellia.uac.common.atomic.verifier;

import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.atomic.support.EntityIdSaltPasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.exception.client.IncorrectPasswordException;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordChecker;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * 检查账户密码是否正确
 * <p>
 * 主要用于登录验证操作
 *
 * @author zhanghan30
 * @date 2022/9/21 02:23
 */
@SuperBuilder
public class PasswordVerifier<E extends BasicEntity<String> & AccountTraits.Credentials> implements RecordChecker<E> {
    /**
     * 输入的密码
     */
    private String password;
    /**
     * 密码编码器
     * <p>
     * 如果没有设置，则默认使用{@link EntityIdSaltPasswordEncoder}
     */
    private PasswordEncoder<? super E> passwordEncoder;

    @Override
    public <T extends E> void check(@Nullable T record) {
        if (!Objects.equals(passwordEncoder.encode(Objects.requireNonNull(record), password), Objects.requireNonNull(record).getPassword())) {
            throw new IncorrectPasswordException();
        }
    }
}
