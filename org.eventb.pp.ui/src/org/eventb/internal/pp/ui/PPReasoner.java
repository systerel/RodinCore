package org.eventb.internal.pp.ui;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.core.seqprover.xprover.XProverReasoner;

public class PPReasoner extends XProverReasoner {

	private static String REASONER_ID = "org.eventb.internal.pp.ui.PPReasoner";
	
	@Override
	public XProverCall newProverCall(IReasonerInput input, Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {
		return new PPProverCall((XProverInput)input,hypotheses,goal,pm);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}



}
