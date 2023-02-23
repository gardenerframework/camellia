package com.jdcloud.gardener.camellia.uac.application.event.schema;

import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.event.standard.schema.ChangedEvent;
import com.jdcloud.gardener.fragrans.event.standard.schema.FieldChangedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 账户某个状态变更事件
 *
 * @author zhanghan30
 * @date 2022/9/22 13:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationStatusChangedEvent implements ChangedEvent<Boolean>, FieldChangedEvent<ApplicationEntityTemplate>, GenericTraits.Id<String> {
    private String id;
    private Boolean before;
    private Boolean after;
    private String field;
}
