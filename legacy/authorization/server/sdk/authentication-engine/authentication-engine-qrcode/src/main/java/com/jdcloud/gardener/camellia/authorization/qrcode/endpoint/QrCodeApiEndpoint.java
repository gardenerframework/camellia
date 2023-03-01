package com.jdcloud.gardener.camellia.authorization.qrcode.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.camellia.authorization.qrcode.configuration.QrCodeOption;
import com.jdcloud.gardener.camellia.authorization.qrcode.event.schema.QrCodeConfirmedEvent;
import com.jdcloud.gardener.camellia.authorization.qrcode.event.schema.QrCodeScannedEvent;
import com.jdcloud.gardener.camellia.authorization.qrcode.exception.server.QrCodeAuthenticationNotSupportedException;
import com.jdcloud.gardener.camellia.authorization.qrcode.schema.response.CreateQrCodeResponse;
import com.jdcloud.gardener.camellia.authorization.qrcode.schema.response.ReadQrCodeStateResponse;
import com.jdcloud.gardener.camellia.authorization.qrcode.service.PrincipalProvider;
import com.jdcloud.gardener.camellia.authorization.qrcode.service.QrCodeService;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2021/12/22 8:31 下午
 */
@RestController
@RequestMapping("/qrcode")
@RequiredArgsConstructor
@Slf4j
@AuthorizationServerRestController
@Component
public class QrCodeApiEndpoint implements ApplicationEventPublisherAware {
    private final QrCodeService qrCodeService;
    private final PrincipalProvider<?> principalProvider;
    private final QrCodeOption qrCodeOption;
    private ApplicationEventPublisher eventPublisher;

    /**
     * 创建二维码
     *
     * @return 二维码
     * @throws Exception 发生异常
     */
    @PostMapping
    public CreateQrCodeResponse createQrCode() throws Exception {
        String token = qrCodeService.generateToken(qrCodeOption.getTokenTtl());
        return new CreateQrCodeResponse(
                qrCodeService.createQrCode(
                        token,
                        qrCodeOption.getUrl(),
                        qrCodeOption.getSize(),
                        1,
                        qrCodeOption.getLogoPath(),
                        qrCodeOption.getLogoSizePercentage(),
                        qrCodeOption.getCodeColor(),
                        qrCodeOption.getBackgroundColor()
                ),
                token
        );
    }

    /**
     * 读取token状态
     *
     * @param token 请求token
     * @return token状态
     */
    @GetMapping("/{token}")
    public ReadQrCodeStateResponse readQrCodeState(
            @Valid @NotBlank @PathVariable("token") String token
    ) {
        if (!StringUtils.hasText(qrCodeOption.getUrl())) {
            throw new QrCodeAuthenticationNotSupportedException();
        }
        return new ReadQrCodeStateResponse(qrCodeService.readQrCodeState(token));
    }

    /**
     * 扫描后由落地页回调
     *
     * @param token   请求令牌
     * @param request http请求
     */
    @PostMapping("/{token}:scan")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markQrCodeAsScanned(
            @Valid @NotBlank @PathVariable("token") String token,
            HttpServletRequest request
    ) {
        //发布事件
        eventPublisher.publishEvent(new QrCodeScannedEvent(token, request));
        qrCodeService.markQrCodeAsScanned(token, qrCodeOption.getMaxSecondsWaitForConfirming());
    }

    /**
     * 扫描后由落地页回调
     *
     * @param token   请求令牌
     * @param request http请求
     */
    @PostMapping("/{token}:confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markQrCodeAsConfirmed(
            @Valid @NotBlank @PathVariable("token") String token,
            HttpServletRequest request
    ) {
        eventPublisher.publishEvent(new QrCodeConfirmedEvent(token, request));
        BasicPrincipal principal = principalProvider.apply(request);
        if (principal == null) {
            throw new ForbiddenException();
        }
        qrCodeService.markQrCodeAsConfirmed(token, principal, qrCodeOption.getMaxSecondsWaitForAuthentication());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
