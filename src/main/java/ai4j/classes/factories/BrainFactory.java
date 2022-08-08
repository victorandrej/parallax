package ai4j.classes.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ai4j.classes.Lobe;
import ai4j.exceptions.DonkeyException;
import ai4j.interfaces.Ability;
import ai4j.interfaces.Brain;
import ai4j.interfaces.IOController;
import ai4j.interfaces.Memory;
import ai4j.records.SavedInstance;

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
