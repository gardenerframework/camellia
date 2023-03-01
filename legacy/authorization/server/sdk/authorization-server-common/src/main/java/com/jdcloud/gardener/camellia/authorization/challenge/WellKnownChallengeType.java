package com.jdcloud.gardener.camellia.authorization.challenge;

/**
 * @author zhanghan30
 * @date 2021/12/28 11:03 上午
 */
public interface WellKnownChallengeType {
    /**
     * google验证器
     */
    String GOOGLE_AUTHENTICATOR = "google";
    /**
     * 短信验证码
     */
    String SMS = "sms";
    /**
     * 邮箱验证码
     */
    String MAIL = "mail";
}
