# 简介

当前组件为认证服务器提供一些基础的后台管理功，包含

* 给定用户id删除给定客户端的登录token
* 查询用户的登录日志
* 管理系统参数

等

# 管理后台和认证服务器的关系

```plantuml
@startuml
!include  https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml
Person(最终用户, 最终用户)
Person(管理员, 管理员)
System(认证服务器, 认证服务器, oauth2-sso)
System(管理后台, 管理后台, administration)

最终用户 --> 认证服务器
管理员 --> 管理后台

管理后台 --> 认证服务器: 部分管理功能调用(通过内部接口)
@enduml
```

# 认证服务器的接口分组

认证服务器对外暴露以下几组接口

* spring security的oauth2授权服务器提供的接口，主要是
    * /login
    * /oauth2/xx
    * /logout
* 认证过程中，对接客户端和浏览器所需的接口，主要是
    * /api/xx
* 对管理后台开放的管理用接口，主要是
    * /administration/xx

# 功能注册

基于以往设计的成功实践，后台管理的功能也是按需注册完，比如给定id踢掉线就可以选择向后台管理引擎注册或不注册。
@AuthenticationServerManagementFeature注解向引擎表达当前激活了一个新的功能。

```java
public @interface AuthenticationServerManagementFeature {
    String value();
}
```

特性的名称之间不得重复
