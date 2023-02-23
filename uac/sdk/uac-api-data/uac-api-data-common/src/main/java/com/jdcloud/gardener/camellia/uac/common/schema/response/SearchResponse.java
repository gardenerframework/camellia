package com.jdcloud.gardener.camellia.uac.common.schema.response;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 通用的搜索用户响应
 *
 * @author zhanghan30
 * @date 2022/8/11 6:58 下午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SearchResponse<C> implements
        ApiStandardDataTraits.Contents<C>,
        ApiStandardDataTraits.TotalNumber {
    @Builder.Default
    private Collection<C> contents = new ArrayList<>(0);
    /**
     * 总数
     */
    private Long total;
}
