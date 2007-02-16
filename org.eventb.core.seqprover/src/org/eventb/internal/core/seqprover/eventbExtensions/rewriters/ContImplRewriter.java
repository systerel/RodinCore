package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

public class ContImplRewriter extends DefaultRewriter {

	public ContImplRewriter(boolean autoFlattening,
			FormulaFactory ff) {
		super(autoFlattening, ff);
	}

	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		Predicate P = predicate.getLeft();
		Predicate Q = predicate.getRight();
		FormulaFactory ff = FormulaFactory.getDefault();
		Predicate notP = ff.makeUnaryPredicate(Predicate.NOT, P, null);
		Predicate notQ = ff.makeUnaryPredicate(Predicate.NOT, Q, null);
		return ff.makeBinaryPredicate(Predicate.LIMP, notQ, notP, null);
	}

}