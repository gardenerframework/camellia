# 简介

当前组件为认证服务器提供一些基础的后台管理功，包含

* 给定用户id删除给定客户端的登录token
* 查询用户的登录日志
* 管理系统参数

等

# 功能注册

基于以往设计的成功实践，后台管理的功能也是按需注册完，比如给定id踢掉线就可以选择向后台管理引擎注册或不注册。
@AuthenticationServerManagementFeature注解向引擎表达当前激活了一个新的功能。

```java
public @interface AuthenticationServerManagementFeature {
    String value();
}
```
特性的名称之间不得重复
