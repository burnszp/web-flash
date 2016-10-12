package cn.enilu.flash.core.db;

import cn.enilu.flash.core.util.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PaginationTest {
	
	public static class User {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if (name != null ? !name.equals(user.name) : user.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

	@SuppressWarnings("unchecked")
	@Test
	public void testToJson() {
        Pagination<User> pagination = buildPagination();
		
		String json = pagination.toJson();
		
		Map<String, Object> restore = JsonUtil.load(json, HashMap.class);
		assertEquals(1, restore.get("page"));
		assertEquals(2, restore.get("total"));
		assertEquals(10, restore.get("perPage"));
		assertEquals(1, restore.get("totalPages"));
		assertNotNull(restore.get("data"));
	}

    private Pagination<User> buildPagination() {
        Pagination<User> pagination = new Pagination<>(1, 10, 2);
        List<User> data = new ArrayList<User>();
        for (int i=0;i<2;i++) {
            User user = new User();
            user.setName("u" + i);
            data.add(user);
        }
        pagination.setData(data);
        return pagination;
    }

    @Test
    public void testFromJson() {
        Pagination<User> pagination = buildPagination();

        String json = pagination.toJson();
        Pagination<User> pagination2 = Pagination.fromJson(json, User.class);

        assertEquals(pagination, pagination2);
    }
}
