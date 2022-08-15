package ai4j.classes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import ai4j.annotations.Required;
import ai4j.annotations.Retain;

public class Parallax {

	private QueueController queueController;
	private TypeController typeController;
	private InstanceController instanceController;
	private List<Class<?>> registeredClasses;

	public Parallax() {
		this.queueController = new QueueController();
		this.typeController = new TypeController();
		this.instanceController = new InstanceController();
		this.registeredClasses = Collections.synchronizedList(new ArrayList<>());
	}

	public void trigger(Object object) {

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		StackTraceElement caller = stack[2];

		caller.getMethodName();

	}

	private void trigger(Method method, Object object) {
		typeController.aceptableClasses(object.getClass())
				.forEach(clazz -> getFieldsByType(clazz, object.getClass()).forEach(field -> {
					Required req = field.getAnnotation(Required.class);

					if (req.clazz().equals(Object.class) || req.clazz().equals(method.getDeclaringClass())) {
						if (field.isAnnotationPresent(Retain.class)
								&& !instanceController.exists(clazz, object.getClass()))
							instanceController.put(clazz, object.getClass(), object);
						else if (!field.isAnnotationPresent(Retain.class))
							queueController.put(object, field);
					}
				}));
		validate();
	}

	private synchronized void validate() {
		registeredClasses.forEach(clazz -> {
			List<Instance> instances = new ArrayList<>();
			Stream<Field> requiredFields = this.getRequiredField(clazz);
			requiredFields.forEach(field -> {

				if (field.isAnnotationPresent(Retain.class))
					this.instanceController.get(clazz, field.getType())
							.ifPresent(instance -> instances.add(new Instance(field, instance)));
				else
					this.queueController.peek(field)
							.ifPresent(instance -> instances.add(new Instance(field, instance)));
			});

			if (instances.size() == requiredFields.count() || requiredFields.anyMatch(
					f -> f.isAnnotationPresent(Required.class) && f.getAnnotation(Required.class).trigger())) {
				
			}

		});

	}

	private Stream<Field> getRequiredField(Class<?> clazz) {
		return Stream.of(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Required.class));
	}

	private Stream<Field> getFieldsByType(Class<?> clazz, Class<?> type) {
		return Stream.of(clazz.getDeclaredFields()).filter(f -> f.getType().equals(type));
	}

}
