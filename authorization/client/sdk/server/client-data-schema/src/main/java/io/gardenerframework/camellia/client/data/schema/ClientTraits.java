package io.gardenerframework.camellia.client.data.schema;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;

/**
 * @author chris
 * @date 2023/10/23
 */
public interface ClientTraits {
    @Trait
    class GrantType {
        /**
         * 允许使用的授权
         */
        private Collection<String> grantType;
    }

    @Trait
    class RedirectUrl {
        /**
         * 重定向url清单
         */
        private Collection<String> redirectUrl;
    }

    @Trait
    class Scope {
        /**
         * 允许访问的授权范围
         */
        private Collection<String> scope;
    }

    @Trait
    class AccessTokenTtl {
        /**
         * 授权令牌的ttl，以秒为单位
         */
        private int accessTokenTtl;
    }

    @Trait
    class RefreshTokenTtl {
        /**
         * 刷新令牌ttl，以秒为单位
         */
        private int refreshTokenTtl;
    }

    @Trait
    class RequireConsent {
        /**
         * 是否要求用户批准授权请求的标记
         * <p>
         * 当且仅当在"authorization_code"模式下有效
         */
        private boolean requireConsent;
    }
}
