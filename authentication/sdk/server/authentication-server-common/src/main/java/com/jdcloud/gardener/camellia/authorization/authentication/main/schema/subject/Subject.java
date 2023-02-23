package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.subject;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.security.core.CredentialsContainer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Subject 是 Principal的集合，代表一个访问系统的真实物体
 * <p>
 * 这个物体一般是个人，也就是一个用户
 * <p>
 * 不过从安全术语的角度看，访问者并不只是人
 *
 * @author zhanghan30
 * @date 2022/5/12 1:12 下午
 * @see User
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class Subject implements CredentialsContainer, Serializable,
        GenericTraits.IdentifierTraits.Id<String>,
        GenericTraits.StatusTraits.LockFlag,
        GenericTraits.StatusTraits.EnableFlag{
    private static final long serialVersionUID = Version.current;
    /**
     * id
     */
    private String id;
    /**
     * 登录凭据
     * <p>
     * 这个玩意有时候无法获得，比如ldap的登录就不会告诉你用户的密码，只是校验密码
     * <p>
     * 因此可以将登录凭据写成用户输入的那个
     */
    @Nullable
    private transient BasicCredentials credentials;
    /**
     * 用户的所有可用登录名
     */
    private Collection<BasicPrincipal> principals;
    /**
     * 被锁定
     */
    private boolean locked;
    /**
     * 激活状态
     */
    private boolean enabled;
    /**
     * 密码过期事件
     */
    @Nullable
    private Date credentialsExpiryDate;
    /**
     * 当前subject的过期事件
     */
    @Nullable
    private Date subjectExpiryDate;

    //其余的属性实现类自己添加

    /**
     * 由引擎负责调用，在不需要的时候擦除密码
     */
    @Override
    public final void eraseCredentials() {
        this.credentials = null;
    }
}
