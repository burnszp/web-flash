package cn.enilu.flash.core.validation;

import cn.enilu.flash.core.db.DB;
import cn.enilu.flash.core.db.IDBProvider;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UniqueTest {
	private static String url = "jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=UTF-8";
	private static BasicDataSource dataSource;
	private static DB db;

	public static class DBProvider implements IDBProvider {

		@Override
		public DB get() {
			return db;
		}

	}

	@Table
	@Unique.List({ @Unique(property = "name", dbProvider = DBProvider.class) })
	public static class UniqueName {
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

	@Table(name = "unique_name")
	@Unique.List({ @Unique(property = "name", condition = "ref_id=?", conditionParameterProperties = { "refId" }, dbProvider = DBProvider.class) })
	public static class UniqueName2 {
		@Id
		private Long id;

		@Column
		private Long refId;

		@Column
		private String name;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getRefId() {
			return refId;
		}

		public void setRefId(Long refId) {
			this.refId = refId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	private Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	@BeforeClass
	public static void setUp() throws Exception {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername("root");
		dataSource.setPassword("");
		dataSource.setUrl(url);

		db = new DB(dataSource, DB.Type.MySQL);

		db.execute("drop table if exists unique_name");
		db.execute("create table unique_name(id bigint auto_increment primary key, ref_id bigint, name varchar(200))");
		db.insert("unique_name", "ref_id", "1", "name", "abc");
	}

	@AfterClass
	public static void tearDown() throws Exception {
		dataSource.close();
		db = null;
	}

	@Test
	public void testUniqueOnNull() {
		UniqueName uniqueName = new UniqueName();
		uniqueName.setName(null);

		Set<ConstraintViolation<UniqueName>> set = validator
				.validate(uniqueName);
		assertEquals(0, set.size());

		uniqueName.setName(null);
		set = validator.validate(uniqueName);
		assertEquals(0, set.size());
	}

	@Test
	public void testUnique() {
		UniqueName uniqueName = new UniqueName();
		uniqueName.setName("abc");

		Set<ConstraintViolation<UniqueName>> set = validator
				.validate(uniqueName);
		assertEquals(1, set.size());

		for (ConstraintViolation<UniqueName> constraintViolation : set) {
			assertEquals("name", constraintViolation.getPropertyPath()
					.iterator().next().getName());
			assertEquals("已存在", constraintViolation.getMessage());
		}

		uniqueName.setName("abc1");
		set = validator.validate(uniqueName);
		assertEquals(0, set.size());
	}

	@Test
	public void testExistsEntity() {
		UniqueName uniqueName = db.find(UniqueName.class, 1);
		Set<ConstraintViolation<UniqueName>> set = validator
				.validate(uniqueName);
		assertEquals(0, set.size());
	}

	@Test
	public void testUniqueCondition() {
		UniqueName2 uniqueName = new UniqueName2();
		uniqueName.setRefId(1L);
		uniqueName.setName("abc");

		Set<ConstraintViolation<UniqueName2>> set = validator
				.validate(uniqueName);
		assertEquals(1, set.size());

		for (ConstraintViolation<UniqueName2> constraintViolation : set) {
			assertEquals("name", constraintViolation.getPropertyPath()
					.iterator().next().getName());
			assertEquals("已存在", constraintViolation.getMessage());
		}

		uniqueName.setRefId(2L);
		set = validator.validate(uniqueName);
		assertEquals(0, set.size());

		uniqueName.setRefId(1L);
		uniqueName.setName("abc2");
		set = validator.validate(uniqueName);
		assertEquals(0, set.size());
	}
}
