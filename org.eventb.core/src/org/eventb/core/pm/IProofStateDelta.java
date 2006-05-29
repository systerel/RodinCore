package org.eventb.core.pm;

import java.util.Collection;


public interface IProofStateDelta {
	public IGoalDelta getGoalDelta();
	public Collection<IHypothesisDelta> getHypothesesDelta();
	public Object getInformation();
	public ProofState getProofState();
}
