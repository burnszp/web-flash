package cn.enilu.flash.core.validation;



import cn.enilu.flash.core.db.IDBProvider;
import cn.enilu.flash.core.db.SpringDBProvider;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *  唯一性验证注解
 * @author enilu(eniluzt@qq.com)
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { UniqueValidator.class })
@Documented
public @interface Unique {

	String message() default "已存在";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends IDBProvider> dbProvider() default SpringDBProvider.class;

	String property();

	String condition() default "";

	String[] conditionParameterProperties() default {};

	@Target({ TYPE, ANNOTATION_TYPE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		Unique[] value();
	}

}
