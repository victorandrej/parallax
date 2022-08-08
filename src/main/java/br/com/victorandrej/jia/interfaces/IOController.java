package br.com.victorandrej.jia.interfaces;

import java.util.Optional;

import br.com.victorandrej.jia.records.SavedInstance;

public interface IOController {

	public Optional<SavedInstance>getSavedInstance(Class<?>type,Object key);
	public Optional<Ability> getAbility(Object key);
}
