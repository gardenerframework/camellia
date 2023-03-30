# 简介

mfa多因子验证是挑战应答使用的一个最常用的场景，其它场景包含了注册和登录所需的动态密码发送和校验。当前模块将mfa进行http服务化，让无论是登录模块还是订单模块等都可以通过远程接口的方式进行调用。
因此，它是一个纯服务化的接口，不会有任何界面

# 前提条件

当提及多因子验证时，首先需要清晰地认知到此刻系统已经识别了来访者的身份并确定了他是一名合法的用户。此时系统已经知晓了用户的信息和数据。

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

最终，发起挑战的请求数据格式是

```java
public class SendChallengeRequest {
    /**
     * 要执行mfa的用户信息，按照实现方的理解来转类型
     * <p>
     * 最终这个认证器要能识别这个用户信息
     */
    @NonNull
    @NotNull
    private Map<String, Object> user;
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

# 获取可用的认证器类型

GET "/mfa"接口地址用来返回所有可用的认证器，响应体的格式是

```java
public class ListAuthenticatorsResponse {
    @NonNull
    private Collection<String> authenticators = new ArrayList<>();
}
```

# 发送挑战

POST "/mfa/{authenticator}:send"接口用来发起一个挑战，参数是

```java
public class SendChallengeRequest {
    /**
     * 要执行mfa的用户信息，按照实现方的理解来转类型
     * <p>
     * 最终这个认证器要能识别这个用户信息
     */
    @NonNull
    @NotNull
    private Map<String, Object> user;
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

user和requestingClient都需要调用方按照mfa服务能理解的json格式进行序列化

# 客户端和场景的反序列化策略

在MfaAuthenticationEndpoint类中，要求输入一组Converter<Map<String, Object>, ? extends RequestingClient>
的bean作为RequestingClient的反序列化转换器。 如果某个转换器认为当前输入的客户端的数据能够被自己转换，那么它就返回经过转换的RequestingClient实例，否则返回null

场景类的反序列化方法是检查scenario能否被反序列化为一个Class且Class是否是Scenario类型的子类，不是的话固定使用MfaAuthenticationServerScenario进行兜底

# 验证挑战

POST "/mfa/{authenticator}:verify"