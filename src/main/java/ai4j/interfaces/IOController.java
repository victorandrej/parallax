package ai4j.interfaces;

import java.util.Optional;

import ai4j.records.SavedInstance;

public interface IOController {

	public Optional<SavedInstance>getSavedInstance(Class<?>type,Object key);
	public Optional<Ability> getAbilityToCreateinstanceOf(Class<?> type,Object key);
	public Optional<Ability> getAbility(Object key);
}
