package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;

public class RemoveNegation extends AbstractManualRewrites implements IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".rn";

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "remove ¬" + pred.getSubFormula(position);
		return "remove ¬ in goal";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	protected Predicate[] rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new RemoveNegationRewriterImpl();

		FormulaFactory ff = FormulaFactory.getDefault();
		Formula predicate = pred.getSubFormula(position);

		Formula newSubPredicate = null;
		if (predicate instanceof UnaryPredicate)
			newSubPredicate = rewriter.rewrite((UnaryPredicate) predicate);
		if (newSubPredicate == null)
			return null;
		return new Predicate[] { pred.rewriteSubFormula(position,
				newSubPredicate, ff) };
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

}
