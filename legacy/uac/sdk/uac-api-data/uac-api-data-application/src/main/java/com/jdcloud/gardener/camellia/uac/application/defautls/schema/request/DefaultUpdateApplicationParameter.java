package com.jdcloud.gardener.camellia.uac.application.defautls.schema.request;

import com.jdcloud.gardener.camellia.uac.application.schema.request.UpdateApplicationParameterTemplate;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 17:00
 */
@NoArgsConstructor
@SuperBuilder
public class DefaultUpdateApplicationParameter extends UpdateApplicationParameterTemplate {
    public DefaultUpdateApplicationParameter(@NotBlank String name, String description, String homepageUrl, String logo, String captchaToken, String challengeId, String response) {
        super(name, description, homepageUrl, logo, captchaToken, challengeId, response);
    }
}
