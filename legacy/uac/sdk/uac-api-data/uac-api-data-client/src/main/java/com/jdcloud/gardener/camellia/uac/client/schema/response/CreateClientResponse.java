package com.jdcloud.gardener.camellia.uac.client.schema.response;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 创建用户的结果
 *
 * @author zhanghan30
 * @date 2022/8/12 9:27 上午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateClientResponse implements ApiStandardDataTraits.Id<String> {
    /**
     * id
     */
    private String id;
}
