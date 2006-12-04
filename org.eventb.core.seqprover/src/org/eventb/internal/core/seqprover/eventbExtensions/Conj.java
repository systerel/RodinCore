package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class Conj extends AbstractRewriter {
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".conj";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred) {
		if (pred == null) {
			return "∧ goal";
		}
		return "∧ hyp (" + pred + ")";
	}

	@Override
	protected Action getHypAction(Predicate pred) {
		if (pred == null) {
			return null;
		}
		return ProverLib.hide(new Hypothesis(pred));
	}

	@Override
	protected Predicate[] rewrite(Predicate pred) {
		// TODO optimize for duplicate sub-formulas
		if (pred.getTag() == Formula.LAND) {
			return ((AssociativePredicate) pred).getChildren();
		}
		return null;
	}

	@Override
	public boolean isApplicable(Predicate pred) {
		return Lib.isConj(pred);
	}

}
