package cn.liumouren.boot.gateway.lb.serverside;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 20:23
 */
@Configuration(proxyBeanMethods = false)
public class GatewayLbServerSideAutoConfiguration {

    @Bean
    public BeanFactoryPostProcessor noLoadBalancerClientFilterBeanFactoryPostProcessor() {
        return new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                // see GatewayNoLoadBalancerClientAutoConfiguration$NoLoadBalancerClientFilter
                // 我们需要满足在没有加入 loadbalancer-starter 的情况下也能使用 lb://, 我们自行替换为 http
                // 所以需要干掉本来的 GatewayNoLoadBalancerClientAutoConfiguration$NoLoadBalancerClientFilter
                // 使用我们自定义的 NoLoadBalancerClientFilter
                ((DefaultListableBeanFactory) beanFactory).removeBeanDefinition("noLoadBalancerClientFilter");
                AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(NoLoadBalancerClientFilter.class).getBeanDefinition();
                ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("noLoadBalancerClientFilter", definition);
            }
        };
    }

    protected static class NoLoadBalancerClientFilter implements GlobalFilter, Ordered {

        private GatewayProperties gatewayProperties;

        public NoLoadBalancerClientFilter(GatewayProperties gatewayProperties) {
            this.gatewayProperties = gatewayProperties;
        }

        @Override
        public int getOrder() {
            return LOAD_BALANCER_CLIENT_FILTER_ORDER;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            URI uri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
            String url = uri.toString();
            if (url.startsWith("lb://")) {
                // 加上 namespace lb://user -> http://user.default
                String svc = uri.getHost();
                String namespace = getNamespace(svc);
                url = url.replaceFirst("lb", "http")
                        .replaceFirst(svc, svc + "." + namespace);
                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, URI.create(url));
            }

            return chain.filter(exchange);
        }


        private String getNamespace(String serviceId) {
            List<RouteDefinition> routes = gatewayProperties.getRoutes();
            if (routes != null) {
                for (RouteDefinition route : routes) {
                    String url = route.getUri().toString();
                    if (url.startsWith("lb://")) {
                        String svc = route.getUri().getHost();
                        if (Objects.equals(serviceId, svc)) {
                            String namespace = route.getMetadata().get("namespace").toString();
                            if (namespace != null) {
                                return namespace;
                            }
                        }
                    }
                }
            }
            return "default";
        }

    }

}
