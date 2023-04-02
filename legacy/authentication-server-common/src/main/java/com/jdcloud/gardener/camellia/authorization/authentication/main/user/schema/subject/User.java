package com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.subject.Subject;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;

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
@LogTarget("用户")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class User extends Subject implements
        GenericTraits.LiteralTraits.Name,
        AccountTraits.VisualTraits.Avatar {
    private static final long serialVersionUID = Version.current;
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

    public User(String id, @Nullable BasicCredentials credentials, Collection<BasicPrincipal> principals, boolean locked, boolean enabled, @Nullable Date credentialsExpiryDate, @Nullable Date subjectExpiryDate, @Nullable String name, @Nullable String avatar) {
        super(id, credentials, principals, locked, enabled, credentialsExpiryDate, subjectExpiryDate);
        this.name = name;
        this.avatar = avatar;
    }
}
