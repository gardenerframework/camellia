package com.jdcloud.gardener.camellia.authorization.challenge.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;

/**
 * 发送挑战的请求
 * <p>
 * 包含了当要发送挑战时的基本要素
 * <p>
 * 为啥不干脆给一个{@link HttpServletRequest}呢，因为其中包含了所有http参数，并有可能包含了用户登录的密码
 * <p>
 * 手机验证码等不应当透露的东西
 * <p>
 * 使用这个东西是方便{@link ChallengeResponseService}的实现类来扩展自己的参数
 *
 * @author ZhangHan
 * @date 2022/5/15 19:05
 */
@AllArgsConstructor
@Getter
public class ChallengeRequest {
    /**
     * http请求头
     * <p>
     * 其中Authorization头已经被去掉，因为其中包含了access token或认证信息
     * <p>
     * http头用于给实现类一些基本的
     */
    private final MultiValueMap<String, String> headers;
    /**
     * 挑战相关的应用组
     */
    private final String clientGroup;
    /**
     * 挑战相关的请求客户端
     */
    @Nullable
    private final Client client;
    /**
     * 挑战相关的用户
     */
    @Nullable
    private final User user;
}
