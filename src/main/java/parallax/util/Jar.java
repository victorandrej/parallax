package parallax.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author victor
 *
 */
public final class Jar {
	public static List<Class<?>> getAllClassFromPackage(Class<?> clazz) {
		return Jar.getAllClassFromPackage(clazz.getClassLoader(), clazz.getPackageName().replace('.', '/'));

	}

	public static List<Class<?>> getAllClassFromPackage(ClassLoader classLoader, String packageName) {
		InputStream stream = classLoader.getResourceAsStream(packageName);

		if (stream == null)
			return new ArrayList<>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		List<Class<?>> classes = new ArrayList<>();

		reader.lines().forEach(line -> {

			if (line.endsWith(".class")) {
				Class<?> clazz = getClass(line, packageName);
				if (clazz != null)
					classes.add(clazz);
			} else
				classes.addAll(getAllClassFromPackage(classLoader, packageName + "/" + line));

		});

		return classes;
	}

	private static Class<?> getClass(String className, String packageName) {
		try {
			return Class.forName(packageName.replace('/', '.') + "." + className.substring(0, className.lastIndexOf('.')));
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}
