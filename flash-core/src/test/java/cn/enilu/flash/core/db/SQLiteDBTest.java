package cn.enilu.flash.core.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.SQLException;

public class SQLiteDBTest extends DBTestBase {
	private static String url = "jdbc:sqlite:sample.db";
	private static Context context;

	@BeforeClass
	public static void setUp() throws Exception {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.sqlite.JDBC");
		dataSource.setUrl(url);
        dataSource.setValidationQuery("select 1");
		DB db = new DB(dataSource, DB.Type.SQLITE);

		context = new Context();
		context.db = db;
		context.dataSource = dataSource;
	}

	@AfterClass
	public static void tearDown() throws Exception {
		context.dataSource.close();
	}

	protected Context getContext() {
		return context;
	}

	@Before
	public void createTable() throws SQLException {
		executeSql(context.dataSource, "drop table if exists sample");
		executeSql(context.dataSource,
				"create table sample(id INTEGER primary key autoincrement, a varchar(200)," +
                        " phone1 varchar(30), phone_2 varchar(30)," +
                        " money real, created_at datetime NOT NULL, updated_at datetime NOT NULL)");
	}

}
