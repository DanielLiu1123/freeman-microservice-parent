package cn.liumouren.boot.web.component;

import cn.hutool.core.util.StrUtil;
import cn.liumouren.boot.common.WebConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 17:35
 */
public class FreemanWebLogFilter extends OncePerRequestFilter implements Ordered {

    private final ThreadLocal<Long> local = new ThreadLocal<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemanWebLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        local.set(System.currentTimeMillis());

        // 调用的 app
        String fromApp = httpServletRequest.getHeader(WebConst.Header.FROM_APP);
        if (StrUtil.isBlank(fromApp)) {
            fromApp = WebConst.DIRECTLY_CALL;
        }

        // 请求路径
        String uri = httpServletRequest.getRequestURI();

        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            // 正常请求
            LOGGER.info("app: {}, uri: {}, consuming: {}",
                    fromApp,
                    uri,
                    System.currentTimeMillis() - local.get()
            );
        } catch (IOException | ServletException e) {
            // 执行链异常, 还是打印出请求信息
            LOGGER.info("app: {}, uri: {}, consuming: {}, cause: {}",
                    fromApp,
                    uri,
                    System.currentTimeMillis() - local.get(),
                    e.getCause().toString()
            );
            throw e;
        } finally {
            // remove
            local.remove();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
