package parallax.record;

import java.lang.reflect.Field;
/**
 * represent the link of instance  and the field who will receive it
 * @author victor
 *
 */
public record Instance(Field field,Object instance) {

}
