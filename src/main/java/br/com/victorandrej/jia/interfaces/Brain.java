package br.com.victorandrej.jia.interfaces;

import java.lang.reflect.Constructor;
import java.util.Optional;

import br.com.victorandrej.jia.exceptions.DonkeyException;

public interface Brain {

	 Object createInstance(Constructor<?> constructor,Object key) throws DonkeyException;

	 Object createInstance(Class<?> type,Object key) throws DonkeyException;
	 
	 Optional<Object>execute(Object key,Object input);

}
