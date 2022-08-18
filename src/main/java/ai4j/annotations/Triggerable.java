package ai4j.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * register the method who can be triggered
 * 
 * @author victor
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Triggerable {
	/**
	 * make execution of method sync or async
	 * 
	 */
	public boolean async() default true;

	/**
	 * order of execution of methods
	 * 
	 */
	public int triggerOrder() default 1;
	
	public CloneType cloneType() default CloneType.NONE;
	
	public Class<?> toClass() default Object.class;
	
}
