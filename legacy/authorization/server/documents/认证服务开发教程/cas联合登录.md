# 简介

"authorization-server-authentication-engine-cas"是与cas进行联合登录认证的服务组件。 cas是一种常见的sso单点登录系统，其和第三方系统之间的交互核心是获取票据(ticket)和验证票据(
其实类似于oauth2的获取token和验证token)。 作为一个早于当前引擎出现的sso产品，cas已经在各大企业内部占有了一席之地。于是在项目的落地过程中难免会有要求当前认证中心对接cas单点登录信息进行集成身份认证的需求

<font color=green>提示</font>: cas的对接具有一定的代表性，当认证中心需要对接其它SSO系统时(如微信，支付宝等)可从当前文档获得一些实现参考

```plantuml
用户 --> 认证中心: 选择cas联合登录
认证中心 --> cas: 跳转到cas登录页
用户 --> cas: 完成身份认证
cas --> 认证中心: 302到回调页面 + ticket=xxx(登录票据)
认证中心 --> 登录接口: authenticationType=cas&ticket=xxx
登录接口 --> cas: 验证票据
```

# 配置cas服务器地址

```java
public class CasOption {
    /**
     * 认证页面地址
     */
    @NotBlank
    private String authenticationPageUrl;
    /**
     * 票据验证服务地址
     */
    @NotBlank
    private String ticketValidationUrl;
    /**
     * 向cas中心注册的回调地址
     */
    @NotBlank
    private String callbackUrl;
}
```

开发人员通过设置"authenticationPageUrl"来设置cas认证系统的登录页，这个url告诉前端界面当需要进行cas身份认证时需要将页面重定向到何处。"callbackUrl"
则是认证服务在cas服务器上配置的回调地址，认证成功后cas服务器会将页面重定向回这个url

"ticketValidationUrl"是票据验证的服务地址，登录接口通过这个地址验证票据

# CasTicketService

```java
public interface CasTicketService<P extends BasicPrincipal> {
    /**
     * 使用票据换取登录名
     *
     * @param ticket 票据
     * @return 登录名 - 如果票据已经过期或不正确等，可以返回null
     * @throws CasExceptions.CasException 如果有问题抛出cas相关的异常
     * @see com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.InvalidTicketException
     * @see com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.InvalidUserException
     * @see com.jdcloud.gardener.camellia.authorization.authentication.main.exception.server.CasServerException
     */
    @Nullable
    P getPrincipal(String ticket) throws CasExceptions.CasException;
}
```

`CasAuthenticationService`是认证服务的一个代理，其调用`CasTicketService`的"getPrincipal"去验证票据并给出用户的登录名。这个登录名是`UserService`的"load"
方法加载用户信息用的。

# 主要异常

`InvalidTicketException`、`InvalidUserException`和`CasServerException`分别是当票据不正确、cas反馈用户状态不正常(如已经注销、冻结等)和cas服务本身有问题时的异常

# 总结

本文主要讲解了如何与cas进行联合登录认证，其主要是通过`CasTicketService`来验证登录票据从而获得用户的登录名
