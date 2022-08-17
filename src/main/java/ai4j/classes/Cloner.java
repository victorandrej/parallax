package ai4j.classes;

import java.lang.reflect.Field;

import org.apache.commons.lang3.ClassUtils;

import ai4j.annotations.CloneType;
import sun.misc.Unsafe;

public class Cloner {

	private static Unsafe unsafe;

	static {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static <T> T clone(T object, CloneType cloneType) {

		switch (cloneType) {
		case DEEP -> {
			return Cloner.deep(object);
		}
		case SHALLOW -> {
			return Cloner.shallow(object);
		}
		default -> {
			return object;
		}
		}

	}

	private static <T> T deep(T object) {
		Class<?> clazz = object.getClass();
		T clone = null;
		try {
			clone = (T) Cloner.unsafe.allocateInstance(clazz);
			deepClone(clazz, object, clone);
			Class<?> superClass;

			do {
				superClass = clazz.getSuperclass();
				deepClone(superClass, object, clone);
			} while (!superClass.equals(Object.class));

		} catch (InstantiationException | IllegalArgumentException | IllegalAccessException e) {
			// will never occour
			e.printStackTrace();
		}

		return clone;

	}

	private static void deepClone(Class<?> clazz, Object instance, Object clone)
			throws IllegalArgumentException, IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			field.set(clone,
					ClassUtils.isPrimitiveOrWrapper(field.getType()) ? field.get(instance) : deep(field.get(instance)));
		}

	}

	private static<T> T shallow(T object) {
		// TODO Auto-generated method stub
		return null;
	}

}
