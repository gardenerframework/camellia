package io.gardenerframework.camellia.authentication.server.main.schema;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.Credentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.EmptyCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.Authentication;

/**
 * 用户认证请求令牌
 * <p>
 * 注意这个没有去用到{@link Authentication}
 * <p>
 * 理由是{@link UserAuthenticationService}是自创的逻辑，并不是spring security的那套
 * <p>
 * 因此也不需要非得遵守security的那个逻辑，那个逻辑是引擎处理的部分
 *
 * @author ZhangHan
 * @date 2022/1/1 0:18
 */
@Getter
@AllArgsConstructor
public class UserAuthenticationRequestToken {
    /**
     * 用户登录的凭据
     * 可以是用户名
     * 可以是手机号
     * 或人脸id
     * 或别的什么东西
     */
    @NonNull
    private final Principal principal;
    /**
     * 提交的密码或其它登录信息
     * <p>
     * 密码等是不会被序列化的
     */
    @NonNull
    private final Credentials credentials;

    public UserAuthenticationRequestToken(@NonNull Principal principal) {
        this.principal = principal;
        //登录凭据设置为空
        this.credentials = new EmptyCredentials();
    }
}
