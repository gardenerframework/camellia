package com.jdcloud.gardener.camellia.authorization.challenge.schema;

import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 挑战的缓存上下文
 *
 * @author ZhangHan
 * @date 2022/5/15 22:52
 * @see ChallengeContextAccessor
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ChallengeContext implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * 当时申请挑战时的环境信息
     */
    private ChallengeEnvironment request;
    /**
     * 过期时间
     */
    private Date expiresAt;
    /**
     * 是否通过了验证
     */
    private boolean verified = false;
}
