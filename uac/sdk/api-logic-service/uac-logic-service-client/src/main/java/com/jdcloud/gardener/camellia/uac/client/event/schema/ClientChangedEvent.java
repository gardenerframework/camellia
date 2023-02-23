package com.jdcloud.gardener.camellia.uac.client.event.schema;

import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.fragrans.event.standard.schema.ChangedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/9/22 13:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientChangedEvent implements ChangedEvent<ClientEntityTemplate> {
    private ClientEntityTemplate before;
    private ClientEntityTemplate after;
}
