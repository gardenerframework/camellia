package io.gardenerframework.camellia.authentication.server.client.schema.response;

import io.gardenerframework.fragrans.data.trait.application.ApplicationTraits;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
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
