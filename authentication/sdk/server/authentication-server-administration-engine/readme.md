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

# @AdministrationServerRestController vs @AuthenticationServerAdministrationController

AdministrationServerRestController和AuthenticationServerAdministrationRestController是后台管理服务器和认证服务器分别用来标记管理用接口的注解。
从上面的关系图中不难看出，部分管理用的接口需要认证服务器来提供(比如为了使得管理服务器不要引入干扰业务逻辑的jar包)

* AdministrationServerRestController: 仅在管理后台使用，表明这是一个管理用的接口
* AuthenticationServerAdministrationController: 仅在认证中心使用，表明这是一个为管理服务器服务的接口

# 管理服务器调用认证服务器管理接口的安全认证
