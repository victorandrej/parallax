package ai4j.classes.internals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ai4j.annotations.CloneType;
import ai4j.annotations.Required;
import ai4j.annotations.Singleton;
import ai4j.annotations.Triggerable;
import ai4j.classes.Parallax;
import ai4j.classes.TypeController;

@Singleton
public class ClassController {

	@Required(fromClass = Parallax.class)
	private Object instance;
	@Required(fromClass = Parallax.class)
	private Method triggeredMethod;
	@Required(fromClass = Parallax.class)
	private CloneType cloneType;
	@Required(fromClass = Parallax.class)
	private Class<?> toClass;
	@Required(fromClass = Parallax.class)
	private TypeController typeController;
	
	private Parallax parallax = Parallax.getInstance();

	@Triggerable
	private void filter() {
		this.typeController.aceptableClasses(instance.getClass()).forEach(this::filter);
	}

	private void filter(Class<?> clazz) {
		if (toClass.equals(Object.class) || clazz.equals(toClass)) {
			for (Field f : clazz.getDeclaredFields()) {
				try {
					parallax.trigger(this.getClass().getMethod("filter"), f, CloneType.NONE, FieldFilter.class);
					parallax.trigger(this.getClass().getMethod("filter"), instance, CloneType.NONE, FieldFilter.class);
					parallax.trigger(this.getClass().getMethod("filter"), triggeredMethod, CloneType.NONE,
							FieldFilter.class);
					parallax.trigger(this.getClass().getMethod("filter"), cloneType, CloneType.NONE, FieldFilter.class);
					parallax.trigger(this.getClass().getMethod("filter"), toClass, CloneType.NONE, FieldFilter.class);
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
