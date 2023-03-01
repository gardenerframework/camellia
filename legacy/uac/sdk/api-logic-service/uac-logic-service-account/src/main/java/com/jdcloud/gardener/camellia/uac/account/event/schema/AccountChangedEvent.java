package com.jdcloud.gardener.camellia.uac.account.event.schema;

import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
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
public class AccountChangedEvent implements ChangedEvent<AccountEntityTemplate> {
    private AccountEntityTemplate before;
    private AccountEntityTemplate after;
}
