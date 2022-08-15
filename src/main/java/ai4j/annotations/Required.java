package ai4j.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
	/**
	 * a class containing a method who return the same type of object referenced by
	 * field. if not specified the field will receive a reference from any class
	 * 
	 */
	public Class<?> clazz() default Object.class;

	/**
	 * if this flag is true when an field obtains a reference of object this class
	 * will trigger a random method if {@link #methodTrigger() methodTrigger} no
	 * specified \n
	 * 
	 */
	public boolean trigger() default false;

	/**
	 * method in class to be triggered, if not specified all methods will be
	 * triggered in order
	 * 
	 */
	public int methodTrigger() default -1;

}
