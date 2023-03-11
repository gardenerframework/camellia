# 用户名密码认证的关键数据和接口

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

UsernamePasswordAuthenticationParameter是用户名密码的认证参，其中密码在提交时还有可能是被加密的，需要进行解密。
对于用户名通常会有多种形式比如支持手机号、邮箱等，因此"principalType"
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

## UsernamePasswordAuthenticationService

组件提供了已经实现的UsernamePasswordAuthenticationService

```java
public class UsernamePasswordAuthenticationService extends AbstractUserAuthenticationService<UsernamePasswordAuthenticationParameter> {
    @NonNull
    private final UsernameResolver resolver;
    @NonNull
    private final Collection<@NonNull Consumer<@NonNull UsernamePasswordAuthenticationParameter>> processors;

    public UsernamePasswordAuthenticationService(@NonNull Validator validator, @NonNull UsernameResolver resolver, @NonNull Collection<@NonNull Consumer<@NonNull UsernamePasswordAuthenticationParameter>> processors) {
        super(validator);
        this.resolver = resolver;
        this.processors = processors;
    }

    @Override
    protected UsernamePasswordAuthenticationParameter getAuthenticationParameter(@NonNull HttpServletRequest request) {
        return new UsernamePasswordAuthenticationParameter(request);
    }

    @Override
    protected UserAuthenticationRequestToken doConvert(@NonNull UsernamePasswordAuthenticationParameter authenticationParameter, @Nullable OAuth2RequestingClient client, @NonNull Map<String, Object> context) throws Exception {
        if (!CollectionUtils.isEmpty(processors)) {
            processors.forEach(
                    processor -> processor.accept(authenticationParameter)
            );
            //消费完要重新验证
            authenticationParameter.validate(this.getValidator());
        }
        return new UserAuthenticationRequestToken(
                resolver.resolve(authenticationParameter.getUsername(), authenticationParameter.getPrincipalType()),
                //明确使用密码类型的凭据，要求走authenticate方法
                PasswordCredentials.builder().password(authenticationParameter.getPassword()).build()
        );
    }

    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @Nullable OAuth2RequestingClient client, @NonNull User user, @NonNull Map<String, Object> context) throws AuthenticationException {
        if (!CollectionUtils.isEmpty(user.getCredentials())) {
            if (!user.getCredentials().contains(authenticationRequest.getCredentials())) {
                throw new BadCredentialsException("");
            }
        }
    }
}
```

其在转换参数时允许注入Consumer的bean来消费已经完成转换的参数。消费者可以做以下几个事

* 对已经加密的密码进行解密操作
* 读取UsernamePasswordAuthenticationParameter中的captchaToken，进行验证码核验

# 自动配置

如果没有生成UsernameResolver则会生成一个默认的，这个默认的就是将请求转换为UsernamePrincipal。同时也会生成默认的UsernamePasswordAuthenticationService。
如果开发过程中开发人员自己实现了子类并进行实例化，那么引擎的就不会生成

# 传输加密

```java
public interface PasswordEncryptionService {
    /**
     * 创建加密秘钥
     *
     * @return 加密秘钥
     * @throws Exception 遇到问题
     */
    Key createKey() throws Exception;

    /**
     * 执行密码解密
     *
     * @param id     秘钥id
     * @param cipher 密文
     * @return 解密后的密码
     * @throws Exception 遇到问题
     */
    String decrypt(@NonNull String id, @NonNull String cipher) throws Exception;

    @AllArgsConstructor
    @NoArgsConstructor
    public class Key {
        /**
         * 秘钥id
         */
        @NonNull
        private String id;
        /**
         * 秘钥
         */
        @NonNull
        private String key;
    }
}
```

用户名密码登录过程中可以使用PasswordEncryptionService帮助密码进行加密，RsaPasswordEncryptionService是其中的一个实现