package com.jdcloud.gardener.camellia.uac.joint.configuration;

import com.jdcloud.gardener.camellia.uac.joint.dao.mapper.ApplicationClientRelationMapper;
import com.jdcloud.gardener.camellia.uac.joint.operation.ApplicationClientDataOperation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/11/24 18:18
 */
@Configuration
@Import({ApplicationClientDataOperation.class})
@MapperScan(basePackageClasses = {ApplicationClientRelationMapper.class})
public class ApplicationClientDataOperationConfiguration {
}
