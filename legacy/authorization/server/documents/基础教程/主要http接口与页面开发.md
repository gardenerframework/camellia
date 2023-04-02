# 简介

本文主要就认证服务器自己的应用页面开发进行讲解，手机应用开发需要参考其它文档。在文中，假设认证服务器的域名为"https://auth.camellia.com"

# 部署模型

```plantuml
@startuml
!include https://s3.cn-south-1.jdcloud-oss.com/c4-plantuml/C4_Container.puml

Person(访问者, 访问者)
System(负载均衡, 负载均衡)
System(认证服务器网页, 认证服务器网页)
System(认证服务器后台, 认证服务器后台)
System(mysql, mysql, 5.7)
System(redis, redis, "4.0(非集群版)")

访问者 --> 负载均衡: https://auth.camellia.com
负载均衡 --> 认证服务器网页: 网页请求
负载均衡 --> 认证服务器后台: 接口请求
认证服务器后台 --> mysql: 选项存储
认证服务器后台 --> redis: 共享session & oauth2访问令牌
@enduml
```

# 登录

在默认的情况下，登录页面的网页入口为域名的根路径(https://auth.camellia.com)，到达该位置的来源可能有2个

* 来自于用户的自主访问
* 从其它对接的应用程序跳转至oauth2的网页授权接口"/oauth2/authorize"后，认证服务器发现用户没有登录将页面重定向过来

## 获取可用的认证类型注册表

调用"GET /api/options/authenticationTypeRegistry"，成功后获得如下响应

```json
{
  "option": {
    "types": [
      "cas",
      "username"
    ]
  },
  "name": "...",
  "readonly": true,
  "versionNumber": null,
  "description": "认证类型注册表"
}
```

"types"字段描述了当前认证服务器支持的登录认证方法，比如

* username: 支持用户名密码校验登录
* cas: 支持对接cas统一登录认证系统

"types"字段返回的结果与认证引擎加载了哪些`UserAuthenticationService`有关

## 提交请求参数

登录请求需要使用表单提交的方式(而非ajax调用的方式)，将认证参数发送到"/login"接口

其中提交的公共参数有

* authenticationType(String): 认证请求的类型，为"/api/options/authenticationTypeRegistry"返回的类型中的一种，比如
  "username"

其余参数基于不同认证类型不尽相同，需要前端和对应认证类型的开发人员进行沟通获取参数名称、类型和含义

## 成功和失败的重定向

既然是表单提交，那么成功后的逻辑就由认证引擎控制。

* 登录过程成功后，后台会发送http 302将页面跳转到"/welcome"
* 登录失败时，后台会发送http 302将页面跳转到"/error"

这两个页面是前端开发的，在负载均衡的路径上要配置跳转到网页的服务器，而不是前端写完了页面放到认证服务器后台上

# 欢迎页

"/welcome"是登录成功的欢迎页，在常见的最佳实践上看，一般这个页面就是个过渡，会将网页再次redirect到整个应用的门户页面

同时，页面可以通过ajax请求"/api/me"获取当前登录用户的信息

```java
public class UserAppearance implements
        GenericTraits.Id<String>,
        GenericTraits.Name,
        AccountTraits.Avatar {
    /**
     * 用户的id
     */
    private String id;
    /**
     * 任何形式的展示名称
     *
     * 昵称，姓名随便
     */
    @Nullable
    private String name;
    /**
     * 任何形式的显示图标
     */
    @Nullable
    private String avatar;
}
```

# 错误页

认证失败或者由后台服务器控制的页面流程发生问题时(通常都是那些表单提交的接口)，服务器后台会发送302重定向到"/error"地址显示错误。
在跳转过来时，携带的参数有

* status(Integer): http 状态码
* phrase(String): http 状态吗对应的短语，比如"Not Found"
* message(String): 一段国际化的消息文本
* code(String): 错误编码

开发人员自行按需组织页面内容

# 多因子认证

在"/login"接口处理过程中，当`MfaAuthenticationChallengeResponseService`决定发送挑战后，引擎会将页面302到"/mfa"
，即多因子认证入口页，携带的参数有

* authenticator: mfa认证方式
* challengeId: 挑战id
* expiresAt: 挑战的过期时间，过期之后没有成功应答，当前mfa挑战就失败了

此外

```java
public class Challenge {
    /**
     * 挑战id
     */
    private final String id;
    /**
     * 挑战的验证形式
     */
    private final String authenticator;
    /**
     * 挑战的过期时间，超过这个时间即认为挑战无效
     */
    private final Date expiresAt;
    /**
     * 其它额外参数，提示给客户端的
     */
    private Map<String, String> parameters;
}
```

中的"parameters"如果不为空，则还有附加这些额外参数，格式是"key1=value1&key2=value2"，最终的形式如下

```text
/mfa?authenticator=xxx&challengeId=12345&key1=value1&key2=value2...
```

同样，这个页面是有前端开发完成的，负载均衡需要正确的按照路径将请求导向前端服务器而不是认证服务器后台

应答完毕后，向"/login"再次发起表单提交，参数固定为

* authenticationType="mfa"
* challengeId=题目id
* response=应答

如果挑战有问题则会重定向到error页面

<font color=red>警告</font>:
mfa因子认证如果用户提交了一次错误的回答则整个mfa挑战就关闭了，这样能防止恶意用户故意枚举当前用户的挑战回答。同时，从体验上讲，回答失败就跳转到错误页面了，当前用户需要重新登录，这部分体验有可能会在未来修改。

# 授权批准页

当对接认证系统的应用需要用户授权才能访问用户信息时，后台会将页面302到"/consent"，并在query string上附加以下参数

* scope: 要求请求的权限范围，以空格为分割
* client_id: 当前正在请求的客户端信息
* state: 一个随机数

授权批准页面可以额外(通过ajax)请求"/api/client/{clientId}"来获取客户端的信息，获取结果为

```java
public class ClientAppearance implements
        GenericTraits.Name,
        GenericTraits.Description,
        GenericTraits.PictureTraits.Logo {
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 客户端的显示名称
     */
    private String name;
    /**
     * 客户端描述
     */
    private String description;
    /**
     * 客户端的logo
     */
    private String logo;
}
```

这个页面用户可以通过scope查看给定的客户端(或称应用)
要读取的用户信息的范围，并选择是进行授权还是关闭页面，当点击授权按钮后，授权页面同样需要使用表单提交的方式，"POST"
以下参数到"/oauth2/authorize"

* scope: 要求请求的权限范围，如果有多个，则提交多个scope参数
* client_id: 当前正在请求的客户端信息
* state: 页面获取到的随机数

如果失败则会302到错误页面

<font color=red>警告</font>: state是一次行生效，也就是如果发生错误，通过浏览器回退到授权页再次点击授权必然会报错(
因为state没有变化)

# 登出页面

登录按钮点击时，页面重定向到后台服务器的/logout"，后台服务器登出成功后会302到"/goodbye"，这个页面想要显示啥自行组织

# 接口返回数据的定制化

从上文可以看到，用户接口和客户端接口都能获取当前的登录用户和正在请求用户授权的客户端，其标准返回的数据已经在上文讲解过。但是现场如果对数据有扩展字段需求，则

* 第一步:
  实现自己的[UserAppearance.java](..%2F..%2Fsdk%2Fauthorization-server-engine%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjdcloud%2Fgardener%2Fcamellia%2Fauthorization%2Fuser%2Fschema%2Fresponse%2FUserAppearance.java)
  以及[ClientAppearance.java](..%2F..%2Fsdk%2Fauthorization-server-engine%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjdcloud%2Fgardener%2Fcamellia%2Fauthorization%2Fclient%2Fschema%2Fresponse%2FClientAppearance.java)
  ，不妨称之为"MyUserAppearance"和"MyClientAppearance"
* 第二步: 声明`Converter<User, MyUserAppearance>`以及`Converter<RegisteredClient, MyClientAppearance>`
  转换器并声明为bean，接口就会将读取出来的用户数据以及客户端数据交给这个转换器进行转换

# 总结

一共要开发的页面有

* 登录页
* 欢迎页
* 错误页
* 批准页
* 登出页

可以获取当前登录用户以及客户端的接口是

* /api/me(当前登录用户)
* /api/client/{clientId}(当前客户端)

当现场需要返回扩展属性时，定义相关的转换器，并在泛型标记上来源类型和目标类型

# 继续阅读

[组件与兼容性](组件与兼容性.md)