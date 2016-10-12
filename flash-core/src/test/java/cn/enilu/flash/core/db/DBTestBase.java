package cn.enilu.flash.core.db;

import cn.enilu.flash.core.lang.Lists;
import org.apache.commons.dbcp2.BasicDataSource;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.Assert.*;

public abstract class DBTestBase {

    static class Context {
        BasicDataSource dataSource;
        DB db;
    }

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

    @Table
    public static class Sample {
        @Id
        private Long id;
        @Column
        private String a;
        @Column
        private String phone1;
        @Column(name = "phone_2")
        private String phone2;
        @Column
        private BigDecimal money;
        @Column
        private DateTime updatedAt;
        @Column
        private Date createdAt;

        public Sample() {
        }

        public Sample(String a) {
            this.a = a;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public BigDecimal getMoney() {
            return money;
        }

        public void setMoney(BigDecimal money) {
            this.money = money;
        }

        public String getPhone1() {
            return phone1;
        }

        public void setPhone1(String phone1) {
            this.phone1 = phone1;
        }

        public String getPhone2() {
            return phone2;
        }

        public void setPhone2(String phone2) {
            this.phone2 = phone2;
        }

        public DateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(DateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
    }

    protected DB db;

    protected abstract Context getContext();

    @Before
    public void setupDB() {
        db = getContext().db;
    }

    @Test
    public void testCUD() {
        Sample sample = new Sample("1");
        BigDecimal money = new BigDecimal("1.11");
        sample.setMoney(money);
        int rows = db.insert(sample);
        assertEquals(1, rows);
        assertEquals(Long.valueOf(1), sample.getId());
        assertNotNull(sample.getCreatedAt());
        assertNotNull(sample.getUpdatedAt());

        int count = db.from("sample").count();
        assertEquals(1, count);

        sample = db.from(Sample.class).first(Sample.class);
        assertEquals(money, sample.getMoney());

        DateTime lastUpdatedAt = sample.getUpdatedAt();
        sleep(1001);

        sample.setA("2");
        rows = db.update(sample);
        assertEquals(1, rows);
        assertTrue(sample.getUpdatedAt().isAfter(lastUpdatedAt));

        sample.setA("abc");
        sample.setUpdatedAt(lastUpdatedAt.plusDays(1));
        db.update(sample, "a");
        Sample sample2 = db.find(Sample.class, sample.getId());
        assertTrue(sample2.getUpdatedAt().isBefore(sample.getUpdatedAt()));
        assertEquals("abc", sample2.getA());

        count = db.from("sample").count();
        assertEquals(1, count);
        String a = db.from("sample").select("a").first(String.class);
        assertEquals("abc", a);

        rows = db.delete(sample);
        assertEquals(1, rows);
        count = db.from("sample").count();
        assertEquals(0, count);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testTouch() {
        List<Sample> samples = prepareData(1);
        Sample sample = samples.get(0);

        DateTime updatedAt = sample.getUpdatedAt();
        sleep(1);

        db.touch(sample);
        assertTrue(sample.getUpdatedAt().isAfter(updatedAt));
    }

    @Test
    public void testLimit() {
        long now = System.currentTimeMillis() / 1000;
        prepareData(10);

        List<Sample> samples = db.from("sample").orderBy("id").limit(1)
                .all(Sample.class);
        assertEquals(1, samples.size());
        assertEquals("0", samples.get(0).getA());
        assertTrue(samples.get(0).getCreatedAt().getTime() / 1000 >= now);
        assertTrue(samples.get(0).getUpdatedAt().getMillis() / 1000 >= now);

        samples = db.from("sample").orderBy("id").limit(1, 1).all(Sample.class);
        assertEquals(1, samples.size());
        assertEquals("1", samples.get(0).getA());
    }

    @Test
    public void testWhere() {
        prepareData(2);
        Sample sample = db.from("sample").where("id", 1).first(Sample.class);
        assertNotNull(sample);
        assertEquals(Long.valueOf(1), sample.getId());
    }

    @Test
    public void testOr() {
        prepareData(2);
        List<Sample> samples = db.from("sample").orderBy("id")
                .segment("id = ? or id = ?", 1, 2).all(Sample.class);
        assertEquals(2, samples.size());
        assertEquals("0", samples.get(0).getA());
        assertEquals("1", samples.get(1).getA());
    }

    @Test
    public void testIn() {
        prepareData(2);
        List<Sample> samples = db.from("sample").in("id", 1, 2)
                .all(Sample.class);
        assertEquals(2, samples.size());
        assertEquals("0", samples.get(0).getA());
        assertEquals("1", samples.get(1).getA());
    }

    @Test
    public void testFind() {
        prepareData(1);
        Sample sample = db.find(Sample.class, 1L);
        assertNotNull(sample);
        assertEquals("0", sample.getA());
        assertEquals("0", sample.getPhone1());
        assertEquals("1", sample.getPhone2());
    }

    @Test
    public void testPagination() {
        int total = 10;
        prepareData(total);

        Pagination<Sample> pagination = db.from("sample").orderBy("id asc")
                .paginate(Sample.class, 1, 3);
        assertEquals(10, pagination.getTotal());
        assertEquals(1, pagination.getPage());
        assertEquals(3, pagination.getPerPage());
        List<Sample> data = pagination.getData();
        assertEquals(3, data.size());
        assertEquals("0", data.get(0).getA());
        assertEquals("1", data.get(1).getA());
        assertEquals("2", data.get(2).getA());

        Pagination<Map<String, Object>> pagination2 = db.from("sample")
                .groupBy("id").paginate(1, 3);
        assertEquals(10, pagination2.getTotal());
        assertEquals(1, pagination2.getPage());
        assertEquals(3, pagination2.getPerPage());
    }

    @Test
    public void testEach() {
        int total = 10;
        prepareData(total);

        final ArrayList<Sample> records = new ArrayList<Sample>();
        db.from(Sample.class).orderBy("id asc")
                .each(Sample.class, new RecordHandler<Sample>() {

                    @Override
                    public void process(Sample record) {
                        records.add(record);
                    }
                });
        for (int i = 0; i < total; i++) {
            assertEquals(Long.valueOf(i + 1), records.get(i).getId());
        }
    }

    @Test
    public void testForEach() {
        int total = 10;
        prepareData(total);

        final ArrayList<Sample> records = new ArrayList<Sample>();
        db.from(Sample.class).orderBy("id asc")
                .forEach(Sample.class, new RecordHandler<Sample>() {
                    @Override
                    public void process(Sample record) {
                        records.add(record);
                    }
                });

        for (int i = 0; i < total; i++) {
            assertEquals(Long.valueOf(i + 1), records.get(i).getId());
        }
    }

    @Test
    public void testIterator() {
        int total = 10;
        prepareData(total);

        for (int batchSize : new int[]{1, 2, 3, 5, 10, 11}) {
            final ArrayList<Sample> records = new ArrayList<Sample>();
            Iterator<Sample> ite = db.from(Sample.class).iterator(Sample.class,
                    batchSize);
            while (ite.hasNext()) {
                Sample sample = (Sample) ite.next();
                records.add(sample);
            }
            for (int i = 0; i < total; i++) {
                assertEquals(Long.valueOf(i + 1), records.get(i).getId());
            }
        }
    }

    protected List<Sample> prepareData(int total) {
        List<Sample> samples = Lists.newArrayList();
        for (int i = 0; i < total; i++) {
            Sample sample = new Sample(String.valueOf(i));
            sample.setPhone1(String.valueOf(i));
            sample.setPhone2(String.valueOf(i + 1));
            db.insert(sample);

            samples.add(sample);
        }
        return samples;
    }

    @Test
    public void testTag() {
        String sql = db.from(Sample.class).tag("test.tag").toSQL();
        assertEquals("select /* tag: test.tag */ * from sample", sql);
    }

    @Test
    public void testTransaction() {
        try {
            db.withTransaction(new Runnable() {
                @Override
                public void run() {
                    prepareData(1);
                    throw new RuntimeException();
                }
            });
            fail();
        } catch (RuntimeException e) {
            //ignore
        }

        int count = db.from(Sample.class).count();
        assertEquals(0, count);
    }

    @Test
    public void testSelectValue() {
        long value = db.selectValue(Long.class, "select 1");
        assertEquals(1L, value);

        prepareData(1);
        Sample sample = db.selectValue(Sample.class, "select * from sample");
        assertNotNull(sample);
        assertEquals(Long.valueOf(1), sample.getId());

        sample = db.selectValue(Sample.class, "select * from sample where id = ?", -1);
        assertNull(sample);
    }

    @Test
    public void testSelectValues() {
        prepareData(2);
        List<Sample> samples = db.selectValues(Sample.class, "select * from sample order by id asc");
        assertNotNull(samples);
        assertEquals(2, samples.size());
        assertEquals(Long.valueOf(1), samples.get(0).getId());
        assertEquals(Long.valueOf(2), samples.get(1).getId());
    }
}
