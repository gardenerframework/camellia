package com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/18 18:12
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveAccountsParameter implements AccountTraits.AccountRelations<String> {
    /**
     * 要删除的账户清单
     */
    @NotEmpty
    @NotNull
    @Valid
    private Collection<@NotBlank String> accountIds;
}
