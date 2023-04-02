# 简介

挑战与应答是用来对操作人进行认证的常用手段，比如短信认证就是一种挑战与应答，认证方将一个短信单独发送给请求方，请求方通过填写正确的验证码来证明手机确实被自己持有。

在当前模块中

# 基本机制

挑战与应答的基本机制是

* 发起方生成一个时效性的问题，并确保这个问题应答发能够回答。这个问题或者是应答方早已将答案告诉给发起方(
  比如密码保护、动态令牌等)，或者是发起方将答案通过秘密手段送达到应答方手中(短信验证码)；
* 发起方将挑战的内容暂时存储，其中可能包含了自己想要后续使用的其它上下文信息，比如应答方的手机号、生成答案所需的seed或线索等；
* 应答方负责将答案以及匹配的挑战id发送给发起方，发起方检查答案是否符合预期，符合预期则标记应答已经完成；

# 重新发送冷却与有效期

任何挑战都有一种需求，那就是重新生成挑战的时间距离上一次挑战不能低于一个给定的时限，即具有cd。比如邮箱验证码，短信验证码等不能频繁发送给用户。
当挑战发出后，用户需要一定的时间来应答挑战，这段可以答题的时间叫做挑战的有效期

# 基本对象

## ChallengeRequest

```java
public interface ChallengeRequest {
}
```

用来表达挑战的请求，开发人员需要明确请求中包含的属性并实现这个接口。接口能够提供的属性通常基于场景和挑战的玩法不同而不同。
比如短信验证的环节，请求参数是手机号，而邮箱验证码的请求参数自然是个邮箱地址

## Challenge

```java

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Challenge implements
        GenericTraits.IdentifierTraits.Id<String>,
        Serializable {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 挑战id
     */
    @NotBlank
    private String id;
    /**
     * 挑战的cd时间剩余
     */
    @Nullable
    private Date cooldownCompletionTime;
    /**
     * 挑战的过期时间，超过这个时间即认为挑战无效
     * <p>
     * 默认就是立刻过期
     */
    @NotNull
    private Date expiryTime;
}
```

用来表达标准化的挑战实体，一个挑战一定有一个识别它的id、cd的结束时间以及整个挑战的过期时间。

## ChallengeContext

```java

public interface ChallengeContext extends Serializable {
}
```

挑战上下文，用来存储挑战的一些基本数据从而当需要验证响应时使用

## 小结

从以上对象不难看出，开发人员首先要定义挑战的请求，请求中包含了生成挑战的所有必须的参数。
挑战生成后，一方面创建返回给调用放的响应对象(Challenge)，一方面定义校验挑战所需的上下文(ChallengeContext)。
比如挑战是密码保护问题，则上下文中可以存储用户的id(用于读取)或直接就是密码保护问题的答案。
再比如挑战是要求输出动态令牌的值，那么上下文中就可以存储令牌的种子等

# 调用应用与场景

从上文中可以发现，挑战以及上下文都需要进行存储。那么为了在不同场景以及被不同应用调用时，彼此之间的挑战以及上下文能被有效隔离，场景以及请求的客户端会作为通用参数贯穿挑战与应答的生命周期。

场景其实是在说当前挑战和应答在做什么，比如在进行MFA验证，还是在执行登录，还是在找回密码，还是在下单时进行二次验证。

# ChallengeResponseService

```java
public interface ChallengeResponseService<
        R extends ChallengeRequest,
        C extends Challenge,
        X extends ChallengeContext> {
    /**
     * 发送挑战
     *
     * @param client   正在请求的客户端
     * @param scenario 场景
     * @param request  挑战请求
     * @return 挑战结果
     * @throws ChallengeResponseServiceException 发送问题
     * @throws ChallengeInCooldownException      发送冷却未结束
     */
    C sendChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    ) throws ChallengeResponseServiceException, ChallengeInCooldownException;

    /**
     * 验证响应是否符合预期
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @param response    挑战响应
     * @return 是否通过校验
     * @throws ChallengeResponseServiceException 校验过程发生问题
     */
    boolean verifyResponse(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull String response
    ) throws ChallengeResponseServiceException;

    /**
     * 加载上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @return 上下文信息
     * @throws ChallengeResponseServiceException 加载出现问题
     */
    @Nullable
    X getContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;

    /**
     * 关闭挑战，即释放资源
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @throws ChallengeResponseServiceException 关闭过程中遇到了问题
     */
    void closeChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;
}
```

作为挑战应答服务的基本接口，提供了服务具备的基本功能以及上下文读取的功能。接口约定了范型，从而使得开发可以声明子类并按照业务的要求处理对应的请求和上下文。

此外不能保证挑战的api接口总是能获得请求客户端的信息，比如通过浏览器直接访问登录接口时，浏览器的ui前端并不会吃饱了撑的去完成浏览器的客户端认证过程，因此登录接口就没有RequestingClient

# AbstractChallengeResponseService

AbstractChallengeResponseService为挑战应答服务提供了基本的cd检查，上下文存储等内置逻辑，它将以下接口逻辑进行串联

## ChallengeResponseCooldownManager

挑战的冷却时间是一个按照时间自然释放的资源，因此不具有释放方法。其只在发送请求时生效。 冷却的纬度包含了应用和场景以及一个从挑战请求计算而来的冷却计时器id。

```java
public interface ChallengeCooldownManager {
    /**
     * 获取冷却的剩余时间
     *
     * @param client   正在请求的客户端
     * @param scenario 场景
     * @param timerId  冷却计时器id
     * @return 剩余时间
     * @throws Exception 发生问题
     */
    @Nullable
    Duration getTimeRemaining(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String timerId
    ) throws Exception;

    /**
     * 开始冷却
     *
     * @param client   正在请求的客户端
     * @param scenario 场景
     * @param timerId  计时器id
     * @param ttl      冷却时间
     * @return 是否由当前调用开始冷却(多并发场景应当只有一个冷却发生)
     * @throws Exception 发生问题
     */
    boolean startCooldown(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String timerId,
            @NonNull Duration ttl
    ) throws Exception;
}
```

## ChallengeContextStore

```java
public interface ChallengeContextStore<X extends ChallengeContext> {
    /**
     * 保存上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景id
     * @param challengeId 挑战id
     * @param context     场下问
     * @param ttl         有效期
     * @throws Exception 异常
     */
    void saveContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull X context,
            @NonNull Duration ttl
    ) throws Exception;

    /**
     * 加载上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @return 上下文
     * @throws Exception 发生问题
     */
    @Nullable
    X loadContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;


    /**
     * 删除上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @throws Exception 发生问题
     */
    void removeContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;
}
```

显而易见，为挑战应答服务提供上下文的存取功能，范型约定了支持的类型。同时引擎提供了`GenericCachedChallengeContextStore`
作为默认实现。这也是因为挑战上下文也可以基于java的序列化和反序列化特性保持类型。

## SaveInChallengeContext注解

在AbstractChallengeResponseService的支持下，挑战请求中可以使用SaveInChallengeContext来表达哪些属性被存储到上下文中。
AbstractChallengeResponseService按照

* 在上下文中必须具有相同名称的属性
* 属性类型是请求中的父类且实现了Serializable接口
* 开发人员在生成上下文时没有赋值( == null)
* 对应属性不是final或者静态变量

的原则，将请求中的属性保存到上下文

# 验证器的按名称注册

一个api接口内部可能会同时存在多个挑战应答服务，程序逻辑基于用户的一些属性决定调用正确的服务发出挑战。这时，作为程序的前端就不得不需要知道具体是哪种类型的挑战以便展示正确的页面。
因此提供ChallengeAuthenticator
注解和ChallengeAuthenticatorNameProvider，分别从静态注解的角度出发或者运行时的角度出发，返回挑战认证器的名称

# Challenge与名称的结合

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml


Person(用户, 用户)
System(浏览器, 浏览器, 客户端)
System(手机app, 手机app, 客户端)
System(小程序, 小程序, 客户端)

System(接口, 接口, api)

用户 --> 浏览器
用户 --> 手机app
用户 --> 小程序

手机app --> 接口
浏览器 --> 接口
小程序 --> 接口

@enduml
```

上图中，用户使用手机app调用接口进行订单操作，接口为了确保用户是自己实际在操作对用户要求执行mfa(一种挑战应答)
，接口自己内部基于用户的属性决策使用短信验证码的方法进行挑战，此时接口内部要有要求用户进行刷脸验证的验证器。
挑战被发出后用户的手机受到了验证码，但是app端需要提供给用户一个界面以及提示来告诉用户输入手机验证码而不是调用摄像头进行刷脸。
于是接口除了返回挑战信息外还额外需要告诉手机app挑战的名字。

面对这种要求，ChallengeAuthenticatorUtils类型提供了支持，它使用proxy动态代理challenge数据与ChallengeAuthenticatorNameProvider接口结合，使得challenge在被诸如objectMapper序列化的时候能够携带"
challengeAuthenticatorName"属性

# 无请求客户端的处理

在引擎中，如果没有请求客户端，则一概默认使用`NullRequestingClientIdProvider`提供的虚假客户端id作为存储key处理

# 总结

`ChallengeResponseService`
主要应当积累挑战与应答的发送和验证的核心逻辑。当业务场景不同时，基于业务的需求定义挑战请求`ChallengeRequest`
与上下文对象`ChallengeContext`。 以及用于按类型加载的`ChallengeResponseService`的子接口，
业务端基于spring的类型加载规则加载所需了的`ChallengeResponseService`
子接口实例并且将请求与上下文以及挑战内容交给具体的实例开发人员实现逻辑。