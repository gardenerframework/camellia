# 挑战请求和上下文

```java
public interface SmsVerificationCodeChallengeContext extends ChallengeContext,
        GenericTraits.IdentifierTraits.Code<String> {
}

public interface SmsVerificationCodeChallengeRequest extends ChallengeRequest,
        MankindTraits.ContactTraits.MobilePhoneNumber {
}

```

请求中要有手机号，上下文保存了验证码

# AbstractSmsVerificationCodeChallengeResponseService

AbstractSmsVerificationCodeChallengeResponseService是AbstractChallengeResponseService的扩展，它封装了以下主要逻辑

* 声明不支持挑战重发，要求cd一到就重新发送验证码
* 支持验证码的生成，默认是6位数字，子类可以自己定义generateCode方法来完成自己的生成逻辑，该方法的参数有客户端和场景，因此子类能够大致基于这些参数实现一个自定义的逻辑，比如对某个应用生成包含字母和数字的，其它的则都是字母的等等
* 声明验证码发送有cd时间，默认为60秒，符合验证码的常规cd
* 将generateCode生成的验证码自动放入ChallengeContext中保存
* 输入的验证码默认和上下文中保存的进行校验

子类要求实现的方法是createSmsVerificationChallenge和createSmsVerificationChallengeContext，也就是基于具体实现的挑战和上下的类型生成对象

# SmsVerificationCodeClient

```java
public interface SmsVerificationCodeClient {
    /**
     * 发送验证码
     *
     * @param client            客户端
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @param code              验证码
     * @throws Exception 发送异常
     */
    void sendVerificationCode(
            @Nullable RequestingClient client,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String code
    ) throws Exception;
}

```

AbstractSmsVerificationCodeChallengeResponseService调用短信验证码客户端进行发送，实现类可以通过客户端、手机号、场景等进行限流等操作

# 事件

SmsVerificationCodeAboutToSendEvent、SmsVerificationCodeSentEvent、SmsVerificationCodeSendingFailedEvent分别是短信客户端调用前，调用失败、调用成功的3个事件。
如果短信的限流政策等和渠道无关，也可以在这里实现

# 内置客户端

[clients](clients)内置了部分对接好的短信渠道