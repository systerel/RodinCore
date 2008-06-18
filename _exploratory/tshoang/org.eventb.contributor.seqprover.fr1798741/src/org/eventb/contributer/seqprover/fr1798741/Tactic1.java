package org.eventb.contributer.seqprover.fr1798741;

import org.eventb.core.seqprover.ITactic;
import org.eventb.contributer.seqprover.fr1798741.AutoRewrites;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;

public class Tactic1  extends AbsractLazilyConstrTactic {

	@Override
	protected ITactic getSingInstance() {
		return BasicTactics.reasonerTac(new AutoRewrites(), new EmptyInput());
	}

}