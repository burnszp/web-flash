package cn.enilu.flash.core.validation;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ConfirmTest {

	@Confirm.List({
			@Confirm(property = "password", confirmProperty = "confirmPassword"),
			@Confirm(property = "email", confirmProperty = "confirmEmail") })
	public static class User {
		private String password;
		private String confirmPassword;

		private String email;
		private String confirmEmail;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getConfirmPassword() {
			return confirmPassword;
		}

		public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getConfirmEmail() {
			return confirmEmail;
		}

		public void setConfirmEmail(String confirmEmail) {
			this.confirmEmail = confirmEmail;
		}

	}

	private Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	@Test
	public void test() {
		User user = new User();
		user.setPassword("123");

		Set<ConstraintViolation<User>> set = validator.validate(user);
		assertEquals(1, set.size());
		for (ConstraintViolation<User> constraintViolation : set) {
			assertEquals("confirmPassword", constraintViolation
					.getPropertyPath().iterator().next().getName());
			assertEquals("两次输入不一致", constraintViolation.getMessage());
		}

		user.setConfirmPassword("123");
		set = validator.validate(user);
		assertEquals(0, set.size());

		user.setPassword(null);
		user.setConfirmPassword(null);
		set = validator.validate(user);
		assertEquals(0, set.size());
	}

}
