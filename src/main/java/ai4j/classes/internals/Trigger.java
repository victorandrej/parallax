package ai4j.classes.internals;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ai4j.annotations.Required;
import ai4j.annotations.Triggerable;
import ai4j.classes.Instance;
import ai4j.classes.logs.LogType;

public final class Trigger {

	private Class<?> clazz;
	private List<Instance> instances;
	private Parallax parallax;

	Trigger(Class<?> clazz, List<Instance> instances, Parallax parallax) {
		this.clazz = clazz;
		this.instances = instances;
		this.parallax = parallax;
	}

	void trigger() {

		Optional<Object> optionalInstance = parallax.createInstance(instances, clazz);
		optionalInstance.ifPresentOrElse(this::execMethods, () -> {
			parallax.log(LogType.WARNNING, "cannot trigger the class: " + clazz.getName());

		});
	}

	private void execMethods(Object instance) {
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

	private void trigerMethod(Method method, Object instance) {
		Triggerable triggerable = method.getAnnotation(Triggerable.class);
		method.setAccessible(true);

		Runnable runnable = () -> {
			try {
				Object returnObject = method.invoke(instance);

				if (!method.getReturnType().equals(void.class) && returnObject != null) {
					for (Class<?> toclass : triggerable.toClass())
						parallax.trigger(method.getDeclaringClass(), returnObject, triggerable.cloneType(), toclass);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				parallax.log(LogType.CRITICAL, "Method: " + method.getName() + " cannot be invoked on class: "
						+ instance.getClass().getName() + " reason: " + e.getMessage());
			}
		};

		if (triggerable.async())
			parallax.run(runnable);
		else
			runnable.run();
	}

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