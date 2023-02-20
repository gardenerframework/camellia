# 引言

挑战与应答是用来对操作人进行认证的常用手段，比如短信认证就是一种挑战与应答，认证方将一个短信单独发送给请求方，请求方通过填写正确的验证码来证明手机确实被自己持

# 基本机制

挑战与应答的基本机制是

* 发起方生成一个时效性的问题，并确保这个问题应答发能够回答。这个问题或者是应答方早已将答案告诉给发起方(比如密码保护、动态令牌等)，或者是发起方将答案通过秘密手段送达到应答方手中(短信验证码)；
* 发起方将挑战的内容暂时存储，其中可能包含了自己想要后续使用的其它上下文信息，比如应答方的某些数据；
* 应答方负责将答案以及匹配的挑战id发送给发起方，发起方检查答案是否符合预期，符合预期则标记应答已经完成；

# 基本对象

## ChallengeRequest

```java
public interface ChallengeRequest {
}
```

用来表达挑战的请求，开发人员需要明确请求中包含的属性并实现这个接口

## Challenge

```java

@SuperBuilder
@Getter
@Setter
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
     * 挑战的过期时间，超过这个时间即认为挑战无效
     */
    @NonNull
    private Date expiryTime;
    /**
     * 元数据，也就是额外信息
     */
    @Nullable
    private Map<String, String> metadata;
}
```

用来表达标准化的挑战实体，一个挑战一定有一个id和类型且明确表达过期时间。在这个基础上，如果挑战还需要向发起方传递额外的信息，则"metadata"属性用来表达这种额外信息

## ChallengeContext

```java

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ChallengeContext implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * 过期时间
     */
    private Date expiryTime;
    /**
     * 是否通过了验证
     */
    @Builder.Default
    private boolean verified = false;
}
```

挑战上下文，主要是挑战是否应答完成的标记

## 小结

从以上对象不难看出，开发人员首先要定义挑战的请求，请求中包含了生成挑战的所有必须的参数。 挑战生成后，一方面创建返回给调用放的响应对象(Challenge)，一方面定义校验挑战所需的上下文。
比如挑战是密码保护问题，则上下文中可以存储用户的id(用于读取)或直接就是密码保护问题的答案。 再比如挑战是要求输出动态令牌的值，那么上下文中就可以存储令牌的种子等

# 挑战的重放

当一个挑战被创建后，它一般需要等待一段时间才会过期。这时用户有可能因为各种各样的原因中断挑战与应答的流程，比如浏览器崩溃或关闭，比如手机app关闭等等。 当下一次需要发送挑战时，开发人员有2种选择：

1. 使用上一次的挑战内容进行重放，也就是继续沿用挑战id和过期时间等，而不是重新发送挑战，毕竟诸如短信渠道等都是按条收费
2. 生成新的挑战以及响应的上下文，重新发送挑战

# 挑战的cd

显而易见，调整在应答前应当考虑是否允许重新挑战，如果不允许，则必须要求完成上一个挑战才能进行新的。 对于已经重放的挑战，自然不需要检查cd，因为并没有新挑战生成。

# 不同调用方以及不同场景带来的存储隔离

每个挑战都有一个自己的id，但是说实话不能保证挑战的id在所有时刻都是不重复的。而且基于不同的应用，不同的场景可能还会对保存的挑战以及上下文的有效期进行不同的约定，比如手机app在订单场景下发送的短信验证码要求5分钟有效(挑战有效期)
，但是重发cd在30秒(cd时间)。网站在登录场景的邮箱验证码在10分钟有效，重发cd在1分钟等等，因此这时参与底层存储key计算的单元至少包含了应用程序以及场景，从而使得不同应用程序在不同的场景下可以获得不同的表现