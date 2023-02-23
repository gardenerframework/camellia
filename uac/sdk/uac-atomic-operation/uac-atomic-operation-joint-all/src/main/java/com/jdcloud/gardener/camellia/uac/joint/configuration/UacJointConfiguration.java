package com.jdcloud.gardener.camellia.uac.joint.configuration;

import com.jdcloud.gardener.camellia.uac.joint.atomic.AccountApplicationRelationAtomicOperation;
import com.jdcloud.gardener.camellia.uac.joint.dao.mapper.JointMapperPackage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/7 11:47
 */
@Configuration
@MapperScan(basePackageClasses = JointMapperPackage.class)
@Import({
        AccountApplicationRelationAtomicOperation.class
})
public class UacJointConfiguration {
}
