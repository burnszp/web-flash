package cn.enilu.flash.core.lang;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ListsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testFill() {

    }

    @Test
    public void testToMap() {
        List<User> users = Lists.newArrayList();
        users.add(new User("u1"));
        users.add(new User("u2"));
        Map<String, User> map = Lists.toMap(users, "name");
        assertEquals(2, map.size());
        assertEquals(users.get(0), map.get("u1"));
        assertEquals(users.get(1), map.get("u2"));
    }

    @Test
    public void testGroup() {
        List<User> users = Lists.newArrayList();
        users.add(new User("u1"));
        users.add(new User("u1"));

        Map<String, List<User>> map = Lists.group(users, "name");
        assertEquals(1, map.size());
        List<User> list = map.get("u1");
        assertEquals(users.get(0), list.get(0));
        assertEquals(users.get(1), list.get(1));
    }

    @Test
    public void testGroupN() {
        List<Long> input = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            input.add(Long.valueOf(i));
        }

        for (int n = 1; n <= input.size() + 1; n++) {
            List<List<Long>> result = Lists.group(input, n);
            List<Long> result2 = Lists.flatten(result);
            assertEquals(input, result2);
        }
    }
}
