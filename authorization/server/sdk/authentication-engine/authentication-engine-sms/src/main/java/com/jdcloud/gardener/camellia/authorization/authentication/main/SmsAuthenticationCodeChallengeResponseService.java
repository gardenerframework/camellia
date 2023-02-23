package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.SmsAuthenticationCodeChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;

/**
 * 短信登录用的验证码服务
 *
 * @author ZhangHan
 * @date 2022/5/15 14:04
 */
public interface SmsAuthenticationCodeChallengeResponseService extends ChallengeResponseService<SmsAuthenticationCodeChallengeRequest, Challenge> {
}
