package cn.enilu.flash.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户访问日志输出过滤器
 * @author enilu
 */
public class AccessLogFilter extends BaseFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        AccessLog accessLog = new AccessLog(request);
        Exception exception = null;
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException e) {
            exception = e;
            throw e;
        } finally {
            accessLog.end(response, exception);
        }
    }

}
