package parallax.controller;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * the controller of singletons instance in application
 * 
 * @author victor
 *
 */
public class InstanceController {
	private Map<Class<?>, Object> instances;

	public InstanceController() {
		this.instances = new ConcurrentHashMap<>();
	}

	public synchronized void put(Object instance) {

		this.instances.put(instance.getClass(), instance);

	}

	public Optional<Object> get(Class<?> clazz) {
		return Optional.ofNullable(this.instances.get(clazz));
	}

	public boolean exists(Class<?> clazz) {
		Object classIntance = instances.get(clazz);

		return classIntance != null;
	}
}
