package com.jdcloud.gardener.camellia.authorization.challenge.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 基准的挑战
 * <p>
 * 从验证端的角度出发，不能总是期待请求端按照申请->应答->执行逻辑操作的模式进行
 * <p>
 * 请求端可能无意或有意的多次请求发送挑战(比如是一个http retry，或者是一个重复提交)
 * <p>
 * 因此令牌可以是一个可重新发送的挑战
 * <p>
 * 可重放还有一层意思就是验证码的生成一般不由当前服务负责
 * <p>
 * 比如google的认证器，用户被引导到mfa的页面后不小心关闭了，再次引导时相关的ttl没有过期，那可以再重放一次
 * <p>
 * 但是短信验证码这种一般重放也没有意义
 *
 * @author ZhangHan
 * @date 2022/5/15 18:43
 */
@AllArgsConstructor
@Data
public class Challenge {
    /**
     * 挑战id
     */
    private final String id;
    /**
     * 挑战的验证形式
     */
    private final String authenticator;
    /**
     * 挑战的过期时间，超过这个时间即认为挑战无效
     */
    private final Date expiresAt;
    /**
     * 其它额外参数，提示给客户端的
     */
    private Map<String, String> parameters;
}
