package parallax.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * the controller of singletons instance in application
 * 
 * @author victor
 *
 */
public class InstanceController {
	private Map<Class<?>, Object> instances;

	public InstanceController() {
		this.instances = new HashMap<>();
	}

	public synchronized void put(Class<?> clazz, Object instance) {

		this.instances.put(clazz, instance);

	}

	public Optional<Object> get(Class<?> clazz) {

		return Optional.ofNullable(this.instances.get(clazz));
	}

	public boolean exists(Class<?> clazz) {
		Object classIntance = instances.get(clazz);

		return classIntance != null;
	}
}
