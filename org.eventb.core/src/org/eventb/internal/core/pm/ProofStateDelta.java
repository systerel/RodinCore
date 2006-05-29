package org.eventb.internal.core.pm;

import java.util.Collection;

import org.eventb.core.pm.IGoalDelta;
import org.eventb.core.pm.IHypothesisDelta;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;

public class ProofStateDelta implements IProofStateDelta {
	private IGoalDelta goalDelta;
	private Collection<IHypothesisDelta> hypothesesDelta;
	private Object information;
	private ProofState proofState;
	
	public ProofStateDelta(IGoalDelta goalDelta, Collection<IHypothesisDelta> hypothesesDelta, Object information, ProofState proofState) {
		this.goalDelta = goalDelta;
		this.hypothesesDelta = hypothesesDelta;
		this.information = information;
		this.proofState = proofState;
	}
	
	public IGoalDelta getGoalDelta() {
		return goalDelta;
	}

	public Collection<IHypothesisDelta> getHypothesesDelta() {
		return hypothesesDelta;
	}
	
	public Object getInformation() {
		return information;
	}

	public ProofState getProofState() {
		return proofState;
	}
}
