# LdapUserServiceOption

```java
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
    /**
     * 读取用户类的那个属性
     */
    @NotBlank
    private String principalAttribute = "sAMAccount";
}
```

包含了ldap服务器的url、分配给认证服务的用户名和密码，查找登录用户的根节点dn以及用户名的属性名称，其中"sAMAccount"
是ad域控服务器的属性名。当对接了openLdap等其它ldap服务时，需要明确服务支持的用户登录属性名称。

# AuthenticatedLdapEntryContextMapper

```java
public interface AuthenticatedLdapEntryContextMapper<T> {
    /**
     * Perform some LDAP operation on the supplied authenticated
     * <code>DirContext</code> instance. The target context will be
     * automatically closed.
     *
     * @param ctx the <code>DirContext</code> instance to perform an operation
     * on.
     * @param ldapEntryIdentification the identification of the LDAP entry used
     * to authenticate the supplied <code>DirContext</code>.
     * @return the result of the operation, if any.
     */
    T mapWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification);
}
```

是spring ldap代码中对读取出的用户进行属性映射的接口，LdapUserService要求开发人员生成这个bean并完成向User类的转换