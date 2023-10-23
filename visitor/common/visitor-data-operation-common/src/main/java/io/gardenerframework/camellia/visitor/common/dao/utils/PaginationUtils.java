package io.gardenerframework.camellia.visitor.common.dao.utils;

import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import org.springframework.util.Assert;

/**
 * @author ZhangHan
 * @date 2022/11/5 10:40
 */
public abstract class PaginationUtils {
    /**
     * 基于查询语句添加分页条件
     *
     * @param statement 查询语句
     * @param pageNo    从那页开始
     * @param pageSize  页大小
     * @return 完成添加后的语句
     */
    public static SelectStatement appendPagination(SelectStatement statement, int pageNo, int pageSize) {
        Assert.isTrue(pageNo > 0, "page no must > 0");
        Assert.isTrue(pageSize > 0, "pageSize no must > 0");
        if (pageSize != Integer.MAX_VALUE) {
            statement.limit(((long) (pageNo - 1)) * pageSize, pageSize);
        }
        return statement;
    }
}
