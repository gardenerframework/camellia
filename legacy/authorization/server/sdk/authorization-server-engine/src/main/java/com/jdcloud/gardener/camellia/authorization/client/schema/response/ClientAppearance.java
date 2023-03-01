package com.jdcloud.gardener.camellia.authorization.client.schema.response;

import com.jdcloud.gardener.fragrans.data.trait.application.ApplicationTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientAppearance implements
        GenericTraits.LiteralTraits.Name,
        GenericTraits.LiteralTraits.Description,
        ApplicationTraits.VisualTraits.Logo {
    private String clientId;
    private String name;
    private String description;
    private String logo;
}
