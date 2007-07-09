package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;

public class RelOvrRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".relOvrRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred == null)
			return " rewrites in goal";
		return " rewrites in hyp (" + pred.getSubFormula(position) + ")";
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
		Formula<?> subFormula = pred.getSubFormula(position);
		if (!(subFormula instanceof Expression))
			return null;
		
		Formula<?> formula = pred.getSubFormula(position.getParent());
		
		if (formula.getTag() == Expression.OVR) {
			IFormulaRewriter rewriter = new RelOvrRewriterImpl(
					(Expression) subFormula);

			Formula<?> newSubFormula = rewriter
					.rewrite((AssociativeExpression) formula);

			if (newSubFormula == formula) // No rewrite occurs
				return null;

			return pred.rewriteSubFormula(position.getParent(), newSubFormula,
					FormulaFactory.getDefault());
		}
		return null;
	}

}
