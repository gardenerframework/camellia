# 引言

本组件负责认证服务器的引擎实现

# 一览图

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

Person(最终用户, 最终用户) 
Person(企业员工, 企业员工) #green

System(网页端, 网页端, 浏览器) 
System(手机app, 手机app, 手机app) 

System(登录服务, 认证服务, api) {
    System(认证组件1, 用户密码, 认证组件) #orange
    System(认证组件2, 短信, 认证组件) #orange
    System(认证组件3, 人脸, 认证组件) #orange
    System(认证服务引擎, 认证服务引擎, 引擎组件) #gray
}

System(网页端2, 网页端, 浏览器) #green
System(手机app2, 手机app, 手机app) #green

System(登录服务2, 认证服务, api) #green {
    System(认证组件4, 用户密码, 认证组件) #orange
    System(认证组件5, 短信, 认证组件) #orange
    System(认证组件6, 人脸, 认证组件) #orange
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

企业内部的C端用户、企业员工都需要进行身份认证。对于不同的使用场景一般架设不同的认证服务实例。尽管实例不同，但都可以使用"authentication-server-engine"作为引擎进行开发。
并配合引擎定义和抽象的认证组件来完成多种多样的认证方法接入

# IAM

认证服务器引擎本质上提供的就是基于oauth2标准的集中身份认证能力

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

该对象是对Spring Security中的`RegisteredClient`的一个摘要，因为原对象中带有客户端密码等敏感信息且必须引入Spring Security组件才能使用。

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

主体完整代表一个登录的对象，可能是个人，可能是个代码，其具有id作为自己的唯一标识。包含一组登录名称(至少具有一个登录名)和一个登录凭据

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

`AuthenticationRequestParameter`为登录认证请求提供了参数的基本型，其构造函数要求输入`HttpServletRequest`，意思是告诉各个认证组件的开发人员从请求中直接解析参数

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

经过验证后的信息中显著保存了被认证的用户，此外因为用户已经认证完成因此不会再给出任何登录凭据。最后，依照Spring Security的约定，将用户的id作为识别符号返回

