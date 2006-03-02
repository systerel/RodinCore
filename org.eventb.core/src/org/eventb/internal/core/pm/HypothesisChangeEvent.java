package org.eventb.internal.core.pm;

import java.util.Collection;
import java.util.EventObject;

import org.eventb.core.pm.IHypothesisChangeEvent;
import org.eventb.core.pm.IHypothesisDelta;

public class HypothesisChangeEvent
	extends EventObject
	implements IHypothesisChangeEvent
{

	/**
	 * Generated serialVersionUID 
	 */
	private static final long serialVersionUID = -4704109244992760885L;

	private Collection<IHypothesisDelta> hypotheses;

	public HypothesisChangeEvent(Object source, Collection<IHypothesisDelta> hypotheses) {
		super(source);
		this.hypotheses = hypotheses;
	}

	public Collection<IHypothesisDelta> getDelta() {
		return hypotheses;
	}

}
