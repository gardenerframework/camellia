package com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/17 18:51
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SearchBindRolesParameter implements
        AccountTraits.AccountRelation<String> {
    /**
     * 按账户id查询
     */
    @NotBlank
    private String accountId;

}
