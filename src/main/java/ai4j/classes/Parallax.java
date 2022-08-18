package ai4j.classes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ai4j.annotations.CloneType;
import ai4j.annotations.Required;
import ai4j.annotations.Singleton;
import ai4j.annotations.Triggerable;
import ai4j.classes.logs.Log;
import ai4j.classes.logs.LogType;

public class Parallax {
	private static Parallax instance;
	private static InstanceController instanceController = new InstanceController();
	private static Log log;

	public static Parallax getInstance() {
		//
		return Parallax.instance;
	}

	private QueueController queueController;
	private TypeController typeController;
	private List<Class<?>> registeredClasses;

	private Parallax() {
		this.queueController = new QueueController();
		this.typeController = new TypeController();
		this.registeredClasses = Collections.synchronizedList(new ArrayList<>());
	}

	public Parallax(Log log) {
		this();
		this.log = log;
	}

	public static Optional<Object> createInstance(List<Instance> instances, Class<?> clazz) {

		Object instance = null;
		boolean isManaged = false;
		if (clazz.isAnnotationPresent(Singleton.class)) {
			Optional<Object> optionalInstance = Parallax.instanceController.get(clazz);
			if (optionalInstance.isPresent()) {
				instance = optionalInstance.get();
				isManaged = true;
			}
		}

		try {

			if (instance != null) {
				Constructor<?> constructor = clazz.getConstructor();
				constructor.setAccessible(true);
				instance = constructor.newInstance();
			}

			for (var i : instances) {
				try {
					i.field().setAccessible(true);
					i.field().set(instance, i.instance());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.push(LogType.CRITICAL, "cannot set value to field: " + i.field().getName() + " of class: "
							+ clazz.getName() + " reason: " + e.getMessage());
				}
			}

			if (!isManaged)
				Parallax.instanceController.put(clazz, instance);

			return Optional.of(instance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.push(LogType.CRITICAL, "cannot create instance of: " + clazz.getName() + " reason: " + e.getMessage());

		}

		return Optional.empty();
	}

	private Stream<Field> getFieldsByType(Class<?> clazz, Class<?> type) {
		return Stream.of(clazz.getDeclaredFields()).filter(f -> f.getType().equals(type));
	}

	private List<Integer> getMethodsToTrigger(Object instance) {

		List<Integer> methodsToTrigger = new ArrayList<>();

		Stream.of(instance.getClass().getFields())
				.filter(f -> hasInstance(f, instance) && f.isAnnotationPresent(Required.class)
						&& f.getAnnotation(Required.class).trigger())
				.map(f -> f.getAnnotation(Required.class).methodTrigger()).reduce(this::joinIntArray).ifPresent(i -> {
					methodsToTrigger.addAll(Arrays.stream(i).distinct().boxed().collect(Collectors.toList()));
				});
		return methodsToTrigger;
	}

	private Stream<Field> getRequiredField(Class<?> clazz) {
		return Stream.of(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Required.class));
	}

	private boolean hasInstance(Field field, Object instance) {
		field.setAccessible(true);
		try {
			return field.get(instance) != null;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.push(LogType.WARNNING, "Cannot verify instance of field: " + field.getName() + " of class: "
					+ field.getDeclaringClass() + " reason: " + e.getMessage());

		}
		return false;
	}

	private int[] joinIntArray(int[] array1, int[] array2) {
		int[] newArray = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, newArray, array1.length, array2.length);
		return newArray;

	}

	private void prepareMethods(Object instance) {
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

	public void register(Class<?> clazz) {

	}

	private void trigerMethod(Method method, Object instance) {
		Triggerable triggerable = method.getAnnotation(Triggerable.class);

		method.setAccessible(true);

		Runnable runnable = () -> {
			try {
				this.trigger(method, method.invoke(instance), triggerable.cloneType(), triggerable.toClass());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.push(LogType.CRITICAL, "Method: " + method.getName() + " cannot be invoked on class: "
						+ instance.getClass().getName() + " reason: " + e.getMessage());
			}
		};

		if (triggerable.async())
			new Thread(runnable).start();
		else
			runnable.run();

	}

	public void trigger(Method triggeredmethod, Object object, CloneType cloneType, Class<?> toClass) {

		typeController.aceptableClasses(object.getClass()).forEach(clazz -> {

			if (toClass.equals(Object.class) || clazz.equals(toClass))
				getFieldsByType(clazz, object.getClass()).forEach(field -> {
					Required req = field.getAnnotation(Required.class);

					if (req.fromClass().equals(Object.class)
							|| req.fromClass().equals(triggeredmethod.getDeclaringClass())) {

						queueController.put(Cloner.clone(object, cloneType), field);

					}
				});
		});

		this.validate();
	}

	private synchronized void validate() {
		registeredClasses.forEach(clazz -> {

			List<Instance> instances = new ArrayList<>();
			Stream<Field> requiredFields = this.getRequiredField(clazz);
			requiredFields.forEach(field -> {

				this.queueController.peek(field).ifPresent(instance -> instances.add(new Instance(field, instance)));
			});

			if (instances.size() == requiredFields.count() || requiredFields.anyMatch(
					f -> f.isAnnotationPresent(Required.class) && f.getAnnotation(Required.class).trigger())) {

				Optional<Object> optionalInstance = Parallax.createInstance(instances, clazz);

				optionalInstance.ifPresentOrElse(this::prepareMethods, () -> {
					log.push(LogType.WARNNING, "cannot trigger the class: " + clazz.getName());

				});
			}

		});

	}

}
