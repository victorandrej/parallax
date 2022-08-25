package parallax;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import parallax.annotations.Entry;
import parallax.annotations.Required;
import parallax.annotations.Singleton;
import parallax.annotations.Triggerable;
import parallax.controller.InstanceController;
import parallax.controller.QueueController;
import parallax.controller.TypeController;
import parallax.log.DefaultLogImplementation;
import parallax.log.Log;
import parallax.log.LogType;
import parallax.record.Instance;
import parallax.thread.ThreadManager;
import parallax.util.Jar;
import parallax.util.Primitive;
import parallax.util.cloner.CloneType;
import parallax.util.cloner.Cloner;

@Singleton
public class Parallax {

	private static Parallax instance;

	public static void exitApplication() {
		if (Parallax.instance != null)
			Parallax.instance.exit();
	}

	private static boolean isTriggerableClass(Class<?> clazz) {
		return Stream.of(clazz.getDeclaredFields()).anyMatch(f -> f.isAnnotationPresent(Required.class))
				|| Stream.of(clazz.getDeclaredMethods()).anyMatch(f -> f.isAnnotationPresent(Triggerable.class));

	}

	private static void registerClasses(Class<?> appClass) {
		List<Class<?>> entryClasses = new ArrayList<>();
		Jar.getAllClassFromPackage(appClass).forEach(c -> {
			if (c.isAnnotationPresent(Singleton.class)
					&& (c.isAnnotationPresent(Entry.class) || Parallax.isTriggerableClass(c))) {

				Parallax.instance.log(LogType.CRITICAL,
						"Class: " + c.getName() + " cannot be triggerable and sigleton");
				Parallax.instance.exit();
				return;
			}

			if (c.isAnnotationPresent(Entry.class))
				entryClasses.add(c);

			Parallax.instance.register(c);
		});

		Parallax.triggerEntryClasses(entryClasses);

	}

	public static void startApplication(Class<?> appClass, int maxThreads) {
		Parallax.startApplication(appClass, new DefaultLogImplementation(), maxThreads);
	}

	public static void startApplication(Class<?> appClass, Log logger, int maxThreads) {
		Parallax.instance = new Parallax(logger, maxThreads);
		Parallax.registerClasses(appClass);
		Parallax.instance.start();

	}

	private static void triggerEntryClasses(List<Class<?>> entryClasses) {
		entryClasses.forEach(c -> new Trigger(c, new ArrayList<>(), Parallax.instance).trigger());
	}

	private QueueController queueController;
	private TypeController typeController;
	private List<Class<?>> registeredClasses;
	private InstanceController instanceController;
	private Log log;
	private ThreadManager threadManager;
	private volatile boolean exit;

	private Parallax(Log log, int maxTreads) {
		this.threadManager = new ThreadManager(maxTreads);
		this.instanceController = new InstanceController();
		this.queueController = new QueueController();
		this.typeController = new TypeController();
		this.registeredClasses = Collections.synchronizedList(new ArrayList<>());
		this.log = log;
		this.exit = false;
	}

	Stream<Class<?>> aceptableClasses(Class<?> clazz) {
		return this.typeController.aceptableClasses(clazz);
	}

	/**
	 * verify if instance can be triggered
	 * 
	 * @param instances
	 * @param requiredFields
	 * @return
	 */
	private boolean canTrigger(List<Instance> instances, List<Field> requiredFields) {
		boolean allInstanceArePopuled = instances.size() == requiredFields.size();
		return allInstanceArePopuled;

	}

	/**
	 * create a instance of class , if a class is singleton return the created
	 * instance previously, or create a new
	 * 
	 * @param instances
	 * @param clazz
	 * @return
	 */
	Optional<Object> createInstance(List<Instance> instances, Class<?> clazz) {

		try {

			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object instance = constructor.newInstance();

			for (var i : instances) {
				try {
					i.field().setAccessible(true);

					i.field().set(instance, i.instance());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.push(LogType.CRITICAL, "cannot set value to field: " + i.field().getName() + " of class: "
							+ clazz.getName() + " reason: " + e.getMessage());
				}
			}

			return Optional.of(instance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.push(LogType.CRITICAL, "cannot create instance of: " + clazz.getName() + " reason: " + e.getMessage());

		}

		return Optional.empty();
	}

	/**
	 * exit the application
	 */
	public void exit() {
		this.exit = true;
	}

	/**
	 * get instance managed in queueController and add in the list
	 * 
	 * @param instances
	 * @param field
	 */
	private void getInstance(List<Instance> instances, Field field) {
		if (field.getType().isAnnotationPresent(Singleton.class)) {
			Optional<Object> instance = this.instanceController.get(field.getType());
			if (!instance.isPresent()) {
				instance = this.createInstance(new ArrayList<>(), field.getType());

				if (!instance.isPresent())
					return;

				this.instanceController.put(instance.get());

			}
			instances.add(new Instance(field, instance.get()));
			return;
		}

		this.queueController.peek(field).ifPresent(instance -> instances.add(new Instance(field, instance)));

	}

	/**
	 * get a list of required fields in class
	 * 
	 * @param clazz
	 * @return
	 */
	private List<Field> getRequiredField(Class<?> clazz) {
		return Stream.of(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Required.class)).toList();
	}

	/**
	 * verify if instance can be accepted in the sending class
	 * 
	 * @param instance
	 * @param field
	 * @param clazz
	 * @param fromClass
	 * @param toClass
	 * @return
	 */
	private boolean isValidInstance(Object instance, Field field, Class<?> clazz, Class<?> fromClass,
			Class<?>[] toClass) {
		Required req = field.getAnnotation(Required.class);
		boolean expectedClass = ((req.fromClass().length == 1 && req.fromClass()[0].equals(Object.class))
				|| Arrays.asList(req.fromClass()).contains(fromClass));

		boolean dispachedClass = ((toClass.length == 1 && toClass[0].equals(Object.class))
				|| Arrays.asList(toClass).contains(clazz));

		Class<?> fieldType = field.getType().isPrimitive() ? Primitive.primitiveToWrapper(field.getType())
				: field.getType();

		boolean sameType = fieldType.isAssignableFrom(instance.getClass());

		return expectedClass && dispachedClass && sameType;

	}

	/**
	 * log an message in application
	 * 
	 * @param type
	 * @param message
	 */
	void log(LogType type, String message) {
		this.log.push(type, message);
	}

	/**
	 * register a class to be managed in application
	 * 
	 * @param clazz
	 */
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

	/**
	 * put an execution in queue of Thread Manager
	 * 
	 * @param runnable
	 */

	void run(Runnable runnable) {
		this.threadManager.put(runnable);
	}

	/**
	 * start the verification of threads queue and requests
	 */
	private void start() {
		this.verify();
	}

	/**
	 * trigger the class
	 * 
	 * @param clazz     class to be triggered
	 * @param instances instances of fields
	 */
	private void trigger(Class<?> clazz, List<Instance> instances) {
		Trigger trigger = new Trigger(clazz, instances, this);
		trigger.trigger();
	}

	/**
	 * trigger a object to application
	 * 
	 * @param fromClass
	 * @param object
	 * @param cloneType
	 * @param toClass
	 */
	void trigger(Class<?> fromClass, Object object, CloneType cloneType, Class<?>[] toClass) {

		for (Class<?> clazz : this.registeredClasses) {
			this.getRequiredField(clazz).forEach(field -> {
				if (isValidInstance(object, field, clazz, fromClass, toClass))
					this.queueController.put(Cloner.clone(object, cloneType), field);
			});
		}
	}

	/**
	 * trigger a object to application, this method get the current class who
	 * trigger it
	 * 
	 * @param object
	 * @param cloneType
	 * @param toClass
	 */
	public void trigger(Object object, CloneType cloneType, Class<?>[] toClass) {
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		try {
			Class<?> fromClass = Class.forName(className);
			this.trigger(fromClass, object, cloneType, toClass);

		} catch (ClassNotFoundException e) {
			// at this point the class always exists
		}

	}

	/**
	 * verify the threads and requests
	 */
	private void verify() {
		while (true) {
			if (exit)
				return;

			this.verifyThreads();
			this.verifyRequests();
		}

	}

	/**
	 * veriy the triggered object sending to the class and triggering if valid
	 */
	private void verifyRequests() {

		for (Class<?> clazz : this.registeredClasses) {

			List<Instance> instances = new ArrayList<>();
			List<Field> requiredFields = this.getRequiredField(clazz);
			requiredFields.forEach(field -> this.getInstance(instances, field));

			if (!canTrigger(instances, requiredFields))
				continue;

			instances.forEach(i -> this.queueController.poll(i.field()));
			this.trigger(clazz, instances);

		}

	}

	/**
	 * verify the threads
	 */
	private void verifyThreads() {
		threadManager.verify();
	}

}
