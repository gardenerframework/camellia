package io.gardenerframework.camellia.authentication.server.user.schema.response;

import io.gardenerframework.fragrans.data.trait.account.AccountTraits;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAppearance implements
        GenericTraits.IdentifierTraits.Id<String>,
        GenericTraits.LiteralTraits.Name,
        AccountTraits.VisualTraits.Avatar {
    private String id;
    /**
     * 任何形式的展示名称
     * <p>
     * 昵称，姓名随便
     */
    @Nullable
    private String name;
    /**
     * 任何形式的显示图标
     */
    @Nullable
    private String avatar;
}
