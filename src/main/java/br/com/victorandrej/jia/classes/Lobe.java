package br.com.victorandrej.jia.classes;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import br.com.victorandrej.jia.interfaces.Brain;
import br.com.victorandrej.jia.interfaces.Memory;
import br.com.victorandrej.jia.records.Souvenir;

/**
 * 
 * @author victor
 *
 */
public class Lobe {
	private Object input;
	private Memory memory;
	private Consumer<Object> eventBeforeExecute;
	private Consumer<Object> eventAfterExecute;
	private Brain brain;

	private Lobe() {
		eventBeforeExecute = o -> {
		};
		eventAfterExecute = o -> {
		};
	}

	public Lobe(Object input, Memory memory, Brain brain) {
		this();
		this.memory = memory;
		this.brain = brain;
		this.input = input;
	}

	protected void beforeExecute(Consumer<Object> before) {
		this.eventBeforeExecute = before;
	}

	protected void afterExecute(Consumer<Object> after) {
		this.eventAfterExecute = after;
	}

	public Object run() {

		Object methodReturn = this.input;
		for (Souvenir souvenir : memory) {
			try {

				Object instance = brain.createInstance(souvenir.constructor(), souvenir.key());

				eventBeforeExecute.accept(methodReturn);
				methodReturn = souvenir.method().invoke(instance, methodReturn);
				eventAfterExecute.accept(methodReturn);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		return methodReturn;
	}

}
