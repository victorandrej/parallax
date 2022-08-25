package parallax;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
		parallax.run(() -> {
			Optional<Object> optionalInstance = parallax.createInstance(instances, clazz);
			optionalInstance.ifPresentOrElse(this::execMethods, () -> {
				parallax.log(LogType.WARNNING, "cannot trigger the class: " + clazz.getName());

			});
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

		Stream.of(instance.getClass().getDeclaredMethods()).forEach(method -> {
			if (method.isAnnotationPresent(Triggerable.class))
				this.trigerMethod(method, instance);
		});
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

}
