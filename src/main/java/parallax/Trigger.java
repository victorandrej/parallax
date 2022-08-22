package parallax;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import parallax.annotations.Required;
import parallax.annotations.Triggerable;
import parallax.log.LogType;
import parallax.record.Instance;
import parallax.util.cloner.CloneType;

/**
 * a trigger of methods
 * 
 * @author victor
 *
 */
public final class Trigger {

	private Class<?> clazz;
	private List<Instance> instances;
	private Parallax parallax;

	Trigger(Class<?> clazz, List<Instance> instances, Parallax parallax) {
		this.clazz = clazz;
		this.instances = instances;
		this.parallax = parallax;
	}

	/**
	 * trigger the annotated method of a class
	 */
	void trigger() {

		Optional<Object> optionalInstance = parallax.createInstance(instances, clazz);
		optionalInstance.ifPresentOrElse(this::execMethods, () -> {
			parallax.log(LogType.WARNNING, "cannot trigger the class: " + clazz.getName());

		});
	}

	/**
	 * get all method who contains the {@link Triggerable Triggerable} annotation
	 * and order
	 * 
	 * @param instance
	 */
	private void execMethods(Object instance) {
		if (instance == null)
			return;

		List<Integer> methodsToTrigger = getMethodsToTrigger(instance);
		Stream.of(instance.getClass().getDeclaredMethods())
				.filter(f -> f.isAnnotationPresent(Triggerable.class) && (methodsToTrigger.isEmpty()
						|| methodsToTrigger.contains(f.getAnnotation(Triggerable.class).triggerOrder())))
				.sorted((o0, o1) -> {
					Triggerable t0 = o0.getAnnotation(Triggerable.class);
					Triggerable t1 = o1.getAnnotation(Triggerable.class);
					return t0.triggerOrder() == t1.triggerOrder() ? 0 : t0.triggerOrder() < t1.triggerOrder() ? -1 : 1;
				}).forEach(method -> this.trigerMethod(method, instance));
	}

	/**
	 * trigger the method and trigger the result to Parallax Application
	 * 
	 * @param method
	 * @param instance
	 */
	private void trigerMethod(Method method, Object instance) {
		Triggerable triggerable = method.getAnnotation(Triggerable.class);
		method.setAccessible(true);

		Runnable runnable = () -> {
			try {
				Object returnObject = method.invoke(instance);

				if (returnObject != null) {
					parallax.trigger(method.getDeclaringClass(), returnObject, triggerable.cloneType(),
							triggerable.toClass());
				}
			} catch (InvocationTargetException e) {
				parallax.trigger(method.getDeclaringClass(), e.getCause(), CloneType.NONE,
						new Class<?>[] { Object.class });
			} catch (IllegalAccessException | IllegalArgumentException e) {
				parallax.trigger(method.getDeclaringClass(), e, CloneType.NONE, new Class<?>[] { Object.class });
				parallax.log(LogType.CRITICAL, "Method: " + method.getName() + " cannot be invoked on class: "
						+ instance.getClass().getName() + " reason: " + e.getMessage());
			}
		};

		if (triggerable.async())
			parallax.run(runnable);
		else
			runnable.run();
	}

	/**
	 * get all methods to be triggered. a method just is valid if all required
	 * fields is instanced, or the field who trigger contains it, Attention: if any
	 * field specify an method just the method will be accepted
	 * 
	 * @param instance
	 * @return
	 */
	private List<Integer> getMethodsToTrigger(Object instance) {

		List<Integer> methodsToTrigger = new ArrayList<>();

		for (Field f : instance.getClass().getFields()) {

			if (!hasInstance(f, instance) || !f.isAnnotationPresent(Required.class)
					|| !f.getAnnotation(Required.class).trigger()) {
				continue;
			}

			int[] values = f.getAnnotation(Required.class).methodTrigger();

			for (int i : values) {
				if (!methodsToTrigger.contains(i))
					methodsToTrigger.add(i);
			}

		}

		return methodsToTrigger;
	}

	/**
	 * verify if field has instance
	 * 
	 * @param field
	 * @param instance
	 * @return
	 */
	private boolean hasInstance(Field field, Object instance) {
		field.setAccessible(true);
		try {
			return field.get(instance) != null;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			parallax.log(LogType.WARNNING, "Cannot verify instance of field: " + field.getName() + " of class: "
					+ field.getDeclaringClass() + " reason: " + e.getMessage());

		}
		return false;
	}

}
