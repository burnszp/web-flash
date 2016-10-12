package cn.enilu.flash.web.filter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class NoSessionFilter extends BaseFilter {

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		RequestWrapper requestWrapper = new RequestWrapper(request);

		ResponseWrapper responseWrapper = new ResponseWrapper(response);
		chain.doFilter(requestWrapper, responseWrapper);
		responseWrapper.flushBuffer();
	}

	/**
	 * 禁止生成JSESSIONID cookie.
	 */
	@SuppressWarnings("deprecation")
    static class HttpSessionWrapper implements HttpSession {
		private Map<String, Object> data = new HashMap<String, Object>();
		private HttpServletRequestWrapper req;

		public HttpSessionWrapper(HttpServletRequestWrapper req) {
			this.req = req;
		}

		@Override
		public long getCreationTime() {
			return 0;
		}

		@Override
		public String getId() {
			return null;
		}

		@Override
		public long getLastAccessedTime() {
			return 0;
		}

		@Override
		public ServletContext getServletContext() {
			return req.getServletContext();
		}

		@Override
		public void setMaxInactiveInterval(int interval) {
		}

		@Override
		public int getMaxInactiveInterval() {
			return 0;
		}

		@SuppressWarnings("deprecation")
		public javax.servlet.http.HttpSessionContext getSessionContext() {
			return null;
		}

		@Override
		public Object getAttribute(String name) {
			return data.get(name);
		}

		@Override
		public Object getValue(String name) {
			return getAttribute(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return new Vector<String>(data.keySet()).elements();
		}

		@Override
		public String[] getValueNames() {
			return data.keySet().toArray(new String[0]);
		}

		@Override
		public void setAttribute(String name, Object value) {
			data.put(name, value);
		}

		@Override
		public void putValue(String name, Object value) {
			setAttribute(name, value);
		}

		@Override
		public void removeAttribute(String name) {
			data.remove(name);
		}

		@Override
		public void removeValue(String name) {
			removeAttribute(name);
		}

		@Override
		public void invalidate() {
			data.clear();
		}

		@Override
		public boolean isNew() {
			return false;
		}

	}

	static class RequestWrapper extends HttpServletRequestWrapper {
		private HttpSessionWrapper session = new HttpSessionWrapper(this);

		public RequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public HttpSessionWrapper getSession() {
			return session;
		}

		@Override
		public HttpSessionWrapper getSession(boolean create) {
			return session;
		}
	}

	/**
	 * 覆盖encode*Url, 禁止通过urlrewrite加入jsessionid.
	 */
    @SuppressWarnings("deprecation")
	static class ResponseWrapper extends HttpServletResponseWrapper {

		public ResponseWrapper(HttpServletResponse resp) throws IOException {
			super(resp);
		}

		@Override
		public String encodeRedirectUrl(String url) {
			return url;
		}

		@Override
		public String encodeRedirectURL(String url) {
			return url;
		}

		@Override
		public String encodeUrl(String url) {
			return url;
		}

		@Override
		public String encodeURL(String url) {
			return url;
		}

	}
}
