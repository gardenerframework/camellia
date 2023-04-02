# 工程模块

"authorization-server-engine"中定义了认证引擎的主要逻辑和代码，"authorization-server-common"则定义了工程引擎所需要的主要接口和数据结构。
如果开发人员是要实现和封装某种登录凭据的认证方式或支持某种MFA认证，可以选择引入"authorization-server-common"而不是"
authorization-server-engine"，
"authorization-server-engine"原则上是当需要实现一个完整的认证服务器时才使用的

# 用户认证接口与相关概念

```java
public interface UserAuthenticationService {
    /**
     * 将http请求转为登录请求对象
     * <p>
     * 如果开发人员认为应当让登录页导向错误页面并定义错误的内容，抛出{@link AuthenticationException}
     * <p>
     * 其它类型的异常都将被解释为认证服务内部错误
     *
     * @param request http 请求
     * @return 登录请求对象
     * @throws AuthenticationException 如果认为当前认证过程应当中断，则抛出异常，
     *                                 比如{@link BadAuthenticationRequestParameterException}来表示参数有问题
     */
    UserAuthenticationRequestToken convert(HttpServletRequest request)
            throws AuthenticationException;

    /**
     * 执行认证
     *
     * @param authenticationRequest 页面发来的认证请求
     * @param user                  用户详情
     * @throws AuthenticationException 有问题抛异常
     */
    void authenticate(UserAuthenticationRequestToken authenticationRequest, User user)
            throws AuthenticationException;
}
```

上面是用户认证接口的定义，不难看出它在系统中执行2个关键操作

* 接受http请求，从中读取客户端或页面提交的参数并转换为一个`UserAuthenticationRequestToken`对象
* 在用户读取出来后，基于自己转换的`UserAuthenticationRequestToken`以及读取出来的`User`执行登录凭据的检验

下面给出一个校验使用用户名和密码作为登录方式的例子

```java
import java.util.Objects;

public class UsernamePasswordAuthenticationService implements UserAuthenticationService {
    private final UsernameResolver resolver;
    private final Validator validator;

    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest httpServletRequest)
            throws AuthenticationException {
        UsernamePasswordParameter parameter = new UsernamePasswordParameter(
                httpServletRequest
        );
        Set<ConstraintViolation<Object>> violations = validator.validate(parameter);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
        return new UserAuthenticationRequestToken(
                new UsernamePrincipal(parameter.getUsername()),
                //明确使用密码类型的凭据，要求走authenticate方法
                new PasswordCredential(parameter.getPassword())
        );
    }

    /**
     * 进行密码之间的比较
     *
     * @param authenticationRequest 认证请求
     * @param user                  认证用户
     * @throws AuthenticationException 认证有问题
     */
    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user)
            throws AuthenticationException {
        if (!Objects.equals(authenticationRequest.getCredentials(), user.getCredentials())) {
            throw new BadCredentialsException(user.getId());
        }
    }
}
```

* convert方法生成了一个`UserAuthenticationRequestToken`，以`UsernamePrincipal`作为登录名的类型，`PasswordCredential`
  作为登录凭据的类型
* authenticate方法则简单的比较了用户的登录凭据和参数提交的一致

## 登录认证请求

在上文中出现了一个新的概念`UserAuthenticationRequestToken`，这个类在登录引擎中代表了用户的抽象化的登录请求

```java
public class UserAuthenticationRequestToken {
    /**
     * 用户登录的凭据
     * 可以是用户名
     * 可以是手机号
     * 或人脸id
     * 或别的什么东西
     */
    private final BasicPrincipal principal;
    /**
     * 提交的密码或其它登录信息
     * <p>
     * 密码等是不会被序列化的
     */
    private final BasicCredential credentials;
}
```

在引擎中，用户的登录名不再被限定为用户名这一简单的字符串类型的形式、而是通过`BasicPrincipal`来表达不同类型的登录名称，打开"
authorization-server-common"，能够至少找到以下类型

* `EmailPrincipal`: 以邮箱作为登录名
* `MembershipCardIdPrincipal`: 以会员卡id作为登录名
* `MobilePhoneNumberPrincipal`: 以手机号作为登录名

以不同java类型作为登录名，便于后续的程序逻辑通过`instance of`得到确定的登录名的类型，并基于类型可以查询数据库中账户表中的不同的字段从而实现检索账户的功能。
因此`UserAuthenticationService`
在实现时，需要仔细考虑应当向后面传递何种java类型的登录名作为`UserAuthenticationRequestToken`的参数使用。

类型的，用户的登录凭据也不单纯地被限定为密码这一简单的形式，通过`BasicCredential`
可以表达不同类型的登录凭据，比如短信验证码、比如邮箱验证码等。后续的逻辑能够通过`instance of`
获得确定的凭据类型从而实现一些自己希望的逻辑。

## throws AuthenticationException

用户认证接口的两个方法都标记了抛出`AuthenticationException`，这个异常是由spring security(引擎使用的基础组件)
使用，用于标记认证过程出现了问题，需要中断流程，比如

```js
if (request.getParameter("username") == null) {
    throw new BadAuthenticationParameterException("username");
}
```

上文假定了convert方法中查找用户名参数并如果发现没有提交则抛出错误参数的问题从而中断流程

<font color=green>提示</font>: 使用异常中断逻辑是java编程中的一种常见方法论

## 按需扩展

认证引擎多种多样的登录认证方式，每一种登录认证方式需要编写一个专用的`UserAuthenticationService`
来处理，比如上面的用户名密码登录方式就具有单独的`UserAuthenticationService`
。目前引擎已经内置的认证方式有

* "authorization-server-authentication-engine-username": 用户名密码形式
* "authorization-server-authentication-engine-sms": 短信认证码形式
* "authorization-server-authentication-engine-qrcode": 二维码形式
* "authorization-server-authentication-engine-cas": 第三方cas认证系统

如果以上登录认证的方式不满足需要，开发人员可以按需扩展`UserAuthenticationService`并在实现类上注解上`AuthenticationType`
来声明一个全新的认证方式。
相关的认证方式会由引擎的`AuthenticationTypeRegistry`通过扫描bean的方式感知并加入到注册表。加入成功后，开发人员可以通过"GET
/api/options"查看是否注册成功
(json path "$.options.authenticationTypeRegistry.option.types")

```json
{
  "options": {
    "authenticationTypeRegistry": {
      "option": {
        "types": [
          "注册的类型编码"
        ]
      },
      "name": "com.jdcloud.gardener.camellia.authorization.authentication.configuration.AuthenticationTypeRegistry",
      "readonly": true,
      "versionNumber": null,
      "description": "认证类型注册表"
    }
  }
}
```

注册成功后，客户端就可以在页面的登录接口"/login"或oauth2的访问令牌接口"/oauth2/token"中通过参数`authenticationType`
指定使用的认证方式，引擎从注册表中取出对应的bean来处理认证请求

# 用户数据服务与相关概念

```java
public interface UserService {

    /**
     * 向统一用户数据访问接口发起认证请求
     * <p>
     * 如果密码有错则抛出{@link BadCredentialsException}
     *
     * @param principal   登录凭据名
     * @param credentials 密码
     * @param context     认证上下文中的共享属性，
     *                    新加了{@link Nullable}注解，如果发现上下文是null，说明当前用户服务正用于其它用途。
     *                    比如用于密码找回场景，或者其他非认证场景
     * @return 认证完毕的用户信息，如果不存在则返回{@code null}
     * @throws AuthenticationException 认证有问题
     */
    @Nullable
    User authenticate(BasicPrincipal principal, PasswordCredential credentials, @Nullable Map<String, Object> context) throws AuthenticationException;

    /**
     * 基于请求凭据去读取而不是认证用户，其余错误按需转为{@link AuthenticationException}
     * <p>
     *
     * @param principal 登录凭据
     * @param context   认证上下文中的共享属性，
     *                  新加了{@link Nullable}注解，如果发现上下文是null，说明当前用户服务正用于其它用途。
     *                  比如用于密码找回场景，或者其他非认证场景
     * @return 用户信息，如果不存在则返回{@code null}
     * @throws AuthenticationException       认证有问题
     * @throws UnsupportedOperationException 如果当前服务对接的用户存储根本不支持直接通过登录名查找用户
     */
    @Nullable
    User load(BasicPrincipal principal, @Nullable Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException;
}
```

用户数据服务的作用是使用认证服务提交的登录名和登录凭据去用户数据库中检索用户数据。按照现代化的登录系统的设计，一般来说用户数据库会有单独的api接口服务和认证服务器进行分离，如下图所示

```plantuml
@startuml
!include https://s3.cn-south-1.jdcloud-oss.com/c4-plantuml/C4_Container.puml

System(认证服务器, 认证服务器, sso)
System(用户数据后台接口, 用户数据后台接口, api)
System(数据存储, 数据存储, database)

认证服务器 --> 用户数据后台接口
用户数据后台接口 --> 数据存储

@enduml
```

在此，用户数据服务就通过http或者其它rpc方式调用用户数据后台接口检索用户数据。

## authenticate or load

其中部分用户数据后台接口要求检索用户前必须提交用户密码，于是authenticate方法就用于这种场景，其在认证引擎中的逻辑是

```js
if (credentials instanceof PasswordCredential) {
    userService.authenticate(principal, credentials, context);
} else {
    userService.load(principal, context);
}
```

引擎基于类型判断登录凭据的类型，如果是密码类型则调用authenticate，否则调用load。这非常符合逻辑，用户登录都没有提交什么密码，那也没有可以提交给后台数据服务用于认证的密码。

## throws AuthenticationException

部分用户数据服务(如ldap或ad域控)
的java客户端当用户的登录凭据不正确或者用户的状态处于锁定等时抛出异常来代替返回错误数据。因此认证引擎认为将这些异常规整化引擎能够理解的`AuthenticationException`
的工作由用户数据服务承担。此外，如果用户存储本身就没有查找到用户或接口出现问题抛出了http
500，将这些问题规整化引擎能够理解的`AuthenticationException`
的工作同样由用户数据服务承担。

## throws UnsupportedOperationException

既然有些用户接口必须要求获得用户密码来进行认证，那么这些用户接口显然不支持load方法，因此如果后台对接了这些用户接口则load方法可以简单抛出`UnsupportedOperationException`
来告诉引擎这个操作不支持

# 总结

本文主要讲解了登录认证过程中使用到的核心接口和作用，一句话总结来说，在`UserAuthenticationService`的"convert"和"
authenticate"方法之间，`UserService`需要使用"
authenticate"或"load"方法去加载用户数据。 加载用户数据所需的登录名由`UserAuthenticationService`
提供，`UserAuthenticationService`的"authenticate"
所需的用户信息则由`UserService`提供。

最后，认证方式支持二次开发，如果引擎目前已有的认证方式不能满足需要，开发人员可以自行定制`UserAuthenticationService`来进行扩展

# 继续阅读

[登录处理过程中的事件体系与上下文传递](登录处理过程中的事件体系与上下文传递.md)