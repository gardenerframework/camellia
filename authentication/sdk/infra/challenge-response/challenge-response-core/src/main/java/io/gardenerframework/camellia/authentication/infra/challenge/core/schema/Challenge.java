package io.gardenerframework.camellia.authentication.infra.challenge.core.schema;

import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 基准的挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 18:43
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Challenge implements
        GenericTraits.IdentifierTraits.Id<String> {
    /**
     * 挑战id
     */
    @NotBlank
    private String id;
    /**
     * 挑战的cd时间剩余
     */
    @Nullable
    private Date cooldownCompletionTime;
    /**
     * 挑战的过期时间，超过这个时间即认为挑战无效
     * <p>
     * 默认就是立刻过期
     */
    @NotNull
    private Date expiryTime;
}
