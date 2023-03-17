package io.gardenerframework.camellia.authentication.server.main.endpoint;

import io.gardenerframework.camellia.authentication.server.main.exception.client.InvalidRequestException;
import io.gardenerframework.camellia.authentication.server.main.qrcode.QrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateQrCodeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.response.GetQrCodeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/3/16 14:11
 */
@RequiredArgsConstructor
public abstract class QrCodeEndpoint<C extends CreateQrCodeRequest> {
    private final QrCodeService<C> service;

    /**
     * 获取二维码的state
     *
     * @param code 编码
     * @return 二维码状态
     * @throws Exception 遇到问题
     */
    @GetMapping("/qrcode/{code}")
    public GetQrCodeStateResponse state(
            @Valid @PathVariable("code") @NotBlank String code
    ) throws Exception {
        return new GetQrCodeStateResponse(service.getState(code).name());
    }

    /**
     * 创建二维码
     *
     * @param request 请求
     * @return 二维码
     * @throws Exception 发生问题
     */
    @PostMapping("/qrcode")
    public QrCodeService.QrCodeDetails create(
            @RequestBody(required = false) @Valid @Nullable C request
    ) throws Exception {
        return service.create(request);
    }

    /**
     * 验证请求是否有效，防止不是扫码发送的请求
     *
     * @param request 请求
     * @return 是否是合法请求
     * @throws Exception 发生问题
     */
    protected abstract boolean validateRequest(
            HttpServletRequest request
    ) throws Exception;

    /**
     * 扫描完毕
     *
     * @param request http请求
     * @param code    编码
     * @throws Exception 发生问题
     */
    @PostMapping("/qrcode/{code}:scan")
    public void scan(
            HttpServletRequest request,
            @Valid @PathVariable("code") @NotBlank String code
    ) throws Exception {
        if (!validateRequest(request)) {
            throw new InvalidRequestException();
        }
        service.changeState(code, QrCodeService.State.SCANNED);
    }

    /**
     * 确认登录
     *
     * @param request http请求
     * @param code    二维码
     * @throws Exception 发生问题
     */
    @PostMapping("/qrcode/{code}:confirm")
    public void confirm(
            HttpServletRequest request,
            @Valid @PathVariable("code") @NotBlank String code
    ) throws Exception {
        if (!validateRequest(request)) {
            throw new InvalidRequestException();
        }
        //保存登录名
        service.savePrincipal(request, code);
        //设置为已确认
        service.changeState(code, QrCodeService.State.CONFIRMED);
    }
}
