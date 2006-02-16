package org.eventb.internal.core.pm;

import java.util.EventObject;

import org.eventb.core.pm.IHypothesisChangeEvent;
import org.eventb.core.pm.IHypothesisDelta;

public class HypothesisChangeEvent
	extends EventObject
	implements IHypothesisChangeEvent
{
	private static final long serialVersionUID = 494648861081632955L;

	public HypothesisChangeEvent(IHypothesisDelta delta) {
		super(delta);
	}

	public IHypothesisDelta getDelta() {
		return (IHypothesisDelta) this.source;
	}

}
