package io.gardenerframework.camellia.authentication.infra.challenge.engine;

import io.gardenerframework.camellia.authentication.infra.challenge.core.*;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.client.schema.RequestingClient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
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
        C extends Challenge,
        X extends ChallengeContext>
        implements ChallengeResponseService<R, C, X> {
    /**
     * 发送的挑战存储
     */
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final ChallengeStore<C> challengeStore;
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
     * 请求在应用和场景下是否应当重放
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @return 是否需要重放
     */
    protected abstract boolean replayChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    );

    /**
     * 返回请求特征
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @return 请求特征
     */
    @NonNull
    protected abstract String getRequestSignature(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    );

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
    @NonNull
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
     * @return 挑战
     * @throws Exception 发生问题
     */
    protected abstract C sendChallengeInternally(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    ) throws Exception;

    /**
     * 创建一个挑战上下文。存储有挑战相关的数据
     *
     * @param client    请求客户端
     * @param scenario  场景
     * @param request   请求
     * @param challenge 挑战
     * @return 挑战上下文
     */
    protected abstract X createContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request,
            @NonNull C challenge
    );

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
        C challenge = tryReplayChallenge(client, scenario, request);
        if (challenge != null) {
            //完成重放
            return challenge;
        }
        //检查cd
        //cd完成的绝对时间
        Date cooldownCompletionTime = null;
        try {
            if (hasCooldown(client, scenario, request)) {
                String timerId = getCooldownTimerId(client, scenario, request);
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
        challenge = tryCreateThenSendChallenge(client, scenario, request);
        //自动设置cd完成时间(不需要启动cd则没有完成时间)
        challenge.setCooldownCompletionTime(cooldownCompletionTime);
        //尝试保存上下文
        trySaveContext(client, scenario, request, challenge);
        //保存挑战
        trySaveChallenge(client, scenario, request, challenge);
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
            C challenge = challengeStore.loadChallenge(
                    client,
                    scenario,
                    challengeId
            );
            X context = challengeContextStore.loadContext(
                    client,
                    scenario,
                    challengeId
            );
            if (challenge == null || context == null) {
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
            challengeStore.removeChallenge(client, scenario, challengeId);
            challengeContextStore.removeContext(client, scenario, challengeId);
        } catch (Exception e) {
            throw new ChallengeResponseServiceException(e);
        }
    }

    /**
     * 尝试重放挑战
     *
     * @param client   请求客户端
     * @param scenario 场景
     * @param request  请求
     * @return 被重放的挑战，或者null
     * @throws ChallengeResponseServiceException 遇到问题
     */
    private C tryReplayChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    ) throws ChallengeResponseServiceException {
        try {
            if (replayChallenge(client, scenario, request)) {
                //当前请求可以被未完成的挑战重放
                String requestSignature = getRequestSignature(client, scenario, request);
                if (StringUtils.hasText(requestSignature)) {
                    String challengeId = challengeStore.getChallengeId(
                            client,
                            scenario,
                            requestSignature
                    );
                    if (StringUtils.hasText(challengeId)) {
                        //返回了请求特征，该特征对应着存储的挑战
                        return challengeStore.loadChallenge(
                                client,
                                scenario,
                                challengeId
                        );
                    }
                }
            }
            return null;
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
     * @return 发送完毕的挑战
     * @throws ChallengeResponseServiceException 发生问题
     */
    @NonNull
    private C tryCreateThenSendChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    ) throws ChallengeResponseServiceException {
        try {
            return sendChallengeInternally(client, scenario, request);
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
            @NonNull C challenge
    ) throws ChallengeResponseServiceException {
        try {
            X context = createContext(client, scenario, request, challenge);
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

    /**
     * 尝试去保存挑战
     *
     * @param client    请求客户端
     * @param scenario  场景
     * @param request   请求
     * @param challenge 挑战
     * @throws ChallengeResponseServiceException 保存问题
     */
    private void trySaveChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request,
            @NonNull C challenge
    ) throws ChallengeResponseServiceException {
        try {
            String requestSignature = getRequestSignature(client, scenario, request);
            if (StringUtils.hasText(requestSignature)) {
                challengeStore.saveChallengeId(
                        client,
                        scenario,
                        requestSignature,
                        challenge.getId(),
                        Duration.between(
                                Instant.now(),
                                challenge.getExpiryTime().toInstant()
                        )
                );
            }
            challengeStore.saveChallenge(
                    client,
                    scenario,
                    challenge.getId(),
                    challenge,
                    //计算存储的有效期
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
