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
public class UsernamePasswordAuthenticationService implements UserAuthenticationService {
    private final Validator validator;
    @NonNull
    private final UsernameResolver resolver;
    @NonNull
    private final Collection<@NonNull BiConsumer<@NonNull HttpServletRequest, @NonNull UsernamePasswordAuthenticationParameter>> processors;

    @Override
    public UserAuthenticationRequestToken convert(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        UsernamePasswordAuthenticationParameter authenticationParameter = new UsernamePasswordAuthenticationParameter(request);
        authenticationParameter.validate(validator);
        if (!CollectionUtils.isEmpty(processors)) {
            processors.forEach(
                    processor -> processor.accept(request, authenticationParameter)
            );
            //消费完要重新验证
            authenticationParameter.validate(validator);
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

用户名密码的传输可以加密，开发人员需要的时加载EncryptionService的实现类，并加载
"username-password-authentication-encryption"组件。它生成一个BiConsumer用来消费请求参数，并调用EncryptionService基于指定的keyId进行解密

```java

@Configuration
public class PasswordEncryptionServiceConfiguration {
    @Bean
    public BiConsumer<
            HttpServletRequest,
            UsernamePasswordAuthenticationParameter> passwordDecryptHelper(
            Validator validator,
            //fix @ConditionalOnBean就是坑逼
            EncryptionService service
    ) {
        return (request, usernamePasswordAuthenticationParameter) -> {
            //取id
            PasswordEncryptionKeyIdParameter passwordEncryptionKeyIdParameter = new PasswordEncryptionKeyIdParameter(request);
            //执行验证
            passwordEncryptionKeyIdParameter.validate(validator);
            //把解密后的密码放进去
            try {
                usernamePasswordAuthenticationParameter.setPassword(
                        new String(
                                service.decrypt(
                                        passwordEncryptionKeyIdParameter.getPasswordEncryptionKeyId(),
                                        Base64.getDecoder().decode(usernamePasswordAuthenticationParameter.getPassword()))
                        ));
            } catch (Exception e) {
                throw new NestedAuthenticationException(e);
            }

        };
    }

    private static class PasswordEncryptionKeyIdParameter extends AuthenticationRequestParameter {
        @NotBlank
        @Getter
        private final String passwordEncryptionKeyId;

        protected PasswordEncryptionKeyIdParameter(HttpServletRequest request) {
            super(request);
            passwordEncryptionKeyId = request.getParameter("passwordEncryptionKeyId");

        }
    }
}
```

从体验上，提交用户名密码校验时，首先需要获取加密密钥对密码进行加密以及base64编码，然后提交的密码是加密后的密文，并附上`passwordEncryptionKeyId=xxx`
作为额外参数