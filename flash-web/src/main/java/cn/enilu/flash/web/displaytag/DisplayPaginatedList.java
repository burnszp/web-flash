package cn.enilu.flash.web.displaytag;

import cn.enilu.flash.core.db.Pagination;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.util.List;

/**
 * 分页用的标签
 *
 * @author enilu(eniluzt@qq.com)
 * @param <T>
 */
public class DisplayPaginatedList<T> implements PaginatedList {

	private Pagination<T> pagination;

	public DisplayPaginatedList(Pagination<T> pagination) {
		this.pagination = pagination;
	}

	@Override
	public List<T> getList() {
		return pagination.getData();
	}

	@Override
	public int getPageNumber() {
		return pagination.getPage();
	}

	@Override
	public int getObjectsPerPage() {
		return pagination.getPerPage();
	}

	@Override
	public int getFullListSize() {
		return pagination.getTotal();
	}

	@Override
	public String getSortCriterion() {
		return null;
	}

	@Override
	public SortOrderEnum getSortDirection() {
		return null;
	}

	@Override
	public String getSearchId() {
		return null;
	}

}
