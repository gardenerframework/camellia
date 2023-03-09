package com.jdcloud.gardener.camellia.uac.application.event.schema;

import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.fragrans.event.standard.schema.CreatedEvent;
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
public class ApplicationCreatedEvent implements CreatedEvent<ApplicationEntityTemplate> {
    /**
     * 创建的应用
     */
    private ApplicationEntityTemplate object;
}
