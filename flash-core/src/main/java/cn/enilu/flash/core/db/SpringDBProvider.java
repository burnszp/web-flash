package cn.enilu.flash.core.db;


import cn.enilu.flash.core.util.ApplicationContextProvider;

public class SpringDBProvider implements IDBProvider {

	@Override
	public DB get() {
		return ApplicationContextProvider.getApplicationContext().getBean(
				DB.class);
	}

}
