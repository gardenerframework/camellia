package com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/11/17 18:51
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SearchApplicationRoleParameter implements
        GenericTraits.Name {
    /**
     * 角色的名称(防止角色过多)
     */
    @Nullable
    private String name;
}
