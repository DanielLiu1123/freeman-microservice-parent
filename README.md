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
- [ ] 针对 FeignClient 调用的定制处理
- [ ] 我们是否需要给所有 FeignClient 一个默认的配置 ??
- [ ] Loadbalancer 相关问题
    - [ ] cache 选择
    - [ ] 负载均衡策略选择
    
3. web  
针对 web 做的集成
- [x] 可选的日志打印
- [x] 可选的异常处理
- [ ] 可选的安全校验

4. cache

NOTE:  
check 一下能否把插件也打包到 aliyun ???
