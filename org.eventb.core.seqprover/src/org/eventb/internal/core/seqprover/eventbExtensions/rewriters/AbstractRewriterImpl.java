/*******************************************************************************
 * Copyright (c) 2006, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - move to tom-2.8
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Abstract implementation of a formula rewriter. The purpose of this class is
 * to provide various utility methods that help writing reasoners that perform
 * formula rewriting.
 * 
 * Laurent Voisin
 */
public abstract class AbstractRewriterImpl extends DefaultRewriter {

	// true enables trace messages
	protected final boolean debug;
	private final String rewriterName;

	public AbstractRewriterImpl(boolean autoFlattening, boolean debug, String rewriterName) {
		super(autoFlattening);
		this.debug = debug;
		this.rewriterName = rewriterName;
	}

	protected final <T extends Formula<T>> void trace(T from, T to, String rule, String... otherRules) {
		if (!debug) {
			return;
		}
		if (from == to) {
			return;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(rewriterName);
		sb.append(": ");
		sb.append(from);
		sb.append("  \u219d  ");
		sb.append(to);

		sb.append("   (");
		sb.append(rule);
		for (final String r : otherRules) {
			sb.append(" | ");
			sb.append(r);
		}
		sb.append(")");

		System.out.println(sb);
	}

	protected AssociativePredicate makeAssociativePredicate(int tag, Predicate... children) {
		final FormulaFactory ff = children[0].getFactory();
		return ff.makeAssociativePredicate(tag, children, null);
	}

	protected BinaryPredicate makeBinaryPredicate(int tag, Predicate left, Predicate right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeBinaryPredicate(tag, left, right, null);
	}

	protected QuantifiedPredicate makeQuantifiedPredicate(int tag, //
			BoundIdentDecl[] boundIdentifiers, Predicate child) {
		final FormulaFactory ff = child.getFactory();
		return ff.makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
	}

	protected UnaryPredicate makeUnaryPredicate(int tag, Predicate child) {
		final FormulaFactory ff = child.getFactory();
		return ff.makeUnaryPredicate(tag, child, null);
	}

	protected RelationalPredicate makeRelationalPredicate(int tag, Expression left, Expression right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	protected SetExtension makeSetExtension(Collection<Expression> expressions) {
		final FormulaFactory ff = expressions.iterator().next().getFactory();
		return ff.makeSetExtension(expressions, null);
	}

	protected SetExtension makeSetExtension(Expression... expressions) {
		final FormulaFactory ff = expressions[0].getFactory();
		return ff.makeSetExtension(expressions, null);
	}

	protected UnaryExpression makeUnaryExpression(int tag, Expression child) {
		final FormulaFactory ff = child.getFactory();
		return ff.makeUnaryExpression(tag, child, null);
	}

	protected BinaryExpression makeBinaryExpression(int tag, Expression left, Expression right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	protected AtomicExpression makeEmptySet(FormulaFactory ff, Type type) {
		return ff.makeEmptySet(type, null);
	}

	protected Expression makeAssociativeExpression(int tag, Expression... children) {
		if (children.length == 1) {
			return children[0];
		}
		final FormulaFactory ff = children[0].getFactory();
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected Expression makeAssociativeExpression(int tag, List<Expression> children) {
		if (children.size() == 1) {
			return children.get(0);
		}
		final FormulaFactory ff = children.get(0).getFactory();
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected SimplePredicate makeSimplePredicate(int tag, Expression expression) {
		final FormulaFactory ff = expression.getFactory();
		return ff.makeSimplePredicate(tag, expression, null);
	}

	protected QuantifiedExpression makeQuantifiedExpression(int tag, //
			BoundIdentDecl[] boundIdentifiers, Predicate pred, Expression expr, Form form) {
		final FormulaFactory ff = pred.getFactory();
		return ff.makeQuantifiedExpression(tag, boundIdentifiers, pred, expr, null, form);
	}

	protected BoundIdentifier makeBoundIdentifier(FormulaFactory ff, int index, Type type) {
		return ff.makeBoundIdentifier(index, null, type);
	}

	protected AtomicExpression makeAtomicExpression(FormulaFactory ff, int tag) {
		return ff.makeAtomicExpression(tag, null);
	}

	protected boolean notLocallyBound(Formula<?> form, int nbBound) {
		for (BoundIdentifier ident : form.getBoundIdentifiers()) {
			if (ident.getBoundIndex() < nbBound) {
				return false;
			}
		}
		return true;
	}

	// Removes exactly one occurrence of the given child from the given
	// associative expression. If not possible raises an error.
	protected Expression removeChild(AssociativeExpression parent, Expression toRemove) {
		final int tag = parent.getTag();
		final Expression[] children = parent.getChildren();
		final int length = children.length;
		if (length == 2) {
			if (toRemove.equals(children[0])) {
				return children[1];
			}
			if (toRemove.equals(children[1])) {
				return children[0];
			}
			assert false;
		}
		final Expression[] newChildren = remove(toRemove, children);
		return makeAssociativeExpression(tag, newChildren);
	}

	// Removes exactly one occurrence of the given expression from the given
	// expressions. If not possible raises an error.
	protected Expression[] remove(Expression toRemove, Expression[] exprs) {
		final int index = Arrays.asList(exprs).indexOf(toRemove);
		assert 0 <= index;
		final int length = exprs.length;
		final Expression[] newExprs = new Expression[length - 1];
		System.arraycopy(exprs, 0, newExprs, 0, index);
		System.arraycopy(exprs, index + 1, newExprs, index, length - index - 1);
		return newExprs;
	}

}
