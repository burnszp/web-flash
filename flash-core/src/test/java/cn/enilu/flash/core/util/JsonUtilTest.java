package cn.enilu.flash.core.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonUtilTest {

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	public static class Foo {

		private String bar;

		public String getBar() {
			return bar;
		}

		public void setBar(String bar) {
			this.bar = bar;
		}

	}
	
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
	public static class Foo2 extends Foo {
	}

    public static class DateTimeFoo {
        private DateTime createdAt = DateTime.now();

        public DateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(DateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

	@Test
	public void testDump() {
		Foo foo = new Foo();
		String json = JsonUtil.dump(foo);
		assertEquals("{}", json);
		foo.setBar("");
		json = JsonUtil.dump(foo);
		assertEquals("{\"bar\":\"\"}", json);
		
		Foo2 foo2 = new Foo2();
		foo2.setBar("");
		json = JsonUtil.dump(foo2);
		assertEquals("{}", json);
	}
	
	@Test
	public void testHashMapDump() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", null);
		map.put("b", 2);
		String json = JsonUtil.dump(map);
		assertEquals("{\"b\":2}", json);
	}

	@Test
	public void testLoadList() {
		String json = "[{\"bar\":\"a\"},{\"bar\":\"b\"}]";
		List<Foo> fooList = JsonUtil.loadList(json, Foo.class);
		assertEquals(2, fooList.size());
		assertEquals("a", fooList.get(0).getBar());
		assertEquals("b", fooList.get(1).getBar());
	}

    @Test
    public void testDateTime() {
        DateTimeFoo foo = new DateTimeFoo();
        DateTime time = foo.getCreatedAt();

        String json = JsonUtil.dump(foo);
        DateTimeFoo foo2 = JsonUtil.load(json, DateTimeFoo.class);
        assertEquals(time, foo2.getCreatedAt());
    }


}
