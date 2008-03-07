package org.eventb.core.seqprover.arith;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;

/**
 * Tries to simplify all predicates in a sequent using pre-defined arithmetic
 * simplification rewritings.
 * 
 * @author htson
 * @see ArithRewrites
 */
public class ArithRewriteTac  extends AbsractLazilyConstrTactic {

	@Override
	protected ITactic getSingInstance() {
		return BasicTactics.reasonerTac(new ArithRewrites(), new EmptyInput());
	}

}
