package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class DoubleImplicationRewriter extends DefaultRewriter {

	public DoubleImplicationRewriter(boolean autoFlattening,
			FormulaFactory ff) {
		super(autoFlattening, ff);
	}
	
	@ProverRule("DERIV_IMP_IMP") 
	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		if (!Lib.isImp(predicate)) {
			return predicate;
		}
		Predicate P = predicate.getLeft();
		Predicate right = predicate.getRight();
		if (!Lib.isImp(right)) {
			return predicate;
		}
		BinaryPredicate bRight = (BinaryPredicate) right;
		Predicate Q = bRight.getLeft();
		Predicate R = bRight.getRight();
		FormulaFactory ff = FormulaFactory.getDefault();
		Predicate pAndq = ff.makeAssociativePredicate(Predicate.LAND,
				new Predicate[] { P, Q }, null);
		return ff.makeBinaryPredicate(Predicate.LIMP, pAndq, R, null);
	}

}