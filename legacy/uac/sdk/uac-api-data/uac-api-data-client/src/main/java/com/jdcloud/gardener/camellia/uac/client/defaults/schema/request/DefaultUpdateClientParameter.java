package com.jdcloud.gardener.camellia.uac.client.defaults.schema.request;

import com.jdcloud.gardener.camellia.uac.client.schema.request.UpdateClientParameterTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/14 16:59
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultUpdateClientParameter extends UpdateClientParameterTemplate {
    public DefaultUpdateClientParameter(@NotBlank String name, String description) {
        super(name, description);
    }
}
