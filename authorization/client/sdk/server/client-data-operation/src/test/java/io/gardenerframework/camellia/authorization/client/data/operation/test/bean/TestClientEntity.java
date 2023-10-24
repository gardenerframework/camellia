package io.gardenerframework.camellia.authorization.client.data.operation.test.bean;

import io.gardenerframework.camellia.authorization.client.data.schema.entity.ClientEntityTemplate;
import io.gardenerframework.fragrans.data.persistence.orm.statement.annotation.TableName;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author chris
 * @date 2023/10/24
 */
@TableName("test_client")
@SuperBuilder
@NoArgsConstructor
public class TestClientEntity extends ClientEntityTemplate {
}
