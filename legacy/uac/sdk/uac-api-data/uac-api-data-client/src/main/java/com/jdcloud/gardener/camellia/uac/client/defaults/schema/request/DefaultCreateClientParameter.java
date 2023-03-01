package com.jdcloud.gardener.camellia.uac.client.defaults.schema.request;

import com.jdcloud.gardener.camellia.uac.client.schema.request.CreateClientParameterTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/14 16:58
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultCreateClientParameter extends CreateClientParameterTemplate {
    public DefaultCreateClientParameter(@NotBlank String name, String description, String captchaToken, String challengeId, String response) {
        super(name, description, captchaToken, challengeId, response);
    }
}
