package ai4j.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * make field or instance of class singleton
 * 
 * @author victor
 *
 */
@Retention(RUNTIME)
@Target( TYPE )
public @interface Singleton {

}
