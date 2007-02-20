package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;

public class FormulaUnfold {

	static FormulaFactory ff = FormulaFactory.getDefault();

	public static Predicate makeExistantial(Expression S) {
		Type type = S.getType();
		assert type instanceof PowerSetType;
		Type baseType = type.getBaseType();

		BoundIdentDecl[] identDecls = getBoundIdentDecls(0, baseType);
		Expression exp = getExpression(0, baseType);

		Predicate pred = ff.makeRelationalPredicate(Predicate.IN, exp, S
				.shiftBoundIdentifiers(identDecls.length, ff), null);
		QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
				Predicate.EXISTS, identDecls, pred, null);
		return qPred;
	}

	private static Expression getExpression(int i, Type type) {

		if (type instanceof ProductType) {
			Type left = ((ProductType) type).getLeft();
			Type right = ((ProductType) type).getRight();
			BoundIdentDecl[] identDeclsLeft = getBoundIdentDecls(i, left);

			Expression expLeft = getExpression(i, left);
			Expression expRight = getExpression(i + identDeclsLeft.length,
					right);
			return ff.makeBinaryExpression(Expression.MAPSTO, expLeft,
					expRight, null);
		} else {
			return ff.makeBoundIdentifier(i, null, type);
		}
	}

	private static BoundIdentDecl[] getBoundIdentDecls(int i, Type type) {
		if (type instanceof ProductType) {
			Type left = ((ProductType) type).getLeft();
			Type right = ((ProductType) type).getRight();
			BoundIdentDecl[] identDeclsLeft = getBoundIdentDecls(i, left);
			BoundIdentDecl[] identDeclsRight = getBoundIdentDecls(i
					+ identDeclsLeft.length, right);
			BoundIdentDecl[] identDecls = new BoundIdentDecl[identDeclsLeft.length
					+ identDeclsRight.length];
			System.arraycopy(identDeclsLeft, 0, identDecls, 0,
					identDeclsLeft.length);
			System.arraycopy(identDeclsRight, 0, identDecls,
					identDeclsLeft.length, identDeclsRight.length);
			return identDecls;
		} else {
			return new BoundIdentDecl[] { ff
					.makeBoundIdentDecl("x", null, type) };
		}
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

	public static Predicate inPow(Expression E, Expression S) {
		return ff.makeRelationalPredicate(Predicate.SUBSETEQ, E, S, null);
	}

	public static Predicate inAssociative(int tag, Expression E,
			Expression[] children) {
		Predicate[] preds = new Predicate[children.length];
		for (int i = 0; i < children.length; ++i) {
			preds[i] = ff.makeRelationalPredicate(Predicate.IN, E, children[i],
					null);
		}
		return ff.makeAssociativePredicate(tag, preds, null);
	}

	public static Predicate inSetMinus(Expression E, Expression S, Expression T) {
		Predicate P = ff.makeRelationalPredicate(Predicate.IN, E, S, null);
		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, E, T, null);
		Predicate notQ = ff.makeUnaryPredicate(Predicate.NOT, Q, null);
		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				notQ }, null);
	}

}
