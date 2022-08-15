package ai4j.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class TypeController {
	private HashMap<Class<?>, List<Class<?>>> aceptableClasses;

	public TypeController() {
		this.aceptableClasses = new HashMap<>();
	}

	public synchronized void register(Class<?> type, Class<?> clazz) {
		List<Class<?>> classes = aceptableClasses.get(type);

		if (classes == null) {
			classes = new ArrayList<>();
			aceptableClasses.put(type, classes);
		}
		classes.add(clazz);
	}

	public synchronized Stream<Class<?>> aceptableClasses(Class<?> type) {

		List<Class<?>> classes = aceptableClasses.get(type);

		if (classes == null) {
			throw new NoSuchElementException("this type is not registered");
		}
		return classes.stream();
	}

}
