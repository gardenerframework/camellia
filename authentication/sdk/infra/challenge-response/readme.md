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

# 调用应用与场景

从上文中可以发现，挑战以及上下文都需要进行存储。那么为了在不同场景以及被不同应用调用时，彼此之间的挑战以及上下文在即使id相同的情况下还能隔离， 场景以及应用id的会作为通用参数贯穿挑战与应答的生命周期

# ChallengeResponseService

```java
public interface ChallengeResponseService {
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
    Challenge sendChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull ChallengeRequest request
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
     * 给定的挑战是否已经通过了验证
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @return 是否已经完成了验证
     * @throws ChallengeResponseServiceException 检验过程中发生了问题
     */
    boolean isChallengeVerified(
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

作为挑战应答服务的基本接口，提供了服务具备的基本功能

# AbstractChallengeResponseService

`AbstractChallengeResponseService`为挑战应答服务提供了基本的cd检查，上下文存储等内置逻辑

## ChallengeStore

```java
/**
 * @author zhanghan30
 * @date 2023/2/20 18:07
 */
public interface ChallengeStore {
    /**
     * 存储挑战
     *
     * @param applicationId    应用id
     * @param scenario         场景
     * @param requestSignature 请求特征
     * @param challenge        挑战
     * @param ttl              有效期
     * @throws Exception 存储问题
     */
    void saveChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature,
            @NonNull Challenge challenge,
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
     * 读取挑战
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @return 挑战
     * @throws Exception 读取异常
     */
    @Nullable
    Challenge loadChallenge(
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
当缓存的挑战数据在有效期结束前被删除时，由于无法计算请求特征，因此请求特征对应的部分会遗留。但是下一个挑战发生时就会通过save方法进行覆盖

## ChallengeResponseCooldownManager

挑战的冷却时间是一个按照时间自然释放的资源，因此不具有释放方法。其只在发送请求时生效。 冷却的纬度包含了应用和场景以及一个从挑战请求计算而来的冷却计时器id


