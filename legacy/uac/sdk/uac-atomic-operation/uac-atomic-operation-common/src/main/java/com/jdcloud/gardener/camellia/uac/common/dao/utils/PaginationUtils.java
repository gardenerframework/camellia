package com.jdcloud.gardener.camellia.uac.common.dao.utils;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import org.springframework.util.Assert;

/**
 * @author ZhangHan
 * @date 2022/11/5 10:40
 */
public abstract class PaginationUtils {
    public static SelectStatement appendPagination(SelectStatement statement, long pageNo, long pageSize) {
        Assert.isTrue(pageNo > 0, "page no must > 0");
        Assert.isTrue(pageSize > 0, "pageSize no must > 0");
        if (pageSize != Long.MAX_VALUE) {
            statement.limit((pageNo - 1) * pageSize, pageSize);
        }
        return statement;
    }
}
