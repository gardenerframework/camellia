package com.jdcloud.gardener.camellia.uac.client.defaults.schema.request;

import com.jdcloud.gardener.camellia.uac.client.schema.request.SearchClientCriteriaParameterTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/14 16:58
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultSearchClientCriteriaParameter extends SearchClientCriteriaParameterTemplate {
    public DefaultSearchClientCriteriaParameter(Collection<String> must, Collection<String> should, String id, String name) {
        super(must, should, id, name);
    }
}
