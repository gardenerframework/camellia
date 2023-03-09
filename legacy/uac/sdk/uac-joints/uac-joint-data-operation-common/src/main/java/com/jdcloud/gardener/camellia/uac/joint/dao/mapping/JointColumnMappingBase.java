package com.jdcloud.gardener.camellia.uac.joint.dao.mapping;

import com.jdcloud.gardener.camellia.uac.common.dao.utils.SqlProviderUtils;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.handler.JsonTypeHandler;
import com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation.ColumnMapping;
import com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation.ColumnTypeHandlerProvider;
import com.jdcloud.gardener.fragrans.data.persistence.template.sql.DomainSqlTemplateBase;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainDaoTemplateRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/11/24 21:00
 */
public abstract class JointColumnMappingBase extends JsonTypeHandler implements ColumnTypeHandlerProvider {
    /**
     * 列对应的实体的mapper的类型
     */
    private final Class<?> mapperType;
    /**
     * 当前聚合了多个json的数据实体的类型，比如应用+客户端的聚合实体
     */
    private final Class<?> jointEntityType;
    /**
     * 要扫描和使用的trait，也就是当前列名
     */
    private final Class<?> columnTrait;

    protected JointColumnMappingBase(Class<?> mapperType, Class<?> jointEntityType, Class<?> columnTrait) {
        //设置为行json的模式
        super(RECORD_ROW_OBJECT_MAPPER);
        this.mapperType = mapperType;
        this.jointEntityType = jointEntityType;
        this.columnTrait = columnTrait;
    }

    @Override
    public Type getTypeReference() {
        //获取应用的激活实现
        Class<?> activeImplementation = Objects.requireNonNull(DomainDaoTemplateRegistry.getItem(mapperType)).getActiveImplementation();
        DomainSqlTemplateBase template = SqlProviderUtils.getActiveProvider(activeImplementation);
        return template.getDomainObjectType(activeImplementation);
    }

    @Override
    public ColumnMapping provide(Class<?> mapperInterface, Method mapperMethod) {
        return new ColumnMapping(
                FieldScannerStaticAccessor.scanner().column(
                        jointEntityType,
                        columnTrait
                ),
                FieldScannerStaticAccessor.scanner().field(
                        jointEntityType,
                        columnTrait
                ),
                this,
                null
        );
    }
}
