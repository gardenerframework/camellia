package com.jdcloud.gardener.camellia.uac.client.schema.request;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;


/**
 * 认证用户账户的请求参数
 * <p>
 * 给了常用的用户名、手机号，邮箱 + 密码的形式
 *
 * @author zhanghan30
 * @date 2022/8/13 11:06 上午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthenticateClientParameter implements
        ApiStandardDataTraits.Id<String>,
        AccountTraits.Credentials {
    /**
     * 用户名
     */
    @NotBlank
    private String id;
    /**
     * 认证所需的密码
     * <p>
     * 但如果是短信登录，人脸登录等就没有这个参数了
     */
    @NotBlank
    private String password;
}
