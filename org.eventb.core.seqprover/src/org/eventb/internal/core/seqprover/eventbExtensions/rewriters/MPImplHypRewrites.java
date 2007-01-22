package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class MPImplHypRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".doubleImplGoalRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		assert pred != null;
		return "mp impl (" + pred.getSubFormula(position) + ")";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	public boolean isApplicable(Predicate pred, IPosition position) {
		Formula formula = pred.getSubFormula(position);
		if (formula instanceof Predicate) {
			return Lib.isImp((Predicate) formula);
		}
		return false;
	}

	@Override
	protected Predicate[] rewrite(Predicate pred, IPosition position) {
		FormulaFactory ff = FormulaFactory.getDefault();
		BinaryPredicate predicate = (BinaryPredicate) pred
				.getSubFormula(position);
		IFormulaRewriter rewriter = new MPImplRewriter(true, ff);
		Predicate newSubPredicate = rewriter.rewrite(predicate);
		return new Predicate[] { pred.rewriteSubFormula(position,
				newSubPredicate, ff) };
	}

}
