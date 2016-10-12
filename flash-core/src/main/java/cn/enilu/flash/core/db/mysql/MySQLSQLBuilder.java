package cn.enilu.flash.core.db.mysql;

import cn.enilu.flash.core.db.GenericSQLBuilder;
import cn.enilu.flash.core.db.ISQLBuilder;

public class MySQLSQLBuilder extends GenericSQLBuilder implements ISQLBuilder {
	
	@Override
	public String getLastInsertIdSQL() {
		return "select last_insert_id()";
	}

}
