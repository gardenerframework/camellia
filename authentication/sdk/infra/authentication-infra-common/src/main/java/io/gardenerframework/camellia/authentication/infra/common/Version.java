package io.gardenerframework.camellia.authentication.infra.common;

/**
 * @author zhanghan30
 * @date 2022/1/11 3:24 下午
 */
public abstract class Version {
    /**
     * 当前版本号
     * 1 主版本
     * 00 次版本
     * 00 小版本
     */
    public static final long current = 10000L;

    private Version() {

    }
}
