package ai4j.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InstanceController {
	private Map<Class<?>, Map<Class<?>, Object>> instances;

	public InstanceController() {
		this.instances = new HashMap<>();
	}

	public synchronized void put(Class<?> clazz, Class<?> type, Object instance) {
		Map<Class<?>, Object> classInstances = instances.get(clazz);
		if (classInstances == null) {
			classInstances = new HashMap<>();
			this.instances.put(clazz, classInstances);
		}
		classInstances.put(type, instance);
	}

	public Optional<Object> get(Class<?> clazz, Class<?> type) {
		Optional<Map<Class<?>, Object>> classIntaces = Optional.ofNullable(this.instances.get(clazz));
		if (classIntaces.isPresent())
			return Optional.ofNullable(classIntaces.get().get(type));
		return Optional.empty();
	}

	public boolean exists(Class<?> clazz, Class<?> type) {
		Map<Class<?>, Object> classIntances = instances.get(clazz);

		return classIntances != null && classIntances.get(type) != null;
	}
}
