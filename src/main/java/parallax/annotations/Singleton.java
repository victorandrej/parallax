package parallax.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * when class is annotated with this, just will have a single instance of class
 * 
 * @author victor
 *
 */
@Retention(RUNTIME)
@Target( TYPE )
public @interface Singleton {

}
