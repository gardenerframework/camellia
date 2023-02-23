package com.jdcloud.gardener.camellia.authorization.qrcode.schema.response;

import com.jdcloud.gardener.camellia.authorization.qrcode.schema.QrCodeState;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhanghan30
 * @date 2021/12/22 9:12 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@LogTarget("登录二维码扫码状态")
public class ReadQrCodeStateResponse {
    /**
     * 二维码的状态
     */
    private QrCodeState state;
}
