package com.jdcloud.gardener.camellia.uac.application.defautls.schema.request;

import com.jdcloud.gardener.camellia.uac.application.schema.request.SearchApplicationCriteriaParameterTemplate;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/8 16:59
 */
@NoArgsConstructor
@SuperBuilder
public class DefaultSearchApplicationCriteriaParameter extends SearchApplicationCriteriaParameterTemplate {
    public DefaultSearchApplicationCriteriaParameter(Collection<String> must, Collection<String> should, String id, String name) {
        super(must, should, id, name);
    }
}
