package com.jdcloud.gardener.camellia.uac.application.schema.response;

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
public class CreateApplicationResponse implements ApiStandardDataTraits.Id<String> {
    /**
     * 账户id
     */
    private String id;
}
