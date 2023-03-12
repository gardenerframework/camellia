# SmsAuthenticationService

SmsAuthenticationService用于支持短信验证码的登录

```java

@AuthenticationType("sms")
@AllArgsConstructor
@SmsAuthenticationServiceComponent
public class SmsAuthenticationService implements UserAuthenticationService {
    private final Validator validator;
    private final SmsAuthenticationChallengeResponseService challengeResponseService;

    @Override
    public UserAuthenticationRequestToken convert(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        SmsAuthenticationParameter smsAuthenticationParameter = new SmsAuthenticationParameter(request);
        //执行验证
        smsAuthenticationParameter.validate(validator);
        return new UserAuthenticationRequestToken(
                MobilePhoneNumberPrincipal.builder().name(smsAuthenticationParameter.getMobilePhoneNumber()).build(),
                SmsVerificationCodeCredentials.builder().code(smsAuthenticationParameter.getCode()).build()
        );
    }

    @Override
    public void authenticate(
            @NonNull UserAuthenticationRequestToken authenticationRequest,
            @Nullable OAuth2RequestingClient client,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        try {
            SmsVerificationCodeCredentials credentials = (SmsVerificationCodeCredentials) authenticationRequest.getCredentials();
            //验证验证码
            if (!challengeResponseService.verifyResponse(
                    client, SmsAuthenticationScenario.class,
                    authenticationRequest.getPrincipal().getName(),
                    credentials.getCode()
            )) {
                //验证码不正确
                throw new BadSmsVerificationCodeException(credentials.getCode());
            }
            //验证成功，删除验证码以免重复使用
            challengeResponseService.closeChallenge(client, SmsAuthenticationScenario.class, authenticationRequest.getPrincipal().getName());
        } catch (ChallengeResponseServiceException e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }
}
```

可见它注册名为sms的认证类型，并要求配合SmsAuthenticationChallengeResponseService执行挑战应答服务

# SmsVerificationCodeEndpoint

```java

@AuthenticationServerRestController
@RequestMapping("/authentication/sms/code")
@SmsAuthenticationServiceComponent
@AllArgsConstructor
public class SmsVerificationCodeEndpoint {
    private final SmsAuthenticationChallengeResponseService service;

    @PostMapping
    public Challenge sendVerificationCode(
            @Valid @RequestBody SendSmsVerificationCodeRequest request
    ) throws ChallengeResponseServiceException {
        try {
            return service.sendChallenge(
                    null,
                    SmsAuthenticationScenario.class,
                    SmsAuthenticationChallengeRequest.builder().mobilePhoneNumber(request.getMobilePhoneNumber()).build()
            );
        } catch (ChallengeInCooldownException e) {
            //不能发送
            throw new SmsVerificationCodeNotReadyException(request.getMobilePhoneNumber(), e.getTimeRemaining());
        }
    }
}
```

SmsVerificationCodeEndpoint用来发送短信验证码，它也要求配合SmsAuthenticationChallengeResponseService使用

# SmsAuthenticationChallengeResponseService

```java

@SmsAuthenticationServiceComponent
public class SmsAuthenticationChallengeResponseService
        extends AbstractSmsVerificationCodeChallengeResponseService<
        SmsAuthenticationChallengeRequest,
        Challenge,
        SmsAuthenticationChallengeContext> {
    @NonNull
    private final SmsAuthenticationOption option;

    protected SmsAuthenticationChallengeResponseService(
            @NonNull GenericCachedChallengeStore challengeStore,
            @NonNull ChallengeCooldownManager challengeCooldownManager,
            @NonNull GenericCachedChallengeContextStore challengeContextStore,
            @NonNull SmsVerificationCodeClient smsVerificationCodeClient,
            @NonNull SmsAuthenticationOption option
    ) {
        super(challengeStore, challengeCooldownManager, challengeContextStore.migrateType(), smsVerificationCodeClient);
        this.option = option;
    }

    @Override
    protected Challenge createSmsVerificationChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull SmsAuthenticationChallengeRequest request,
            @NonNull Map<String, Object> payload
    ) {
        return Challenge.builder()
                //手机号就是挑战id
                .id(request.getMobilePhoneNumber())
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(option.getVerificationCodeTtl()))))
                .build();
    }

    @Override
    protected @NonNull SmsAuthenticationChallengeContext createSmsVerificationChallengeContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull SmsAuthenticationChallengeRequest request,
            @NonNull Challenge challenge,
            @NonNull Map<String, Object> payload
    ) {
        //验证码外面会搞定
        //fix 不填会报错
        return SmsAuthenticationChallengeContext.builder().code("")
                .build();
    }
}
```

SmsAuthenticationChallengeResponseService利用AbstractSmsVerificationCodeChallengeResponseService预定义的能力，并配合SmsVerificationCodeClient发送验证码。
验证码的有效期由SmsAuthenticationOption提供，默认是300秒

