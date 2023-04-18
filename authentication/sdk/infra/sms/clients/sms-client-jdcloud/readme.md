# 简介

JdCloudSmsClient实现了京东云的短信客户端对接，可用来发布短信动态密码。

# JdCloudSmsClientSecurityOption

JdCloudSmsClientSecurityOption是客户端的配置类，主要保存ak/sk

```java
public class JdCloudSmsClientSecurityOption {
    /**
     * key id
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String accessKeyId;
    /**
     * key
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String accessKey;
}
```

# SmsVerificationCodeTemplateProvider

JdCloudSmsClient.SmsVerificationCodeTemplateProvider要求开发人员给定一个短信模板id和签名id，这些都是京东云短信所需的

```java
    public interface SmsVerificationCodeTemplateProvider {
    /**
     * 返回签名id
     *
     * @param client   应用id
     * @param scenario 场景
     * @return 签名id
     */
    String getSignId(@Nullable RequestingClient client, Class<? extends Scenario> scenario);

    /**
     * 返回模板id
     *
     * @param client   应用id
     * @param scenario 场景
     * @return 模板id
     */
    String getTemplateId(@Nullable RequestingClient client, Class<? extends Scenario> scenario);
}
```

