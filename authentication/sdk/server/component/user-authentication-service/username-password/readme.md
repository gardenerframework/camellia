# 用户名密码的认证

## UsernamePasswordAuthenticationParameter

```java
public class UsernamePasswordAuthenticationParameter extends AuthenticationRequestParameter implements
        AccountTraits.IdentifierTraits.Username,
        SecurityTraits.SecretTraits.Password,
        SecurityTraits.TuringTraits.CaptchaToken {
    /**
     * 用户名
     */
    @NotBlank
    private String username;
    /**
     * 密码
     */
    @NotBlank
    private String password;
    /**
     * 登录名类型
     */
    @Nullable
    private String principalType;
    /**
     * 验证码(滑块什么得)
     */
    @Nullable
    private String captchaToken;


    public UsernamePasswordAuthenticationParameter(HttpServletRequest request) {
        super(request);
        this.username = request.getParameter("username");
        this.password = request.getParameter("password");
        this.principalType = request.getParameter("principalType");
        this.captchaToken = request.getParameter("captchaToken");
    }
}
```

UsernamePasswordAuthenticationParameter是用户名密码的认证参，其中密码在提交时还有可能是被加密的，需要进行解密。 对于用户名通常会有多种形式比如支持手机号、邮箱等，因此"principalType"
可以显式的表达用户名的类型，开发人员也可以基于字符串的特征进行推断

## UsernameResolver

```java
public interface UsernameResolver {
    /**
     * 执行登录名转换
     *
     * @param username      输入的用户名
     * @param principalType 登录名类型
     * @return 转换好的
     * @throws AuthenticationException 如果觉得无法执行转换，抛出异常中断认证过程
     */
    Principal resolve(@NonNull String username, @Nullable String principalType) throws AuthenticationException;
}
```

用户名密码认证服务会使用UsernameResolver去分析用户名字符串的特征，并结合可能出现的登录名类型的参数最终给出登录名的实例

