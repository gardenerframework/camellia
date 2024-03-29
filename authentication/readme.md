# 简介

认证是指对来访者的身份进行识别，并在必要的情况采用多步骤、多因子的形式确认来访者的登录信息没有被泄露且行为由自己发起。当前模块下的所有组件都是直接或间接为这一目的服务的。

* [common](./sdk/common): 定义非常公共的组件以及数据定义和相关的组件
* [infra](./sdk/infra): 定义认证的基本组件
* [server](./sdk/server): 定义了认证服务器组件

# 常规登录认证([server](sdk%2Fserver))

Camellia Unified Authentication Server Engine(CUASE)是一个款基于spring security框架开发，服从oauth2标准的认证服务器引擎，它主要解决客户的以下几个问题

* 多个零散系统使用不同的技术栈完成最终用户的登录，用户在多个系统间要不停的切换账号、密码，需要实现单点登录(sso)
* 单点登录系统的能力受限，不能支持短信登录、应用扫码登录、小程序扫码登录、人脸识别登录等符合现代化应用特征的用户认证方式，改造起来成本非常高，需要具备一个面向未来且具备扩展的认证引擎
* 当前单点登录的方案基于域名进行cookie共享，子集团(子公司)无法按照预期注册新的域名
* 公司战略上要求对当前的用户进行开放，允许合作伙伴使用公司的注册用户进行登录，而现在的认证系统无法进行支持，需要较高成本的改造
* 公司内部开发或购买的c/s结构的应用程序(如云桌面)需要关联公司的员工账号进行登录，而现在的登录系统不知道怎么支持

在特性上，它能够

* 按需开发新的认证方法: CUASE引擎将认证流程高度抽象，提供标准化的认证方法接口(java接口)用于实现新的认证方法
* 按需对接现有的挑战应答服务: CUASE能够以内嵌的方式或者远程调用的方式嵌入已有的挑战应答机制，提供多因子验证能力
* 按需对接用户存储系统: CUASE提供标准化的用户存储系统对接接口(java接口)，开发人员可以通过实现接口对接企业内部所需的用户存储，如ldap
* 通过监听事件插入自定义逻辑: CUASE在用户的登录认证过程向外发送可进行同步/异步监听的应用事件，通过这些应用事件，开发人员可以实现诸如记录登录日志、检查用户是否在风控黑名单而阻止登录等所需的逻辑
* 基于oauth2标准进行对接: CUASE服从oauth2标准实现，应用程序可以通过oauth2的标准化流程和接口完成对接

认证系统引擎的一个架构图概览如下

```plantuml
@startuml

!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

System(浏览器, 浏览器)
System(手机app, 手机app)
System(微信小程序, 微信小程序)
System(登录认证接口, 登录认证服务, UserAuthenticationService)
System(用户数据库访问服务, 用户数据库访问服务, UserService)
System(事件监听器, 事件监听器, @EventListener)
System(用户数据库, 用户数据库)
System(mfa认证服务, mfa认证服务)

浏览器 -d->  登录认证接口
手机app -d->  登录认证接口
微信小程序 -d->  登录认证接口
登录认证接口 -d-> 用户数据库访问服务
登录认证接口 -l-> mfa认证服务
用户数据库访问服务 -d-> 用户数据库
事件监听器 -l-> 登录认证接口

@enduml
```

# 多因子认证([mfa](sdk%2Finfra%2Fmfa))

Camellia Unified Multi-factor Authentication Server Engine(CUMFASE)
可以通过引入已有的挑战应答服务来实现多因子验证以及动态密码验证的统一归集。这样可以对业务系统
(特别是**非java代码的rest api**)的其他模块提供多因子验证的统一服务能力

在特性上，已经完成的有

* 短信动态密码校验

可以服务的场景有

* 密码找回时的动态密码生成和校验
* 重置密码时的动态密码生成和校验
* 登录、下单等其它风险操作时的动态密码生成和校验
* 其它需要动态密码校验的场景

_提示_: 手机扫码这种偶尔会出现的功能有时候也会被当做多因子验证的形式使用，比如要求下单前使用手机app扫一下二维码。目前的设计上，二维码和多因子还没有进行合并，也许在未来真实有需求发生时会合并

# 下一步阅读

在开展开发工作，建议先进入[common](sdk%2Fcommon)了解一些基本概念