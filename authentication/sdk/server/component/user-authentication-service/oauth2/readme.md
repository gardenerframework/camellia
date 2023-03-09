# 基于oauth2标准的第三方IAM对接所使用的核心数据和接口

## OAuth2AuthorizationCodeParameter

```java

@Getter
@Setter
public class OAuth2AuthorizationCodeParameter extends AuthenticationRequestParameter {
    /**
     * 授权码，用于sns软件交换用户信息
     */
    @NotBlank
    private String code;
    /**
     * sns网站回传的state
     * <p>
     * 这是一个必须使用的参数，用于防止CSRF攻击
     */
    @NotBlank
    private String state;

    public OAuth2AuthorizationCodeParameter(HttpServletRequest request) {
        super(request);
        this.code = request.getParameter("code");
        this.state = request.getParameter("state");
    }
}
```

其中有2个oauth2标准第三方对接的属性

* code: 由第三方iam给出的授权码
* state: 由认证服务器生成的state，第三方负责将这个state传送回认证服务器

# OAuth2BaseUserAuthenticationService

OAuth2BaseUserAuthenticationService为基于oauth2进行联合登录的第三方iam提供了用户登录名转换的功能， 它主要调用"getPrincipal"
并基于OAuth2AuthorizationCodeParameter中的授权码去第三方iam读取用户并转为一个有待UserService读取的登录名，比如微信openid。
如果UserService能读取这个登录名，则认证就成功了，否则视作用户不存在。

# state的生成和保存

首先"POST /api/authentication/state/oauth2/{type}"接口提供state生成，其中类型就是最终实现的认证服务的`@AuthenticationType`注解的值。
从接口controller的实现上可知其实调用的是OAuth2BaseUserAuthenticationService.createState方法生成state，而OAuth2BaseUserAuthenticationService在创建state时使用OAuth2StateStore来保存和验证state。
默认情况下，使用的是CachedOAuth2StateStore