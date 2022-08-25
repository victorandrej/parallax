package parallax.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import parallax.util.cloner.CloneType;

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
	 * make execution of method sync or async. if false the another methods won't be triggered until the current method finish
	 * 
	 */
	public boolean async() default true;



	/**
	 * type of clone used in method return
	 * 
	 * @return
	 */
	public CloneType cloneType() default CloneType.DEEP;

	/**
	 * who class will receive this instance, Object.class represents all
	 * 
	 * @return
	 */
	public Class<?>[] toClass() default Object.class;

}
