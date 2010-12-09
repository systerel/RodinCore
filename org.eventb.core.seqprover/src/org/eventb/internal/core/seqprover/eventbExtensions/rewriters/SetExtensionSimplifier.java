/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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

// TODO Benoît present the algorithm here
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
		for (final Expression member : originalMembers) {
			if (member.getTag() == INTLIT) {
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
		if (extremumChild == null) {
			// no literal was found in the set extension, nothing to do
			return original;
		}
		result.add(extremumPosition, extremumChild);
		return ff.makeSetExtension(result, null);
	}

	protected abstract boolean isNewExtremum(BigInteger value);

}