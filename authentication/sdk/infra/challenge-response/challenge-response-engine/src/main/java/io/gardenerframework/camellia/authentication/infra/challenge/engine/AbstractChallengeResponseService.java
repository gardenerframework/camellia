package io.gardenerframework.camellia.authentication.infra.challenge.engine;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.SaveInChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 挑战服务泛型，用于发送挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 19:03
 */
@AllArgsConstructor
public abstract class AbstractChallengeResponseService<
        R extends ChallengeRequest,
        C extends Challenge,
        X extends ChallengeContext>
        implements ChallengeResponseService<R, C, X> {
    /**
     * 冷却管理器
     */
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final ChallengeCooldownManager challengeCooldownManager;
    /**
     * 上下文存储
     */
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final ChallengeContextStore<X> challengeContextStore;

    /**
     * 当前应用在当前场景下面对当前请求是否存在着cd
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @return 是否存在cd
     */
    protected abstract boolean hasCooldown(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    );

    /**
     * 由请求计算出冷却的计时器id
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @return 计时器id
     */
    @Nullable
    protected abstract String getCooldownTimerId(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    );

    /**
     * 给出基于应用，场景以及请求的cd时间
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @return cd时间
     */
    protected abstract int getCooldownTime(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    );

    /**
     * 完成挑战的创建和发送
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @param payload  生产挑战过程中一些希望上下文保存时访问的数据放这里
     * @return 挑战
     * @throws Exception 发生问题
     */
    protected abstract C sendChallengeInternally(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request,
            @NonNull Map<String, Object> payload
    ) throws Exception;

    /**
     * 创建一个挑战上下文。存储有挑战相关的数据
     *
     * @param client    请求客户端
     * @param scenario  场景
     * @param request   请求
     * @param challenge 挑战
     * @param payload   开发人员需要的数据放这里
     * @return 挑战上下文
     */
    protected abstract X createContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request,
            @NonNull C challenge,
            @NonNull Map<String, Object> payload
    );

    /**
     * 处理{@link SaveInChallengeContext}注解
     *
     * @param client   请求注解
     * @param scenario 场景
     * @param request  请求
     * @param context  上下文
     */
    protected void copySaveInContextFields(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request,
            @NonNull X context
    ) {
        ReflectionUtils.doWithFields(
                request.getClass(),
                field -> {
                    SaveInChallengeContext annotation = AnnotationUtils.findAnnotation(field, SaveInChallengeContext.class);
                    if (annotation != null) {
                        //具有当前注解
                        Class<?> fieldType = field.getType();
                        field.setAccessible(true);
                        Object fieldValue = field.get(request);
                        //在context中寻找同名的field
                        Field fieldInContext = FieldUtils.getField(context.getClass(), field.getName(), true);
                        if (fieldInContext != null) {
                            Class<?> fieldTypeInContext = fieldInContext.getType();
                            Object fieldValueInContext = null;
                            if (
                                //要求context的必须是请求中的父类且支持序列化
                                    fieldTypeInContext.isAssignableFrom(fieldType) && Serializable.class.isAssignableFrom(fieldTypeInContext)
                                            //不能已经有值
                                            && (fieldValueInContext = fieldInContext.get(context)) == null
                                            //不能是final和static
                                            && !Modifier.isFinal(fieldInContext.getModifiers()) && !Modifier.isStatic(fieldInContext.getModifiers())) {
                                //完成赋值
                                fieldInContext.set(context, fieldValue);
                            }
                        }
                    }
                }
        );
    }


    /**
     * 完成校验
     *
     * @param client      请求客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @param context     挑战对应的上下文
     * @param response    响应
     * @return 是否通过
     * @throws Exception 验证过程出现问题
     */
    protected abstract boolean verifyChallengeInternally(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull X context,
            @NonNull String response
    ) throws Exception;

    /**
     * 发送挑战
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  挑战请求
     * @return 挑战
     * @throws ChallengeResponseServiceException 挑战服务出现问题
     * @throws ChallengeInCooldownException      挑战还在cd中
     */
    @Override
    public C sendChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    ) throws ChallengeResponseServiceException, ChallengeInCooldownException {
        //检查cd
        //cd完成的绝对时间
        Date cooldownCompletionTime = null;
        String timerId;
        try {
            if (hasCooldown(client, scenario, request) && StringUtils.hasText(timerId = getCooldownTimerId(client, scenario, request))) {
                Duration timeRemaining = challengeCooldownManager.getTimeRemaining(client, scenario, timerId);
                if (timeRemaining != null) {
                    throw new ChallengeInCooldownException(timeRemaining);
                }
                //cd已经消失
                //启动cd
                Duration cooldown = Duration.ofSeconds(getCooldownTime(client, scenario, request));
                //设置cd完成绝对时间
                cooldownCompletionTime = Date.from(Instant.now().plus(cooldown));
                boolean started = challengeCooldownManager.startCooldown(
                        client, scenario, timerId,
                        cooldown
                );
                if (!started) {
                    //当前调用没有启动cd成功
                    timeRemaining = challengeCooldownManager.getTimeRemaining(client, scenario, timerId);
                    throw new ChallengeInCooldownException(timeRemaining);
                }
            }
        } catch (ChallengeInCooldownException e) {
            throw e;
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
        //cd已经抢占或者不需要cd
        //创建挑战并完成发送
        //贯穿生成挑战以及生成上下文所需的载荷
        Map<String, Object> payload = new HashMap<>();
        C challenge = tryCreateThenSendChallenge(client, scenario, request, payload);
        //自动设置cd完成时间(不需要启动cd则没有完成时间)
        challenge.setCooldownCompletionTime(cooldownCompletionTime);
        //尝试保存上下文
        trySaveContext(client, scenario, request, challenge, payload);
        //返回挑战
        return challenge;
    }

    /**
     * 检查响应是否符合预期
     *
     * @param client      请求客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @param response    挑战响应
     * @return 是否验证通过
     * @throws ChallengeResponseServiceException 验证过程中发生问题
     */
    @Override
    public boolean verifyResponse(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull String response
    ) throws ChallengeResponseServiceException {
        try {
            X context = challengeContextStore.loadContext(
                    client,
                    scenario,
                    challengeId
            );
            if (context == null) {
                //挑战已经过期
                return false;
            }
            return verifyChallengeInternally(
                    client,
                    scenario,
                    challengeId,
                    context,
                    response
            );
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
    }

    @Nullable
    @Override
    public X getContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException {
        try {
            return challengeContextStore.loadContext(client, scenario, challengeId);
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
    }

    /**
     * 关闭挑战，释放资源
     *
     * @param client      请求客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @throws ChallengeResponseServiceException 发生问题
     */
    @Override
    public void closeChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException {
        try {
            challengeContextStore.removeContext(client, scenario, challengeId);
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
    }

    /**
     * 尝试创建并发送挑战
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @param payload  保存开发人员自定义数据的载荷
     * @return 发送完毕的挑战
     * @throws ChallengeResponseServiceException 发生问题
     */
    @NonNull
    private C tryCreateThenSendChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request,
            @NonNull Map<String, Object> payload
    ) throws ChallengeResponseServiceException {
        try {
            return sendChallengeInternally(client, scenario, request, payload);
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
    }

    /**
     * 尝试创建并保存上下文
     *
     * @param client    请求客户端
     * @param scenario  场景
     * @param request   请求
     * @param challenge 挑战
     * @throws ChallengeResponseServiceException 保存异常
     */
    private void trySaveContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request,
            @NonNull C challenge,
            @NonNull Map<String, Object> payload
    ) throws ChallengeResponseServiceException {
        try {
            X context = createContext(client, scenario, request, challenge, payload);
            copySaveInContextFields(client, scenario, request, context);
            challengeContextStore.saveContext(
                    client,
                    scenario,
                    challenge.getId(),
                    context,
                    Duration.between(
                            Instant.now(),
                            challenge.getExpiryTime().toInstant()
                    )
            );
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
    }
}
