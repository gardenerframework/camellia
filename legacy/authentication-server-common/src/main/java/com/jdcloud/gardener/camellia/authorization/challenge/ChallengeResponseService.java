package com.jdcloud.gardener.camellia.authorization.challenge;

import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ChallengeId;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.UsingContextFactory;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ValidateChallengeEnvironment;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import org.springframework.lang.Nullable;

/**
 * 挑战服务泛型，用于发送挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 19:03
 */
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
