# 简介

短信认证是登录、下单，支付等场景时经常用来确认用户当前身份和操作意愿的组件

# 验证码发送引擎

"sms-authentication-engine"组件提供了短信认证服务，该服务主要是发送验证码和检查验证码是否正确

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml

System(调用方1, 调用应用, application 1) #red
System(调用方2, 调用应用, application 2) #red

System(登录服务, 登录服务, api) #orange
System(订单服务, 订单服务, api) #orange

System(认证引擎, sms-authentication-engine, jar)
System(短信渠道, 短信渠道, api) #grey

调用方1 --> 登录服务
调用方2 --> 登录服务

调用方1 --> 订单服务
调用方2 --> 订单服务

订单服务 --> 认证引擎
登录服务 --> 认证引擎

认证引擎 --> 短信渠道

@enduml
```

作为短信认证引擎的sdk，自然会被多种服务和应用作为依赖组件使用，使用组件的api接口还会被多个应用调用。

## 应用和场景

作为底层依赖的组件，仅仅使用被发送的手机号作为唯一key来存储验证码显然是不合适的。比如

* 手机app在下单这个场景时发送了一个验证码
* 手机app在登录这个场景时发送了一个验证码
* 手机app在登录时进行多因子验证这个场景时发送了一个验证码
* 网页上在发起退单时发送了一个验证码

这些场景比如用途不一，接入系统使用的端不同，如果仅仅用手机号作为唯一的key，则旧的验证码会被新的进行覆盖从而造成运行时错误

因此应用(applicationId)、手机号(mobilePhoneNumber)、场景(scenario)3要素构成了存储验证码的联合主键

## 场景的定义感知

场景是一种枚举值，常规做法是定义字符串常量。然而不同开发人员彼此之间定义场景时使用字符串可能一样。这样当合入同一个工程，并使用同一个缓存或底层存储时，彼此并不能知道自己的定义干预了对方的定义。

于是引擎使用了类路径进行定义

* 第一显著降低了重复名称的可能
* 合入工程时因为类路径问题会编译报错

## SmsAuthenticationService

引擎对外提供的短信验证服务类，提供以下几个接口方法

```java
public class SmsAuthenticationService {

    /**
     * 发送验证码
     *
     * @param applicationId     应用程序id。用来分辨当前验证码是什么应用程序要求发出
     * @param mobilePhoneNumber 手机号
     * @param scenario          什么场景下发出
     * @param code              验证码
     * @param cooldown          冷却时间
     */
    public void sendCode(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String code,
            @NonNull Duration cooldown
    ) {
    }

    /**
     * 校验验证码是否正确
     *
     * @param applicationId     应用程序id。用来分辨当前验证码是什么应用程序要求发出
     * @param mobilePhoneNumber 手机号
     * @param scenario          什么场景下发出
     * @param code              验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String code
    ) {

    }

    /**
     * 获取验证码的剩余发送时间
     *
     * @param applicationId     应用服务id
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @return 剩余时间
     */
    @Nullable
    public Duration getTimeRemaining(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario
    ) {

    }

    /**
     * 如果现场需要进行验证码的编码，则覆盖本方法
     *
     * @param code 原来的验证码
     * @return 编码后的
     */
    @NonNull
    protected String encodeCode(@NonNull String code) {

    }
}
```

分别是发送、验证和获取发送剩余时间。当发送验证码时，服务类会自己判断是否还在发送cd中，如果是，则抛出`SmsAuthenticationInCooldownException`来中断发送。 开发人员可以捕捉该异常并获得cd的剩余时间。

## SmsAuthenticationCodeStore

这是服务类依赖的验证码基础工具，主要是验证码的存储，读取，cd剩余时间查询和移除等增删改查操作

```java
public interface SmsAuthenticationCodeStore {
    /**
     * 存储验证码，前提是当前应用场景下，该手机的上一个验证码还没过期
     *
     * @param applicationId     当前要发送的应用组(如果是oauth2的应用组，则需要有客户端访问凭据)
     * @param mobilePhoneNumber 要发送的手机号(服务层不管手机号是否是已经存在的用户，由前置逻辑检查)
     * @param scenario          场景
     * @param code              要求保存的验证码
     * @param ttl               有效期
     * @return 存储成功，不成功服务不会发送验证码，不成功的的可能有很多，比如同一时间点了多次，造成对同一手机号可能的多次发送
     * @throws Exception 透传产生的问题
     */
    boolean saveCodeIfAbsent(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario,
            String code,
            Duration ttl
    ) throws Exception;


    /**
     * 给出发送剩余时间
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @return 剩余时间
     * @throws Exception 透传产生的问题
     */
    @Nullable
    Duration getTimeRemaining(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario
    ) throws Exception;

    /**
     * 返回保存的短信验证码
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @return 验证码
     * @throws Exception 透传产生的问题
     */
    @Nullable
    String getCode(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario
    ) throws Exception;

    /**
     * 删除验证码，一般是验证码已经验证成功
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @throws Exception 透传产生的问题
     */
    void removeCode(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario
    ) throws Exception;
}
```

其中`saveCodeIfAbsent`明确要求实现方基于自己的验证码id计算逻辑检查验证码是否已经存储成功且没有过期。这确保了同一应用对同一手机号在同一个场景下，如果产生了并发问题发送了多个验证码，只有一个能成功和发送。
`removeCode`是服务类内部使用，其作用是当发送失败时删除保存的验证码。

## SmsAuthenticationClient

短信验证客户端负责的是触达，仅此而已

```java
public interface SmsAuthenticationClient {
    /**
     * 基于应用组，向指定的手机号发送短信
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 发给哪个手机
     * @param code              验证码
     * @param scenario          当前发送使用的场景
     * @throws Exception 透传产生的问题
     */
    void sendCode(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario,
            String code
    ) throws Exception;
}
```

发送时，客户端可以基于场景和应用选择不同的模板，比如"登录验证码为: [xxxx]"或者"支付验证码是: [xxxx]"

## 应用事件

当发送验证码前，发送完毕和发送失败时分别有应用事件通知(ApplicationEventPublisher)，有需要时使用`@EventListener`订阅应用事件

* `SmsAuthenticationAboutToSendEvent`: 发送前，可以订阅并对应用、场景、手机号进行流量控制等操作。必要情况下通过抛出异常中断发送过程
* `SmsAuthenticationSentEvent`和`SmsAuthenticationFailToSendEvent`: 分别表达发送成功和失败，可以用来实现比如和短信渠道的对账，监控失败率等记录功能

<font color=red>注意</font>: 这些事件反馈的是短信渠道的相关通知，即即将向短信渠道投递，投递完成和投递失败

# 已经完成的认证客户端

[clients](./clients)目录中实现了部分常见的短信下发渠道客户端

* [sms-authentication-client-jdcloud](./clients/sms-authentication-client-jdcloud)实现了京东云的短信对接

```java
public class JdCloudSmsAuthenticationClientSecurityOption {
    /**
     * key id
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String accessKeyId;
    /**
     * key
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String accessKey;
}
```

`JdCloudSmsAuthenticationClientSecurityOption`定义了对接所需的ak/sk，开发人员可以通过api选项托管能力设置这两个数据，可以编写配置类来在运行时修改选项的值。

```java
public interface JdCloudSmsTemplateProvider {
    /**
     * 返回签名id
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @return 签名id
     */
    String getSignId(String applicationId, Class<? extends Scenario> scenario);

    /**
     * 返回模板id
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @return 模板id
     */
    String getTemplateId(String applicationId, Class<? extends Scenario> scenario);
}
```

`JdCloudSmsTemplateProvider`使用应用id和场景作为输入，开发人员可以决策使用哪种签名和消息模板