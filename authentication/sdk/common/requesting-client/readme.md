# 简介

请求客户端(RequestingClient)指的是正在请求当前接口的**前端**应用客户端，来访者可以基于oauth2的标准协议也可以使用其它的客户端校验机制

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml


Person(用户, 用户)
System(浏览器, 浏览器, 客户端)
System(手机app, 手机app, 客户端)
System(小程序, 小程序, 客户端)

System(接口, 接口, api)
System(中台服务, 中台服务, api)

用户 --> 浏览器
用户 --> 手机app
用户 --> 小程序

手机app --> 接口
浏览器 --> 接口
小程序 --> 接口

接口 --> 中台服务
@enduml
```

通常情况下，用户会通过多种客户端访问后台的接口来办理业务，这些请求第一层会访问到对外的接口api，第二层从接口api调用内部中台服务。

无论是接口还是中台服务往往都需要对访问的前端应用提供不同功能(受制于应用所在设备的能力，比如能展示的列表项不同等等)
，这种在内部接口调用的过程中就需要传递(RequestingClient)的信息。

在认证服务器的内部以及多因子认证服务器的内部都需要RequestingClient来表达当前正在访问的前端应用是什么，并基于这些应用隔离诸如用户登录信息、动态密码等数据

# RequestingClient & OAuth2RequestingClient

```java

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public abstract class RequestingClient implements Serializable {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * client id
     */
    @NotBlank
    private String clientId;
}

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class OAuth2RequestingClient extends RequestingClient {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 访问的授权类型
     */
    @NotBlank
    private String grantType;
    /**
     * 对用户信息的访问范围
     */
    @NotNull
    @Singular
    private Collection<@Valid @NotBlank String> scopes;
}
```

从定义可见请求客户端分为一个基本类型以及oauth2标准的客户端属性，包含客户端id、请求的授权类型以及请求访问的数据范围，并搭配上了javax支持的constraints注解。

# 使用场景

* 在[challenge-response](..%2F..%2Finfra%2Fchallenge-response)中，通常生成的挑战上下文都是基于不同的客户端id进行隔离
* 当其它应用在开发过程中希望传入的除了一个"clientId"属性外，
  还需要额外传入一些已经从客户端存储或接口读取出来的数据时，可以定义自己的RequestingClient类型并在调用时进行序列化和反序列化使用