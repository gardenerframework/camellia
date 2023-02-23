package com.jdcloud.gardener.camellia.uac.application.defautls.schema.response;

import com.jdcloud.gardener.camellia.uac.application.schema.response.ApplicationAppearanceTemplate;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/11/8 17:07
 */
@NoArgsConstructor
@SuperBuilder
public class DefaultApplicationAppearance extends ApplicationAppearanceTemplate {
    public DefaultApplicationAppearance(String id, String name, String description, String homepageUrl, String logo, boolean enabled, String creator) {
        super(id, name, description, homepageUrl, logo, enabled, creator);
    }
}
