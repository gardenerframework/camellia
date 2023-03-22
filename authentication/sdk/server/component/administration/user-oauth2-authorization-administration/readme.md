# 简介

OAuth2Authorization是spring security oauth2 授权服务器中用来保存用户授权信息的上下文对象，其中包含

* access token
* refresh token
* id token
* 用户信息
* 客户端信息

等关键数据

当前组件主要管理用户完成的授权信息的查询和移除(踢掉给定用户)

# 关系图

```plantuml
@startuml
!include  https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

System(accessToken, access token)
System(refreshToken, refresh token)
System(idToken, id token)
System(OAuth2Authorization, OAuth2Authorization)

accessToken --> OAuth2Authorization
refreshToken --> OAuth2Authorization
idToken --> OAuth2Authorization

@enduml
```

存储关系上，通常访问令牌，刷新令牌，id令牌都会指向关联的OAuth2Authorization对象，由OAuth2Authorization保存用户信息

```plantuml
@startuml
!include  https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

Person(User, User)

System(client1, Client)
System(client2, Client)
System(client3, Client)


System(OAuth2Authorization1, OAuth2Authorization)
System(OAuth2Authorization2, OAuth2Authorization)
System(OAuth2Authorization3, OAuth2Authorization)

User --> client1
User --> client2
User --> client3

client1 --> OAuth2Authorization1
client2 --> OAuth2Authorization2
client3 --> OAuth2Authorization3

@enduml
```

用户授权一个客户端时，该客户端就会拿着授权码或者用户的登录凭据获得一个OAuth2Authorization对象。所以如果需要以用户为维度查询有效的授权信息，那就需要存储用户与授权的对应关系

# 管理维度以及实现路径

目前可以预见的，会以用户的id、客户端的id两个维度出发查询用户是否有登录以及当前还没有过期的OAuth2Authorization对象有哪些。那么在功能上就有

* 删除当前用户的所有OAuth2Authorization对象(全渠道踢出)
* 删除当前用户在指定客户端id上的OAuth2Authorization对象(单渠道踢出)