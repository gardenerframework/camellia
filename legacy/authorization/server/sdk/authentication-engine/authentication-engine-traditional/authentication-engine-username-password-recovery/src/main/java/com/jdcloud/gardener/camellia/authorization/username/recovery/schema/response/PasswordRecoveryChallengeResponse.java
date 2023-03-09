package com.jdcloud.gardener.camellia.authorization.username.recovery.schema.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/1/10 12:49 下午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryChallengeResponse {
    /**
     * 挑战id
     */
    private String challengeId;
    /**
     * 给前端的一个提示，用的什么玩意验证，比如邮箱，还是短信
     */
    private String authenticator;
    /**
     * 冷却时间
     * <p>
     * 一般来说只有短信验证码有用
     * <p>
     * 0 = 没有cd
     */
    private long cooldown;
    /**
     * 其它所需的额外参数
     */
    private Map<String, String> parameters;
}
