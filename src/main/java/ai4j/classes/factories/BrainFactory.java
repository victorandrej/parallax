package ai4j.classes.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ai4j.annotations.Recreate;
import ai4j.classes.Lobe;
import ai4j.exceptions.DonkeyException;
import ai4j.interfaces.Ability;
import ai4j.interfaces.Brain;
import ai4j.interfaces.IOController;
import ai4j.interfaces.Memory;
import ai4j.records.SavedInstance;
import ai4j.records.Souvenir;

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

			try {
				constructor.setAccessible(true);
				return constructor.newInstance(this.createParameters(constructor.getParameters(),key));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// this will never occur
				return null;
			}
		}

		private Object[] createParameters(Parameter[] parametersObjects,Object key) {
			List<Object> parameters = new ArrayList<>();
			for (Parameter parameter : parametersObjects) {
				if (parameter.isAnnotationPresent(Recreate.class)) {
					parameters.add(this.createInstance(parameter.getType(), key));
				} else {
					parameters.add(ioController.getSavedInstance(parameter.getType(), key).orElseThrow(
							() -> new DonkeyException("unable to create instance of " + parameter.getType().getName()))
							.instance());
				}
			}
			return parameters.toArray();
		}

		@Override
		public Object createInstance(Class<?> type, Object key) throws DonkeyException {

			Optional<Ability> makeInstanceOf = ioController.getAbilityToCreateinstanceOf(type, key);
			Ability ability = makeInstanceOf
					.orElseThrow(() -> new DonkeyException("unable to create instance of " + type.getName()));
			
			boolean isFirstSouvenir = true;
			Object value = null;
			
			for (Memory memory : ability) {
				if (isFirstSouvenir) {
					Souvenir souvenir = memory.iterator().next();
					value = this.createParameters(souvenir.method().getParameters(),key);
					isFirstSouvenir = false;
				}
				
				value = new Lobe(value, memory, this).run();
			}
			return value;
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
