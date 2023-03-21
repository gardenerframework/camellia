package io.gardenerframework.camellia.authentication.server.common.configuration;

import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 管理服务器的路径配置
 *
 * @author ZhangHan
 * @date 2022/5/11 12:45
 */
@ApiOption(readonly = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class AdministrationServerPathOption {
    /**
     * rest api的上下文路径
     */
    private String restApiContextPath = "/api";
}
