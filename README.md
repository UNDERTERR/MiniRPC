# MiniRPC 项目复盘总结

## 🎉 项目完成情况

**项目状态: ✅ 核心功能全部完成**  
**代码质量: ✅ 高内聚低耦合，符合设计原则**  
**最后更新: 2026-02-06**

---

## 📊 项目统计

| 指标 | 数值 |
|------|------|
| **模块数量** | 3个 (api/common/simple) |
| **代码文件** | 66个 Java文件 |
| **总代码行数** | 约2,700行 |
| **扩展点数量** | 6个SPI扩展接口 |
| **设计模式** | 单例、工厂、代理、策略等 |

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────┐
│   服务消费者 (Consumer)                           │
│   ├─ @RpcReference 注解注入代理对象               │
│   ├─ RpcClientProxy 动态代理拦截调用              │
│   └─ Spring BeanPostProcessor 自动扫描            │
└──────────────────┬──────────────────────────────┘
                   │ RPC调用
                   ▼
┌─────────────────────────────────────────────────┐
│   MiniRPC 核心框架                                │
│   ├─ 服务发现 (ServiceDiscovery) - SPI扩展        │
│   ├─ 负载均衡 (LoadBalance) - SPI扩展             │
│   ├─ 序列化 (Serializer) - SPI扩展                │
│   ├─ 压缩 (Compress) - SPI扩展                    │
│   ├─ 传输层 (RpcRequestTransport) - SPI扩展       │
│   └─ 扩展加载器 (ExtensionLoader) - 类Dubbo SPI   │
└──────────────────┬──────────────────────────────┘
                   │ Netty/Socket
                   ▼
┌─────────────────────────────────────────────────┐
│   服务提供者 (Provider)                           │
│   ├─ @RpcService 注解标记服务                     │
│   ├─ NettyRpcServer 监听请求                      │
│   ├─ RpcRequestHandler 处理调用                   │
│   └─ 服务注册到 ZooKeeper                         │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────┴──────────────────────────────┐
│   基础设施层                                      │
│   ├─ ZooKeeper (服务注册与发现)                   │
│   ├─ Netty (高性能NIO通信)                        │
│   ├─ Curator (ZK客户端)                           │
│   └─ Spring (IoC容器)                             │
└─────────────────────────────────────────────────┘
```

---

## ✅ 功能模块清单

### 1. API模块 (MiniRPC-api) ✅
- **接口定义**: 服务接口与DTO分离
- **POJO定义**: 请求/响应实体类

**代码文件**: `Hello.java`, `HelloService.java`

### 2. Common模块 (MiniRPC-common) ✅
- **枚举定义**: 压缩类型、序列化类型、负载均衡策略等8个枚举
- **异常体系**: RpcException、SerializeException
- **SPI机制**: ExtensionLoader实现类Dubbo的SPI扩展加载
- **工具类**: 单例工厂、线程池工厂、属性文件加载等
- **线程池配置**: 自定义线程池参数配置

**核心类**:
- `ExtensionLoader`: SPI扩展加载器，支持按需加载和缓存
- `SingletonFactory`: 双检锁单例工厂
- `ThreadPoolFactoryUtil`: 线程池统一管理，支持优雅关闭

### 3. Simple模块 (MiniRPC-simple) ✅

#### 3.1 通信层
- **Netty实现**: NettyRpcClient/NettyRpcServer
- **Socket实现**: SocketRpcClient/SocketRpcServer (备用)
- **编解码器**: RpcMessageEncoder/RpcMessageDecoder
- **心跳机制**: IdleStateHandler 5秒心跳保活
- **粘包处理**: LengthFieldBasedFrameDecoder

#### 3.2 服务治理
- **服务注册**: ZkServiceRegistryImpl (ZooKeeper)
- **服务发现**: ZkServiceDiscoveryImpl (支持缓存)
- **优雅关闭**: CustomShutdownHook 自动注销服务

#### 3.3 负载均衡
- **随机策略**: RandomLoadBalance
- **一致性哈希**: ConsistentHashLoadBalance (160虚拟节点)

#### 3.4 序列化
- **Kryo**: 高性能二进制序列化
- **Protostuff**: 基于Protobuf的序列化
- **Hessian**: 跨语言二进制序列化

#### 3.5 压缩
- **Gzip**: 标准Gzip压缩

#### 3.6 Spring集成
- **@RpcService**: 服务提供方注解
- **@RpcReference**: 服务消费方注解
- **@RpcScan**: 包扫描注解
- **SpringBeanPostProcessor**: 自动处理注解，注入代理

#### 3.7 代理层
- **RpcClientProxy**: JDK动态代理，拦截方法调用
- **异步转同步**: CompletableFuture.get() 阻塞等待结果

---

## 🔧 技术栈

### 核心框架
- **Netty**: 4.1.x (NIO通信框架)
- **Spring**: 5.x (IoC容器)
- **ZooKeeper**: 3.x (服务注册中心)
- **Curator**: 4.x (ZK客户端)

### 序列化
- **Kryo**: 5.x (高性能序列化)
- **Protostuff**: 1.x (Protobuf实现)
- **Hessian**: 4.x (跨语言序列化)

### 工具库
- **Lombok**: 代码简化
- **Guava**: Google工具库
- **SLF4J + Logback**: 日志框架

### 构建工具
- **Maven**: 项目构建与依赖管理

---

## 🚀 使用方式

### 1. 服务提供方

```java
// 1. 实现接口
@RpcService(group = "test", version = "1.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}

// 2. 启动服务
@SpringBootApplication
@RpcScan(basePackage = "org.example")
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
```

### 2. 服务消费方

```java
@SpringBootApplication
@RpcScan(basePackage = "org.example")
public class ConsumerApplication {
    
    @RpcReference(group = "test", version = "1.0")
    private HelloService helloService;
    
    public void test() {
        // 像调用本地方法一样调用远程服务
        String result = helloService.sayHello("World");
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
```

### 3. 配置SPI扩展

在 `resources/extensions/` 目录下配置扩展实现：

```properties
# org.example.serialize.Serializer
kyro=org.example.serialize.kyro.KryoSerializer
protostuff=org.example.serialize.protostuff.ProtostuffSerializer
hessian=org.example.serialize.hessian.HessianSerializer

# org.example.loadbalance.LoadBalance
random=org.example.loadbalance.loadbalancer.RandomLoadBalance
consistentHash=org.example.loadbalance.loadbalancer.ConsistentHashLoadBalance
```

---

## ⚠️ 已知限制

### 1. 注册中心
- 当前仅支持 ZooKeeper，未实现 Nacos/Etcd 等
- ZK 连接异常时的重试机制需要完善

### 2. 传输层
- Netty 连接池管理简单，未实现复杂的连接复用策略
- 未实现熔断、限流等服务治理能力

### 3. 序列化
- 未支持 JSON 序列化（可选补充）
- 未实现序列化方式的动态协商

### 4. 异步调用
- 当前为异步发送、同步等待（CompletableFuture.get()）
- 未提供纯异步回调接口

---

## 🔐 安全特性

- **魔数校验**: 协议头包含魔数，防止非法数据包
- **版本控制**: 协议版本号，确保兼容性
- **请求ID**: UUID + AtomicInteger 确保请求唯一性
- **服务分组**: group + version 实现服务隔离

---

## 📈 性能优化

- **连接复用**: ChannelProvider 缓存 TCP 连接
- **虚拟节点**: 一致性哈希160虚拟节点，均衡分布
- **SPI缓存**: ExtensionLoader 缓存扩展实例，避免重复反射
- **单例模式**: SingletonFactory 确保重量级对象只创建一次
- **线程池**: 统一管理线程池，优雅关闭避免资源泄漏
- **心跳保活**: IdleStateHandler 维持长连接，减少握手开销

---

## 📂 项目结构

```
MiniRPC/
├── MiniRPC-api/                    # API接口模块
│   └── src/main/java/org/example/
│       ├── Hello.java
│       └── HelloService.java
│
├── MiniRPC-common/                 # 公共模块
│   └── src/main/java/org/example/
│       ├── enums/                  # 枚举定义
│       ├── exception/              # 异常类
│       ├── extension/              # SPI扩展机制
│       ├── factory/                # 工厂类
│       └── utils/                  # 工具类
│
├── MiniRPC-simple/                 # 核心实现模块
│   └── src/main/java/org/example/
│       ├── annotaion/              # 自定义注解
│       ├── compress/               # 压缩实现
│       ├── config/                 # 配置类
│       ├── loadbalance/            # 负载均衡
│       ├── provider/               # 服务提供
│       ├── proxy/                  # 代理层
│       ├── registry/               # 服务注册发现
│       ├── remoting/               # 远程通信
│       │   ├── dto/                # 数据传输对象
│       │   ├── handler/            # 请求处理器
│       │   └── transport/          # 传输层实现
│       ├── serialize/              # 序列化实现
│       └── spring/                 # Spring集成
│
├── notes/                          # 架构图与笔记
├── pom.xml                         # 父POM
└── PROJECT_SUMMARY.md              # 本文档
```

---

## 🎯 核心流程

### 服务调用流程

```
1. 消费者调用接口方法
        ↓
2. RpcClientProxy.invoke() 拦截
        ↓
3. 构建 RpcRequest 对象
        ↓
4. ServiceDiscovery.lookupService() 获取地址
        ↓
5. LoadBalance.select() 选择节点
        ↓
6. 序列化 (Serializer.serialize)
        ↓
7. 压缩 (Compress.compress)
        ↓
8. NettyRpcClient.sendRpcRequest() 发送
        ↓
9. CompletableFuture 等待响应
        ↓
10. 反序列化获取结果
```

### 服务注册流程

```
1. Spring 初始化 Bean
        ↓
2. SpringBeanPostProcessor 检测 @RpcService
        ↓
3. 构建 RpcServiceConfig
        ↓
4. ServiceProvider.publishService()
        ↓
5. 注册到 ZooKeeper (/rpc/服务名/地址)
        ↓
6. NettyRpcServer 启动监听端口
```

---

## 🚀 下一步建议

### 优先级 P0（必须）
1. **完善文档**: 编写详细的设计文档和使用手册
2. **单元测试**: 为核心组件编写单元测试，覆盖率80%+
3. **集成测试**: 编写端到端的调用测试用例

### 优先级 P1（重要）
4. **服务治理**: 实现熔断器 (Circuit Breaker)
5. **限流降级**: Sentinel 集成，防止服务雪崩
6. **监控指标**: Micrometer + Prometheus 指标采集
7. **链路追踪**: SkyWalking / Zipkin 分布式追踪

### 优先级 P2（优化）
8. **多注册中心**: 支持 Nacos、Consul、Etcd
9. **异步调用**: 提供 CompletableFuture 回调接口
10. **泛化调用**: 不依赖接口的泛化调用方式
11. **网关接入**: 支持通过 API Gateway 调用

### 优先级 P3（扩展）
12. **服务分组路由**: 根据标签路由到不同集群
13. **权重配置**: 动态调整服务节点权重
14. **配置中心**: 与 Nacos/Apollo 集成动态配置

---

## 📞 项目信息

- **项目名称**: MiniRPC
- **项目描述**: 基于 Java 实现的轻量级 RPC 框架，模拟 Dubbo 核心机制
- **开发周期**: 约1周
- **团队规模**: 1人
- **代码仓库**: https://github.com/UNDERTERR/MiniRPC
- **学习价值**: 深入理解 RPC 原理、Netty 通信、SPI 机制、动态代理

---

## 💡 核心设计亮点

### 1. 自定义 SPI 机制
仿 Dubbo 实现 ExtensionLoader，支持：
- 按需加载扩展实现
- 扩展实例缓存
- 双检锁确保线程安全

### 2. 协议设计
自定义二进制协议：
```
| 魔数(4B) | 版本(1B) | 长度(4B) | 类型(1B) | 编码(1B) | 压缩(1B) | ID(4B) | Body |
```

### 3. 粘包拆包处理
使用 Netty 的 LengthFieldBasedFrameDecoder，根据协议头中的长度字段精确分割数据包。

### 4. 心跳机制
客户端 5 秒无数据自动发送心跳，服务端检测超时断开连接，维持长连接健康。

### 5. 一致性哈希
160 虚拟节点 + MD5 哈希，确保：
- 相同参数总是路由到同一节点
- 节点上下线只影响少量数据

---

