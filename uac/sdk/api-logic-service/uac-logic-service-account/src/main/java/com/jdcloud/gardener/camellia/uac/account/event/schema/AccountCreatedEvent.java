package com.jdcloud.gardener.camellia.uac.account.event.schema;

import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.fragrans.event.standard.schema.CreatedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/9/22 13:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountCreatedEvent implements CreatedEvent<AccountEntityTemplate> {
    /**
     * 创建的账户
     * <p>
     * 密码已经被隐藏
     */
    private AccountEntityTemplate object;
    /**
     * 指示密码是否是生成的而不是用户自己输入的
     */
    private boolean passwordGenerated;
    /**
     * 生成的密码
     * <p>
     * 这个因为不是用户输入的，可能要通过各种各样的手段发送给用户
     */
    @Nullable
    private String generatedPassword;
}
