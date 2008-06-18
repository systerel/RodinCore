package org.eventb.contributer.seqprover.fr1834715.manual;

import java.util.Arrays;

import org.eventb.contributer.seqprover.fr1834715.AutoRewriterImpl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;

public class ManualRewrites extends AbstractManualRewrites implements
		IReasoner {

	public static final String REASONER_ID = "org.eventb.contributer.seqprover.fr1834715.manualRewrites";

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "remove ⊂ in " + pred.getSubFormula(position);
		return "remove ⊂ in goal";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	protected Predicate rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new AutoRewriterImpl();

		FormulaFactory ff = FormulaFactory.getDefault();
		Formula<?> predicate = pred.getSubFormula(position);

		Formula<?> newSubPredicate = null;
		if (predicate instanceof Predicate
				&& predicate.getTag() == Predicate.SUBSET)
			newSubPredicate = rewriter.rewrite((RelationalPredicate) predicate);
		if (newSubPredicate == null)
			return null;
		return pred.rewriteSubFormula(position, newSubPredicate, ff);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

}
