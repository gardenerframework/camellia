package com.jdcloud.gardener.camellia.uac.client.event.schema;

import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.event.standard.schema.ChangedEvent;
import com.jdcloud.gardener.fragrans.event.standard.schema.FieldChangedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/11/15 13:55
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientSecurityFieldChangedEvent<F> implements ChangedEvent<F>, FieldChangedEvent<ClientEntityTemplate>, GenericTraits.Id<String> {
    private String id;
    private F before;
    private F after;
    private String field;
}
