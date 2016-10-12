package cn.enilu.flash.web;

import cn.enilu.flash.core.db.Query;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.displaytag.tags.TableTagParameters;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将request请求中查询参数包转为查询表单，方便service中数据库查询
 * @author enilu(eniluzt@qq.com)
 */
public class QueryForm {
	private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^d-(\\d+)-s");

	private Map<String, String[]> conditions = Maps.newHashMap();
	private String orderBy;
	private int page = 1;

	/**
	 * 判断查询条件中是否包含指定属性
	 * @param name 字段名称-对应数据库字段名
	 * @return
	 */
	public boolean contains(String name) {
		return conditions.containsKey(name);
	}

	/**
	 * 判断查询条件中指定属性是否有多个值
	 * @param name 字段名称
	 * @return
	 */
	public boolean containsMultiValue(String name) {
		String[] values = conditions.get(name);
		return values != null && values.length > 1;
	}

	/**
	 * 增加查询条件
	 * @param name 字段名称
	 * @param value 字段值
	 */
	public void set(String name, String value) {
		conditions.put(name, new String[] { value });
	}

	/**
	 * 增加查询条件，针对in 条件
	 * @param name 字段名称
	 * @param value 字段值数组
	 */
	public void set(String name, String[] value) {
		conditions.put(name, value);
	}

	/**
	 * 获取查询条件值
	 * @param name 字段名称
	 * @return 指定字段名称对应的查询值，如果该值是数组，则返回数组中第一个值
	 */
	public String get(String name) {
		String values[] = conditions.get(name);
		if (values != null) {
			return values[0];
		}
		return null;
	}

	/**
	 * 获取查询条件值，针对查询值是数组的字段
	 * @param name 字段名称
	 * @return 查询值数组
	 */
	public String[] getValues(String name) {
		return conditions.get(name);
	}

	/**
	 * 获取要排序的字段
	 * @return 排序的字段名称
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置要排序的字段
	 * @param orderBy
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * 获取所有查询条件
	 * @return
	 */
	public Map<String, String[]> getConditions() {
		return conditions;
	}

	/**
	 * 获取当前查询页数
	 * @return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * 设置当前查询页数
	 * @param page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * 在Query查询对象中组装like查询条件
	 * @param q Query查询对象
	 * @param names 字段名称列表
	 */
	public void setupLikeConditions(Query q, String... names) {
		for (String name : names) {
			if (!contains(name)) {
				continue;
			}
			q.like(name, "%" + get(name) + "%");
		}
	}
	/**
	 * 在Query查询对象中组装equals查询条件
	 * @param q Query查询对象
	 * @param names 字段名称列表
	 */
	public void setupEqConditions(Query q, String... names) {
		for (String name : names) {
			if (!contains(name)) {
				continue;
			}

			if (containsMultiValue(name)) {
				q.where(name, getValues(name));
			} else {
				q.where(name, get(name));
			}
		}
	}

	private static String detectTableId(HttpServletRequest req) {
		// d-149522-s
		for (String name : req.getParameterMap().keySet()) {
			Matcher m = TABLE_NAME_PATTERN.matcher(name);
			if (m.find()) {
				return m.group(1);
			}
		}
		return null;
	}

	/**
	 * 将客户单请求条件组装为QueryForm对象
	 * @param req 客户端请求对象
	 * @return QueryForm 封装后的查询表单对象
	 */
	public static QueryForm build(HttpServletRequest req) {
		QueryForm q = new QueryForm();

		String tableId = detectTableId(req);
		if (tableId != null) {
			String sort = req.getParameter("d-" + tableId + "-"
					+ TableTagParameters.PARAMETER_SORT);
			String order = req.getParameter("d-" + tableId + "-"
					+ TableTagParameters.PARAMETER_ORDER);
			order = "2".equals(order) ? "!" : "";
			if (sort != null) {
				q.setOrderBy(sort + order);
			}
		}

		String page = req.getParameter("page");
		if (!Strings.isNullOrEmpty(page)) {
			q.setPage(Integer.parseInt(page));
		}

		// override sortBy by if order present
		String order = req.getParameter("order");
		if (!Strings.isNullOrEmpty(order)) {
			q.setOrderBy(order);
		}

		Enumeration<String> e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if ("page".equals(name) || "order".equals(name)) {
				continue;
			}
			if (Strings.isNullOrEmpty(req.getParameter(name))) {
				continue;
			}
			String values[] = req.getParameterValues(name);
			q.set(name, values);
		}

		return q;
	}
}
