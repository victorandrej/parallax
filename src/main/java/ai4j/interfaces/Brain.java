package ai4j.interfaces;

import java.lang.reflect.Constructor;
import java.util.Optional;

import ai4j.exceptions.DonkeyException;

public interface Brain {

	 Object createInstance(Constructor<?> constructor,Object key) throws DonkeyException;

	 Object createInstance(Class<?> type,Object key) throws DonkeyException;
	 
	 Optional<Object>execute(Object key,Object input);

}
