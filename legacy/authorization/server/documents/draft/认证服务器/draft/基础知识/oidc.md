# 引言

oidc(open id connect)
是一个基于oauth2标准的扩展标准，用于定义集中身份认证的标准化端点以及数据结构，阅读者首先需要了解oauth2标准(
参考: [https://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html](https://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html))
或这篇中文版的介绍性文字: [https://www.cnblogs.com/lishiqi-blog/p/11164961.html](https://www.cnblogs.com/lishiqi-blog/p/11164961.html)

# 术语

详尽的术语首先请阅读[https://openid.net/specs/openid-connect-core-1_0.html](https://openid.net/specs/openid-connect-core-1_0.html)
，本文在此给出几个常用的术语

* 认证服务器(IDP/OP): 提供用户和客户端身份校验能力的http(s)服务
* 用户: 指需要进行认证登录的人
* 客户端: 指需要授权服务器帮忙鉴定访问者是否已经登录的应用程序，比如浏览器，比如手机app，甚至是电冰箱，微波炉等智能设备(
  假设微波炉在开机前需要扫码登录)
* 访问令牌: 一般来说是一个字符串id，由认证服务器颁发给客户端，客户端通过这个令牌读取用户的信息
* 认证类型: 客户端能够使用什么类型的请求对用户进行验证，包括
    * code: 一般就是重定向到登录页，登录后认证服务器给客户端颁发一个一次性的授权码，客户端凭授权码获得访问令牌
    * password: 客户端直接提交用户的用户名和密码(
      非常罕见，一般是只有认证服务器所在的公司的开发团队自己编写的客户端会这样)

此外，oauth2标准也支持去扩展自己的用户认证类型，比如人脸认证

# 授权过程

```text
+--------+                                   +--------+
|        |                                   |        |
|        |---------(1) AuthN Request-------->|        |
|        |                                   |        |
|        |  +--------+                       |        |
|        |  |        |                       |        |
|        |  |  End-  |<--(2) AuthN & AuthZ-->|        |
|        |  |  User  |                       |        |
|   RP   |  |        |                       |   OP   |
|        |  +--------+                       |        |
|        |                                   |        |
|        |<--------(3) AuthN Response--------|        |
|        |                                   |        |
|        |---------(4) UserInfo Request----->|        |
|        |                                   |        |
|        |<--------(5) UserInfo Response-----|        |
|        |                                   |        |
+--------+                                   +--------+
```

上图是oidc官网上的一张流程示意图，包含的主要信息是

* RP(客户端)向认证服务器(OP)发起认证请求(使用页面重定向或直接访问认证接口)
* OP进行登录用户的认证
* OP给RP授权一个访问令牌以及终端用户的摘要信息(Id Token)
* RP继续访问用户信息端点获得用户详情
* OP返回用户详情

# Id Token

基于oidc标准化组织的要求，在oauth2访问令牌的基础上，增加一个id token，他一般是一个jwt令牌，下面是google回的一个样例

```json
{
  "access_token": "ya29.a0ARrdaM_zsco-f21koRF3s_akbQ-ro8UyDbB05SYNQSxVapqc98YpGPNVMMuNz-LXQepW8ZT_XYUcEd-t_g2Vu9A7RT-uEvCQx5Z9DIabklE9L6B0zEN5CjSgOBD6rwkpLNVtl6ir4CaPwmE1YuvkdSaj6mbl",
  "expires_in": 3599,
  "scope": "https://www.googleapis.com/auth/userinfo.email openid",
  "token_type": "Bearer",
  "id_token": "eyJhbGciOiJSUzI1NiIsImtpZCI6ImMxODkyZWI0OWQ3ZWY5YWRmOGIyZTE0YzA1Y2EwZDAzMjcxNGEyMzciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiOTUzODU4NjM4MDUwLW91OGNwZTgwcGhoM3U3cjAwcnJjb2NxbDFvZTdkMGN2LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiOTUzODU4NjM4MDUwLW91OGNwZTgwcGhoM3U3cjAwcnJjb2NxbDFvZTdkMGN2LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA2OTE4NjE2MTAyMjYwMjE3Mjg5IiwiZW1haWwiOiJzaHV3ZWk3MTZAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJ6YmpZZm1DcDFLZ3MydmR5eUM1Q25RIiwiaWF0IjoxNjM5MTU0Nzc3LCJleHAiOjE2MzkxNTgzNzd9.jkm_H85-UD6Ykem9T05n8aPgvUaUS91XuSKQ4xm45qpH6_w-hGjhD3wcT5bnIaIJTGFbwL7UsAw_HebtvprK-HjnzHZUvDtoEPlofwJj6cU-CAV6MCYGXOixlgzSfK13wG4gzouMlKW_X8X8CoMjFZEEi7jLGd26d65PZKmE_ZMioGtLlNG4pEhiehym8m9hKcL42YHa0QY-eKQfWN5gNZRNBafsXSR1IbDznVYZlwdtsQWQBJWXbfd8-McNUK7zQXGNhWUDRPEP8FNzk3tbIKz8V8DfUgYWbC-_3lDqb7QNhZEkUaIyueAP90tENiW3d_I1_zbQ0V2Koe3lAXBe8g"
}
```

可见在发给客户端访问token的时候有一个标准化的字段`id_token`，其内容是一串base64后的密文

在oidc标准中，id
token要求必备的字段参考[https://openid.net/specs/openid-connect-core-1_0.html#IDToken](https://openid.net/specs/openid-connect-core-1_0.html#IDToken)
中的第2章的定义，对id_token的客户端校验标准在[https://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation](https://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation)
中进行了定义

id token是最终用户信息的一个摘要，比如可能长这样

```json
{
  "active": true,
  "client_id": "test",
  "iat": 1640707946,
  "exp": 1640880746,
  "scope": "openid",
  "token_type": "Bearer",
  "nbf": 1640707946,
  "sub": "root",
  "aud": [
    "test"
  ],
  "iss": "http://localhost:9090"
}
```

其中的sub可以被理解为是用户的识别符号

# 用户信息端点

根据标准，如果要读取详细的用户信息，则需要访问用户信息端点，端点按照授权请求中要求的访问范围，有限地返回用户的信息。比如，scope中没有要求返回身份证，则用户信息端点的返回也不会有身份证

# 标准化端点

基于oauth2标准，认证服务器支持的标准化端点有

## 授权端点: authorization endpoint

这个一般用于`code`和`id_token`模式的授权，它会显示一个用户认证的页面(扫码，用户名密码，短信直接登录等)
要求用户登录，这个端点不支持`client_credentials`登录

该接口在认证服务器的标准url为`/oauth2/authorize`

## 访问令牌颁发端点: token endpoint

认证服务器验证客户端信息并颁发访问令牌的接口，标准化url为`/oauth2/token`

注意，授权端点不颁发token

## 用户信息读取端点: userinfo endpoint

用户端点要求客户端必须携带访问令牌才能访问，否则应当返回http
401，它在oidc中也有标准化的数据结构，具体参考[https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims](https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims)

下面给一个google的用户信息端点的返回

```json
{
  "issued_to": "953858638050-ou8cpe80phh3u7r00rrcocql1oe7d0cv.apps.googleusercontent.com",
  "audience": "953858638050-ou8cpe80phh3u7r00rrcocql1oe7d0cv.apps.googleusercontent.com",
  "user_id": "106918616102260217289",
  "expires_in": 3480,
  "email": "shuwei716@gmail.com",
  "email_verified": true,
  "issuer": "accounts.google.com",
  "issued_at": 1639154777
}
```

如果你读了标准后也不难发现，除了email的部分之外，google并没有鸟标准

## 令牌验证端点: Token Introspect

对于后台服务器来说，对前端提交的令牌都应当妥善的进行验证，包含它是否有效，是否已经不处于激活状态等等，因此oidc提供校验端点`/oauth2/introspect`
，后台服务器用自己的client id、client
secret进行认证并提交要检验的token

# 总结

简单来说，流程就是，访问授权端点拿验证码->用验证码在访问令牌颁发端点换令牌->自行解开jwt token内的id_token进行校验->
携带access_token到用户信息端点读用户