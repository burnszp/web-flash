package cn.enilu.flash.web.filter;

import cn.enilu.flash.core.lang.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private static final Pattern ON_PATTERN = Pattern.compile("on\\w{2,20}=[^>]+",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private static final Pattern EVIL_ENTITY_PATTERN = Pattern.compile("&(colon|newline|tab);", Pattern.CASE_INSENSITIVE);

    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public HttpSession getSession() {
        return getSession(false);
    }

    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }

        return encodedValues;
    }

    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);

        if (parameter.endsWith("_escaped")) {
            return value;
        }

        if (value == null) {
            return null;
        }

        if ("low".equals(super.getParameter("xss-filtering-level"))
                || "low".equals(super.getHeader("xss-filtering-level"))) {
            return stripXSS(value);
        }

        return cleanXSS(value);
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);

        if (value == null) {
            return null;
        }

        return cleanXSS(value);
    }

    private String cleanXSS(String value) {
        value = EVIL_ENTITY_PATTERN.matcher(value).replaceAll("");
        //You'll need to remove the spaces from the html entities below
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");

        value = value.replaceAll("'", "&#39;");

        value = value.replaceAll("eval\\((.*)\\)", "");

        value = SCRIPT_PATTERN.matcher(value).replaceAll("");

        value = ON_PATTERN.matcher(value).replaceAll("");

        return value;
    }

    private String stripXSS(String value) {
        if (Strings.isBlank(value)) {
            return value;
        }
        
        // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
        // avoid encoded attacks.
        // value = ESAPI.encoder().canonicalize(value);

        // Avoid null characters
        value = value.replaceAll("", "");

        value = EVIL_ENTITY_PATTERN.matcher(value).replaceAll("");

        // Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid anything in a src='...' type of e­xpression
        //这里会把 图片 src 过滤掉
            /*
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            */

        //onerror=alert(/By:wooyun:M0ster/)
        scriptPattern = Pattern.compile("on\\w+=[^>]+", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        /**
         * 全站提交图片时都不能使用外站图片
         */
        scriptPattern = Pattern.compile("src[\\r\\n]*=[\\r\\n]*[\\'\\\"]?https?://([^'\">]+)[\\'\\\"]?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid eval(...) e­xpressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid e­xpression(...) expressions
        scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid vbscript:... e­xpressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid onload= e­xpressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        return value;
    }

}