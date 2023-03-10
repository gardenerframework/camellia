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
 * ???{@link ChallengeResponseService}???????????????
 * <p>
 * ????????????????????????????????????????????????
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
     * ????????????????????????????????????????????????
     *
     * @param proceedingJoinPoint ??????
     * @return ?????????????????????
     * @throws Throwable ??????????????????????????????
     */
    @Around("target(com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService)")
    public Object proxyAllChallengeResponseService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Assert.isTrue(proceedingJoinPoint.getTarget() instanceof ChallengeResponseService, "target is not a ChallengeResponseService instance");
        //???????????????????????????????????????
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        //??????ValidateEnv??????
        ValidateChallengeEnvironment validateChallengeEnvironment = AnnotationUtils.findAnnotation(signature.getMethod(), ValidateChallengeEnvironment.class);
        if (validateChallengeEnvironment != null) {
            handleValidateChallengeEnvironmentAnnotation(proceedingJoinPoint, signature);
        }
        //?????????????????????????????????????????????
        ValidateChallenge validateChallenge = AnnotationUtils.findAnnotation(signature.getMethod(), ValidateChallenge.class);
        if (validateChallenge != null) {
            handleValidateChallenge(proceedingJoinPoint, signature);
        }
        //???????????????????????????
        if (Challenge.class.isAssignableFrom(signature.getMethod().getReturnType()) && "sendChallenge".equals(signature.getMethod().getName())) {
            return onSendChallenge(proceedingJoinPoint, signature);
        }
        //??????????????????
        if (boolean.class.isAssignableFrom(signature.getMethod().getReturnType()) && signature.getMethod().getName().equals("validateResponse")) {
            return onValidateResponse(proceedingJoinPoint, getChallengeId(proceedingJoinPoint, signature));
        }
        if (void.class.isAssignableFrom(signature.getMethod().getReturnType()) && signature.getMethod().getName().equals("closeChallenge")) {
            //fixed ?????????????????????????????????2???closeChallenge
            return onCloseChallenge(proceedingJoinPoint, getChallengeId(proceedingJoinPoint, signature));
        }
        return proceedingJoinPoint.proceed();
    }

    /**
     * ????????????????????????
     *
     * @param proceedingJoinPoint ??????
     * @param signature           ????????????
     */
    private void handleValidateChallengeEnvironmentAnnotation(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature) {
        String challengeId = getChallengeId(proceedingJoinPoint, signature);
        ChallengeContext context = getContext((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        if (context == null) {
            //???????????????????????????????????????
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
     * ???????????????????????????????????????
     *
     * @param proceedingJoinPoint ??????
     * @param signature           ????????????
     */
    private void handleValidateChallenge(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature) {
        String challengeId = getChallengeId(proceedingJoinPoint, signature);
        ChallengeContext context = getContext((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        if (context == null || !context.isVerified()) {
            throw new InvalidChallengeException(challengeId);
        }
    }

    /**
     * ????????????id
     *
     * @param proceedingJoinPoint ??????
     * @param signature           ????????????
     * @return ??????id
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
     * ?????????????????????
     * <p>
     * ????????????????????????????????????????????????????????????
     * <p>
     * token????????????????????????????????????
     * <p>
     * ??????api????????????AccessTokenDetails??????
     *
     * @return ????????????
     */
    @Nullable
    private User getUser() {
        //?????????????????????????????????
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
        //???????????????access token
        return accessTokenDetails.getUser();
    }

    /**
     * ??????????????????????????????
     * <p>
     * ??????????????????????????????????????????????????????registerClient
     * <p>
     * token???????????????(???????????????client id & secret)
     * <p>
     * ??????api??????????????????token??????????????????????????????token???????????????
     *
     * @return ?????????
     */
    @Nullable
    private RegisteredClient getRegisteredClient() {
        //?????????????????????????????????
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2ClientAuthenticationToken) {
            //??????????????????????????????????????????
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
        //??????????????????token??????????????????
        return accessTokenDetails.getRegisteredClient();
    }

    /**
     * ??????????????????????????????
     * <p>
     * ??????????????????????????????????????????????????????client
     * <p>
     * token???????????????(???????????????client id & secret)
     * <p>
     * ??????api??????????????????token??????????????????????????????token???????????????
     *
     * @return ?????????
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
     * ????????????????????????
     *
     * @param proceedingJoinPoint ??????
     * @param signature           ????????????
     * @return ??????
     * @throws Throwable ????????????
     */
    @SuppressWarnings("unchecked")
    protected Challenge onSendChallenge(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature) throws Throwable {
        ChallengeRequest request = (ChallengeRequest) proceedingJoinPoint.getArgs()[0];
        //???????????????cd???????????????
        //fixed ?????????????????????cd????????????????????????????????????????????????????????????????????????cd????????????????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????cd????????????????????????????????????1?????????cd??????
        //????????????????????????????????????????????????????????????????????????cd???????????????cd??????????????????????????????????????????
        String cooldownKey = getCooldownKey((ChallengeResponseService<ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), request);
        if (cooldownKey != null) {
            //????????????????????????cd
            validateCooldown((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), cooldownKey);
        }
        //?????????????????????????????????????????????
        Challenge challenge = (Challenge) proceedingJoinPoint.proceed();
        //?????????????????????????????????
        if (challenge != null) {
            //???????????????????????????
            //??????????????????????????????cd - ???????????????cd??????
            UsingContextFactory annotation = AnnotationUtils.findAnnotation(signature.getMethod(), UsingContextFactory.class);
            Assert.notNull(annotation, "@UsingContextFactory must present on sendChallenge method");
            ChallengeContextFactory<ChallengeRequest, ? extends ChallengeContext> challengeContextFactory = (ChallengeContextFactory<ChallengeRequest, ? extends ChallengeContext>) challengeContextFactories.get(annotation.value());
            Assert.notNull(challengeContextFactory, "bean of type" + annotation.value() + " not presents");
            ChallengeContext context = challengeContextFactory.createContext(request, challenge);
            Duration ttl = Duration.between(Instant.now(), challenge.getExpiresAt().toInstant());
            if (!this.challengeContextCacheManager.setCacheIfNotPresents((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challenge.getId(), context, ttl)) {
                //????????????????????????????????????????????????
                GenericLoggerStaticAccessor.basicLogger().debug(
                        log,
                        GenericBasicLogContent.builder()
                                .what(challenge.getClass())
                                .how(new Word() {
                                    @Override
                                    public String toString() {
                                        return "??????";
                                    }
                                }).detail(new Detail() {
                                    private final String id = challenge.getId();
                                }).build(),
                        null
                );
                //????????????????????????cd
            } else {
                //????????????????????????cd
                if (cooldownKey != null) {
                    //??????????????????????????????cd
                    startCooldown((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), cooldownKey);
                }
            }
        }
        return challenge;
    }

    /**
     * ????????????????????????????????????
     *
     * @param proceedingJoinPoint ??????
     * @param challengeId         ??????id
     * @return ????????????
     * @throws Throwable ??????
     */
    protected boolean onValidateResponse(ProceedingJoinPoint proceedingJoinPoint, String challengeId) throws Throwable {
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        ChallengeContext context = getContext((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        if (context == null) {
            //???????????????????????????????????????
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
                                    return "???????????????";
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
                //????????????????????????????????????????????????
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
                //????????????
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
     * ???????????????
     *
     * @param proceedingJoinPoint ??????
     * @throws Throwable ???????????????
     */
    protected Object onCloseChallenge(ProceedingJoinPoint proceedingJoinPoint, String challengeId) throws Throwable {
        this.challengeContextCacheManager.delete((ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>) proceedingJoinPoint.getTarget(), challengeId);
        return proceedingJoinPoint.proceed();
    }

    /**
     * ???????????????????????????
     *
     * @param target  ??????
     * @param request ??????
     * @return ???????????????
     */
    private String getCooldownKey(ChallengeResponseService<ChallengeRequest, ? extends Challenge> target, ChallengeRequest request) {
        //??????????????????????????????????????????cd
        //fixed ?????????????????????cd????????????????????????????????????????????????????????????????????????cd????????????????????????????????????
        //?????????mfa????????????????????????1???????????????cd??????????????????????????????????????????????????????????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????cd???????????????cd??????????????????????????????????????????(ChallengeCoolingDownException)
        //??????????????????cd???????????????CooldownInformer??????????????????????????????????????????cd?????????cd?????????????????????????????????????????????
        String key = target.getCooldownKey(request);
        return key;
    }

    /**
     * ????????????????????????cd
     *
     * @param target      ??????????????????
     * @param cooldownKey ?????????key
     */
    private void validateCooldown(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> target, String cooldownKey) {
        Duration ttl = this.challengeCooldownCacheManager.ttl(target, cooldownKey);
        if (ttl != null) {
            throw new ChallengeCoolingDownException(cooldownKey, ttl.getSeconds());
        }
    }

    /**
     * ????????????cd
     *
     * @param target      ????????????
     * @param cooldownKey ??????key
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
     * ???????????????
     *
     * @param clazz       ?????????
     * @param challengeId ??????id
     * @return ?????????
     */
    @Nullable
    @Override
    public ChallengeContext getContext(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> clazz, String challengeId) {
        return challengeContextCacheManager.get(clazz, challengeId);
    }

    /**
     * ??????http??????
     *
     * @return http ??????
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
