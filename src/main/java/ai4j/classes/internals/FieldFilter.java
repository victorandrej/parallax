package ai4j.classes.internals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ai4j.annotations.CloneType;
import ai4j.annotations.Required;
import ai4j.annotations.Singleton;
import ai4j.annotations.Triggerable;
import ai4j.classes.Cloner;
import ai4j.classes.Parallax;
import ai4j.classes.QueueController;

@Singleton
public class FieldFilter {

	@Required(fromClass = ClassController.class)
	private Object instance;
	@Required(fromClass = ClassController.class)
	private Method triggeredMethod;
	@Required(fromClass = ClassController.class)
	private CloneType cloneType;
	@Required(fromClass = ClassController.class)
	private Class<?> toClass;
	@Required(fromClass = ClassController.class)
	private Field field;
	@Required(fromClass = Parallax.class)
	private QueueController queueController;

	@Triggerable
	public void filter() {

		if (field.getType().equals(instance.getClass())) {
			Required req = field.getAnnotation(Required.class);
			if (req.fromClass().equals(Object.class) || req.fromClass().equals(triggeredMethod.getDeclaringClass())) {
				queueController.put(Cloner.clone(instance, cloneType), field);
			}

		}

	}

}
