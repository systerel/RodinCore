package org.eventb.internal.core.pm;

import org.eventb.core.pm.IGoalChangeEvent;
import org.eventb.core.pm.IGoalDelta;

public class GoalChangeEvent implements IGoalChangeEvent {

	IGoalDelta delta;
	
	public GoalChangeEvent(IGoalDelta delta) {
		this.delta = delta;
	}
	public IGoalDelta getDelta() {
		return delta;
	}

}
