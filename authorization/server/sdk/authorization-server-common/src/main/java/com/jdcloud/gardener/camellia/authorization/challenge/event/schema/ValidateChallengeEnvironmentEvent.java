package com.jdcloud.gardener.camellia.authorization.challenge.event.schema;

import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeEnvironment;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 要求验证环境事件
 *
 * @author zhanghan30
 * @date 2022/5/23 7:09 下午
 */
@Getter
@AllArgsConstructor
public class ValidateChallengeEnvironmentEvent {
    private final String challengeId;
    /**
     * 在请求时的环境信息
     */
    private final ChallengeEnvironment request;
    /**
     * 当前的环境信息
     */
    private final ChallengeEnvironment current;
}
