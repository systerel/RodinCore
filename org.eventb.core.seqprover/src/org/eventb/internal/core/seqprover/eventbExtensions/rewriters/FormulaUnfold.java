package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.eventbExtensions.Lib;

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

	public static Predicate inSetExtention(Expression E, Expression[] members) {
		Predicate[] predicates = new Predicate[members.length];
		for (int i = 0; i < members.length; ++i) {
			Expression member = members[i];
			if (member.equals(E)) {
				return Lib.True;
			} else {
				predicates[i] = ff.makeRelationalPredicate(Predicate.EQUAL, E,
						member, null);
			}
		}
		if (predicates.length == 1) {
			return predicates[0];
		}
		return ff.makeAssociativePredicate(Predicate.LOR, predicates, null);
	}

	public static Predicate inGeneralised(int tag, Expression E, Expression S) {
		Type type = S.getType();
		Type baseType = type.getBaseType();
		assert (baseType != null);

		BoundIdentDecl decl = ff.makeBoundIdentDecl("s", null, baseType);

		BoundIdentifier ident = ff.makeBoundIdentifier(0, null, baseType);
		Predicate P = ff.makeRelationalPredicate(Predicate.IN, ident, S
				.shiftBoundIdentifiers(1, ff), null);
		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, E
				.shiftBoundIdentifiers(1, ff), ident, null);
		if (tag == Predicate.EXISTS)
			return ff.makeQuantifiedPredicate(tag,
					new BoundIdentDecl[] { decl }, ff.makeAssociativePredicate(
							Predicate.LAND, new Predicate[] { P, Q }, null),
					null);
		else {
			return ff.makeQuantifiedPredicate(tag,
					new BoundIdentDecl[] { decl }, ff.makeBinaryPredicate(
							Predicate.LIMP, P, Q, null), null);
		}
	}

	public static Predicate inQuantified(int tag, Expression E,
			BoundIdentDecl[] x, Predicate P, Expression T) {
		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, E
				.shiftBoundIdentifiers(1, ff), T, null);
		if (tag == Predicate.EXISTS)
			return ff.makeQuantifiedPredicate(tag, x, ff
					.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
							P, Q }, null), null);
		else {
			return ff.makeQuantifiedPredicate(tag, x, ff.makeBinaryPredicate(
					Predicate.LIMP, P, Q, null), null);
		}
	}

	public static Predicate inDom(Expression E, Expression r) {
		Type type = r.getType();

		Type baseType = type.getBaseType();
		assert baseType instanceof ProductType;
		Type rType = ((ProductType) baseType).getRight();

		BoundIdentDecl decl = ff.makeBoundIdentDecl("y", null, rType);

		BoundIdentifier ident = ff.makeBoundIdentifier(0, null, rType);

		Expression left = ff.makeBinaryExpression(Expression.MAPSTO, E
				.shiftBoundIdentifiers(1, ff), ident, null);
		Predicate pred = ff.makeRelationalPredicate(Predicate.IN, left, r
				.shiftBoundIdentifiers(1, ff), null);
		return ff.makeQuantifiedPredicate(Predicate.EXISTS,
				new BoundIdentDecl[] { decl }, pred, null);
	}

	public static Predicate inRan(Expression F, Expression r) {
		Type type = r.getType();

		Type baseType = type.getBaseType();
		assert baseType instanceof ProductType;
		Type rType = ((ProductType) baseType).getLeft();

		BoundIdentDecl decl = ff.makeBoundIdentDecl("x", null, rType);

		BoundIdentifier ident = ff.makeBoundIdentifier(0, null, rType);

		Expression left = ff.makeBinaryExpression(Expression.MAPSTO, ident, F
				.shiftBoundIdentifiers(1, ff), null);
		Predicate pred = ff.makeRelationalPredicate(Predicate.IN, left, r
				.shiftBoundIdentifiers(1, ff), null);
		return ff.makeQuantifiedPredicate(Predicate.EXISTS,
				new BoundIdentDecl[] { decl }, pred, null);
	}

	public static Predicate inConverse(Expression E, Expression F, Expression r) {
		Expression map = ff.makeBinaryExpression(Expression.MAPSTO, F, E, null);
		return ff.makeRelationalPredicate(Predicate.IN, map, r, null);
	}

	public static Predicate inDomManipulation(boolean restricted, Expression E,
			Expression F, Expression S, Expression r) {
		Predicate P;
		if (restricted) {
			P = ff.makeRelationalPredicate(Predicate.IN, E, S, null);
		} else {
			P = ff.makeRelationalPredicate(Predicate.NOTIN, E, S, null);
		}

		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, ff
				.makeBinaryExpression(Expression.MAPSTO, E, F, null), r, null);
		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				Q }, null);
	}

	public static Predicate inRanManipulation(boolean restricted, Expression E,
			Expression F, Expression r, Expression T) {

		Predicate P = ff.makeRelationalPredicate(Predicate.IN, ff
				.makeBinaryExpression(Expression.MAPSTO, E, F, null), r, null);

		Predicate Q;
		if (restricted) {
			Q = ff.makeRelationalPredicate(Predicate.IN, F, T, null);
		} else {
			Q = ff.makeRelationalPredicate(Predicate.NOTIN, F, T, null);
		}

		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				Q }, null);
	}

}
