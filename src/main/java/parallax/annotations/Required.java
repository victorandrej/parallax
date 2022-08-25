package parallax.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * field annoted with this will receive a instance of another triggered class
 * 
 * @author victor
 *
 */
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
	/**
	 * a class containing a method who return the same type of object referenced by
	 * field. if not specified the field will receive a reference from any class
	 * 
	 */
	public Class<?>[] fromClass() default Object.class;
}
