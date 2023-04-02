# 简介

挑战与应答服务定义了mfa的基础，它可以使得开发人员编写代码将各种各样的动态密码校验算法整合起来，比如[sms](..%2Fsms)
目录就定义了短信验证码的挑战应答实现。
不过，这些整合后的代码有以下几个显著问题

* 代码包只能被java语言使用
* 当前组件使用了spring boot的新版本，部分老旧系统不得不强制升级
* 当验证逻辑被修改后，所有引用了jar的工程都要重新打包和发布

等等

因此当前组件就将挑战应答服务进行统一的http服务化呈现，并且由于这种服务主要用于多因子验证过程，因此模块定为mfa，使用者也能很好的理解当前模块的作用。

# 核心接口

## MfaEndpointSkeleton

MfaEndpointSkeleton用于定义mfa服务的接口，包含了列出所有认证器、发送挑战，验证挑战和关闭挑战。

[mfa-server-engine](mfa-server-engine) & [mfa-client](mfa-client)的controller以及feign client都使用了这个类

```java
public interface MfaEndpointSkeleton<C extends Challenge> {
    /**
     * 列出所有支持的验证器名称
     *
     * @return 获取验证器名称
     * @throws Exception 发生的问题
     */
    ListAuthenticatorsResponse listAuthenticators() throws Exception;

    /**
     * 发送挑战
     *
     * @param authenticator 认证器
     * @param request       发送请求
     * @return 挑战
     * @throws Exception 发生的问题
     */
    C sendChallenge(
            @Valid @MfaAuthenticatorSupported String authenticator,
            @Valid SendChallengeRequest request
    ) throws Exception;

    /**
     * 结果验证请求
     *
     * @param authenticator 认证器
     * @param request       结果验证请求
     * @return 是否验证成功
     * @throws Exception 发生的问题
     */
    ResponseVerificationResponse verifyResponse(
            @Valid @MfaAuthenticatorSupported String authenticator,
            @Valid VerifyResponseRequest request
    ) throws Exception;

    /**
     * 关闭挑战
     *
     * @param authenticator 认证器
     * @param request       请求参数
     * @throws Exception 发生的问题
     */
    void closeChallenge(
            @Valid @MfaAuthenticatorSupported String authenticator,
            @Valid CloseChallengeRequest request
    ) throws Exception;
}
```

## MfaAuthenticator

```java
public interface MfaAuthenticator<
        R extends ChallengeRequest,
        C extends Challenge,
        X extends ChallengeContext> extends ChallengeResponseService<R, C, X> {
}
```

可见MfaAuthenticator是ChallengeResponseService的一个子类，它其实是一个标记接口，用于指定为mfa服务的挑战应答服务对象。没有实现这个标记接口的对象不会被MfaAuthenticatorRegistry管理

## MfaAuthenticatorRegistry

```java
public interface MfaAuthenticatorRegistry {
    /**
     * 获取验证器的所有名称
     *
     * @return 验证器名称
     */
    Collection<String> getAuthenticatorNames();

    /**
     * 获取指定的认证器
     *
     * @param name 认证器名称
     * @return 认证器实例
     */
    @Nullable
    <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext,
            T extends MfaAuthenticator<R, C, X>>
    T getAuthenticator(@NonNull String name);
}
```

MfaAuthenticator注册表，提供按照认证器的名称(@ChallengeAuthenticator注解
或实现ChallengeAuthenticatorNameProvider接口)查找对应认证器的功能

# 核心接口和参数

## 获取可用的认证器类型

GET "/mfa"接口地址用来返回所有可用的认证器，响应体的格式是

```java

@Getter
@Setter
@NoArgsConstructor
public class ListAuthenticatorsResponse {
    private Collection<String> authenticators = new ArrayList<>();

    public ListAuthenticatorsResponse(Collection<String> authenticators) {
        setAuthenticators(authenticators);
    }

    public void setAuthenticators(Collection<String> authenticators) {
        this.authenticators = authenticators == null ? new ArrayList<>() : authenticators;
    }
}
```

## 发送挑战

POST "/mfa/{authenticator}:send"接口用来发起一个挑战，参数是

```java

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SendChallengeRequest {
    /**
     * 远程请求的认证器所需的挑战请求
     * <p>
     * 这个json会被{@link MfaAuthenticator}的泛型解析
     */
    @NotNull
    private Map<String, Object> challengeRequest;
    /**
     * 实际请求的客户端
     * <p>
     * 最终这个认证器要能识别这个客户端
     */
    @Nullable
    @RequestingClientSupported
    private Map<String, Object> requestingClient;
    /**
     * 执行mfa验证的场景，比如登录，比如下订单
     */
    @Nullable
    private String scenario;
}
```

challengeRequest将按照查找到的MfaAuthenticator给定的ChallengeRequest泛型参数进行反序列化和验证

## 验证挑战

POST "/mfa/{authenticator}:verify"接口用来验证挑战，参数是

```java

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class VerifyResponseRequest {
    /**
     * 实际请求的客户端
     * <p>
     * 最终这个认证器要能识别这个客户端
     */
    @Nullable
    @RequestingClientSupported
    private Map<String, Object> requestingClient;
    /**
     * 执行mfa验证的场景，比如登录，比如下订单
     */
    @NotBlank
    private String scenario;
    /**
     * 挑战id
     */
    @NotBlank
    private String challengeId;
    /**
     * 应答
     */
    @NotBlank
    private String response;
}
```

"challengeId"和"response"分别代表挑战id和响应内容

## 关闭挑战

POST "/mfa/{authenticator}:close"接口用来验证挑战，参数是

```java

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CloseChallengeRequest {
    /**
     * 实际请求的客户端
     * <p>
     * 最终这个认证器要能识别这个客户端
     */
    @Nullable
    @RequestingClientSupported
    private Map<String, Object> requestingClient;
    /**
     * 执行mfa验证的场景，比如登录，比如下订单
     */
    @NotBlank
    private String scenario;
    /**
     * 挑战id
     */
    @NotBlank
    private String challengeId;
}
```

# 用户，场景，客户端

在挑战应答服务中，每个方法都有的参数是客户端(RequestingClient)和场景(Scenario)

```plantuml
@startuml
!include  https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml
Person(用户, 用户) #red

System(手机app, 手机app)
System(网站, 网站)
System(小程序 , 小程序)

System(订单模块, 订单模块)
System(密码管理模块, 密码管理模块)

System(mfa服务, mfa服务)

用户 --> 手机app
用户 --> 网站
用户 --> 小程序

手机app --> 订单模块
小程序 --> 订单模块
网站 --> 订单模块

手机app --> 密码管理模块
小程序 --> 密码管理模块
网站 --> 密码管理模块


订单模块 --> mfa服务
密码管理模块 --> mfa服务
@enduml
```

如上图所示，用户会通过手机、网站、小程序等访问后台的订单模块、密码管理模块。这些模块使用mfa服务作为统一的验证服务将验证码等发送给最终用户。从mfa服务的视角出发，RequestingClient是手机app、小程序、网站。原因是这些是真正请求业务的客户端。订单模块，密码管理模块是请求mfa服务的客户端，不代表最终的业务。这些业务客户端通常会有不同的mfa流量限制策略或者不同cd时间等策略，而不是对订单模块有策略。

更重要的是，挑战的cd时间，保存的上下文可能需要将最终客户端的id作为缓存key的一部分，比如订单模块的client
id是123，那么如果mfa只看到了订单模块的客户端id，则从手机app，网站发起的挑战请求相关所有上下文数据的缓存key就变成了

* "{123}:{挑战id 1}"
* "{123}:{挑战id 2}"

显而易见，客户端id的组成部分和访问的业务客户端无关，变成了内部模块的id。这样当想要按照不同业务客户端区分缓存的key的需求出现时，现有的数据结构就无法进行满足

场景同样是区分挑战的一个关键因素，在CachedChallengeStoreTemplate等类中作为缓存key的一部分存在。场景一般是内部模块告诉给mfa服务的，比如订单模块会告诉mfa服务当前是下单场景。

在原始的设计上，场景是类型安全的，意味着场景必须是一个java类型。但是作为http服务，调用者可能并不是java程序，而是go，python等语言编写的程序，没有理由要求这些程序的开发必须传输一个java类型，因此才用字符串作为参数。

# 挑战请求、场景以及客户端数据的反序列化

在MfaAuthenticationEndpoint类中，要求输入一组"Converter<Map<String, Object>, ? extends RequestingClient>"
的bean作为RequestingClient的反序列化转换器。 如果某个转换器认为当前输入的客户端的数据能够被自己转换，那么它就返回经过转换的RequestingClient实例，否则返回null。

```java
public class OAuth2RequestingClientDeserializer implements Converter<Map<String, Object>, OAuth2RequestingClient> {
    private final ObjectMapper objectMapper;
    private final HandlerMethodArgumentBeanValidator beanValidator;

    @Nullable
    @Override
    public OAuth2RequestingClient convert(@NonNull Map<String, Object> source) {
        OAuth2RequestingClient oAuth2RequestingClient;
        try {
            oAuth2RequestingClient = objectMapper.convertValue(source, OAuth2RequestingClient.class);
            beanValidator.validate(oAuth2RequestingClient);
        } catch (Exception e) {
            //转换能出错必然不是这个类型或者验证失败
            return null;
        }
        return oAuth2RequestingClient;
    }
}
```

上面是oauth2请求客户端的一个实现参考，它通过objectMapper进行类型转换，然后再调用beanValidator执行验证。beanValidator在javax
constraints验证不通过时抛出BadRequestArgumentsException

场景类的反序列化方法是检查scenario能否被反序列化为一个Class且Class是否是Scenario类型的子类，不是的话固定使用MfaServerMiscellaneousScenario进行兜底

# 微服务客户端

[mfa-authentication-server-client](mfa-authentication-server-client)定义了基于spring cloud openfeign的客户端

```java
public interface MfaAuthenticationClientPrototype<C extends Challenge> extends MfaAuthenticationEndpointSkeleton<C> {
    @Override
    @GetMapping("/mfa")
    ListAuthenticatorsResponse listAuthenticators() throws Exception;

    @PostMapping("/mfa/{authenticator}:send")
    @Override
    C sendChallenge(
            @PathVariable("authenticator") @Valid String authenticator,
            @Valid @RequestBody SendChallengeRequest request
    ) throws Exception;

    @PostMapping("/mfa/{authenticator}:verify")
    @Override
    ResponseVerificationResponse verifyResponse(
            @PathVariable("authenticator") @Valid String authenticator,
            @Valid @RequestBody VerifyResponseRequest request
    ) throws Exception;

    @PostMapping("/mfa/{authenticator}:close")
    @Override
    void closeChallenge(
            @PathVariable("authenticator") @Valid String authenticator,
            @Valid @RequestBody CloseChallengeRequest request
    ) throws Exception;
}
```

MfaAuthenticationClientPrototype是feign client的接口原型。具体使用时，按照调用返回的挑战类型，继承客户端原型后使用，例如

```java

@FeignClient(name = "mfa-authentication", decode404 = true)
public interface SampleChallengeClient extends
        MfaAuthenticationClientPrototype<SampleChallenge> {

}
```

这样也便于开发人员在使用时按照实际的mfa认证服务在微服务管理系统中的注册名进行调用