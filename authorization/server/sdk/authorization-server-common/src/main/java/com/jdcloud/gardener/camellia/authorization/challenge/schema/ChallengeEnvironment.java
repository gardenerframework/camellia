package com.jdcloud.gardener.camellia.authorization.challenge.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

/**
 * 挑战环境
 * <p>
 * 用于环境检查
 *
 * @author zhanghan30
 * @date 2022/5/23 6:53 下午
 * @see ChallengeRequest
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeEnvironment implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * http请求头
     * <p>
     * 其中Authorization头已经被去掉，因为其中包含了access token或认证信息
     * <p>
     * http头用于给实现类一些基本的
     */
    private MultiValueMap<String, String> headers;
    /**
     * 挑战相关的应用组
     */
    private String clientGroup;
    /**
     * 挑战相关的请求客户端
     */
    @Nullable
    private Client client;
    /**
     * 挑战相关的用户
     */
    @Nullable
    private User user;
}
