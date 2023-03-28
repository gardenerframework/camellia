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

System

@enduml
```