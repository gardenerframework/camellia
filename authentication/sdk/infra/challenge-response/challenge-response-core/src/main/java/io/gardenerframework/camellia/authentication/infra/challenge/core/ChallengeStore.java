package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.client.schema.RequestingClient;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/20 18:07
 */
public interface ChallengeStore<C extends Challenge> {
    /**
     * 保存挑战id与请求特征的对应关系
     *
     * @param client           正在请求的客户端
     * @param scenario         场景
     * @param requestSignature 请求特征
     * @param challengeId      挑战id
     * @throws Exception 发生问题
     */
    void saveChallengeId(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature,
            @NonNull String challengeId,
            @NonNull Duration ttl
    ) throws Exception;

    /**
     * 返回挑战id
     *
     * @param client           正在请求的客户端
     * @param scenario         场景
     * @param requestSignature 请求特征
     * @return 对应的挑战id
     */
    @Nullable
    String getChallengeId(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature
    );

    /**
     * 存储挑战
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @param challenge   挑战
     * @param ttl         有效期
     * @throws Exception 存储问题
     */
    void saveChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull C challenge,
            @NonNull Duration ttl
    ) throws Exception;

    /**
     * 读取挑战
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @return 挑战
     * @throws Exception 读取异常
     */
    @Nullable
    C loadChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;

    /**
     * 移除挑战
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @throws Exception 移除异常
     */
    void removeChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;
}
