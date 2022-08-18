package ai4j.classes.internals;

import java.util.List;

import ai4j.annotations.Required;
import ai4j.annotations.Triggerable;
import ai4j.classes.Instance;

public final class Trigger {
	@Required
	private Class<?> clazz;
	@Required
	private List<Instance> instances;
	
	@Required
	boolean trigger;
	
	@Triggerable()
	private void trigger() {
		if (trigger) {
			
			
			
		}
		
		
		
	}
	
}
