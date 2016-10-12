package cn.enilu.flash.web.filter;



import cn.enilu.flash.core.lang.Strings;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssFilter extends BaseFilter {
    private Pattern ignoreUrlsPattern;

    public void init(FilterConfig filterConfig) throws ServletException {
        String ignoreUrlsParam = filterConfig.getInitParameter("ignoreUrls");
        if (!Strings.isBlank(ignoreUrlsParam)) {
            ignoreUrlsPattern = Pattern.compile(ignoreUrlsParam);
        }
    }

    private boolean shouldIgnore(HttpServletRequest request) {
        if (ignoreUrlsPattern == null) {
            return false;
        }
        Matcher m = ignoreUrlsPattern.matcher(request.getRequestURI());
        return m.find();
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (shouldIgnore(request)) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(new XssHttpServletRequestWrapper(request), response);
        }
    }

}