package com.jdcloud.gardener.camellia.uac.client.defaults.schema.response;

import com.jdcloud.gardener.camellia.uac.client.schema.response.ClientAppearanceTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/14 16:59
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultClientAppearance extends ClientAppearanceTemplate {
    public DefaultClientAppearance(Collection<String> grantType, String id, String name, String description, boolean enabled, boolean requireConsent, Collection<String> redirectUri, Collection<String> scope, String creator) {
        super(grantType, id, name, description, enabled, requireConsent, redirectUri, scope, creator);
    }
}
