# WeChatUserAuthenticationService

```java
public class WeChatUserAuthenticationService extends OAuth2BaseUserAuthenticationService
        implements InitializingBean {

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull String authorizationCode) throws Exception {
        Map<String, Object> response = restTemplate.getForObject(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appId}" +
                        "&secret={appSecret}" +
                        "&code={code}" +
                        "&grant_type=authorization_code",
                Map.class,
                option.getAppId(),
                option.getAppSecret(),
                authorizationCode
        );
        if (response == null) {
            throw new InternalAuthenticationServiceException("no response");
        }
        if (response.get("errcode") != null) {
            throw new InternalAuthenticationServiceException("error = " + response.get("errmsg"));
        }
        return WeChatOpenIdPrincipal.builder().name(String.valueOf(response.get("openid"))).build();
    }
}
```

根据微信接口的说明文档获取openid

# WeChatUserAuthenticationServiceOption

```java
public class WeChatUserAuthenticationServiceOption {
    /**
     * app id
     */
    @NotBlank
    private String appId;
    /**
     * app密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String appSecret;
}

```

设置选项要求给定微信的应用id和密码