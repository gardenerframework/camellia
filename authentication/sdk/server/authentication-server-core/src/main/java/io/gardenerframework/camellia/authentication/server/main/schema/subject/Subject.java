package io.gardenerframework.camellia.authentication.server.main.schema.subject;

import io.gardenerframework.camellia.authentication.server.common.Version;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.Credentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

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
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class Subject implements Serializable,
        GenericTraits.IdentifierTraits.Id<String>,
        GenericTraits.StatusTraits.LockFlag,
        GenericTraits.StatusTraits.EnableFlag {
    private static final long serialVersionUID = Version.current;
    /**
     * id
     */
    @NonNull
    private String id;
    /**
     * 登录凭据
     * <p>
     * 这个玩意有时候无法获得，比如ldap的登录就不会告诉你用户的密码，只是校验密码
     * <p>
     * 因此可以将登录凭据写成用户输入的那个
     */
    @Nullable
    private transient Credentials credentials;
    /**
     * 用户的所有可用登录名
     */
    @Singular
    @NonNull
    private Collection<Principal> principals;
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
     * 当前subject的过期时间
     */
    @Nullable
    private Date subjectExpiryDate;

    //其余的属性实现类自己添加

    /**
     * 由引擎负责调用，在不需要的时候擦除密码
     */
    public final void eraseCredentials() {
        this.credentials = null;
    }
}
