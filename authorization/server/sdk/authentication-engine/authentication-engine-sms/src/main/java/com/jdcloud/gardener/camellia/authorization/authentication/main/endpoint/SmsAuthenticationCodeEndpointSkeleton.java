package com.jdcloud.gardener.camellia.authorization.authentication.main.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.SendSmsAuthenticationCodeRequest;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response.SendSmsAuthenticationCodeResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author zhanghan30
 * @date 2022/9/1 7:19 下午
 */
public interface SmsAuthenticationCodeEndpointSkeleton {
    /**
     * 发送短信
     *
     * @param httpServletRequest http请求
     * @param request            发送请求
     * @return 发送结果
     */
    default SendSmsAuthenticationCodeResponse sendCode(
            HttpServletRequest httpServletRequest,
            @Valid SendSmsAuthenticationCodeRequest request
    ) {
        return null;
    }
}
