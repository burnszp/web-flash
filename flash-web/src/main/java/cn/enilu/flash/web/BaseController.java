package cn.enilu.flash.web;

import cn.enilu.flash.web.auth.UserContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * controller基类
 * @author  enilu(eniluzt@qq.com)
 */
public class BaseController {
	/**
	 * 获取request
	 * @return request
	 */
	protected HttpServletRequest getRequest() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return sra.getRequest();
	}

	/**
	 * 获取response
	 * @return response
	 */
	protected HttpServletResponse getResponse() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return sra.getResponse();
	}

	/**
	 * 获取客户端ip
	 * @return
	 */
	protected String getRealIP() {
		return WebUtil.getRealIP(getRequest());
	}

	/**
	 * 根据指定request获取客户端ip
	 * @param request
	 * @return
	 */
	protected String getRealIP(HttpServletRequest request) {
		return WebUtil.getRealIP(request);
	}

	/**
	 * 获取cookie
	 * @param request  request对象
	 * @param name cookie名称
	 * @return
	 */
	protected Cookie getCookie(HttpServletRequest request, String name) {
		return WebUtil.getCookie(request, name);
	}

	/**
	 * 获取cookie的字符串值
	 * @param request request对象
	 * @param name cookie名称
	 * @return
	 */
	protected String getCookieValue(HttpServletRequest request, String name) {
		return WebUtil.getCookieValue(request, name);
	}

	/**
	 * 写入永久性cookie值
	 * @param response response对象
	 * @param name cookie名称
	 * @param value cookie值
	 */
	protected void addPermanencyCookie(HttpServletResponse response,
			String name, String value) {
		addPermanencyCookie(response, name, value, "/");
	}

	protected void addPermanencyCookie(HttpServletResponse response,
			String name, String value, String path) {
		int expiry = 10 * 365 * 24 * 3600;
		addCookie(response, name, value, path, expiry, true);
	}

	/**
	 * 写入有效期为非永久的cookie
	 * @param response response对象
	 * @param name cookie名称
	 * @param value cookie值
	 * @param path cookie路径
	 * @param expiry 有效期，单位：秒
	 */
	protected void addCookie(HttpServletResponse response, String name,
							 String value, String path, int expiry) {
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
	protected void addCookie(HttpServletResponse response, String name,
			String value, String path, int expiry, boolean httpOnly) {
		WebUtil.addCookie(response, name, value, path, expiry, httpOnly);
	}

	/**
	 * 获取请求方法类型
	 * @param request request对象
	 * @return
	 */
	protected String getMethod(HttpServletRequest request) {
		return request.getMethod();
	}

	/**
	 * 根据request获取系统当前操作用户
	 * @param request request对象
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getCurrentUser(HttpServletRequest request) {
		return (T) request.getAttribute(UserContext.USER_ATTRIBUTE);
	}

	/**
	 * 获取当前操作用户
	 * @param <T>
	 * @return
	 */
	protected <T> T getCurrentUser() {
		return (T) getRequest().getAttribute(UserContext.USER_ATTRIBUTE);
	}
	
	protected UserContext getUserContext() {
		return (UserContext) getRequest().getAttribute(UserContext.CONTEXT_ATTRIBUTE);
	}

	/**
	 * 从request中获取查询条件
	 * @param request request对象
	 * @return QueryForm
	 */
	protected QueryForm getQueryForm(HttpServletRequest request) {
		QueryForm queryForm = QueryForm.build(request);
        request.setAttribute("qf", queryForm);
		return queryForm;
	}

	/**
	 * 判断当前请求是否是get请求
	 * @param request
	 * @return
	 */
	protected boolean isGet(HttpServletRequest request) {
		return "GET".equalsIgnoreCase(getMethod(request));
	}

	/**
	 * 判断当前请求是否是post请求
	 * @param request
	 * @return
	 */
	protected boolean isPost(HttpServletRequest request) {
		return "POST".equalsIgnoreCase(getMethod(request));
	}

	/**
	 * 判断当前请求是否是put请求
	 * @param request
	 * @return
	 */
	protected boolean isPut(HttpServletRequest request) {
		return "PUT".equalsIgnoreCase(getMethod(request));
	}

	/**
	 * 判断当前请求是否是delete请求
	 * @param request
	 * @return
	 */
	protected boolean isDelete(HttpServletRequest request) {
		return "DELETE".equalsIgnoreCase(getMethod(request));
	}

	/**
	 * 判断当前请求是否是ajax请求
	 * @param request
	 * @return
	 */
	public boolean isAjax(HttpServletRequest request) {
		return WebUtil.isAjax(request);
	}

}
