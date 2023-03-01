package com.jdcloud.gardener.camellia.authorization.qrcode.service;

import com.google.zxing.WriterException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.qrcode.schema.QrCodeContext;
import com.jdcloud.gardener.camellia.authorization.qrcode.schema.QrCodeState;
import com.jdcloud.gardener.fragrans.data.cache.client.CacheClient;
import com.jdcloud.gardener.fragrans.data.cache.manager.BasicCacheManager;
import com.jdcloud.gardener.fragrans.toolkits.barcode.QrCodeTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2021/12/22 8:34 下午
 */
@Slf4j
@Component
public class QrCodeService {
    private final QrCodeTool qrCodeTool;
    private final BasicCacheManager<QrCodeContext> contextCacheManager;

    protected QrCodeService(QrCodeTool qrCodeTool, CacheClient cacheClient) {
        this.qrCodeTool = qrCodeTool;
        this.contextCacheManager = new BasicCacheManager<QrCodeContext>(cacheClient, null, QrCodeContext.class) {
        };
    }

    /**
     * 获取下一个访问令牌
     *
     * @param ttl 占位有效期
     * @return 令牌
     * @throws InterruptedException 中断异常
     */
    public String generateToken(int ttl) throws InterruptedException {
        String token;
        boolean regenerate = false;
        long start = System.currentTimeMillis();
        do {
            token = UUID.randomUUID().toString();
            if (regenerate) {
                Thread.sleep(20);
            }
            regenerate = true;
            long current = System.currentTimeMillis();
            if (current - start >= 10000) {
                throw new IllegalStateException("cannot get available qrcode token");
            }
        } while (!contextCacheManager.setIfNotPresents(
                token,
                new QrCodeContext(token, null, QrCodeState.WAIT_FOR_SCANNING),
                Duration.ofSeconds(ttl)
        ));
        return token;
    }

    /**
     * 生成二维码
     *
     * @param token           请求令牌
     * @param url             url地址
     * @param size            大小
     * @param margin          留空
     * @param logoPath        图标路径
     * @param logoRatio       比例
     * @param codeColor       码颜色
     * @param backgroundColor 背景色
     * @return 创建结果
     * @throws IOException     读取错误
     * @throws WriterException 写入错误
     */
    public String createQrCode(
            String token,
            String url,
            int size,
            int margin,
            String logoPath,
            float logoRatio,
            int codeColor,
            int backgroundColor
    ) throws IOException, WriterException {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);
        uriComponentsBuilder.queryParam("token", token);
        return qrCodeTool.createSquareQrCode(
                uriComponentsBuilder.build().toString(),
                size,
                margin,
                logoPath,
                logoRatio,
                codeColor,
                backgroundColor
        );
    }

    /**
     * 查询二维码状态
     *
     * @param token 请求令牌
     * @return 状态
     */
    public QrCodeState readQrCodeState(@NonNull String token) {
        QrCodeContext context = contextCacheManager.get(token);
        if (context == null) {
            return QrCodeState.EXPIRED;
        } else {
            return context.getState();
        }
    }

    /**
     * 设置二维码为已经扫描的状态(此时还未确认登录)
     *
     * @param token                       令牌
     * @param maxSecondsWaitForConfirming 设置键为等待登录确认的最大市场
     */
    public void markQrCodeAsScanned(@NonNull String token, int maxSecondsWaitForConfirming) {
        contextCacheManager.setIfPresents(
                token,
                //转为待确认
                new QrCodeContext(token, null, QrCodeState.WAIT_FOR_CONFIRMING),
                Duration.ofSeconds(maxSecondsWaitForConfirming)
        );
    }

    /**
     * 二维码登录已确认
     *
     * @param token                           令牌
     * @param principal                       用户信息
     * @param maxSecondsWaitForAuthentication 等到认证的最长时间。
     *                                        当用户确认后，需要一段时间等着页面跳转和认证，需要保存这个数据到这个时间
     */
    public <P extends BasicPrincipal> void markQrCodeAsConfirmed(@NonNull String token, P principal, int maxSecondsWaitForAuthentication) {
        contextCacheManager.setIfPresents(
                token,
                new QrCodeContext(token, principal, QrCodeState.CONFIRMED),
                Duration.ofSeconds(maxSecondsWaitForAuthentication)
        );
    }

    /**
     * 读取用户信息
     *
     * @param token token
     * @return 用户信息
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <P extends BasicPrincipal> P getPrincipal(@NonNull String token) {
        QrCodeContext context = contextCacheManager.get(token);
        if (context == null) {
            return null;
        } else {
            return (P) context.getPrincipal();
        }
    }
}
