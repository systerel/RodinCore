/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.UNMINUS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.UnaryExpression;

/**
 * Simplifies arithmetic multiplication expressions.  Implements rules:
 * <pre>
 *    [|SIMP_SPECIAL_PROD_0|]
 *    [|SIMP_SPECIAL_PROD_1|]
 *    [|SIMP_SPECIAL_PROD_MINUS_EVEN|]
 *    [|SIMP_SPECIAL_PROD_MINUS_ODD|]
 * </pre>
 * 
 * @author Laurent Voisin
 */
public class MultiplicationSimplifier {

	public static Expression simplify(AssociativeExpression original,
			FormulaFactory factory) {
		assert original.getTag() == MUL;
		return new MultiplicationSimplifier(original, factory).simplify();
	}

	private final AssociativeExpression original;
	private final FormulaFactory ff;

	private boolean positive = true;
	private boolean changed = false;
	private List<Expression> newChildren;
	private Expression knownResult;

	private MultiplicationSimplifier(AssociativeExpression original,
			FormulaFactory ff) {
		this.original = original;
		this.ff = ff;
	}

	private Expression simplify() {
		processChildren();
		return result();
	}

	private void processChildren() {
		for (Expression child : original.getChildren()) {
			if (processChild(child)) {
				break;
			}
		}
	}

	private boolean processChild(Expression child) {
		switch (child.getTag()) {
		case Expression.INTLIT:
			return processIntegerLiteral((IntegerLiteral) child);
		case Expression.UNMINUS:
			return processUnaryMinus((UnaryExpression) child);
		default:
			addNewChild(child);
			return false;
		}
	}

	private boolean processIntegerLiteral(IntegerLiteral child) {
		BigInteger val = child.getValue();
		if (val.signum() == 0) {
			knownResult = child;
			return true;
		}
		if (val.signum() < 0) {
			val = val.abs();
			negateResult();
		}
		if (val.equals(BigInteger.ONE)) {
			changed = true;
		} else {
			addNewChild(ff.makeIntegerLiteral(val, null));
		}
		return false;
	}

	private boolean processUnaryMinus(UnaryExpression child) {
		negateResult();
		return processChild(child.getChild());
	}

	private void addNewChild(Expression newChild) {
		if (newChildren == null) {
			newChildren = new ArrayList<Expression>();
		}
		newChildren.add(newChild);
	}

	private void negateResult() {
		positive = !positive;
		changed = true;
	}

	private Expression result() {
		if (knownResult != null) {
			return knownResult;
		}
		if (!changed) {
			return original;
		}
		if (positive) {
			return unsignedResult();
		} else {
			return opposite(unsignedResult());
		}
	}

	private Expression unsignedResult() {
		if (newChildren == null) {
			return one();
		}
		if (newChildren.size() == 1) {
			return newChildren.get(0);
		}
		return ff.makeAssociativeExpression(MUL, newChildren, null);
	}

	private Expression one() {
		return ff.makeIntegerLiteral(BigInteger.ONE, null);
	}

	private Expression opposite(Expression unsigned) {
		switch (unsigned.getTag()) {
		case INTLIT:
			final BigInteger value = ((IntegerLiteral) unsigned).getValue();
			return ff.makeIntegerLiteral(value.negate(), null);
		default:
			return ff.makeUnaryExpression(UNMINUS, unsigned, null);
		}
	}

}
