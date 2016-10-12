package cn.enilu.flash.core.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 重复输入验证注解
 * @author enilu(eniluzt@qq.com)
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { ConfirmValidator.class })
@Documented
public @interface Confirm {

	String message() default "不一致";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String property();

	String confirmProperty();

	@Target({ TYPE, ANNOTATION_TYPE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		Confirm[] value();
	}

}
