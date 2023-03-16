package io.gardenerframework.camellia.authentication.server.main.qrcode;

import io.gardenerframework.camellia.authentication.server.main.configuration.QrCodeBasedAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateQrCodeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/16 14:32
 */
@RequiredArgsConstructor
public abstract class QrCodeService<
        C extends CreateQrCodeRequest,
        O extends QrCodeBasedAuthenticationServiceOption
        > implements InitializingBean {
    private static final String STATE = "state";
    private static final String PRINCIPAL = "principal";
    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final O option;
    private final CacheClient client;
    private BasicCacheManager<State> stateCacheManager;
    private BasicCacheManager<Principal> principalCacheManager;


    protected String[] buildNameSpace() {
        return new String[]{
                "camellia",
                "authentication",
                "server",
                "component",
                "user-authentication-service",
                "qrcode",
                this.getClass().getName()
        };
    }

    /**
     * 创建图片
     *
     * @param request 请求
     * @param code    二维码
     * @return 图片的base64编码
     * @throws Exception 发生问题
     */
    protected abstract String createImage(@NonNull C request, @NonNull String code) throws Exception;

    /**
     * 创建二维码
     *
     * @param request 请求
     * @return 二维码
     * @throws Exception 发生问题
     */
    public QrCodeDetails createCode(@NonNull C request) throws Exception {
        //码id
        String code = UUID.randomUUID().toString();
        //是否生成吗
        String image = null;
        if (option.isCreateImage()) {
            image = createImage(request, code);
        }
        //存储二维码的状态为CREATED
        stateCacheManager.set(
                buildNameSpace(),
                code,
                STATE,
                State.CREATED,
                Duration.ofSeconds(option.getTtl())
        );
        return QrCodeDetails.builder()
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(option.getTtl()))))
                .code(code)
                .image(image)
                .build();
    }

    /**
     * 获取state
     *
     * @param code 编码
     * @return 当前码的状态
     * @throws Exception 遇到问题
     */
    public State getState(@NonNull String code) throws Exception {
        State state = stateCacheManager.get(buildNameSpace(), code, STATE);
        return state == null ? State.EXPIRED : state;
    }

    /**
     * 从http请求中获取登录名
     *
     * @param request 获取用户
     * @return 用户登录凭据
     * @throws Exception 发生问题
     */
    protected abstract Principal getPrincipalFromRequest(@NonNull HttpServletRequest request) throws Exception;

    /**
     * 存储扫码后确认登录的用户所对应的登录名
     *
     * @param request http请求
     * @param code    二维码
     * @throws Exception 发生问题
     */
    public void savePrincipalWithCode(@NonNull HttpServletRequest request, @NonNull String code) throws Exception {
        Duration ttl = stateCacheManager.ttl(buildNameSpace(), code, STATE);
        if (ttl != null) {
            this.principalCacheManager.set(buildNameSpace(), code, PRINCIPAL, getPrincipalFromRequest(request), ttl);
        }
    }

    /**
     * 获取保存的凭据
     *
     * @param code 二维码
     * @return 保存的登录名
     * @throws Exception 发生问题
     */
    @Nullable
    public Principal getSavedPrincipal(@NonNull String code) throws Exception {
        return principalCacheManager.get(buildNameSpace(), code, PRINCIPAL);
    }

    /**
     * 变化state
     *
     * @param code  二维码
     * @param state 新的state
     * @throws Exception 异常
     */
    public void changeState(@NonNull String code, @NonNull State state) throws Exception {
        Duration ttl = stateCacheManager.ttl(buildNameSpace(), code, STATE);
        //还没过期
        if (ttl != null) {
            stateCacheManager.setIfPresents(buildNameSpace(), code, STATE, state, ttl);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.stateCacheManager = new BasicCacheManager<State>(client) {
        };
        this.principalCacheManager = new BasicCacheManager<Principal>(client) {
        };
    }

    public enum State {
        /**
         * 创建了
         */
        CREATED,
        /**
         * 扫描了
         */
        SCANNED,
        /**
         * 确认了
         */
        CONFIRMED,
        /**
         * 过期了
         */
        EXPIRED
    }

    @NoArgsConstructor
    @SuperBuilder
    public static class QrCodeDetails {
        /**
         * 二维码id
         */
        @NonNull
        private String code;
        /**
         * 如果只是创建随机id，那么不需要什么图片
         */
        @Nullable
        private String image;
        /**
         * 二维码的过期时间
         */
        @NonNull
        private Date expiryTime;
    }
}
