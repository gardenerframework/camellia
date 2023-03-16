package io.gardenerframework.camellia.authentication.server.main.schema.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author zhanghan30
 * @date 2023/3/16 14:20
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class CreateQrCodeRequest {
    /**
     * 大小
     */
    @Positive
    private int size = 280;
    /**
     * 研发rgb
     * <p>
     * 默认是0x000000
     */
    @PositiveOrZero
    private long color = 0L;
}
