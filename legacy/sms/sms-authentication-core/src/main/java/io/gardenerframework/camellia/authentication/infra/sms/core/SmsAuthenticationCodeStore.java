package io.gardenerframework.camellia.authentication.infra.sms.core;

import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/14 18:30
 */
public interface SmsAuthenticationCodeStore {
    /**
     * 存储验证码，前提是当前应用场景下，该手机的上一个验证码还没过期
     *
     * @param applicationId     当前要发送的应用组(如果是oauth2的应用组，则需要有客户端访问凭据)
     * @param mobilePhoneNumber 要发送的手机号(服务层不管手机号是否是已经存在的用户，由前置逻辑检查)
     * @param scenario          场景
     * @param code              要求保存的验证码
     * @param ttl               有效期
     * @return 存储成功，不成功服务不会发送验证码，不成功的的可能有很多，比如同一时间点了多次，造成对同一手机号可能的多次发送
     * @throws Exception 透传产生的问题
     */
    boolean saveCodeIfAbsent(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario,
            String code,
            Duration ttl
    ) throws Exception;


    /**
     * 给出发送剩余时间
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @return 剩余时间
     * @throws Exception 透传产生的问题
     */
    @Nullable
    Duration getTimeRemaining(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario
    ) throws Exception;

    /**
     * 返回保存的短信验证码
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @return 验证码
     * @throws Exception 透传产生的问题
     */
    @Nullable
    String getCode(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario
    ) throws Exception;

    /**
     * 删除验证码，一般是验证码已经验证成功
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @throws Exception 透传产生的问题
     */
    void removeCode(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario
    ) throws Exception;
}
