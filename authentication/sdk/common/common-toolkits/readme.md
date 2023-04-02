# 简介

在认证组件中，有很多的内容和类是公共需要的，而不是某个组件特定需要，这样的工具类就会定义在这里

# SerializationVersionNumber

SerializationVersionNumber定义了一个稳定的序列化版本号。由于认证引擎起源于Spring
Security，其使用session作为认证对象的状态存储，而为了能够多机器部署，需要搭配redis缓存完成缓存的序列化和反序列化。
于是session中的每一个key就要求必须实现`Serializable`接口，这个接口如果类加了个属性减了个属性就会出现报错，因此需要一个稳定的版本号
