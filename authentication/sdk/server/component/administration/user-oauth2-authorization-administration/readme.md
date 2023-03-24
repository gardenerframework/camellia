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
System(OAuth2Authorization11, OAuth2Authorization)
System(OAuth2Authorization2, OAuth2Authorization)
System(OAuth2Authorization3, OAuth2Authorization)

User --> client1
User --> client2
User --> client3

client1 --> OAuth2Authorization1
client1 --> OAuth2Authorization11
client2 --> OAuth2Authorization2
client3 --> OAuth2Authorization3

@enduml
```

用户授权一个客户端时，该客户端就会拿着授权码或者用户的登录凭据获得一个OAuth2Authorization对象。所以如果需要以用户为维度查询有效的授权信息，那就需要存储用户与授权的对应关系.

# 管理维度

目前可以预见的，会以用户的id、客户端的id两个维度出发查询用户是否有登录以及当前还没有过期的OAuth2Authorization对象有哪些。那么在功能上就有

* 删除当前用户的所有OAuth2Authorization对象(全渠道踢出)
* 删除当前用户在指定客户端id上的OAuth2Authorization对象(单渠道踢出)

```java
public class UserAuthorizedOAuth2Authorization {
    /**
     * 用户id
     */
    @NotNull
    public String userId;
    /**
     * 客户端id
     */
    @Nullable
    private String clientId;
    /**
     * 当前授权是针对哪个设备执行的
     */
    @Nullable
    private String deviceId;
    /**
     * 授权id
     */
    @NotNull
    private String oauth2AuthorizationId;
}
```

上面的数据结构增加了设备id的属性，这是因为一个客户端的用户可能在不同设备(app/pad)上登录。
从数据库的视角出发，这是一张庞大的关系表格。如果在线用户达到上千万或者上亿，那么整个需要存储的关系对照信息将是十亿或者百亿级别

# 通过指令和拦截调用机制实现删除OAuth2Authorization

基于上面可遇见的数据量得知，通过一张数据库表格来保存所有映射关系可不是个好主意。于是，为了删除用户授权，可以在redis或其它存储中保存用户要求请求什么授权的指令

* 全渠道退出: 在redis中保存以用户id为key的指令，并加上这个指令的发出时间，并在OAuth2AuthorizationService的findByXx接口处进行拦截，检查当前授权的发生时间是在指令下达时间前还是时间后。
  如果在时间前发生，第一步调用remove方法删除指定授权，然后返回null作为结果
* 单渠道退出: 在redis中保存用户id + client id，其余玩法一样
* 单设备退出: 在redis中保存用户id + client id + device id，其它玩法一样