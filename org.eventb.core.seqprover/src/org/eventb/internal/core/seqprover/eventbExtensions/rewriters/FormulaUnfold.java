/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.*;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
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

	private FormulaFactory ff;

	public FormulaUnfold(FormulaFactory ff){
		this.ff = ff;
	}

	public Predicate makeExistantial(Expression S) {
		Type type = S.getType();
		assert type instanceof PowerSetType;
		Type baseType = type.getBaseType();

		BoundIdentDecl[] identDecls = getBoundIdentDecls(baseType);
		
		Expression exp = getExpression(identDecls.length - 1, baseType);

		Predicate pred = ff.makeRelationalPredicate(Predicate.IN, exp, S
				.shiftBoundIdentifiers(identDecls.length, ff), null);
		QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
				Predicate.EXISTS, identDecls, pred, null);
		return qPred;
	}

	private  Expression getExpression(int i, Type type) {

		if (type instanceof ProductType) {
			Type left = ((ProductType) type).getLeft();
			Type right = ((ProductType) type).getRight();
			BoundIdentDecl[] identDeclsLeft = getBoundIdentDecls(left);

			Expression expLeft = getExpression(i, left);
			Expression expRight = getExpression(i - identDeclsLeft.length,
					right);
			return ff.makeBinaryExpression(Expression.MAPSTO, expLeft,
					expRight, null);
		} else {
			return ff.makeBoundIdentifier(i, null, type);
		}
	}

	private  BoundIdentDecl[] getBoundIdentDecls(Type type) {
		if (type instanceof ProductType) {
			Type left = ((ProductType) type).getLeft();
			Type right = ((ProductType) type).getRight();
			BoundIdentDecl[] identDeclsLeft = getBoundIdentDecls(left);
			BoundIdentDecl[] identDeclsRight = getBoundIdentDecls(right);
			BoundIdentDecl[] identDecls = new BoundIdentDecl[identDeclsRight.length
					+ identDeclsLeft.length];
			System.arraycopy(identDeclsLeft, 0, identDecls,
					0, identDeclsLeft.length);
			System.arraycopy(identDeclsRight, 0, identDecls, identDeclsLeft.length,
					identDeclsRight.length);
			return identDecls;
		} else {
			return new BoundIdentDecl[] { ff
					.makeBoundIdentDecl("x", null, type) };
		}
	}

	public Predicate deMorgan(int tag, Predicate[] children) {
		Predicate[] newChildren = new Predicate[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeUnaryPredicate(Predicate.NOT, children[i],
					null);
		}
		return ff.makeAssociativePredicate(tag, newChildren, null);
	}

	public Predicate negImp(Predicate P, Predicate Q) {
		Predicate notQ = ff.makeUnaryPredicate(Predicate.NOT, Q, null);

		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				notQ }, null);
	}

	public Predicate negQuant(int tag, BoundIdentDecl[] idents,
			Predicate P) {
		Predicate notP = ff.makeUnaryPredicate(Predicate.NOT, P, null);
		return ff.makeQuantifiedPredicate(tag, idents, notP, null);
	}

	public Predicate inMap(Expression E, Expression F, Expression S,
			Expression T) {
		Predicate P = ff.makeRelationalPredicate(Predicate.IN, E, S, null);
		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, F, T, null);
		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				Q }, null);
	}

	public Predicate inPow(Expression E, Expression S) {
		return ff.makeRelationalPredicate(Predicate.SUBSETEQ, E, S, null);
	}

	public Predicate inAssociative(int tag, Expression E,
			Expression[] children) {
		Predicate[] preds = new Predicate[children.length];
		for (int i = 0; i < children.length; ++i) {
			preds[i] = ff.makeRelationalPredicate(Predicate.IN, E, children[i],
					null);
		}
		return ff.makeAssociativePredicate(tag, preds, null);
	}

	public Predicate inSetMinus(Expression E, Expression S, Expression T) {
		Predicate P = ff.makeRelationalPredicate(Predicate.IN, E, S, null);
		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, E, T, null);
		Predicate notQ = ff.makeUnaryPredicate(Predicate.NOT, Q, null);
		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { P,
				notQ }, null);
	}

	public Predicate inSetExtention(Expression E, Expression[] members) {
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

	public Predicate inGeneralised(int tag, Expression E, Expression S) {
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

	public Predicate inQuantified(int tag, Expression E,
			BoundIdentDecl[] boundIdentDecls, Predicate P, Expression T) {
		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, E
				.shiftBoundIdentifiers(boundIdentDecls.length, ff), T, null);
		if (tag == Predicate.EXISTS)
			return ff.makeQuantifiedPredicate(tag, boundIdentDecls, ff
					.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
							P, Q }, null), null);
		else {
			return ff.makeQuantifiedPredicate(tag, boundIdentDecls, ff
					.makeBinaryPredicate(Predicate.LIMP, P, Q, null), null);
		}
	}

	public Predicate inDom(Expression E, Expression r) {
		Type type = r.getType();

		Type baseType = type.getBaseType();
		assert baseType instanceof ProductType;
		Type rType = ((ProductType) baseType).getRight();

		BoundIdentDecl[] boundIdentDecls = getBoundIdentDecls(rType);
		Expression expression = getExpression(boundIdentDecls.length - 1, rType);
		Expression left = ff.makeBinaryExpression(Expression.MAPSTO, E
				.shiftBoundIdentifiers(boundIdentDecls.length, ff), expression, null);
		Predicate pred = ff.makeRelationalPredicate(Predicate.IN, left, r
				.shiftBoundIdentifiers(boundIdentDecls.length, ff), null);
		return ff.makeQuantifiedPredicate(Predicate.EXISTS,
				boundIdentDecls, pred, null);
	}

	public Predicate inRan(Expression F, Expression r) {
		Type type = r.getType();

		Type baseType = type.getBaseType();
		assert baseType instanceof ProductType;
		Type rType = ((ProductType) baseType).getLeft();

		BoundIdentDecl[] boundIdentDecls = getBoundIdentDecls(rType);
		Expression expression = getExpression(boundIdentDecls.length - 1, rType);

		Expression left = ff.makeBinaryExpression(Expression.MAPSTO, expression, F
				.shiftBoundIdentifiers(boundIdentDecls.length, ff), null);
		Predicate pred = ff.makeRelationalPredicate(Predicate.IN, left, r
				.shiftBoundIdentifiers(boundIdentDecls.length, ff), null);
		return ff.makeQuantifiedPredicate(Predicate.EXISTS,
				boundIdentDecls, pred, null);
	}

	public Predicate inConverse(Expression E, Expression F, Expression r) {
		Expression map = ff.makeBinaryExpression(Expression.MAPSTO, F, E, null);
		return ff.makeRelationalPredicate(Predicate.IN, map, r, null);
	}

	public Predicate inDomManipulation(boolean restricted, Expression E,
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

	public Predicate inRanManipulation(boolean restricted, Expression E,
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

	public Predicate subsetEq(Expression S, Expression T) {
		Type type = S.getType();
		assert type instanceof PowerSetType;
		Type baseType = type.getBaseType();

		BoundIdentDecl[] identDecls = getBoundIdentDecls(baseType);
		Expression exp = getExpression(identDecls.length - 1, baseType);

		Predicate P = ff.makeRelationalPredicate(Predicate.IN, exp, S
				.shiftBoundIdentifiers(identDecls.length, ff), null);

		Predicate Q = ff.makeRelationalPredicate(Predicate.IN, exp, T
				.shiftBoundIdentifiers(identDecls.length, ff), null);
		return ff.makeQuantifiedPredicate(Predicate.FORALL, identDecls, ff
				.makeBinaryPredicate(Predicate.LIMP, P, Q, null), null);
	}

	public Predicate subset(Expression S, Expression T) {
		Predicate incl = ff.makeRelationalPredicate(SUBSETEQ, S, T, null);
		Predicate eq = ff.makeRelationalPredicate(EQUAL, S, T, null);
		Predicate neq = ff.makeUnaryPredicate(NOT, eq, null);
		return ff.makeAssociativePredicate(LAND, new Predicate[] { incl, neq },
				null);
	}

	public Predicate inRelImage(Expression F, Expression r, Expression S) {
		Type type = S.getType();
		assert type instanceof PowerSetType;
		Type baseType = type.getBaseType();

		BoundIdentDecl[] identDecls = getBoundIdentDecls(baseType);
		
		Expression exp = getExpression(identDecls.length - 1, baseType);

		// x : S
		Predicate pred1 = ff.makeRelationalPredicate(Predicate.IN, exp, S
				.shiftBoundIdentifiers(identDecls.length, ff), null);
		
		// x |-> F
		Expression map = ff.makeBinaryExpression(Predicate.MAPSTO, exp, F
				.shiftBoundIdentifiers(identDecls.length, ff), null);
		
		// x |-> F : r
		Predicate pred2 = ff.makeRelationalPredicate(Predicate.IN, map, r
				.shiftBoundIdentifiers(identDecls.length, ff), null);
		
		QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
				Predicate.EXISTS, identDecls,
				ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
						pred1, pred2 }, null), null);
		return qPred;
	}

	public Predicate inForwardComposition(Expression E, Expression F,
			Expression[] rels) {
		// Create the bound identifiers
		Collection<BoundIdentDecl> identDecls = new ArrayList<BoundIdentDecl>();
		for (int i = 0; i < rels.length - 1; i++) {
			Expression rel = rels[i];
			Type type = rel.getType();
			assert type instanceof PowerSetType;
			PowerSetType powerType = (PowerSetType) type;
			Type baseType = powerType.getBaseType();
			assert baseType instanceof ProductType;
			ProductType pType = (ProductType) baseType;
			Type right = pType.getRight();
			BoundIdentDecl[] boundIdentDecls = getBoundIdentDecls(right);
			for (BoundIdentDecl boundIdentDecl : boundIdentDecls) {
				identDecls.add(boundIdentDecl);
			}
		}

		// Create the predicates
		Collection<Predicate> newChildren = new ArrayList<Predicate>();
		int size = identDecls.size();
		final int maxSize = size;
		Expression prev = E.shiftBoundIdentifiers(maxSize, ff);
		for (int i = 0; i < rels.length - 1; i++) {
			Expression rel = rels[i];
			Type type = rel.getType();
			assert type instanceof PowerSetType;
			PowerSetType powerType = (PowerSetType) type;
			Type baseType = powerType.getBaseType();
			assert baseType instanceof ProductType;
			ProductType pType = (ProductType) baseType;
			Type right = pType.getRight();
			Expression expression = getExpression(size - 1, right);
			BoundIdentDecl[] boundIdentDecls = getBoundIdentDecls(right);
			size = size - boundIdentDecls.length;
			BinaryExpression map = ff.makeBinaryExpression(Expression.MAPSTO,
					prev, expression, null);
			newChildren.add(ff.makeRelationalPredicate(Predicate.IN, map, rel
					.shiftBoundIdentifiers(maxSize, ff), null));
			prev = expression;
		}
		BinaryExpression map = ff.makeBinaryExpression(Expression.MAPSTO, prev,
				F.shiftBoundIdentifiers(maxSize, ff), null);
		newChildren.add(ff.makeRelationalPredicate(Predicate.IN, map,
				rels[rels.length - 1].shiftBoundIdentifiers(maxSize, ff), null));

		QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
				Predicate.EXISTS, identDecls, ff.makeAssociativePredicate(
						Predicate.LAND, newChildren, null), null);
		return qPred;
	}

	public Predicate inPfun(Expression f, Expression S, Expression T) {
		Expression pfun = ff.makeBinaryExpression(Expression.REL, S, T, null);
		Predicate pred = ff
				.makeRelationalPredicate(Predicate.IN, f, pfun, null);
		
		Type sType = S.getType();
		assert sType instanceof PowerSetType;
		Type sBaseType = ((PowerSetType) sType).getBaseType();
		BoundIdentDecl[] x = getBoundIdentDecls(sBaseType);
		Type tType = T.getType();
		assert tType instanceof PowerSetType;
		Type tBaseType = ((PowerSetType) tType).getBaseType();
		BoundIdentDecl[] y = getBoundIdentDecls(tBaseType);
		BoundIdentDecl[] z = getBoundIdentDecls(tBaseType);
		
		int length = x.length + y.length + z.length;
		BoundIdentDecl[] boundIdentifiers = new BoundIdentDecl[length];
		System.arraycopy(x, 0, boundIdentifiers, 0, x.length);
		System.arraycopy(y, 0, boundIdentifiers, x.length, y.length);
		System.arraycopy(z, 0, boundIdentifiers, x.length + y.length, z.length);

		f = f.shiftBoundIdentifiers(length, ff);
		
		Expression xExpression = getExpression(length
				- 1, sBaseType);
		Expression yExpression = getExpression(y.length + z.length
				- 1, tBaseType);
		Expression zExpression = getExpression(z.length
				- 1, tBaseType);
		
		Expression map1 = ff.makeBinaryExpression(Expression.MAPSTO,
				xExpression, yExpression, null);
		Predicate pred1 = ff.makeRelationalPredicate(Predicate.IN, map1, f,
				null);
		Expression map2 = ff.makeBinaryExpression(Expression.MAPSTO,
				xExpression, zExpression, null);
		Predicate pred2 = ff.makeRelationalPredicate(Predicate.IN, map2, f,
				null);
		Predicate left = ff.makeAssociativePredicate(Predicate.LAND,
				new Predicate[] { pred1, pred2 }, null);
		Predicate right = ff.makeRelationalPredicate(Predicate.EQUAL,
				yExpression, zExpression, null);
		Predicate impPred = ff.makeBinaryPredicate(Predicate.LIMP, left, right,
				null);
		Predicate forall = ff.makeQuantifiedPredicate(Predicate.FORALL, boundIdentifiers, impPred, null);
		
		AssociativePredicate aPred = ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {pred, forall}, null);
		return aPred;
	}


	public Predicate makeExistSingletonSet(Expression S) {
		Type type = S.getType();
		assert type instanceof PowerSetType;
		Type baseType = type.getBaseType();

		BoundIdentDecl[] identDecls = getBoundIdentDecls(baseType);

		Expression exp = getExpression(identDecls.length - 1, baseType);

		Expression singleton = ff.makeSetExtension(exp, null);
		Predicate pred = ff.makeRelationalPredicate(Predicate.EQUAL, S
				.shiftBoundIdentifiers(identDecls.length, ff), singleton, null);
		QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
				Predicate.EXISTS, identDecls, pred, null);
		return qPred;
	}

}
