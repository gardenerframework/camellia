# 多因子验证触发机制

认证引擎的`MfaAuthenticationListener`监听`UserAuthenticatedEvent`事件，并通过`MfaAuthenticationChallengeResponseService`
接口来将是否执行多因子验证的逻辑交由给开发人员决策

## MfaAuthenticationChallengeResponseService

```java
public interface MfaAuthenticationChallengeResponseService extends ChallengeResponseService<MfaAuthenticationChallengeRequest, Challenge> {
    @Nullable
    @Override
    @UsingContextFactory(MfaAuthenticationChallengeContextFactory.class)
    Challenge sendChallenge(MfaAuthenticationChallengeRequest request);
}
```

这个接口扩展了标准的`ChallengeResponseService`(挑战-应答服务接口)，当sendChallenge返回一个非`null`的`Challenge`对象时，引擎就认为应当执行mfa多因子验证

## MfaAuthenticationChallengeRequest

```java

public class MfaAuthenticationChallengeRequest extends ChallengeRequest {
    /**
     * 发起挑战时，用户使用的登录名
     */
    private final BasicPrincipal principal;
    /**
     * 认证过程中使用的上下文
     */
    private final Map<String, Object> context;
}
```

其中包含了当前用户正在使用的登录名以及事件中的上下文，其基类

```java
public class ChallengeRequest {
    /**
     * http请求头
     * <p>
     * 其中Authorization头已经被去掉，因为其中包含了access token或认证信息
     * <p>
     * http头用于给实现类一些基本的
     */
    private final MultiValueMap<String, String> headers;
    /**
     * 挑战相关的应用组
     */
    private final String clientGroup;
    /**
     * 挑战相关的请求客户端
     */
    @Nullable
    private final Client client;
    /**
     * 挑战相关的用户
     */
    @Nullable
    private final User user;
}
```

则主要包含了http头(比如用来分析风险和异地)以及用户信息

## Challenge

```java
public class Challenge {
    /**
     * 挑战id
     */
    private final String id;
    /**
     * 挑战的验证形式
     */
    private final String authenticator;
    /**
     * 挑战的过期时间，超过这个时间即认为挑战无效
     */
    private final Date expiresAt;
    /**
     * 其它额外参数，提示给客户端的
     */
    private Map<String, String> parameters;
}
```

上面是挑战对象的定义，包含了"题目"的id、使用验证形式，答题的有效时间和额外的参数

从业务理解上，`authenticator`
就相当于告诉应用端，应当启用什么多因子验证流程，这个流程的编码是开发人员自己定义的，流程的使用体验也是基于现场的需求定义。比如现场要求使用短信验证码进行多因子验证，那么开发团队决定给这个验证流程一个编码，称为"sms"
，于是当需要启动短信验证时，返回的挑战对象的authenticator就需要为`"sms"`，应用端看到"sms"类型的多因子验证后，按照产品定义的需求展示短信验证码的输入页面并将用户输入的值提交给认证服务器进行校验

## ChallengeResponseService

综述部分的文章已经讲解过，挑战和应答是一种可标准化抽象的流程，引擎将其定义为`ChallengeResponseService`

```java
public interface ChallengeResponseService<R extends ChallengeRequest, C extends Challenge> {
    /**
     * 给出当前挑战的cd计算key
     * <p>
     * 冷却时间可以按用户，或者按用户+应用组等自由决定
     * <p>
     * 冷却时间key的管理是由引擎负责的，在key存在时会不允许发送挑战
     * <p>
     * 如果基于请求的一些信息，觉得不需要进行冷却时间的检查，则可以选择生成空的key，这丫NG引擎就不会再检查
     *
     * @param request 挑战请求
     * @return cooldown 上线文，如果认为当前请求与冷却无关，不需要检查冷却逻辑，则返回null
     */
    @Nullable
    default String getCooldownKey(R request) {
        return null;
    }

    /**
     * 给出cd时间
     * <p>
     * 一般来说，挑战的cd都是固定的，比如什么短信验证码一分钟之类的
     * <p>
     * cd时间内不重新发送
     *
     * @return cd时间
     */
    default long getCooldown() {
        return 0;
    }

    /**
     * 发送挑战
     * <p>
     * 再次说明，单个个人或者客户端可能一直都在要求发送挑战
     * <p>
     * 因此需要思考什么时候重新生成挑战，什么时候发送之前未完成的
     *
     * @param request 请求对象
     * @return 挑战令牌.
     * 当认为不需要发送挑战时，可以为空，
     * 比如当前情况下不需要mfa认证，则可以发送一个空的挑战令牌表达放行请求
     */
    @Nullable
    @UsingContextFactory(DefaultChallengeContextFactory.class)
    C sendChallenge(R request);

    /**
     * 验证应答
     * <p>
     * 并且要求验证挑战环境
     *
     * @param id       挑战id
     * @param response 应答
     * @return 是否合法(只要不是合法都返回 false)
     * @throws InvalidChallengeException 当前挑战不合法，比如id不存在，比如ttl过期
     */
    @ValidateChallengeEnvironment
    boolean validateResponse(@ChallengeId String id, String response) throws InvalidChallengeException;

    /**
     * 关闭挑战，意味着回收与挑战相关的资源
     *
     * @param id 挑战id
     */
    void closeChallenge(@ChallengeId String id);
}
```

在定义上，包含了发送挑战(sendChallenge)，验证挑战(validateResponse)和关闭挑战释放资源(closeChallenge)的方法，同时也就相当于定义了挑战和应答的主要逻辑

## 短信验证码的示例

面对短信验证码的场景，本文给出一个示例来开阔开发人员的思路

```java
import java.time.Duration;
import java.util.UUID;

public class SmsCodeMfaAuthenticationChallengeResponseService implements MfaAuthenticationChallengeResponseService {
    @Override
    public Challenge sendChallenge(MfaAuthenticationChallengeRequest request) {
        //生产验证码
        String code = generateCode();
        String challengeId = UUID.randomUUID().toString();
        //定义一分钟有效
        Date expires = Duation.from(new Date()).plus(Duration.ofSeconds(60));
        smsClient.sendCode(request.getUser().getMobilePhoneNumber(), code);
        //缓存保存
        redisClient.set(challengeId, code, expires);
        //返回挑战
        return new Challenge(
                challengeId,
                "sms",
                expires,
                null
        );
    }
}
```

总结来说就是生成码->调短信客户端发送码->在redis中保存码

# 应答的提交

当用户作答完成时，应用端需要向"/login"(认证服务器自己的网页页面)或"/oauth2/token"接口提交以下标准化的参数

* authenticationType="mfa"
* challengeId=题目id
* response=应答

引擎内置的`MfaAuthenticationService`会处理这种类型的认证方法。有兴趣者可以自行阅读

# 多因子验证机制

`MfaAuthenticationService`要求`MfaAuthenticationChallengeResponseService`通过"validateResponse"方法来基于题目的id和应答来给出挑战是否通过的信号。
挑战通过后，认证引擎会从保存的上下文中取出完成认证的用户信息并自动设置用户的登录状态

# "万物皆可挑战"

多因子认证在传统意义上会被认为是发送一个短信、填写一根个动态令牌、刷一下脸等等。本文在此将其称为需要进行直接且简单的应答。然而，多因子验证显然能实现的逻辑不止如此。

考虑这样一个复杂的场景: 未成年登录一个在线商城，考虑到他可能使用家长的银行卡消费，在登录时需要以下复杂步骤

* 要求家长输入自己的登录密码
* 成功后要求家长再输入一个短信验证码
* 最后要求家长通过人脸识别
* 以上关卡全部通过后，未成年人登录成功

这个看起来非常麻烦的流程其实可以用多因子验证来解决。这是因为多因子验证就是卡在访问者登录成功之前的一次考试，没有人规定过考试只能有一道题

现在不妨假设定义authenticator=parent-consent

```plantuml
@startuml
!include https://s3.cn-south-1.jdcloud-oss.com/c4-plantuml/C4_Container.puml

Person(家长, 家长)
System(应用, 应用, 手机app)
System(认证服务器, 认证服务器, sso)
Boundary(mfa校验接口组, mfa接口组) {
    System(密码校验接口, 密码校验, 后台接口)
    System(短信验证码发送与校验, 短信验证码发送与校验, 后台接口)
    System(刷脸认证校验, 刷脸认证校验, 后台接口)
    System(应答token校验, 应答token校验接口, 后台接口)
}

家长 --> 应用: 2. 执行家长操作步骤
认证服务器 --> 应用: 1. authenticator=parent-consent

应用 --> 密码校验接口: 2.1 验证密码
应用 --> 短信验证码发送与校验: 2.2 验证短信
应用 --> 刷脸认证校验: 2.3 刷脸
应用 --> 应用: 3. 绑定challengeId和操作结果token

应用 --> 认证服务器: 4. response=操作结果token
认证服务器 --> 应答token校验: 5. 校验应答token
@enduml
```

* 认证服务器要求手机应用执行mfa，验证类型为"parent-consent"
* 手机基于认证类型弹出家长认证的第一步：输入密码
* 第二步发送短信并进行验证，注意此处和认证服务器的sms类型的多因子验证没有一毛钱关系
* 第三步由app唤起摄像头和前端SDK刷脸，并与后台接口比对人脸是否符合要求
* 全部验证完成，手机app将挑战id和一个token绑定在一起，token存在就代表挑战成功，为了防止重放，通常token还具有有效期
* 调用/oauth2/token接口传入挑战id和token，由`MfaAuthenticationChallengeResponseService`的编写者去查看应答的token是否还有效

到此，如果家长操作成功，认证引擎就因为未成年人登录成功，因为其完成了多因子登录过程

# 总结

本文讲解了多因子认证的挑战与应答机制，这一机制在认证引擎上也是开发可定制的，并甚至可以扩展出非常复杂的逻辑

# 继续阅读

[主要http接口与页面开发](主要http接口与页面开发.md)