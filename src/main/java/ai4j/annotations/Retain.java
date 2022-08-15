package ai4j.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * Retains the first value until end life of application
 * @author victor
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Retain {

}
