package cn.enilu.flash.web;

public class Page {

	private int page = 1;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		if (page < 1) {
			page = 1;
		}
		this.page = page;
	}

}
