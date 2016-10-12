package cn.enilu.flash.core.db;

import cn.enilu.flash.core.db.annotation.EntityReference;
import cn.enilu.flash.core.db.annotation.EntityReferences;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class DBLoadTest {

	@Table
	public static class User {

		@Id
		private Long id;
		@Column
		private String name;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	@Table
	public static class UserStat {
		@Id
		private Long userId;

		@Column
		private Integer pageview;

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Integer getPageview() {
			return pageview;
		}

		public void setPageview(Integer pageview) {
			this.pageview = pageview;
		}

	}

	@Table
	public static class Image {
		@Id
		private Long id;
		@Column
		private Long userId;
		@Column
		private String uri;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

	}

	public static class UserView extends User {

		@EntityReference(referenceProperty = "id")
		private UserStat userStat;

		@EntityReferences(referenceClass = Image.class, property = "userId", orderBy = "uri desc")
		private List<Image> images;

		public UserStat getUserStat() {
			return userStat;
		}

		public void setUserStat(UserStat userStat) {
			this.userStat = userStat;
		}

		public List<Image> getImages() {
			return images;
		}

		public void setImages(List<Image> images) {
			this.images = images;
		}

	}

	private static String url = "jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=UTF-8";
	private static BasicDataSource dataSource;
	private static DB db;

	static void executeSql(DataSource dataSource, String sql)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();
		} finally {
			conn.close();
		}
	}

	@BeforeClass
	public static void setUp() throws Exception {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername("root");
		dataSource.setPassword("");
		dataSource.setUrl(url);

		db = new DB(dataSource, DB.Type.MySQL);

		db.execute("drop table if exists image");
		db.execute("drop table if exists user_stat");
		db.execute("drop table if exists user");
		db.execute("create table user(id bigint auto_increment primary key, name varchar(200))");
		db.execute("create table user_stat(user_id bigint primary key, pageview int default 0)");
		db.execute("create table image(id bigint auto_increment primary key, user_id bigint, uri varchar(255))");

		User user = new User();
		user.setName("test");
		db.insert(user);

		UserStat userStat = new UserStat();
		userStat.setUserId(user.getId());
		userStat.setPageview(1);
		db.insert(userStat);

		for (int i = 0; i < 3; i++) {
			Image image = new Image();
			image.setUserId(user.getId());
			image.setUri("/" + i);
			db.insert(image);
		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		dataSource.close();
	}

	@Test
	public void test() {
		User user = db.find(User.class, 1);
		UserView userView = db.find(UserView.class, 1);
		db.load(userView);
		assertNotNull(userView.getUserStat());
		assertEquals(Integer.valueOf(1), userView.getUserStat().getPageview());

		assertEquals(3, userView.getImages().size());
		for (int i = 2; i >= 0; i--) {
			Image image = userView.getImages().get(2 - i);
			assertEquals(user.getId(), image.getUserId());
			assertEquals("/" + i, image.getUri());
		}

        user.setName(null);
        db.update(user);
        user = db.find(User.class, 1);
        assertNull(user.getName());
	}

	@Test
	public void testReplace() {
		Pagination<UserView> userViewPagination = db.from(User.class).paginate(
				UserView.class, 1);
		db.load(userViewPagination);
		
		assertEquals(1, userViewPagination.getTotal());
		UserStat userStat = userViewPagination.getData().get(0).getUserStat();
		assertNotNull(userStat);
		assertEquals(Integer.valueOf(1), userStat.getPageview());
	}

}
