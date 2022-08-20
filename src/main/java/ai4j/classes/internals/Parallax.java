package ai4j.classes.internals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.ClassUtils;

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

	private QueueController queueController;
	private TypeController typeController;
	private List<Class<?>> registeredClasses;
	private InstanceController instanceController;
	private Log log;
	private ThreadManager threadManager;


	public Parallax(Log log, int maxTreads) {
		this.threadManager = new ThreadManager(maxTreads);
		this.instanceController = new InstanceController();
		this.queueController = new QueueController();
		this.typeController = new TypeController();
		this.registeredClasses = Collections.synchronizedList(new ArrayList<>());
		this.log = log;
	}

	public void register(Class<?> clazz) {
		if (clazz == null) {
			this.log.push(LogType.WARNNING, "fail on register class, class is null");
			return;
		}

		Stream.of(clazz.getDeclaredFields()).forEach(f -> {
			Required req = f.getAnnotation(Required.class);
			if (req != null) {
				this.queueController.register(f);
				for (Class<?> fromclass : req.fromClass())
					this.typeController.register(fromclass, clazz);
			}
		});
		registeredClasses.add(clazz);
	}

	void log(LogType type, String message) {
		this.log.push(type, message);
	}

	Optional<Object> createInstance(List<Instance> instances, Class<?> clazz) {

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

			if (instance == null) {
				Constructor<?> constructor = clazz.getDeclaredConstructor();
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

			if (!isManaged && clazz.isAnnotationPresent(Singleton.class))
				this.instanceController.put(clazz, instance);

			return Optional.of(instance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.push(LogType.CRITICAL, "cannot create instance of: " + clazz.getName() + " reason: " + e.getMessage());

		}

		return Optional.empty();
	}

	private List<Field> getRequiredField(Class<?> clazz) {
		return Stream.of(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Required.class)).toList();
	}

	public void trigger(Object object, CloneType cloneType, Class<?> toClass) {
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		try {
			Class<?> fromClass = Class.forName(className);
			this.trigger(fromClass, object, cloneType, toClass);

		} catch (ClassNotFoundException e) {
			// at this point the class always exists
		}

	}

	void trigger(Class<?> fromClass, Object object, CloneType cloneType, Class<?> toClass) {

		for (Class<?> clazz : this.registeredClasses) {
			this.getRequiredField(clazz).forEach(field -> {
				if (isValidInstance(object, field, cloneType, clazz, fromClass, toClass))
					this.queueController.put(Cloner.clone(object, cloneType), field);
			});
		}
	}

	private void verifyRequests() {

		for (Class<?> clazz : this.registeredClasses) {
		//	threadManager.put(() -> {
				List<Instance> instances = new ArrayList<>();
				List<Field> requiredFields = this.getRequiredField(clazz);
				requiredFields.forEach(field -> this.getInstance(instances, field));

				if (!canTrigger(instances, requiredFields))
					return;

				instances.forEach(i -> this.queueController.poll(i.field()));
				this.trigger(clazz, instances);
		//	});
		}

	}

	public void start() {
		while (true) {
			this.verifyThreads();
			this.verifyRequests();
		
		}
	}

	private void verifyThreads() {
		threadManager.verify();
	}

	private void trigger(Class<?> clazz, List<Instance> instances) {
		Trigger trigger = new Trigger(clazz, instances, this);
		trigger.trigger();
	}

	private boolean canTrigger(List<Instance> instances, List<Field> requiredFields) {
		boolean allInstanceArePopuled = instances.size() == requiredFields.size();
		boolean anyFieldHasTriggered = requiredFields.stream()
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

	private boolean isValidInstance(Object object, Field field, CloneType cloneType, Class<?> clazz, Class<?> fromClass,
			Class<?> toClass) {
		Required req = field.getAnnotation(Required.class);
		boolean expectedClass = ((req.fromClass().length == 1 && req.fromClass()[0].equals(Object.class))
				|| Arrays.asList(req.fromClass()).contains(fromClass));

		boolean requestedClass = (toClass.equals(Object.class) || toClass.equals(clazz));

		Class<?> fieldType = field.getType().isPrimitive() ? ClassUtils.primitiveToWrapper(field.getType())
				: field.getType();

		boolean sameType = fieldType.equals(object.getClass());

		return expectedClass && requestedClass && sameType;

	}

	Stream<Class<?>> aceptableClasses(Class<?> clazz) {
		return this.typeController.aceptableClasses(clazz);
	}

	void run(Runnable runnable) {
		this.threadManager.put(runnable);
	}

}
