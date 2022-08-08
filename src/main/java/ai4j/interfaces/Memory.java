package ai4j.interfaces;

import ai4j.records.Souvenir;

public interface Memory extends Iterable<Souvenir>{

	public Object key();
}
