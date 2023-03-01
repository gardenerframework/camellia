package com.jdcloud.gardener.camellia.uac.common.schema.request;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.support.DefaultGenericMaxPageSizeProvider;
import com.jdcloud.gardener.fragrans.validation.constraints.range.Max;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;

/**
 * @author zhanghan30
 * @date 2022/8/11 7:47 下午
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaginationParameter implements
        ApiStandardDataTraits.PageNo,
        ApiStandardDataTraits.PageSize {
    /**
     * 页码，默认值是1
     */
    @Positive
    @Builder.Default
    private Integer pageNo = 1;
    /**
     * 页大小，默认值是10
     */
    @Positive
    @Builder.Default
    @Max(provider = DefaultGenericMaxPageSizeProvider.class)
    private Integer pageSize = 10;
}
