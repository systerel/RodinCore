package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;

// TODO : maybe remove the hyp splitting from this rewriter since this is implemented in the ConjD reasoner.
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
	protected IHypAction getHypAction(Predicate pred) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@ProverRule({"AND_L", "AND_R"})
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
