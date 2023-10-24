package io.gardenerframework.camellia.authorization.client.data.schema.entity;

import io.gardenerframework.camellia.client.data.schema.ClientTraits;
import io.gardenerframework.fragrans.data.persistence.template.annotation.DomainObjectTemplate;
import io.gardenerframework.fragrans.data.schema.annotation.SkipInGenericUpdateOperation;
import io.gardenerframework.fragrans.data.schema.entity.BasicOperationTraceableEntity;
import io.gardenerframework.fragrans.data.trait.application.ApplicationTraits;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * 客户端记录模板
 *
 * @author chris
 * @date 2023/10/23
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@DomainObjectTemplate
public class ClientEntityTemplate extends BasicOperationTraceableEntity<String> implements
        GenericTraits.LiteralTraits.Name,
        GenericTraits.LiteralTraits.Description,
        SecurityTraits.SecretTraits.Password,
        ApplicationTraits.VisualTraits.Logo,
        ClientTraits.GrantType,
        ClientTraits.Scope,
        ClientTraits.RedirectUrl,
        ClientTraits.AccessTokenTtl,
        ClientTraits.RefreshTokenTtl,
        ClientTraits.RequireConsent {
    /**
     * 客户端名称
     */
    private String name;
    /**
     * 客户端的描述
     */
    @Nullable
    private String description;
    /**
     * 客户端的一个logo图标
     * <p>
     * 在用户批准页面上可以暂时一个图标
     */
    @Nullable
    private String logo;
    /**
     * 客户端凭据
     */
    @SkipInGenericUpdateOperation
    private String password;
    /**
     * 批准的授权形式
     */
    @SkipInGenericUpdateOperation
    private Collection<String> grantType;
    /**
     * 授权的访问范围
     */
    @SkipInGenericUpdateOperation
    private Collection<String> scope;
    /**
     * 授权的重定向地址
     */
    @SkipInGenericUpdateOperation
    private Collection<String> redirectUrl;
    /**
     * 默认的访问令牌ttl，秒为单位
     */
    @SkipInGenericUpdateOperation
    private int accessTokenTtl;
    /**
     * 默认的刷新令牌ttl，秒为单位
     */
    @SkipInGenericUpdateOperation
    private int refreshTokenTtl;
    /**
     * 客户端是否在authorization_code授权下跳过客户批准环节
     */
    @SkipInGenericUpdateOperation
    private boolean requireConsent;
}
