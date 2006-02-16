package org.eventb.internal.core.pm;

import org.eventb.core.pm.IPOChangeEvent;
import org.eventb.core.pm.IPODelta;

public class POChangeEvent implements IPOChangeEvent {

	IPODelta delta;
	
	public POChangeEvent(IPODelta delta) {
		this.delta = delta;
	}
	public IPODelta getDelta() {
		return delta;
	}

}
