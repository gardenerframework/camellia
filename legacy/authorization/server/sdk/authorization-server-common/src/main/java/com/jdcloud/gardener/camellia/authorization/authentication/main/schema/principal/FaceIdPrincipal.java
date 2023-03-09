package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal;

import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.log.annotation.ReferLogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/1/2 23:23
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ReferLogTarget(value = BasicPrincipal.class, prefix = "人脸类型的")
public class FaceIdPrincipal extends BasicPrincipal {
    private static final long serialVersionUID = Version.current;

    public FaceIdPrincipal(String name) {
        super(name);
    }
}
