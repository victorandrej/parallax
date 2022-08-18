package ai4j.classes.internals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ai4j.annotations.CloneType;
import ai4j.annotations.Required;
import ai4j.annotations.Singleton;
import ai4j.classes.Cloner;
import ai4j.classes.Instance;
import ai4j.classes.InstanceController;
import ai4j.classes.QueueController;
import ai4j.classes.TypeController;
import ai4j.classes.logs.Log;
import ai4j.classes.logs.LogType;

public class Parallax {
	private static Parallax instance;

	public static Parallax getInstance() {
		//
		return Parallax.instance;
	}

	private QueueController queueController;
	private TypeController typeController;
	private List<Class<?>> registeredClasses;
	private InstanceController instanceController = new InstanceController();
	private Log log;

	private Parallax() {
		this.queueController = new QueueController();
		this.typeController = new TypeController();
		this.registeredClasses = Collections.synchronizedList(new ArrayList<>());
	}

	public Parallax(Log log) {
		this();
		this.log = log;
	}

	public void log(LogType type, String message) {
		this.log.push(type, message);
	}

	public Optional<Object> createInstance(List<Instance> instances, Class<?> clazz) {

		Object instance = null;
		boolean isManaged = false;
		if (clazz.isAnnotationPresent(Singleton.class)) {
			Optional<Object> optionalInstance = this.instanceController.get(clazz);
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
				this.instanceController.put(clazz, instance);

			return Optional.of(instance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.push(LogType.CRITICAL, "cannot create instance of: " + clazz.getName() + " reason: " + e.getMessage());

		}

		return Optional.empty();
	}

	private Stream<Field> getRequiredField(Class<?> clazz) {
		return Stream.of(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Required.class));
	}

	void trigger(Class<?> fromClass, Object object, CloneType cloneType, Class<?> toClass) {

		List<Instance> instances = new ArrayList<>();

		for (Class<?> clazz : this.registeredClasses) {
			new Thread(() -> {
				Stream<Field> requiredFields = this.getRequiredField(clazz);
				requiredFields.forEach(field -> {
					if (validInstance(object, field, cloneType, clazz, fromClass, toClass))
						this.queueController.put(Cloner.clone(object, cloneType), field);

					this.getInstance(instances, field);
				});

				if (!canTrigger(instances, requiredFields))
					return;

				this.trigger(clazz, instances);
				instances.forEach(i -> this.queueController.poll(i.field()));

			}).start();
		}

	}

	private void trigger(Class<?> clazz, List<Instance> instances) {
		Trigger trigger = new Trigger(clazz, instances, this);
		trigger.trigger();
	}

	private boolean canTrigger(List<Instance> instances, Stream<Field> requiredFields) {
		boolean allInstanceArePopuled = instances.size() == requiredFields.count();
		boolean anyFieldHasTriggered = requiredFields
				.anyMatch(f -> f.isAnnotationPresent(Required.class) && f.getAnnotation(Required.class).trigger());

		return allInstanceArePopuled || anyFieldHasTriggered;

	}

	private void getInstance(List<Instance> instances, Field field) {
		if (field.getType().equals(this.getClass())) {
			instances.add(new Instance(field, this));
			return;
		}

		this.queueController.peek(field).ifPresent(instance -> instances.add(new Instance(field, instance)));

	}

	private boolean validInstance(Object object, Field field, CloneType cloneType, Class<?> clazz, Class<?> fromClass,
			Class<?> toClass) {
		Required req = field.getAnnotation(Required.class);
		boolean expectedClass = (req.fromClass().equals(Object.class) || req.fromClass().equals(fromClass));
		boolean requestedClass = (toClass.equals(Object.class) || toClass.equals(clazz));
		boolean sameType = field.getType().equals(object.getClass());

		return expectedClass && requestedClass && sameType;

	}

	public Stream<Class<?>> aceptableClasses(Class<?> clazz) {
		return this.typeController.aceptableClasses(clazz);
	}

}
