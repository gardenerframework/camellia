# 引言

挑战与应答是用来对操作人进行认证的常用手段，比如短信认证就是一种挑战与应答，认证方将一个短信单独发送给请求方，请求方通过填写正确的验证码来证明手机确实被自己持

# 基本机制

挑战与应答的基本机制是

* 发起方生成一个时效性的问题，并确保这个问题应答发能够回答。这个问题或者是应答方早已将答案告诉给发起方(比如密码保护、动态令牌等)，或者是发起方将答案通过秘密手段送达到应答方手中(短信验证码)；
* 发起方将挑战的内容暂时存储，其中可能包含了自己想要后续使用的其它上下文信息，比如应答方的某些数据；
* 应答方负责将答案以及匹配的挑战id发送给发起方，发起方检查答案是否符合预期，符合预期则标记应答已经完成；

# 重新发送冷却与有效期

任何挑战都有一种需求，那就是重新生成挑战的时间距离上一次挑战不能低于一个给定的时限，即具有cd。比如邮箱验证码，短信验证码等不能频繁发送给用户。 当挑战发出后，用户需要一定的时间来应答挑战，这段可以答题的时间叫做挑战的有效期

# 基本对象

## ChallengeRequest

```java
public interface ChallengeRequest {
}
```

用来表达挑战的请求，开发人员需要明确请求中包含的属性并实现这个接口。接口能够提供的属性通常基于场景不同而不同。 比如短信验证的环节，在登录请求时提供的属性是当前登录的用户信息或直接是手机号。
在订单环节则可能提供的是用户id，还需要去读取用户身份信息。因此挑战应答服务的开发需要处理多种形式的挑战请求，并从中获取对自己有用的数据

## Challenge

```java
public class Challenge implements
        GenericTraits.IdentifierTraits.Id<String>,
        GenericTraits.GroupingTraits.Type<String>,
        Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * 挑战id
     */
    @NonNull
    private String id;
    /**
     * 挑战的类型，比如是短信验证码，还是动态令牌
     */
    @NonNull
    private String type;
    /**
     * 挑战的cd时间剩余
     */
    @Nullable
    private Date cooldownCompletionTime;
    /**
     * 挑战的过期时间，超过这个时间即认为挑战无效
     */
    @NonNull
    private Date expiryTime;
}
```

用来表达标准化的挑战实体，一个挑战一定有一个识别它的id、cd的结束时间以及整个挑战的过期时间。 至于类型，其的用途如下

* 当挑战的发起方明确知道需要什么类型的挑战应答模式时这个属性就没有必要，比如找回密码时提供了短信/邮箱验证码以及动态令牌3个模式，用户指定了动态令牌，于是挑战的类型就确定了，后续也必然使用动态令牌的形式发起挑战，此时类型属性就无所谓
* 当挑战方不清楚当前挑战是哪种类型时，就需要这个属性完成与前端的交互。
  比如mfa认证提供了动态令牌、短信验证码等多个手段，而具体采取什么手段是后台按照人员配置的，这时挑战就只得基于人员的属性进行动态的调用，于是前端也需要这个属性来展示正确的页面

## ChallengeContext

```java

public interface ChallengeContext extends Serializable {
}
```

挑战上下文，用来存储挑战的一些基本数据从而当需要验证响应时使用

## 小结

从以上对象不难看出，开发人员首先要定义挑战的请求，请求中包含了生成挑战的所有必须的参数。 挑战生成后，一方面创建返回给调用放的响应对象(Challenge)，一方面定义校验挑战所需的上下文。
比如挑战是密码保护问题，则上下文中可以存储用户的id(用于读取)或直接就是密码保护问题的答案。 再比如挑战是要求输出动态令牌的值，那么上下文中就可以存储令牌的种子等

# 挑战的重放

当一个挑战被创建后，它一般需要等待一段时间才会过期。这时用户有可能因为各种各样的原因中断挑战与应答的流程，比如浏览器崩溃或关闭，比如手机app关闭等等。 当下一次需要发送挑战时，开发人员有2种选择：

1. 使用上一次的挑战内容进行重放，也就是继续沿用挑战id和过期时间等，而不是重新发送挑战，毕竟诸如短信渠道等都是按条收费
2. 生成新的挑战以及响应的上下文，重新发送挑战

# 逻辑的聚合与业务多样化之间冲突的解决

既然定义了挑战与应答作为基础设施组件，那么自然是希望常见的挑战主逻辑以及应答的校验聚合在服务代码中解决，比如短信的发送和校验，比如动态令牌的校验，比如人脸的认证识别等。
但是从上文的数据定义不难发现，挑战的请求是基于业务去定义的，比如短信挑战需要手机号，比如动态令牌的挑战可能需要记录人员的令牌seed等。且基于不同场景，可能还有不同的参数要求和上下文的存储需求。 因此挑战应答服务内部更多的是聚合核心的逻辑。
基于不同应用需求的调用，传递的上下文以及请求应当是由业务需求去定义，并通过范型的方法声明需要能够处理这种请求以及上下文的挑战应答服务。更进一步，可以将这种范型直接声明为一个接口，要求直接装载明确接口的实现。比如

* MFA验证场景的挑战应答服务接口
* 短信登录的挑战应答接口
* 找回密码时的短信验证码挑战应答接口

等等

这样能够明确地令开发知晓当前的代码在做什么，以防挑战应答服务被错误地装载到不正确的逻辑中的问题发生

# 调用应用与场景

从上文中可以发现，挑战以及上下文都需要进行存储。那么为了在不同场景以及被不同应用调用时，彼此之间的挑战以及上下文在即使id相同的情况下还能隔离， 场景以及应用id的会作为通用参数贯穿挑战与应答的生命周期。
场景其实是在说当前挑战和应答在做什么，比如在进行MFA验证，还是在执行登录，还是在找回密码，还是在下单时进行二次验证。

通过上文不难看出，其实挑战与应答的接口实例其实一定程度上表达了场景的概念(基于不同需求定义不同的子接口)
。但是，为了避免同一个接口实例为多个场景同时服务，造成无法分辨的情况发生；同时，场景是外部需求定义的，而不是接口实现类来具体定义的，因此接口依然大量的使用了场景参数的方式，由调用方来决定场景。

# ChallengeResponseService

```java
public interface ChallengeResponseService<
        R extends ChallengeRequest,
        C extends Challenge,
        X extends ChallengeContext> {
    /**
     * 发送挑战
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param request       挑战请求
     * @return 挑战结果
     * @throws ChallengeResponseServiceException 发送问题
     * @throws ChallengeInCooldownException      发送冷却未结束
     */
    C sendChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    ) throws ChallengeResponseServiceException, ChallengeInCooldownException;

    /**
     * 验证响应是否符合预期
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @param response      挑战响应
     * @return 是否通过校验
     * @throws ChallengeResponseServiceException 校验过程发生问题
     */
    boolean verifyResponse(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull String response
    ) throws ChallengeResponseServiceException;

    /**
     * 加载上下文
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @return 上下文信息
     * @throws ChallengeResponseServiceException 加载出现问题
     */
    @Nullable
    X getContext(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;

    /**
     * 关闭挑战，即释放资源
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @throws ChallengeResponseServiceException 关闭过程中遇到了问题
     */
    void closeChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;
}
```

作为挑战应答服务的基本接口，提供了服务具备的基本功能以及上下文读取的功能。接口约定了范型，从而使得开发可以声明子类并按照业务的要求处理对应的请求和上下文。

此外不难看出，接口并不保留挑战是否已经被成功验证过一次的状态。理由是在成功验证状态保存到使用挑战id+业务参数执行逻辑前有一段空档期，恶意代码可以利用这段空档期没有实际地完成挑战但可以执行希望的业务操作，比如重置密码

# AbstractChallengeResponseService

`AbstractChallengeResponseService`为挑战应答服务提供了基本的cd检查，上下文存储等内置逻辑，它将以下接口逻辑进行串联

## ChallengeStore

```java
/**
 * @author zhanghan30
 * @date 2023/2/20 18:07
 */
public interface ChallengeStore<C extends Challenge> {
    /**
     * 保存挑战id与请求特征的对应关系
     *
     * @param applicationId    应用id
     * @param scenario         场景
     * @param requestSignature 请求特征
     * @param challengeId      挑战id
     * @throws Exception 发生问题
     */
    void saveChallengeId(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature,
            @NonNull String challengeId,
            @NonNull Duration ttl
    ) throws Exception;

    /**
     * 返回挑战id
     *
     * @param applicationId    应用id
     * @param scenario         场景
     * @param requestSignature 请求特征
     * @return 对应的挑战id
     */
    @Nullable
    String getChallengeId(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature
    );

    /**
     * 存储挑战
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @param challenge     挑战
     * @param ttl           有效期
     * @throws Exception 存储问题
     */
    void saveChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull C challenge,
            @NonNull Duration ttl
    ) throws Exception;

    /**
     * 读取挑战
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @return 挑战
     * @throws Exception 读取异常
     */
    @Nullable
    C loadChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;

    /**
     * 移除挑战
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @throws Exception 移除异常
     */
    void removeChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;
}
```

`ChallengeStore`为挑战的重放提供辅助，它基于请求的特征以及应用和场景来存储已经生成的挑战。 从调用方法上不难看出，首先希望将请求特征与保存的挑战id进行绑定，随后基于挑战id读取暂存的数据，并在关闭挑战时删除暂存的数据。
当缓存的挑战数据在有效期结束前被删除时，由于无法计算请求特征，因此请求特征对应的部分会遗留。但是下一个挑战发生时就会通过save方法进行覆盖。

接口约定了范型，用于表达特定挑战的存取。这样让开发在序列化和反序列化时能够明确的知晓类型。 引擎为开发提供了一个`GenericCachedChallengeStore`
，其基于java的序列化接口完成挑战的存储和读取，java的序列化在存储时会保存目标类型信息，因此任何`Challenge`的子类都能通过这个接口完成序列化和反序列化

## ChallengeResponseCooldownManager

挑战的冷却时间是一个按照时间自然释放的资源，因此不具有释放方法。其只在发送请求时生效。 冷却的纬度包含了应用和场景以及一个从挑战请求计算而来的冷却计时器id。

```java
public interface ChallengeCooldownManager {
    /**
     * 获取冷却的剩余时间
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param timerId       冷却计时器id
     * @return 剩余时间
     * @throws Exception 发生问题
     */
    @Nullable
    Duration getTimeRemaining(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String timerId
    ) throws Exception;

    /**
     * 开始冷却
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param timerId       计时器id
     * @param ttl           冷却时间
     * @return 是否由当前调用开始冷却(多并发场景应当只有一个冷却发生)
     * @throws Exception 发生问题
     */
    boolean startCooldown(
            @NonNull String applicationId,
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
     * @param applicationId 应用id
     * @param scenario      场景id
     * @param challengeId   挑战id
     * @param context       场下问
     * @param ttl           有效期
     * @throws Exception 异常
     */
    void saveContext(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull X context,
            @NonNull Duration ttl
    ) throws Exception;

    /**
     * 加载上下文
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @return 上下文
     * @throws Exception 发生问题
     */
    @Nullable
    X loadContext(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;


    /**
     * 删除上下文
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @throws Exception 发生问题
     */
    void removeContext(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;
}
```

显而易见，为挑战应答服务提供上下文的存取功能，范型约定了支持的类型。同时引擎提供了`GenericCachedChallengeContextStore`作为默认实现。这也是因为挑战上下文也可以基于java的序列化和反序列化特性保持类型。

# 总结

`ChallengeResponseService`主要应当积累挑战与应答的发送和验证的核心逻辑。当业务场景不同时，基于业务的需求定义挑战请求`ChallengeRequest`与上下文对象`ChallengeContext`。
以及用于按类型加载的`ChallengeResponseService`的子接口， 业务端基于spring的类型加载规则加载所需了的`ChallengeResponseService`
子接口实例并且将请求与上下文以及挑战内容交给具体的实例开发人员实现逻辑。