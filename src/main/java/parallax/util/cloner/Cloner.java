package parallax.util.cloner;

import java.lang.reflect.Field;

import parallax.util.Primitive;
import sun.misc.Unsafe;

/**
 * a cloner of objects
 * 
 * @author victor
 *
 */
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

	private static <T> T shallow(T object) {
		return clone(object, Cloner::shallowClone);
	}

	private static <T> T deep(T object) {
		return clone(object, Cloner::deepClone);
	}

	@SuppressWarnings("unchecked")
	private static <T> T clone(T object, CloneMethod cloneMethod) {
		Class<?> clazz = object.getClass();
		T clone = null;
		try {
			clone = (T) Cloner.unsafe.allocateInstance(clazz);
			cloneMethod.clone(clazz, object, clone);
			Class<?> superClass = clazz.getSuperclass();

			do {
				cloneMethod.clone(superClass, object, clone);
				superClass = superClass.getSuperclass();
			} while (superClass != null);

		} catch (Exception e) {
			// will never occour
			e.printStackTrace();
		}

		return clone;

	}

	private static <T> void deepClone(Class<?> clazz, T instance, T clone)
			throws IllegalArgumentException, IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			boolean isPrimitive = Primitive.isPrimitiveOrWrapper(field.getType())
					|| field.getType().equals(String.class);
			field.setAccessible(true);
			field.set(clone, isPrimitive ? field.get(instance) : deep(field.get(instance)));
		}

	}

	private static <T> void shallowClone(Class<?> clazz, T instance, T clone)
			throws IllegalArgumentException, IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			field.set(clone, field.get(instance));
		}

	}

	private interface CloneMethod {
		public <T> void clone(Class<?> clazz, T instance, T clone) throws Exception;
	}

}
