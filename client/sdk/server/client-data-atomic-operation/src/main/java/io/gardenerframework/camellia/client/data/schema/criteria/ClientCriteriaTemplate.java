package io.gardenerframework.camellia.client.data.schema.criteria;

import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author chris
 * @date 2023/10/23
 */
@Setter
@Getter
@SuperBuilder
public class ClientCriteriaTemplate implements
        GenericTraits.LiteralTraits.Name {
    /**
     * 应用的名称，判等而不是包含
     */
    private String name;
}