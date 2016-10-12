package cn.enilu.flash.core.db.sqlite;


import cn.enilu.flash.core.db.GenericSQLBuilder;
import cn.enilu.flash.core.db.ISQLBuilder;

public class SQLiteSQLBuilder extends GenericSQLBuilder implements ISQLBuilder {

	@Override
	public String getLastInsertIdSQL() {
		return "select last_insert_rowid()";
	}

	public void lock() {
		//no select xxx for update
	}
}
