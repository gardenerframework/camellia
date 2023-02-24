package io.gardenerframework.camellia.authentication.server.main.schema.subject.principal;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 基础登录名
 * <p>
 * 其它的子类分别代表一种登录形式
 * <p>
 * 比如用户名
 * <p>
 * 比如手机号
 * <p>
 * 比如邮箱
 * <p>
 * 一个用户(也称主体)，允许有多种不同的登录名
 *
 * @author ZhangHan
 * @date 2022/1/1 0:43
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
public abstract class Principal implements
        java.security.Principal,
        Serializable,
        GenericTraits.LiteralTraits.Name {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 名字
     */
    @NonNull
    private String name;
}
