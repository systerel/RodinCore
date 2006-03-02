package org.eventb.core.pm;

import java.util.Collection;

public interface IHypothesisChangeEvent {
	public Collection<IHypothesisDelta> getDelta();
}
