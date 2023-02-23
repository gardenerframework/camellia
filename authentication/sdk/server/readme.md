# 引言

本组件负责认证服务器的引擎实现

# 一览图

## 不同类型用户的认证需求

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

Person(最终用户, 最终用户) 
Person(企业员工, 企业员工) #green

System(网页端, 网页端, 浏览器) 
System(手机app, 手机app, 手机app) 

Boundary(登录服务, 认证服务, api) {
    System(认证服务引擎, 认证服务引擎, 引擎组件) #gray
}

System(网页端2, 网页端, 浏览器) #green
System(手机app2, 手机app, 手机app) #green

Boundary(登录服务2, 认证服务, api) {
    System(认证服务引擎2, 认证服务引擎, 引擎组件)
}

System(最终用户数据库, 最终用户数据库, 身份源)

System(企业员工数据库, 企业员工数据库, 身份源) #green

最终用户 --> 网页端
最终用户 --> 手机app

网页端 --> 登录服务
手机app --> 登录服务

登录服务 --> 最终用户数据库

企业员工 --> 网页端2
企业员工 --> 手机app2

网页端2 --> 登录服务2
手机app2 --> 登录服务2

登录服务2 --> 企业员工数据库

@enduml
```

上图首先简介了常见的C端用户与平台企业员工使用认证系统的例子。每一个认证服务都可以使用当前认证服务引擎进行开发，并分别连接最终用户的数据库和企业员工的数据库。

## 引擎内部结构

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml


Boundary(认证服务引擎, 认证服务引擎, sdk) {
    Boundary(基本认证组件1, 基本认证组件) {
        System(认证组件1, 用户密码, 认证组件)
        System(认证组件2, 短信, 认证组件)
        System(认证组件3, 人脸, 认证组件)
    }
    Boundary(多因子认证1, 多因子认证组件) {
        System(多因子认证组件1, 短信, 认证组件)
        System(多因子认证组件2, 邮箱, 认证组件)
        System(多因子认证组件3, 动态密码, 认证组件)
    }
    System(事件监听器, 事件监听器, 监听器)
}

@enduml
```

打开引擎的内部，主要由事件监听器、多因子认证组件以及核心的基本认证组件构成，基本认证组件基于Spring
Security的功能完成用户登录凭据的校验；多因子认证组件则基于挑战与应答实现用户登录确认前的最终验证；
事件监听器负责监听认证主流程中发出的事件，并通过抛出异常的方式中断认证流程

# 基本数据结构和核心接口

## 客户端

当第三方应用向iam申请用户信息时都需要由iam颁发给它一个客户端凭据以及能够访问的用户信息的范围和许可的授权申请方法

```java
public class Client implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * client id
     */
    private final String clientId;
    /**
     * 访问的授权类型
     */
    private final String grantType;
    /**
     * 对用户信息的访问范围
     */
    private final Set<String> scopes;
    /**
     * 客户端元数据，用于开发人员在客户端内保存自己的一些所需数据
     * <p>
     * 第一级key是provider的类路径，值是提供的元数据
     */
    private final Map<String, Map<String, String>> metadata = new ConcurrentHashMap<>();
}
```

该对象是对Spring Security中的`RegisteredClient`的一个摘要，因为原对象中带有客户端密码等敏感信息且必须引入Spring
Security组件才能使用。

`metadata`是由开发人员向对象中写入的元数据，其第一个key是`ClientMetadataProvider`的类型，值是由provider提供的数据。

## 元数据的作用

客户端元数据随着客户端数据贯穿整个认证过程，认证过程中的事件监听，登录和授权的保存等都能够基于元数据定制业务逻辑

## 主体

主体是代表即将认证的东西，可能是个客户端，可能是个用户。不过由于Spring Security中对于客户端有独立的对象表达，因此在此基本用来表达待认证的用户

## Principal

`Principal`代表的是用户提交的登录名称或识别符号，如用户名、员工卡号、会员卡号、手机号等。不同的类型用这个类型的子类代表。

```java
public abstract class Principal implements java.security.Principal,
        Serializable, GenericTraits.LiteralTraits.Name {
    private static final long serialVersionUID = Version.current;
    /**
     * 名字
     */
    private String name;
}
```

其中的主要属性是名称

## Credentials

`Credentials`代表用户提交的登录凭据，比如密码，短信验证码等，同样不同的类型也需要使用子类来表达

## Subject

```java
public abstract class Subject implements Serializable,
        GenericTraits.IdentifierTraits.Id<String>,
        GenericTraits.StatusTraits.LockFlag,
        GenericTraits.StatusTraits.EnableFlag {
    private static final long serialVersionUID = Version.current;
    /**
     * id
     */
    private String id;
    /**
     * 登录凭据
     * <p>
     * 这个玩意有时候无法获得，比如ldap的登录就不会告诉你用户的密码，只是校验密码
     * <p>
     * 因此可以将登录凭据写成用户输入的那个
     */
    @Nullable
    private transient Credentials credentials;
    /**
     * 用户的所有可用登录名
     */
    private Collection<Principal> principals;
    /**
     * 被锁定
     */
    private boolean locked;
    /**
     * 激活状态
     */
    private boolean enabled;
    /**
     * 密码过期事件
     */
    @Nullable
    private Date credentialsExpiryDate;
    /**
     * 当前subject的过期时间
     */
    @Nullable
    private Date subjectExpiryDate;

    //其余的属性实现类自己添加

    /**
     * 由引擎负责调用，在不需要的时候擦除密码
     */
    public final void eraseCredentials() {
        this.credentials = null;
    }
}
```

主体完整代表一个登录的对象，可能是个人，可能是个代码，其具有id作为自己的唯一标识。包含一组登录名称(至少具有一个登录名)
和一个登录凭据

## 用户

用户是主体的一个类型

```java
public class User extends Subject implements
        GenericTraits.LiteralTraits.Name,
        AccountTraits.VisualTraits.Avatar {
    private static final long serialVersionUID = Version.current;
    /**
     * 任何形式的展示名称
     * <p>
     * 昵称，姓名随便
     */
    @Nullable
    private String name;
    /**
     * 任何形式的显示图标
     */
    @Nullable
    private String avatar;
}
```

除去主体的基本属性外，额外扩展了名字和头像

## 认证请求

```java
public abstract class AuthenticationRequestParameter {
    protected AuthenticationRequestParameter(HttpServletRequest request) {
    }
}
```

`AuthenticationRequestParameter`为登录认证请求提供了参数的基本型，其构造函数要求输入`HttpServletRequest`
，意思是告诉各个认证组件的开发人员从请求中直接解析参数

## UserAuthenticationService

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
    UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException;

    /**
     * 执行认证
     *
     * @param authenticationRequest 页面发来的认证请求
     * @param user                  用户详情
     * @throws AuthenticationException 有问题抛异常
     */
    void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException;
}
```

用户认证服务是IAM认证系统的核心接口，它表达两个意图

* 将http请求转为用户认证请求
* 执行认证，如果没有抛出任何异常则认为认证通过

## UserAuthenticationRequestToken

```java
public class UserAuthenticationRequestToken {
    /**
     * 用户登录的凭据
     * 可以是用户名
     * 可以是手机号
     * 或人脸id
     * 或别的什么东西
     */
    @NonNull
    private final Principal principal;
    /**
     * 提交的密码或其它登录信息
     * <p>
     * 密码等是不会被序列化的
     */
    @NonNull
    private final Credentials credentials;

    public UserAuthenticationRequestToken(@NonNull Principal principal) {
        this.principal = principal;
        //登录凭据设置为空
        this.credentials = new EmptyCredentials();
    }
}
```

在用户认证请求中可见需要给定登录名以及登录凭据

## UserAuthenticatedAuthentication

```java
public class UserAuthenticatedAuthentication extends AbstractAuthenticationToken {
    /**
     * 完成认证的用户
     */
    @Getter
    private final User user;

    public UserAuthenticatedAuthentication(User user) {
        super(Collections.emptyList());
        super.setAuthenticated(true);
        this.user = user;
    }

    /**
     * 永远不会返回密码
     *
     * @return {@literal null}
     */
    @Override
    @Nullable
    public final Object getCredentials() {
        return null;
    }

    /**
     * 当前认证过的用户作为主体
     *
     * @return 用户主体
     */
    @Override
    public User getPrincipal() {
        return user;
    }

    /**
     * 当前登录用户的名称
     *
     * @return 当前认证用户的id
     */
    @Override
    public String getName() {
        return this.user.getId();
    }
}
```

经过验证后的信息中显著保存了被认证的用户，此外因为用户已经认证完成因此不会再给出任何登录凭据。最后，依照Spring
Security的约定，将用户的id作为识别符号返回

## AbstractUserAuthenticationService

```java
public abstract class AbstractUserAuthenticationService<P extends AuthenticationRequestParameter>
        implements UserAuthenticationService {
    /**
     * 验证器
     */
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final Validator validator;

    /**
     * 从请求中获取认证请求参数
     *
     * @param request 请求
     * @return 参数
     */
    protected abstract P getAuthenticationParameter(HttpServletRequest request);

    /**
     * 从转换的参数中进行转换
     *
     * @param authenticationParameter 认证参数
     * @return 认证请求
     */
    protected abstract UserAuthenticationRequestToken doConvert(P authenticationParameter);

    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException {
        P authenticationParameter = Objects.requireNonNull(getAuthenticationParameter(request));
        Set<ConstraintViolation<Object>> violations = validator.validate(authenticationParameter);
        if (!CollectionUtils.isEmpty(violations)) {
            //执行检查参数合法性
            throw new BadAuthenticationRequestParameterException(violations);
        }
        return Objects.requireNonNull(doConvert(authenticationParameter));
    }
}
```

`AbstractUserAuthenticationService`为用户的认证服务提供了与`AuthenticationRequestParameter`的串联逻辑支持。
它要求子类去创建参数对象，然后利用`Validator`
进行校验。因此，`AuthenticationRequestParameter`的子类可以使用类似`@NotBlank`
等验证注解而不需要自行在逻辑中进行判断。验证失败抛出`BadAuthenticationRequestParameterException`
，它是`AuthenticationException`的一个子类。会被Spring
Security框架处理。

## AuthenticationType & AuthenticationEndpoint

认证服务需要声明它的类型`AuthenticationType`注解，其是一个字符串，建议使用一个单词简单的表达当前认证的方法，比如"
username"、"sms"、"qrcode"等。 不同类型的认证服务器将通过提交的参数重的"
authentication_type"
属性来进行请求路由。引擎将确保调用类型对应的认证服务。目前类型与服务是1:1对应关系

`AuthenticationEndpoint`注解则使得认证服务能够声明自己支持的认证端点。认证服务器引擎有2个端点，如下图所示

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

System(浏览器, 浏览器)
System(app, app)

System(网页认证端点, 网页认证端点, "/login")
System(app认证端点, app认证端点, "/oauth2/token")
System(认证服务, 认证服务)

浏览器 --> 网页认证端点
app --> app认证端点

网页认证端点 --> 认证服务
app认证端点 --> 认证服务

@enduml
```

2个端点在Spring
Security中对应着不同的Filter，在Filter中则调用相同的认证服务。那么，为了使得认证服务器能够声明自己支持的端点，可以标记上`AuthenticationEndpoint`

比如需求上人脸识别仅仅支持app端，那么就可以如下声明

```java

@AuthenticationEndpoint(Endpoint.OAUTH2)
public class FaceAuthenticationService implements UserAuthenticationService {
    //...
}
```

## UserService

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
    User authenticate(Principal principal, PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException;

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
    User load(Principal principal, @Nullable Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException;
}
```

`UserServvice`利用`UserAuthenticationService`输出的`UserAuthenticationRequestToken`中的`principal`
读取用户信息，它可以直接读数据库，也可以调用远程接口。
特别是，如果对接的接口要求必须提交用户名密码才能完成认证那么`UserAuthenticationRequestToken`中也包含了`credentials`。
在引擎的逻辑上，如果发现`credentials`
是`PasswordCredentials`，则优先调用`authenticate`，如果不是`PasswordCredentials`，则调用`load`

# 事件

在认证过程中为了开发人员能够插入自己的逻辑来中断认证过程或记录一些日志，引擎提供了以下事件:

```java
public abstract class AuthenticationEvent {
    /**
     * http请求头
     * <p>
     * 其中Authorization头已经被去掉，因为其中包含了access token或认证信息
     * <p>
     * http头用于给实现类一些基本的请求判断逻辑，特别是检查UserAgent判断是不是手机端，以及来源ip等
     */
    @NonNull
    private final MultiValueMap<String, String> headers;
    /**
     * 认证方式
     */
    @NonNull
    private final String authenticationType;
    /**
     * 登录请求的用户名以及类型
     */
    @NonNull
    private final Principal principal;
    /**
     * 但前准备要访问系统的客户端
     * <p>
     * 不是token endpoint 没有客户端
     */
    @Nullable
    private final Client client;
    /**
     * 贯穿登录认证过程的上下文
     * <p>
     * 可以用来存取一些属性
     */
    @NonNull
    private final Map<String, Object> context;
}
```

`AuthenticationEvent`是认证事件的基类，包含了大量认证过程中的属性和上下文。

`ClientAuthenticatedEvent`是认证过程的开始事件，如果客户端使用oauth2提供的接口进行认证(授权接口或token接口)
则一定会有客户端信息，这个客户端信息通过了Spring Security的认证后就会发送该事件

`UserAboutToLoadEvent`是即将调用`UserService`进行用户数据加载的事件，在加载前可以检查用户名是否已经在黑名单中或者密码错误次数过多还在封锁

`UserLoadedEvent`是用户信息加载成功的事件，加载完毕后即将使用`UserAuthenticationService.authenticate`方法进行登录凭据的校验

`UserAuthenticatedEvent`显然是校验通过后的事件，可以进一步检查用户的数据是否合法，比如是否封锁，比如是否被冻结

`AuthenticationSuccessEvent`是认证过程成功的标志

`AuthenticationFailedEvent`是认证过程中捕捉到任何异常导致中断的标志

## CareForAuthorizationEnginePreservedPrincipal & CareForAuthorizationEnginePreservedException

部分登录名和异常的类型是引擎内部使用的，通常来说开发人员不需要关注。当需要关注时，在事件监听上添加以上注解

# Api分组与选项

### AuthenticationServerRestController

这个注解用于给认证服务器上的，非Spring Security的表单提交类型的rest接口提供分组功能。

分组后，这些接口可以享受fragrans的api分组功能并服从`AuthenticationServerPathOption`的统一前缀路径设置

### AuthenticationServerPathOption

```java

@ApiOption(readonly = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class AuthenticationServerPathOption {
    /**
     * rest api的上下文路径
     */
    private String restApiContextPath = "/api";
    /**
     * 网页端登录入口的地址
     * <p>
     * 只有POST会处理
     */
    private String webAuthenticationEndpoint = "/login";
    /**
     * 登录网页的地址
     */
    private String webLoginPage = "/";
    /**
     * 登录网页的地址
     */
    private String webLoginSuccessPage = "/welcome";
    /**
     * web应用类型的认证错误跳向的错误页面
     */
    private String webAuthenticationErrorPage = "/error";
    /**
     * 需要mfa多因子验证时转向的页面
     */
    private String webMfaChallengePage = "/mfa";
    /**
     * 成功登出后的跳转地址
     */
    private String webLogoutPage = "/goodbye";
    /**
     * 网页登出地址
     * <p>
     * 什么请求类型可以
     */
    private String webLogoutEndpoint = "/logout";

    /**
     * oauth2的授权申请接口
     */
    private String oAuth2AuthorizationEndpoint = "/oauth2/authorize";
    /**
     * oauth2的授权批准网页地址
     */
    private String oAuth2AuthorizationConsentPage = "/consent";
    /**
     * oauth2的令牌接口
     */
    private String oAuth2TokenEndpoint = "/oauth2/token";
    /**
     * oidc的用户信息接口
     */
    private String oidcUserInfoEndpoint = "/userinfo";
}
```

可见路径配置中设置了很多接口的地址或前缀，这些前缀目前还不支持修改

# 认证处理入口

引擎的认证处理入口有`WebAuthenticationEntryProcessingFilter`和`OAuth2TokenEndpointFilter`两个，在默认情况下分别对应"
/login"和"/oauth2/token"(
附送路径配置选项)

# LoginAuthenticationRequestConverter

上文的2个认证入口都会使用`LoginAuthenticationRequestConverter`作为用户的登录认证请求的转换器。
它主要将http请求转为`LoginAuthenticationRequestToken`认证请求

```java
public class LoginAuthenticationRequestToken implements Authentication {
    private final transient UserAuthenticationRequestToken userAuthenticationRequestToken;
    /**
     * 当前要进行用户访问的客户端
     * <p>
     * {@link OAuth2ClientAuthenticationFilter}认证了client id和密码
     * 别的没有认证
     */
    @Nullable
    private final transient OAuth2ClientUserAuthenticationToken clientUserAuthenticationRequestToken;
    /**
     * 客户端组
     */
    private final transient String clientGroup;
    /**
     * 认证请求的上下文，主要是给{@link LoginAuthenticationRequestAuthenticator}用的
     */
    private final transient LoginAuthenticationRequestContext context;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return userAuthenticationRequestToken.getCredentials();
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Principal getPrincipal() {
        return userAuthenticationRequestToken.getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        //没有认证当然返回false
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        //do nothing
    }

    @Override
    public String getName() {
        return getPrincipal().getName();
    }
}
```

这个请求是交给Spring Security引擎的认证请求类型，包含了客户端信息、用户认证请求信息以及上下文信息

```java
public class LoginAuthenticationRequestContext {
    /**
     * 由哪个服务来完成认证过程
     */
    private final UserAuthenticationService userAuthenticationService;
    /**
     * 携带的http请求
     */
    private final HttpServletRequest httpServletRequest;
}
```

可见传递的上下文就是负责转换请求的用户认证服务以及http请求

# UserAuthenticationServiceRegistry

在`LoginAuthenticationRequestConverter`
的转换逻辑中使用到了用户认证服务的注册表。注册表收取所有`UserAuthenticationService`
类型的bean，提取`AuthenticationType`注解中表达的类型，以及`AuthenticationEndpoint`
注解中表达的支持的认证接口，并最后判断是否具有`AuthorizationEnginePreserved`
注解。 注册表在注册的过程中会检查`AuthenticationType`是否已经被注册，如果已经被注册则会阻止引擎启动

通过注册表的分析，以及搭配`AuthenticationTypeParameter`从http请求中读取"authenticationType"
属性，`LoginAuthenticationRequestConverter`
就能从请求中分析出需要的用户认证服务并存储到`LoginAuthenticationRequestContext`中。

特别的，`AuthenticationTypeParameter`的`authenticationType`属性具有`AuthenticationTypeSupported`注解，因此如果提交的认证类型不被支持则会报错

# AuthenticationTypeRegistry

利用`UserAuthenticationServiceRegistry`，`AuthenticationTypeRegistry`为api选项注入目前注册的所有认证类型的功能

