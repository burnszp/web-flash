package cn.enilu.flash.core.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *  * 验证注解
 * @author enilu(eniluzt@qq.com)
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { IPValidator.class })
public @interface IP {
	String message() default "IP地址不正确";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}