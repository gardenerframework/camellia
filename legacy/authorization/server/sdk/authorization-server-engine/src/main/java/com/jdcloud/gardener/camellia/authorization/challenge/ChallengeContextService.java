package com.jdcloud.gardener.camellia.authorization.challenge;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.RegisteredClientProxy;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticatedAuthentication;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.OAuth2GrantTypeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.OAuth2ScopeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ChallengeId;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.UsingContextFactory;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ValidateChallenge;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ValidateChallengeEnvironment;
import com.jdcloud.gardener.camellia.authorization.challenge.event.schema.ValidateChallengeEnvironmentEvent;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.ChallengeCoolingDownException;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeEnvironment;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.authorization.common.utils.HttpRequestUtils;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.Expired;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Read;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Validate;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对{@link ChallengeResponseService}的动态代理
 * <p>
 * 主要是实现一下全局使用的统一逻辑
 *
 * @author ZhangHan
 * @date 2022/5/15 22:23
 */
@Aspect
@Slf4j
@Component
public class ChallengeContextService implements ApplicationEventPublisherAware, ChallengeContextAccessor {
    private final AccessTokenDetails accessTokenDetails;
    private final ChallengeContextCacheManager challengeContextCacheManager;
    private final ChallengeCooldownCacheManager challengeCooldownCacheManager;
    private final ClientGroupProvider clientGroupProvider;
    private final Map<Class<? extends ChallengeContextFactory<? extends ChallengeRequest, ? extends ChallengeContext>>, ChallengeContextFactory<? extends ChallengeRequest, ? extends ChallengeContext>> challengeContextFactories = new ConcurrentHashMap<>();
    private ApplicationEventPublisher eventPublisher;

    @SuppressWarnings("unchecked")
    public ChallengeContextService(AccessTokenDetails accessTokenDetails, ChallengeContextCacheManager challengeContextCacheManager, ChallengeCooldownCacheManager challengeCooldownCacheManager, ClientGroupProvider clientGroupProvider, Collection<ChallengeContextFactory<? extends ChallengeRequest, ? extends ChallengeContext>> challengeContextFactories) {
        this.accessTokenDetails = accessTokenDetails;
        this.challengeContextCacheManager = challengeContextCacheManager;
        this.challengeCooldownCacheManager = challengeCooldownCacheManager;
        this.clientGroupProvider = clientGroupProvider;
        challengeContextFactories.forEach(
                challengeContextFactory -> this.challengeContextFactories.put((Class<? extends ChallengeContextFactory<? extends ChallengeRequest, ? extends ChallengeContext>>) challengeContextFactory.getClass(), challengeContextFactory)
        );
    }

    /**
     * 拦截所有服务类的所有公有方法执行
     *
     * @param proceedingJoinPoint 切点
     * @return 原方法的返回值
     * @throws Throwable 执行过程中发生的异常
     */
    @Around("target(com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService)")
    public Object proxyAllChallengeResponseService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Assert.isTrue(proceedingJoinPoint.getTarget() instanceof ChallengeResponseService, "target is not a ChallengeResponseService instance");
        //类型符合，且切入的是个方法
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        //处理ValidateEnv注解
        ValidateChallengeEnvironment validateChallengeEnvironment = AnnotationUtils.findAnnotation(signature.getMethod(), ValidateChallengeEnvironment.class);
        if (validateChallengeEnvironment != null) {
            handleValidateChallengeEnvironmentAnnotation(proceedingJoinPoint, signature);
        }
        //处理要求挑战已经验证通过的注解
        ValidateChallenge validateChallenge = AnnotationUtils.findAnnotation(signature.getMethod(), ValidateChallenge.class);
        if (validateChallenge != null) {
            handleValidateChallenge(proceedingJoinPoint, signature);
        }
        //当前方法是发送挑战
        if (Challenge.class.isAssignableFrom(signature.getMethod().getReturnType()) && "sendChallenge".equals(signature.getMethod().getName())) {
            return onSendChallenge(proceedingJoinPoint, signature);
        }
        //验证应答接口
        if (boolean.class.isAssignableFrom(signature.getMethod().getReturnType()) && signature.getMethod().getName().equals("validateResponse")) {
            return onValidateResponse(proceedingJoinPoint, getChallengeId(proceedingJoinPoint, signature));
        }
        if (void.class.isAssignableFrom(signature.getMethod().getReturnType()) && signature.getMethod().getName().equals("closeChallenge")) {
            //fixed 之前会和下面的一起调用2次closeChallenge
            return onCloseChallenge(proceedingJoinPoint, getChallengeId(proceedingJoinPoint, signature));
        }
        return proceedingJoinPoint.proceed();
    }

    /**
     * 处理校验环境注解
     *
     * @param proceedingJoinPoint 切点
     * @param signature           方法签名
     */
    private void handleValidateChallengeEnvironmentAnnotation(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature) {
        String challengeId = getChallengeId(proceedingJoinPoint, signature);
        ChallengeContext context = getContext((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        if (context == null) {
            //上下文已过期，无法进行验证
            throw new InvalidChallengeException(challengeId);
        }
        RegisteredClient registeredClient = getRegisteredClient();
        eventPublisher.publishEvent(
                new ValidateChallengeEnvironmentEvent(
                        challengeId,
                        context.getRequest(),
                        new ChallengeEnvironment(
                                HttpRequestUtils.getSafeHttpHeaders(getHttpServletRequest()),
                                clientGroupProvider.getClientGroup(registeredClient),
                                getClient(),
                                getUser()
                        )
                )
        );
    }

    /**
     * 检查挑战是否已经被成功应答
     *
     * @param proceedingJoinPoint 切点
     * @param signature           方法签名
     */
    private void handleValidateChallenge(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature) {
        String challengeId = getChallengeId(proceedingJoinPoint, signature);
        ChallengeContext context = getContext((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        if (context == null || !context.isVerified()) {
            throw new InvalidChallengeException(challengeId);
        }
    }

    /**
     * 获取挑战id
     *
     * @param proceedingJoinPoint 切点
     * @param signature           方法签名
     * @return 挑战id
     */
    private String getChallengeId(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature) {
        String challengeId = null;
        Set<Method> overrideHierarchy = MethodUtils.getOverrideHierarchy(signature.getMethod(), Interfaces.INCLUDE);
        for (Method method : overrideHierarchy) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int index = 0;
            for (Annotation[] annotations : parameterAnnotations) {
                for (Annotation parameterAnnotation : annotations) {
                    if (parameterAnnotation instanceof ChallengeId) {
                        challengeId = (String) proceedingJoinPoint.getArgs()[index];
                        break;
                    }
                }
                index++;
            }
        }
        Assert.notNull(challengeId, "cannot determine challengeId. use @ChallengeId on correct parameter");
        return challengeId;
    }

    /**
     * 尝试去获得用户
     * <p>
     * 网页登录入口则看看从安全上下文中直接读取
     * <p>
     * token登录入口没有用户登录信息
     * <p>
     * 常规api则尝试从AccessTokenDetails中取
     *
     * @return 当前用户
     */
    @Nullable
    private User getUser() {
        //尝试从安全上下文中读取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserAuthenticatedAuthentication) {
            GenericLoggerStaticAccessor.operationLogger().debug(
                    log,
                    GenericOperationLogContent.builder()
                            .what(UserAuthenticatedAuthentication.class)
                            .operation(new Read())
                            .state(new Done())
                            .detail(new SecurityContextHoldingAuthenticationDetail(((UserAuthenticatedAuthentication) authentication).getUser().getId()))
                            .build(),
                    null
            );
            return ((UserAuthenticatedAuthentication) authentication).getUser();
        }
        //没有则查看access token
        return accessTokenDetails.getUser();
    }

    /**
     * 尝试获取访问的客户端
     * <p>
     * 当前如果是网页登录接口，则基本不会有registerClient
     * <p>
     * token接口则会有(因为要提交client id & secret)
     * <p>
     * 其它api接口则尝试看token是否提供，提供了就从token信息中读取
     *
     * @return 客户端
     */
    @Nullable
    private RegisteredClient getRegisteredClient() {
        //尝试从安全上下文中读取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2ClientAuthenticationToken) {
            //代理一下，禁止访问客户端密码
            GenericLoggerStaticAccessor.operationLogger().debug(
                    log,
                    GenericOperationLogContent.builder()
                            .what(OAuth2ClientAuthenticationToken.class)
                            .operation(new Read())
                            .state(new Done())
                            .detail(new SecurityContextHoldingAuthenticationDetail(Objects.requireNonNull(((OAuth2ClientAuthenticationToken) authentication).getRegisteredClient()).getClientId()))
                            .build(),
                    null
            );
            return RegisteredClientProxy.proxy(((OAuth2ClientAuthenticationToken) authentication).getRegisteredClient());
        }
        //没有则尝试从token的信息中读取
        return accessTokenDetails.getRegisteredClient();
    }

    /**
     * 尝试获取访问的客户端
     * <p>
     * 当前如果是网页登录接口，则基本不会有client
     * <p>
     * token接口则会有(因为要提交client id & secret)
     * <p>
     * 其它api接口则尝试看token是否提供，提供了就从token信息中读取
     *
     * @return 客户端
     */
    @Nullable
    private Client getClient() {
        Client client = accessTokenDetails.getClient();
        if (client != null) {
            return client;
        }
        RegisteredClient registeredClient = getRegisteredClient();
        if (registeredClient != null) {
            OAuth2GrantTypeParameter grantTypeParameter = new OAuth2GrantTypeParameter(getHttpServletRequest());
            OAuth2ScopeParameter scopeParameter = new OAuth2ScopeParameter(getHttpServletRequest());
            return new Client(
                    registeredClient.getClientId(),
                    grantTypeParameter.getGrantType(),
                    new HashSet<>(scopeParameter.getScopes())
            );
        }
        return null;
    }

    /**
     * 当要求发送挑战时
     *
     * @param proceedingJoinPoint 切点
     * @param signature           方法签名
     * @return 结果
     * @throws Throwable 应该没有
     */
    @SuppressWarnings("unchecked")
    protected Challenge onSendChallenge(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature) throws Throwable {
        ChallengeRequest request = (ChallengeRequest) proceedingJoinPoint.getArgs()[0];
        //检查是不是cd时间还没到
        //fixed 挑战被应答后，cd时间没有结束导致下一个不需要发挑战的逻辑因上一个cd时间没有结束无法正确工作
        //举例，短信验证通过后，用户正常登录系统，但关闭挑战不结束cd时间，所以基于这个用户的1分钟的cd还在
        //这时认证逻辑要求发送尝试挑战，于是代理类在这检查cd时间，发现cd时间还没结束，于是不允许登录
        String cooldownKey = getCooldownKey((ChallengeResponseService<ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), request);
        if (cooldownKey != null) {
            //发送之前首先检查cd
            validateCooldown((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), cooldownKey);
        }
        //生成请求，同时注意可能是重放的
        Challenge challenge = (Challenge) proceedingJoinPoint.proceed();
        //挑战发送才有下面的逻辑
        if (challenge != null) {
            //证明挑战真实发送了
            //挑战真实发送了才开始cd - 都没发挑战cd什么
            UsingContextFactory annotation = AnnotationUtils.findAnnotation(signature.getMethod(), UsingContextFactory.class);
            Assert.notNull(annotation, "@UsingContextFactory must present on sendChallenge method");
            ChallengeContextFactory<ChallengeRequest, ? extends ChallengeContext> challengeContextFactory = (ChallengeContextFactory<ChallengeRequest, ? extends ChallengeContext>) challengeContextFactories.get(annotation.value());
            Assert.notNull(challengeContextFactory, "bean of type" + annotation.value() + " not presents");
            ChallengeContext context = challengeContextFactory.createContext(request, challenge);
            Duration ttl = Duration.between(Instant.now(), challenge.getExpiresAt().toInstant());
            if (!this.challengeContextCacheManager.setCacheIfNotPresents((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challenge.getId(), context, ttl)) {
                //这是一次重放，不需要改变任何东西
                GenericLoggerStaticAccessor.basicLogger().debug(
                        log,
                        GenericBasicLogContent.builder()
                                .what(challenge.getClass())
                                .how(new Word() {
                                    @Override
                                    public String toString() {
                                        return "重放";
                                    }
                                }).detail(new Detail() {
                                    private final String id = challenge.getId();
                                }).build(),
                        null
                );
                //而且也不需要进入cd
            } else {
                //只有非重放才进入cd
                if (cooldownKey != null) {
                    //具有冷却上下文才开始cd
                    startCooldown((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), cooldownKey);
                }
            }
        }
        return challenge;
    }

    /**
     * 要验证应答是否合法的时候
     *
     * @param proceedingJoinPoint 切点
     * @param challengeId         挑战id
     * @return 是否合法
     * @throws Throwable 问题
     */
    protected boolean onValidateResponse(ProceedingJoinPoint proceedingJoinPoint, String challengeId) throws Throwable {
        //读一下也所谓的，而且前面验证如果比较耗时，则其实这里就已经过期了也就不需要校验应答了
        ChallengeContext context = getContext((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        if (context == null) {
            //上下文已过期，无法进行验证
            throw new InvalidChallengeException(challengeId);
        }
        if (context.isVerified()) {
            GenericLoggerStaticAccessor.basicLogger().debug(
                    log,
                    GenericBasicLogContent.builder()
                            .what(ChallengeContext.class)
                            .how(new Word() {
                                @Override
                                public String toString() {
                                    return "已完成验证";
                                }
                            }).detail(new Detail() {
                                private final String id = challengeId;
                            }).build(),
                    null
            );
            throw new InvalidChallengeException(challengeId);
        }
        boolean valid = (boolean) proceedingJoinPoint.proceed();
        if (valid) {
            GenericLoggerStaticAccessor.operationLogger().debug(
                    log,
                    GenericOperationLogContent.builder()
                            .what(ChallengeContext.class)
                            .operation(new Validate())
                            .state(new Done())
                            .detail(new Detail() {
                                private final String id = challengeId;
                            }).build(),

                    null

            );
            context.setVerified(true);
            Duration ttl = Duration.between(Instant.now(), context.getExpiresAt().toInstant());
            if (ttl.getSeconds() <= 0) {
                //不需要更新了，直接删除上下文就行
                GenericLoggerStaticAccessor.basicLogger().debug(
                        log,
                        GenericBasicLogContent.builder()
                                .what(ChallengeContext.class)
                                .how(new Expired())
                                .detail(new Detail() {
                                    private final String now = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(new Date());
                                    private final String expiresAt = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(context.getExpiresAt());
                                }).build()
                        ,
                        null
                );
                this.challengeContextCacheManager.delete((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
                throw new InvalidChallengeException(challengeId);
            }
            if (!this.challengeContextCacheManager.setCacheIfPresents((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId, context, ttl)) {
                //自然消失
                GenericLoggerStaticAccessor.basicLogger().debug(
                        log,
                        GenericBasicLogContent.builder()
                                .what(ChallengeContext.class)
                                .how(new Expired())
                                .detail(new Detail() {
                                    private final String now = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(new Date());
                                    private final String expiresAt = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(context.getExpiresAt());
                                }).build(),
                        null
                );
                throw new InvalidChallengeException(challengeId);
            }
        }
        return valid;
    }

    /**
     * 关闭挑战时
     *
     * @param proceedingJoinPoint 切点
     * @throws Throwable 遇到的问题
     */
    protected Object onCloseChallenge(ProceedingJoinPoint proceedingJoinPoint, String challengeId) throws Throwable {
        this.challengeContextCacheManager.delete((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        return proceedingJoinPoint.proceed();
    }

    /**
     * 获取冷却上下文信息
     *
     * @param target  目标
     * @param request 请求
     * @return 上下文信息
     */
    private String getCooldownKey(ChallengeResponseService<ChallengeRequest, ? extends Challenge> target, ChallengeRequest request) {
        //从当前请求查看是否已经放进入cd
        //fixed 挑战被应答后，cd时间没有结束导致下一个不需要发挑战的逻辑因上一个cd时间没有结束无法正确工作
        //举例，mfa短信验证通过后，1分钟的验证cd还在。尝试用户在另外一个电脑或者浏览器上还要登录系统
        //这时认证逻辑要求发送尝试挑战，于是代理类在知晓挑战不应当发出之前在这检查cd时间，发现cd时间还没结束，于是不允许登录(ChallengeCoolingDownException)
        //于是就在检查cd的时候问问CooldownInformer。当前的这次请求是不是要检查cd，如果cd上下文不为空再去检查，否则放过
        String key = target.getCooldownKey(request);
        return key;
    }

    /**
     * 检查是否应当进入cd
     *
     * @param target      被代理的目标
     * @param cooldownKey 上下文key
     */
    private void validateCooldown(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> target, String cooldownKey) {
        Duration ttl = this.challengeCooldownCacheManager.ttl(target, cooldownKey);
        if (ttl != null) {
            throw new ChallengeCoolingDownException(cooldownKey, ttl.getSeconds());
        }
    }

    /**
     * 开始执行cd
     *
     * @param target      代理目标
     * @param cooldownKey 冷却key
     */
    private void startCooldown(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> target, String cooldownKey) {
        long ttl = target.getCooldown();
        this.challengeCooldownCacheManager.setCacheIfNotPresents(target, cooldownKey, ttl, Duration.ofSeconds(ttl));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    /**
     * 读取上下文
     *
     * @param clazz       服务类
     * @param challengeId 挑战id
     * @return 上下文
     */
    @Nullable
    @Override
    public ChallengeContext getContext(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> clazz, String challengeId) {
        return challengeContextCacheManager.get(clazz, challengeId);
    }

    /**
     * 获取http请求
     *
     * @return http 请求
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(requestAttributes).getRequest();
    }

    /**
     * @author ZhangHan
     * @date 2022/5/31 16:21
     */
    @AllArgsConstructor
    public static class SecurityContextHoldingAuthenticationDetail implements Detail {
        private final String id;
        private final String source = SecurityContext.class.getCanonicalName();

    }
}
