package com.jdcloud.gardener.camellia.uac.account.event.schema;

import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

/**
 * 账户某个状态变更事件
 *
 * @author zhanghan30
 * @date 2022/9/22 13:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountPasswordChangedEvent implements GenericTraits.Id<String> {
    /**
     * 那个账户的
     */
    private String id;
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
