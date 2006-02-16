package org.eventb.internal.core.pm;

import org.eventb.core.pm.IGoalChangeEvent;
import org.eventb.core.pm.IGoalDelta;

public class ProofTreeChangeEvent implements IGoalChangeEvent {

	IGoalDelta delta;
	
	public ProofTreeChangeEvent(IGoalDelta delta) {
		this.delta = delta;
	}
	public IGoalDelta getDelta() {
		return delta;
	}

}
