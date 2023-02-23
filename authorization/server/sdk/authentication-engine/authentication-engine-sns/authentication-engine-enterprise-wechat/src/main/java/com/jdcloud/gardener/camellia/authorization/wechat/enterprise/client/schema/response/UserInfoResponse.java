package com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client.schema.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ZhangHan
 * @date 2022/11/8 22:07
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponse extends ResponseBase {
    /**
     * 微信企业内员工
     */
    private String userId;
    /**
     * 非企业内部员工
     */
    private String openId;
}
