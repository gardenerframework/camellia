package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/3/14 12:21
 */
@NoArgsConstructor
@Getter
@Setter
@ApiOption(readonly = false)
public class LdapUserServiceOption {
    /**
     * ldap服务器地址
     */
    @NotBlank
    private String url;
    /**
     * ldap用户名
     */
    @NotBlank
    private String userDn;
    /**
     * ldap密码
     */
    @NotBlank
    private String password;
    /**
     * 分区后缀，一般都是dc=xxx,dc=xxx的格式
     */
    @NotBlank
    private String baseDomainDn;
}
