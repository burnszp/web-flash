package cn.enilu.flash.core.lang;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BeansTest {
	private User user;

	@Before
	public void setup() {
		user = new User();
		user.setName("test");
		user.setPassword("111111");

		User.Image image = new User.Image();
		image.setId(1L);
		image.setUrl("/1.jpg");

		user.setAvatar(image);
	}

	@Test
	public void testGet() {
		String name = Beans.get(user, "name");
		assertEquals(user.getName(), name);

		Long imageId = Beans.get(user, "avatar.id");
		assertEquals(1L, imageId.longValue());
	}

	@Test
	public void testSet() {
		Beans.set(user, "name", "name");
		assertEquals("name", user.getName());

		Beans.set(user, "avatar.id", 2L);
		assertEquals(2L, user.getAvatar().getId().longValue());
	}

	@Test
	public void testCopy() {
		User result = Beans.copy(user, User.class);
		assertEquals(user, result);

		result = Beans.copy(user, User.class, "password", "name");
		assertNotEquals(user, result);
		assertNull(result.getName());
        assertNull(result.getPassword());
	}

	@Test
	public void testExtend() {
		User user1 = new User();
		Beans.extend(user1, user, "password", "name");
		assertEquals(user.getName(), user1.getName());
		assertEquals(user.getPassword(), user1.getPassword());
		assertNull(user1.getAvatar());

		user1 = new User();
		Beans.extend(user1, user);
		assertEquals(user.getName(), user1.getName());
		assertEquals(user.getPassword(), user1.getPassword());
		assertEquals(user.getAvatar(), user1.getAvatar());
		
		user1 = new User();
		user1.setName("abc");
		user.setName(null);
		Beans.extend(user1, user, true);
		assertEquals("abc", user1.getName());
	}
}
