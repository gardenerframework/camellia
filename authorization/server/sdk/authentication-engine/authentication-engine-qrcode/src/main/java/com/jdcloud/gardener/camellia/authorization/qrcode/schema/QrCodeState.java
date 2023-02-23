package com.jdcloud.gardener.camellia.authorization.qrcode.schema;

/**
 * @author zhanghan30
 * @date 2021/12/22 11:01 下午
 */
public enum QrCodeState {
    /**
     * 等待扫码
     */
    WAIT_FOR_SCANNING,
    /**
     * 等待确认(已扫码)
     */
    WAIT_FOR_CONFIRMING,
    /**
     * 过期
     */
    EXPIRED,
    /**
     * 已经确认
     */
    CONFIRMED;
}
