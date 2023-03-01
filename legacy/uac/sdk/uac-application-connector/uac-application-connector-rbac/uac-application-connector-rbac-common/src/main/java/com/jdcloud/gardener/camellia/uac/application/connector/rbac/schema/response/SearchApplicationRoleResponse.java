package com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.response;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author zhanghan30
 * @date 2022/11/17 19:19
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SearchApplicationRoleResponse implements
        ApiStandardDataTraits.Contents<RoleAppearance>,
        ApiStandardDataTraits.TotalNumber {
    /**
     * 角色清单
     */
    private Collection<RoleAppearance> contents;
    /**
     * 查询结果总大小
     */
    private Long total;

    @Override
    public Collection<RoleAppearance> getContents() {
        return contents == null ? new LinkedList<>() : contents;
    }

    @Override
    public Long getTotal() {
        return total == null ? 0 : total;
    }
}
