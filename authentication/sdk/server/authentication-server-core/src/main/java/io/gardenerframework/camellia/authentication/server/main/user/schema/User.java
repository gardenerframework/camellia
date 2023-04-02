package io.gardenerframework.camellia.authentication.server.main.user.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.Subject;
import io.gardenerframework.fragrans.data.trait.account.AccountTraits;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * 读取出的用户
 * <p>
 * 用户是一种{@link Subject}
 * <p>
 * 这里的用户是仅限认证服务器内部使用的
 * <p>
 * 资源服务器想要获取用户的详细信息应当调用账户管理接口
 * <p>
 * 当前认证服务器原则上只提供账户id
 *
 * @author ZhangHan
 * @date 2022/4/18 11:25
 */

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class User extends Subject implements
        GenericTraits.LiteralTraits.Name,
        AccountTraits.VisualTraits.Avatar {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 任何形式的展示名称
     * <p>
     * 昵称，姓名随便
     */
    @NonNull
    @Builder.Default
    private String name = "";
    /**
     * 任何形式的显示图标
     */
    @Nullable
    private String avatar;
}
