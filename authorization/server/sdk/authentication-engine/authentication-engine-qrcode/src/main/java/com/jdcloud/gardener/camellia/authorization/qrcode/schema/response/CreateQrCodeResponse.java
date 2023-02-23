package com.jdcloud.gardener.camellia.authorization.qrcode.schema.response;

import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhanghan30
 * @date 2021/12/22 9:10 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@LogTarget("登录二维码")
public class CreateQrCodeResponse {
    /**
     * 二维码图片
     */
    private String image;
    /**
     * 二维码对应的请求令牌，用于查询二维码的状态
     */
    private String token;
}
