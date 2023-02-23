package com.jdcloud.gardener.camellia.authorization.username.recovery.schema.request;

import com.jdcloud.gardener.camellia.authorization.username.recovery.constraints.Password;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/1/10 12:55 下午
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    /**
     * 密码找回时给定的token
     */
    @NotBlank
    private String challengeId;
    /**
     * 验证码
     */
    @NotBlank
    private String response;
    /**
     * 新的密码
     * <p>
     * 为什么没有密码校验
     * <p>
     * 因为调接口的程序需要把同样的字符串传2次吗？
     * <p>
     * 校验密码是否相同再提交是前端的责任
     */
    @Password
    private String password;
}
