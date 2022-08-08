package br.com.victorandrej.jia.classes.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.victorandrej.jia.classes.Lobe;
import br.com.victorandrej.jia.exceptions.DonkeyException;
import br.com.victorandrej.jia.interfaces.Ability;
import br.com.victorandrej.jia.interfaces.Brain;
import br.com.victorandrej.jia.interfaces.IOController;
import br.com.victorandrej.jia.interfaces.Memory;
import br.com.victorandrej.jia.records.SavedInstance;

public class BrainFactory {

	public static Brain build(IOController ioController) {
		return new BrainImplementation(ioController);
	}

	private static class BrainImplementation implements Brain {

		IOController ioController;

		public BrainImplementation(IOController ioController) {
			this.ioController = ioController;
		}

		@Override
		public Object createInstance(Constructor<?> constructor, Object key) throws DonkeyException {
			List<Object> parameters = new ArrayList<>();
			for (Class<?> parameter : constructor.getParameterTypes())
				parameters.add(this.createInstance(parameter, key));
			try {
				constructor.setAccessible(true);
				return constructor.newInstance(parameters.toArray());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// this will never occur
				return null;
			}
		}

		@Override
		public Object createInstance(Class<?> type, Object key) throws DonkeyException {
			Optional<SavedInstance> savedInstance = ioController.getSavedInstance(type, key);
			return savedInstance
					.orElseThrow(() -> new DonkeyException("unable to create instance of " + type.getName()))
					.instance();
		}

		@Override
		public Optional<Object> execute(Object key, Object input) {
			Optional<Ability> abilityOptional = ioController.getAbility(key);
			Object memoryResult = input;
			for (Memory memory : abilityOptional.get()) {
				Lobe lobe = new Lobe(memoryResult, memory, this);
				memoryResult = lobe.run();
			}

			return Optional.ofNullable(memoryResult);
		}

	}
}
