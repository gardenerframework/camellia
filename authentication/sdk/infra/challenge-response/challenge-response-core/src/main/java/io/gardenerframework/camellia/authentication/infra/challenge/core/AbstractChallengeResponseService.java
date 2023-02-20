package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;

/**
 * 挑战服务泛型，用于发送挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 19:03
 */
@AllArgsConstructor
public abstract class AbstractChallengeResponseService<
        R extends ChallengeRequest,
        X extends ChallengeContext,
        C extends Challenge
        > {
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final ChallengeStore challengeStore;
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final ChallengeResponseCooldownManager challengeResponseCooldownManager;
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final ChallengeResponseContextStore<? super X> challengeResponseContextStore;

    /**
     * 请求在应用和场景下是否应当重放
     *
     * @param applicationId 应用id
     * @param request       请求
     * @param scenario      场景
     * @return 是否需要重放
     */
    protected abstract boolean replayChallenge(
            @NonNull String applicationId,
            @NonNull R request,
            @NonNull Class<? extends Scenario> scenario
    );

    /**
     * 返回请求特征
     *
     * @param applicationId 请求的应用id
     * @param request       请求
     * @param scenario      场景
     * @return 请求特征
     */
    protected abstract String getRequestSignature(
            @NonNull String applicationId,
            @NonNull R request,
            @NonNull Class<? extends Scenario> scenario
    );

    /**
     * 当前挑战应答是否需要进行cd，比如发短信，邮件等都不能一直发，需要进行cd
     *
     * @return 是/否
     */
    protected abstract boolean hasCooldown();

    /**
     * 在需要进行cd的情况下，给出cd的key，比如按用户，按手机号，按ip等
     *
     * @param request 挑战请求
     * @return cd的key
     */
    @NonNull
    protected abstract String getCooldownKey(@NonNull R request);

    /**
     * 获取cd时间
     *
     * @param request 请求
     * @return cd时间
     */
    protected abstract int getCooldownTime(@NonNull R request);

    /**
     * 创建一个挑战对象
     *
     * @param request 请求
     * @return 挑战对象 - 基于自己的策略决定是使用一个已有的对象，还是去新建一个
     * @throws Exception 发送出错
     */
    @NonNull
    protected abstract C sendChallengeInternally(@NonNull R request) throws Exception;

    /**
     * 创建一个挑战上下文，用于验证挑战时使用
     *
     * @param request   请求
     * @param challenge 生成的挑战对象
     * @return 上下文
     */
    protected abstract X createContext(@NonNull R request, @NonNull C challenge);

    /**
     * 验证应答
     *
     * @param id       挑战id
     * @param response 应答
     * @return 是否合法(只要不是合法都返回 false)
     */
    public abstract boolean verifyResponse(String id, String response);

    /**
     * 关闭挑战，意味着回收与挑战相关的资源
     *
     * @param id 挑战id
     */
    public abstract void closeChallenge(String id);

    /**
     * 发送挑战
     * <p>
     * 再次说明，单个个人或者客户端可能一直都在要求发送挑战
     * <p>
     * 因此需要思考什么时候重新生成挑战，什么时候发送之前未完成的
     *
     * @param applicationId 应用id
     * @param request       请求对象
     * @param scenario      场景
     * @return 挑战令牌.
     */
    public C sendChallenge(
            @NonNull String applicationId,
            @NonNull R request,
            @NonNull Class<? extends Scenario> scenario
    ) {
        try {
            if (replayChallenge(applicationId, request, scenario)) {
                String requestSignature = getRequestSignature(applicationId, request, scenario);
                C challenge = challengeStore.loadChallenge(applicationId, requestSignature, scenario);
                if (challenge != null) {
                    //挑战还没有过期，直接返回即可，不需要发送
                    return challenge;
                }
            }
            //检查挑战是否已经在cd中，这种情况下就相当于发送下一个挑战前要求上一个挑战先被关闭
            if (hasCooldown()) {
                //获取基于当前请求的cd存储key
                String key = getCooldownKey(request);
                //如果是一个之前挑战的重放，则不需要生成key，从而既不检查cd，也不进入cd
                if (StringUtils.hasText(key)) {
                    Duration timeRemaining = challengeResponseCooldownManager.getTimeRemaining(key);
                    if (timeRemaining != null) {
                        //仍然在cd
                        throw new ChallengeInCooldownException(timeRemaining);
                    }
                    //cd已经消失，开始新的cd
                    if (!challengeResponseCooldownManager.startCooldown(key, Duration.ofSeconds(getCooldownTime(request)))) {
                        //冷却时间已经被其他线程或并发调用占用
                        timeRemaining = challengeResponseCooldownManager.getTimeRemaining(key);
                        //抛出cd中的异常
                        throw new ChallengeInCooldownException(timeRemaining);
                    }
                }
            }
            //生成挑战并且执行发送到目标用户的任务
            C challenge = sendChallengeInternally(request);
            //生成上下文
            X context = createContext(request, challenge);
            //保存上下文
            challengeResponseContextStore.saveContext(this.getClass(), challenge.getId(), context,
                    Duration.between(
                            new Date().toInstant(),
                            challenge.getExpiryTime().toInstant()
                    ));
            return challenge;
        } catch (ChallengeInCooldownException e) {
            throw e;
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
    }
}
