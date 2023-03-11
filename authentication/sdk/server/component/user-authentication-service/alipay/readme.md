# AlipayUserAuthenticationService

完成与支付宝扫码登录的对接

```java

@AuthenticationType("alipay")
@AlipayUserAuthenticationServiceComponent
public class AlipayUserAuthenticationService extends OAuth2BaseUserAuthenticationService {


    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull String authorizationCode) throws Exception {
        //初始化阿里客户端
        initAlipayClientFactory();
        Client client = Factory.getClient(Client.class);
        AlipaySystemOauthTokenResponse token = client.getToken(authorizationCode);
        return AlipayOpenIdPrincipal.builder().name(token.getUserId()).build();
    }

    private boolean signUpdated() {
        String sign = String.format("%s:%s:%s:%s",
                option.getAppId(),
                option.getPrivateKey(),
                option.getEncryptKey(),
                option.getAliPublicKey()
        );
        if (alipayOptionSign.equals(sign)) {
            return false;
        } else {
            alipayOptionSign = sign;
            return true;
        }
    }


    private synchronized void initAlipayClientFactory() {
        if (signUpdated()) {
            Config config = new Config();
            config.protocol = "https";
            config.gatewayHost = "openapi.alipay.com";
            config.signType = "RSA2";
            config.appId = option.getAppId();
            config.merchantPrivateKey = option.getPrivateKey();
            config.encryptKey = option.getEncryptKey();
            config.alipayPublicKey = option.getAliPublicKey();
            Factory.setOptions(config);
        }
    }
}
```

基于阿里支付宝的sdk获取Client然后调用接口获取token

# AlipayUserAuthenticationServiceOption

```java
public class AlipayUserAuthenticationServiceOption {
    /**
     * 应用id
     * <p>
     * 只读
     */
    @NotBlank
    private String appId;
    /**
     * 应用私钥(不对外展示)
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String privateKey;
    /**
     * 商家的aes加密密钥
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String encryptKey;
    /**
     * 阿里的公钥
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String aliPublicKey;
}

```

选项要求给定支付宝对接所需的应用id、私钥、公钥以及加密秘钥