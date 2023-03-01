package com.jdcloud.gardener.camellia.uac.application.defautls.schema.request;

import com.jdcloud.gardener.camellia.uac.application.schema.request.CreateApplicationParameterTemplate;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 16:58
 */
@NoArgsConstructor
@SuperBuilder
public class DefaultCreateApplicationParameter extends CreateApplicationParameterTemplate {
    public DefaultCreateApplicationParameter(@NotBlank String name, String description, String homepageUrl, String logo, String captchaToken, String challengeId, String response) {
        super(name, description, homepageUrl, logo, captchaToken, challengeId, response);
    }
}
