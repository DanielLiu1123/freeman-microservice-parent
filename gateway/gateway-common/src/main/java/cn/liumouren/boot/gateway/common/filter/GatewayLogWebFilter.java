package cn.liumouren.boot.gateway.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * 我们使用 {@link WebFilter} 拦截所以请求
 * <p> {@link GlobalFilter} 只能拦截接入 gateway 的请求, 我们拦截不到 /actuator/health 等等请求
 *
 * @author freeman
 * @date 2021/12/26 15:59
 */
public class GatewayLogWebFilter implements WebFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(GatewayLogWebFilter.class);

    /**
     * <p> 我们需要防止程序异常而没有及时 remove 掉请求生成的 uuid
     * <p> 所以使用 {@link WeakHashMap}, 帮助我们做 gc
     */
    private final Map<String, Long> map = new WeakHashMap<>();

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String id = UUID.randomUUID().toString();
        map.put(id, System.currentTimeMillis());
        String path = exchange.getRequest().getPath().value();
        return chain.filter(exchange)
                .doOnSuccess(unused -> {
                    ServerHttpResponse response = exchange.getResponse();
                    if (response.getStatusCode().isError()) {
                        log.info("uri: {}, consuming: {}, cause: {}", path, System.currentTimeMillis() - map.get(id), response.getStatusCode().name());
                    } else {
                        log.info("uri: {}, consuming: {}", path, System.currentTimeMillis() - map.get(id));
                    }
                })
                .doOnError(e -> log.info("uri: {}, consuming: {}, cause: {}", path, System.currentTimeMillis() - map.get(id), e.getCause() == null ? e.getMessage() : e.getCause().getMessage()))
                .doAfterTerminate(() -> {
                    // 由于 id 引用存在, 不会被 gc 清除
                    map.remove(id);
                });
    }
}
