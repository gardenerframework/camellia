# 简介

请求客户端(RequestingClient)指的是正在请求受到Oauth2标准保护的资源，本组件就提供这种客户端的数据定义，并进一步提供向客户端内填充元数据的provider

```plantuml
@startuml
!include https://plantuml.s3.cn-north-1.jdcloud-oss.com/C4_Container.puml


Person(用户, 用户)
System(浏览器, 浏览器, 客户端)
System(手机app, 手机app, 客户端)
System(小程序, 小程序, 客户端)

System(接口, 接口, api)

用户 --> 浏览器
用户 --> 手机app
用户 --> 小程序

手机app --> 接口
浏览器 --> 接口
小程序 --> 接口
@enduml
```

现在的情况下，用户会通过多种客户端访问后台的接口来办理业务。面对不同的客户端接口可能需要做出不同的反应，比如手机app专属商品推荐，又或者是需要对单个客户端或者应用的访问次数进行限制。
本组件再次就是用来给出来访客户端的定义。

需要注意的是，一般浏览器是客户端，但是难以从oauth2的角度获取其客户端id。

# RequestingClient & OAuth2RequestingClient

```java
public abstract class RequestingClient implements Serializable {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 客户端元数据，用于开发人员在客户端内保存自己的一些所需数据
     * <p>
     * 第一级key是provider的类路径，值是提供的元数据
     * <p>
     * 屏蔽原始的getter
     */
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Serializable> metadata = new ConcurrentHashMap<>();
    /**
     * client id
     */
    @NonNull
    private String clientId;

    /**
     * 设置元数据
     *
     * @param providerType 类型
     * @param metadata     元数据
     */
    public <M extends Serializable> void setMetadata(@NonNull String providerType, @NonNull M metadata) {
        this.metadata.put(providerType, metadata);
    }

    /**
     * 获取某个provider给出的元数据
     *
     * @param providerType 类型
     * @return 元数据
     */
    @Nullable
    @SuppressWarnings("unchecked")

    public <M extends Serializable> M getMetadata(@NonNull String providerType) {
        return (M) metadata.get(providerType);
    }
}

public class OAuth2RequestingClient extends RequestingClient {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 访问的授权类型
     */
    @NonNull
    private String grantType;
    /**
     * 对用户信息的访问范围
     */
    @NonNull
    @Singular
    private Set<@NonNull String> scopes;
}
```

从定义可见请求客户端包含了oauth2标准的基本属性，包含客户端id、请求的授权类型以及请求访问的数据范围。此外这些属性一旦设置就不能更改。
为了能够使得业务开发向客户端中加入一些自定义的属性和数据，可以通过"setMetadata"
方法设置元数据。由于请求客户端可能会被存储在session中等导致序列化，因此要求元数据也必须支持序列化

将`OAuth2RequestingClient`进行特别的定义是因为从认证的角度出发，请求客户端(`RequestingClient`)
可能并不只是基于oauth2协议。因此当其它协议定义了客户端的数据和属性时，可以再去定义符合协议特征的子类

# RequestingClientMetadataProvider

```java

@FunctionalInterface
public interface RequestingClientMetadataProvider<M extends Serializable> {
    /**
     * 返回元数据的部分，多个provider完全提供完所有碎片后整合为最终的元数据
     *
     * @param clientId 客户端id
     * @return 元数据，如果为空就不会加入到{@link RequestingClient}中
     */
    @Nullable
    M getMetadataPiece(@NonNull String clientId);
}
```

负责为客户端提供元数据，泛型返回具体的元数据类型