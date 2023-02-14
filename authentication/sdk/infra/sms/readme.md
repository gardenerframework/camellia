# 简介

短信认证是登录、下单，支付等场景时经常用来确认用户当前身份和操作意愿的组件

# 验证码发送引擎

"sms-authentication-engine"组件提供了短信认证服务，该服务主要是发送验证码和检查验证码是否正确

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

System(调用方1, 调用应用, application 1) #red
System(调用方2, 调用应用, application 2) #red

System(登录服务, 登录服务, api) #orange
System(订单服务, 订单服务, api) #orange

System(认证引擎, sms-authentication-engine, jar)
System(短信渠道, 短信渠道, api) #grey

调用方1 --> 登录服务
调用方2 --> 登录服务

调用方1 --> 订单服务
调用方2 --> 订单服务

订单服务 --> 认证引擎
登录服务 --> 认证引擎

认证引擎 --> 短信渠道

@enduml
```

作为短信认证引擎的sdk，自然会被多种