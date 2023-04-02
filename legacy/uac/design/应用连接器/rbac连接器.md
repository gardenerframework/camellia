# 引言

在大型系统的建设中，大量的应用依靠的外部团队或其它公司来建设。在这种情况下，后台管理人员需要对人员进行统一授权时就不得不进入到各个已有的系统去添加人员，角色和权限。这是因为

* 已有的系统已经具有了用户数据库和rbac能力
* 项目的成本，造价，工期等因素影响，其它团队伙伴不愿意改造代码
* 这部分功能减轻了后台人员的劳动强度但是没有业务的本质提升

面对这种场景，可以考虑使用应用RBAC连接器(Application RBAC Connector)

# 主要功能

RBAC连接器为uac或其它应用系统提供查询接入应用权限信息，为指定账户创建赋予权限的标准化操作接口和对接服务

# ApplicationRbacConnectorSkeleton 框架

遵循uac的标准设计模式，`ApplicationRbacConnectorSkeleton`定义连接器的基本功能

```java
public interface ApplicationRbacConnectorSkeleton {

    /**
     * 对应用执行角色搜索
     *
     * @param applicationId       应用id
     * @param searchParameter     搜索参数
     * @param paginationParameter 分页参数
     * @return 搜索结果
     */
    SearchApplicationRoleResponse searchRoles(
            @Valid @NotBlank String applicationId,
            @Valid SearchApplicationRoleParameter searchParameter,
            @Valid PaginationParameter paginationParameter
    );

    /**
     * 按照用户读取绑定的角色
     *
     * @param applicationId 应用id
     * @param parameter     参数参数
     * @return 搜索结果
     */
    SearchApplicationRoleResponse searchBindRoles(
            @Valid @NotBlank String applicationId,
            @Valid SearchBindRolesParameter parameter
    );

    /**
     * 执行账户角色重新绑定
     * <p>
     * 当账户在应用中不存在时，应当执行先添加用户到应用，再执行绑定的逻辑
     *
     * @param applicationId 应用id
     * @param parameter     绑定参数
     */
    void bindRoles(
            @Valid @NotBlank String applicationId,
            @Valid BindAccountsToApplicationRolesParameter parameter
    );

    /**
     * 从应用中删除账户
     * <p>
     * 即从应用中移除授权用户
     *
     * @param applicationId 应用id
     * @param parameter     要删除的人员清单
     */
    void removeAccounts(
            @Valid @NotBlank String applicationId,
            @Valid RemoveAccountsParameter parameter
    );
}
```

框架接口的主要目标是方便开发人员按照接口实现aop切面，从而整体进行一些前置/后置的逻辑，比如访问权限控制

接口约定了几个主要的方法:

* 提供查询角色功能
* 提供所有角色功能
* 提供查询给定账号绑定的角色功能
* 提供从应用中删除账号的功能(取消应用访问授权)

这样，对接应用端的若干复杂逻辑就由这个接口封装，对前端统一提供

# ApplicationRbacConnectorService

```java
public interface ApplicationRbacConnectorService extends ApplicationRbacConnectorSkeleton {
    /**
     * 返回支持的应用id清单，为空会抛异常(什么都不支持有个屁用)
     *
     * @return 支持的应用id清单
     */
    Collection<String> supportApplicationIds();
}

```

应用连接器http api依赖服务，一个服务可以通过`supportApplicationIds`声明自己支持多个应用id而不是一个，但至少必须声明支持一个

# 支持的接口

`ApplicationRbacConnectorEndpointBase`定义了支持的http api的框架，在此假设其实现类使用的统一前缀是"
/management/application"

## 查询角色

```http request
GET /management/application/{applicationId}/role
```

支持的查询参数有

* name: 角色具有的名称，原则上是判等
* pageNo: 页码，防止角色清单一次性返回过多
* pageSize: 页大小，防止角色清单一次性返回过多

对接应用的接口一般不会一次性返回所有角色，因此分页参数在此也是为了支持这种场景

## 查询账户绑定的角色

```http request
GET /management/application/{applicationId}/joint:account+role
```

支持的查询参数有

* accountId: 账户id或等效的字符串, 比如用户名

## 绑定角色到账户

```http request
POST /management/application/{applicationId}/joint:account+role
```

绑定的行为含义是<font color=orange>使用当前要求的角色清单覆盖应用内指定账户(支持批量)的角色清单</font>
，并且<font color=orange>
如果给定账户没有在应用自己的数据库中，则应当首先完成账户的创建工作(若有必要)</font>

## 取消账户应用访问权

```http request
DELETE /management/application/{applicationId}/account
```

取消的含义是<font color=orange>解除给定账户(支持批量)在应用中的所有角色</font>，并且<font color=orange>删除账户(
若有必要)</font>
。当账户取消角色授权后，相应的账户访问该应用应当被导向无授权的相关页面

# 用例

## 独立部署

应用连接器并不强制要求必须和uac一同打包部署，其具有自己的api接口基准类型，能够声明为独立的api运行。 这种情况广泛适用于

* 客户侧现在已经具有rbac控制系统且不具有应用连接能力
* 各个应用团队不愿意改造自己的代码

这时现场团队可以独立部署一个RBAC连接器并编写页面解决用户的问题

## 与uac的应用逻辑服务模块打包部署

当现场部署包内引入了

* "uac-application-connector-rbac-engine"
* "uac-logic-service-application"

则"uac-application-connector-rbac-engine"自动启用服务拦截器，配合`ApplicationServiceTemplate.readApplication`
来检查应用id的合法性，否则默认实现是不校验(
或者按需实现`ApplicationIdChecker`)

此外，引入了"uac-logic-service-account"时，取消绑定，执行绑定时将检查账户是否存在。当某个账户不存在时则报错处理

