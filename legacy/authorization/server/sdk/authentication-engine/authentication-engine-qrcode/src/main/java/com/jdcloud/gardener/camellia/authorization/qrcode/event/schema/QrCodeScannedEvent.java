package com.jdcloud.gardener.camellia.authorization.qrcode.event.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2022/8/5 7:34 下午
 */
@AllArgsConstructor
@Getter
public class QrCodeScannedEvent {
    /**
     * 扫描的token
     */
    private final String token;
    /**
     * http请求
     */
    private final HttpServletRequest request;
}
