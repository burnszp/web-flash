package cn.enilu.flash.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * web工具类
 * @author enilu(eniluzt@qq.com)
 */
public final class WebUtil {
    private WebUtil() {
    }

    /**
     * 获取客户端
     * @param request 客户端请求
     * @return
     */
    public static String getRealIP(HttpServletRequest request) {
        String ip = request.getHeader("HTTP_X_REAL_IP");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 判断当前请求是否是ajax请求
     * @param request 客户端请求
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        String requestType = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestType);
    }

    /**
     * 获取指定cookie的字符串值
     * @param request 客户端信息
     * @param name cookie 名称
     * @return cookie值
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie c = getCookie(request, name);
        if (c == null) {
            return null;
        }
        return c.getValue();
    }

    /**
     * 获取指定cookie值
     * @param request 客户端信息
     * @param name  cookie名称
     * @return cookie对象
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    /**
     * 向客户端添加永久性cookie信息
     * @param response 服务端对象
     * @param name cookie名称
     * @param value cookie值
     */
    public static void addPermanencyCookie(HttpServletResponse response,
                                       String name, String value) {
        addPermanencyCookie(response, name, value, "/");
    }

    /**
     * 向客户端添加cookie信息 并指定cookie信息所在路径，默认有效期是10年（没错我就是要永久生效）
     * @param response 服务端对象
     * @param name cookie名称
     * @param value cookie值
     * @param path cookie文件路径
     */
    public static void addPermanencyCookie(HttpServletResponse response,
                                       String name, String value, String path) {
        int expiry = 10 * 365 * 24 * 3600;
        addCookie(response, name, value, path, expiry, true);
    }

    /**
     * 向客户端添加cookie信息，并提供详细的设置参数
     * @param response 服务端对象
     * @param name cookie名称
     * @param value cookie值
     * @param path cookie文件路径
     * @param expiry 有效期，单位秒
     * @param httpOnly 指定cookie是否可通过客户端脚本访问
     */
    public static void addCookie(HttpServletResponse response, String name,
                             String value, String path, int expiry, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expiry);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }
}
