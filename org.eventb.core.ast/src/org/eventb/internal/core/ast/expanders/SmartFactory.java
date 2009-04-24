/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.expanders;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.SETEXT;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.expanders.ISmartFactory;

public class SmartFactory implements ISmartFactory {

	protected final FormulaFactory ff;

	public SmartFactory(FormulaFactory ff) {
		this.ff = ff;
	}

	public FormulaFactory getFormulaFactory() {
		return ff;
	}

	public Predicate disjoint(Expression left, Expression right) {
		final Expression leftMember = getSingletonMember(left);
		if (leftMember != null) {
			return not(in(leftMember, right));
		}
		final Expression rightMember = getSingletonMember(right);
		if (rightMember != null) {
			return not(in(rightMember, left));
		}
		final Type type = left.getType();
		return equals(inter(type, left, right), emptySet(type));
	}

	public Expression getSingletonMember(Expression expr) {
		if (expr.getTag() != SETEXT)
			return null;
		final Expression[] members = ((SetExtension) expr).getMembers();
		if (members.length != 1)
			return null;
		return members[0];
	}

	public Expression emptySet(Type type) {
		return ff.makeEmptySet(type, null);
	}

	public Predicate equals(Expression left, Expression right) {
		return ff.makeRelationalPredicate(EQUAL, left, right, null);
	}

	public Expression inter(Type type, Expression... exprs) {
		switch (exprs.length) {
		case 0:
			return type.getBaseType().toExpression(ff);
		case 1:
			return exprs[0];
		default:
			return ff.makeAssociativeExpression(BINTER, exprs, null);
		}
	}

	public Predicate in(Expression member, Expression set) {
		switch (set.getTag()) {
		case SETEXT:
			final Expression[] children = ((SetExtension) set).getMembers();
			switch (children.length) {
			case 0:
				return ff.makeLiteralPredicate(BFALSE, null);
			case 1:
				return equals(member, children[0]);
			}
			break;
		case EMPTYSET:
			return ff.makeLiteralPredicate(BFALSE, null);
		}
		if (set.isATypeExpression()) {
			return ff.makeLiteralPredicate(BTRUE, null);
		}
		return ff.makeRelationalPredicate(IN, member, set, null);
	}

	public Predicate land(List<Predicate> preds) {
		switch (preds.size()) {
		case 0:
			return ff.makeLiteralPredicate(BTRUE, null);
		case 1:
			return preds.get(0);
		default:
			return ff.makeAssociativePredicate(LAND, preds, null);
		}
	}

	public Predicate lor(List<Predicate> preds) {
		switch (preds.size()) {
		case 0:
			return ff.makeLiteralPredicate(BFALSE, null);
		case 1:
			return preds.get(0);
		default:
			return ff.makeAssociativePredicate(LOR, preds, null);
		}
	}

	public Predicate not(Predicate pred) {
		if (pred.getTag() == NOT) {
			return ((UnaryPredicate) pred).getChild();
		}
		return ff.makeUnaryPredicate(NOT, pred, null);
	}

	public Expression union(Type type, Expression... exprs) {
		switch (exprs.length) {
		case 0:
			return emptySet(type);
		case 1:
			return exprs[0];
		default:
			final Expression attempt = extensionSetMerge(exprs);
			if (attempt != null) {
				return attempt;
			}
			return ff.makeAssociativeExpression(BUNION, exprs, null);
		}
	}

	private Expression extensionSetMerge(Expression[] exprs) {
		final Set<Expression> members = new LinkedHashSet<Expression>(
				exprs.length * 2);
		for (Expression expr : exprs) {
			if (expr instanceof SetExtension) {
				final SetExtension setExt = (SetExtension) expr;
				final Expression[] subMembers = setExt.getMembers();
				members.addAll(Arrays.asList(subMembers));
			} else {
				return null;
			}
		}
		return ff.makeSetExtension(members, null);
	}

}