#### freeman-microservice-parent 集成的功能
1. common  
spring-boot 集成所用的工具包  
- [x] 工具类
- [x] 异常
- [x] 常量
- [x] 默认配置的加载

2. openfeign  
针对 openFeign 做的集成
- [x] 远程调用请求头来源添加
- [x] FeignClient 和 Controller 的 Primer 设置
- [x] 给所有 FeignClient 一个默认的配置
  - 自定义 Contract 和 ErrorDecoder
- [ ] 针对 FeignClient 调用的定制处理
  
3. loadbalancer
- [x] k8s 服务端负载均衡
- [x] k8s 客户端负载均衡
  - 已实现服务下线客户端立即感知
  - 服务上线感知(需要吗???, 暂时的处理是设置一个合理的缓存时间)
    
4. web  
针对 web 做的集成
- [x] 可选的日志打印
- [x] 可选的异常处理
- [x] 自定义注解解析
- [ ] 可选的安全校验 (网关做???)

5. cache
- [ ]

6. db
- [x] mysql 动态数据源 (使用sharding-jdbc)
- [x] mongo 动态数据源

7. gateway
- [x] 客户端负载均衡
- [x] 服务端负载均衡
- [ ] 权限校验
- [ ] 分布式限流

NOTE:  
check 一下能否把插件也打包到 aliyun ???

TODO:

