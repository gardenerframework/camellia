package com.jdcloud.gardener.camellia.uac.common.schema.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用的更改是否类型的请求参数
 *
 * @author zhanghan30
 * @date 2022/8/12 10:54 上午
 */
@Getter
@Setter
public class ChangeBooleanFlagParameter {
    private boolean flag;
}
