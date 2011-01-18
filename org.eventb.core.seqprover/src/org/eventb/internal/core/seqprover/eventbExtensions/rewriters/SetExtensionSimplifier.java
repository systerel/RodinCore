/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.SetExtension;

/**
 * Framework for simplifying set extension expressions. Currently, it implements
 * the following:
 * <ul>
 * <li>simplification for min({a, .. , {min(b)}, .. , c})</li>
 * <li>simplification for max({a, .. , {max(b)}, .. , c})</li>
 * </ul>
 * <p>
 * This class provides one static method per type of simplification. It is
 * designed around a hierarchy of subclasses, the leaves of which implement an
 * actual type of simplification. This allows good code factoring.
 * </p>
 * 
 * @author Benoît Lucet
 */
public abstract class SetExtensionSimplifier {

	private final SetExtension original;
	private final Expression[] originalMembers;
	protected final FormulaFactory ff;

	// Value of the last extremum found in the members
	protected BigInteger extremumValue;

	public static Expression simplifyMin(SetExtension expression,
			FormulaFactory ff) {
		return new MinSimplifier(expression, ff).simplify();
	}

	public static Expression simplifyMax(SetExtension expression,
			FormulaFactory ff) {
		return new MaxSimplifier(expression, ff).simplify();
	}

	private SetExtensionSimplifier(SetExtension expression, FormulaFactory ff) {
		original = expression;
		originalMembers = original.getMembers();
		this.ff = ff;
	}

	private static class MinSimplifier extends SetExtensionSimplifier {

		MinSimplifier(SetExtension expression, FormulaFactory ff) {
			super(expression, ff);
		}

		@Override
		protected boolean isNewExtremum(BigInteger value) {
			return extremumValue.compareTo(value) > 0;
		}

	}

	private static class MaxSimplifier extends SetExtensionSimplifier {

		MaxSimplifier(SetExtension expression, FormulaFactory ff) {
			super(expression, ff);
		}

		@Override
		protected boolean isNewExtremum(BigInteger value) {
			return extremumValue.compareTo(value) < 0;
		}

	}

	protected Expression simplify() {
		final List<Expression> result = new ArrayList<Expression>();
		IntegerLiteral extremumChild = null;
		extremumValue = null;
		int extremumPosition = -1;
		int nbIntLit = 0;
		for (final Expression member : originalMembers) {
			if (member.getTag() == INTLIT) {
				++ nbIntLit;
				final IntegerLiteral intlit = (IntegerLiteral) member;
				final BigInteger value = intlit.getValue();
				if (extremumChild == null || isNewExtremum(value)) {
					extremumChild = intlit;
					extremumValue = value;
					extremumPosition = result.size();
				}
			} else {
				result.add(member);
			}
		}
		if (nbIntLit < 2) {
			// at most one literal was found in the set extension, nothing new
			return original;
		}
		result.add(extremumPosition, extremumChild);
		return ff.makeSetExtension(result, null);
	}

	protected abstract boolean isNewExtremum(BigInteger value);

}