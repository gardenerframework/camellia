package com.jdcloud.gardener.camellia.authorization.qrcode.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.camellia.authorization.qrcode.service.QrCodeService;
import com.jdcloud.gardener.fragrans.data.cache.manager.annotation.Cached;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 二维码上下文
 *
 * @author zhanghan30
 * @date 2022/4/11 4:39 下午
 * @see QrCodeService
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Cached(namespaces = {"camellia", "authorization", "authentication", "engine", "qrcode"}, suffix = "context")
public class QrCodeContext implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * 识别符号
     */
    private String token;
    /**
     * 用户登录凭据，从应用扫码后获取的
     */
    @Nullable
    private BasicPrincipal principal;
    /**
     * 状态
     */
    private QrCodeState state;
}
