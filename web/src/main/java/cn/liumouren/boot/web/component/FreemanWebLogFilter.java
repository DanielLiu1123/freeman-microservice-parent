package cn.liumouren.boot.web.component;

import cn.hutool.core.util.StrUtil;
import cn.liumouren.boot.common.constant.WebConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
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

    private static final ThreadLocal<Long> local = new ThreadLocal<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemanWebLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        local.set(System.currentTimeMillis());

        // 调用的 app
        String fromApp = request.getHeader(WebConst.Header.FROM_APP);
        if (StrUtil.isBlank(fromApp)) {
            fromApp = WebConst.DIRECTLY_CALL;
        }

        // 请求路径
        String uri = request.getRequestURI();

        try {
            chain.doFilter(request, response);
            logSuccess(fromApp, uri);
        } catch (IOException | ServletException e) {
            logFail(fromApp, uri, e);
            throw e;
        } finally {
            // remove
            local.remove();
        }
    }

    private void logFail(String fromApp, String uri, Exception e) {
        // 执行链异常, 还是打印出请求信息
        LOGGER.info("app: {}, uri: {}, consuming: {}, cause: {}",
                fromApp,
                uri,
                System.currentTimeMillis() - local.get(),
                e.getCause().toString()
        );
    }

    private void logSuccess(String fromApp, String uri) {
        // 正常请求
        LOGGER.info("app: {}, uri: {}, consuming: {}",
                fromApp,
                uri,
                System.currentTimeMillis() - local.get()
        );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
