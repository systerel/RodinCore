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
			newChildren[i] = ff.makeUnaryPredicate(Predicate.NOT, children[i],
					null);
		}
		return ff.makeAssociativePredicate(tag, newChildren, null);
	}

	public static Predicate negImp(Predicate P, Predicate Q) {
		Predicate notQ = ff.makeUnaryPredicate(Predicate.NOT, Q, null);

		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				notQ }, null);
	}

	public static Predicate negQuant(int tag, BoundIdentDecl[] idents,
			Predicate P) {
		Predicate notP = ff.makeUnaryPredicate(Predicate.NOT, P, null);
		return ff.makeQuantifiedPredicate(tag, idents, notP, null);
	}

	public static Predicate inMap(Expression E, Expression F, Expression S,
			Expression T) {
		Predicate P = ff.makeRelationalPredicate(Predicate.IN, E, S, null);
		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, F, T, null);
		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				Q }, null);
	}

}
