package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

public class FormulaUnfold {

	static FormulaFactory ff = FormulaFactory.getDefault();

	public static Predicate makeExistantial(Expression S) {
		BoundIdentDecl identDecl = ff.makeBoundIdentDecl("x0", null);
		BoundIdentifier ident = ff.makeBoundIdentifier(1, null);
		Predicate pred = ff.makeRelationalPredicate(Predicate.IN, ident, S,
				null);
		return ff.makeQuantifiedPredicate(Predicate.EXISTS,
				new BoundIdentDecl[] { identDecl }, pred, null);
	}

	public static Predicate deMorgan(int tag, Predicate[] children) {
		Predicate[] newChildren = new Predicate[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeUnaryPredicate(Predicate.NOT, children[i], null);
		}
		return ff.makeAssociativePredicate(tag, newChildren, null);
	}

}
