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
System(认证服务器, 认证服务器, oauth2-sso) #red
System(管理后台, 管理后台, administration) #green
System(数据存储, 数据存储, database/redis) 
System(消息队列, 消息队列, kafka)

最终用户 -d-> 认证服务器
管理员 -d-> 管理后台

管理后台 -l-> 认证服务器: 部分管理功能调用(通过内部接口)
管理后台 -d-> 数据存储: 数据变更
认证服务器 -d-> 数据存储: 数据读取
认证服务器 -d-> 消息队列
管理后台 -d-> 消息队列

@enduml
```

认证服务器的目标是为了向最终用户提供身份认证功能，管理后台向管理员提供认证服务器的配置变更、登录态管理等能力。
管理后台对认证服务器的管理分为2种模式

* 模式1: 调用认证服务器提供的管理用接口
* 模式2: 修改认证服务器的数据并通过消息等机制通知认证服务器数据已经变更

其中模式1的必要时是因为认证服务器使用的某些类和组件引入了管理后台非必要的jar包，或对管理后台的业务逻辑有干扰性。比如用于管理用户授权token的OAuth2AuthorizationService就是认证服务器独有的包，它会引入一系列spring
security的组件。这些组件使得管理服务器如果想要基于其它组件进行管理人员的RBAC访问控制，就不得不在spring
security和目标组件共存的情况下探索兼容之道，实属没有必要

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

认证服务器对外开通的接口是仅仅为管理服务器准备的，因此认证服务器需要在接口上认证调用者的身份。

