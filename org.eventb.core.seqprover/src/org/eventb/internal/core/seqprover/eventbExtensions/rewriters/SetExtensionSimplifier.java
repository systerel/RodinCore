package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.INTLIT;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.SetExtension;

public abstract class SetExtensionSimplifier {

	private final SetExtension original;
	private final Expression[] originalMembers;

	public static List<Expression> simplifyMin(SetExtension expression) {
		return new MinSimplifier(expression).simplify();
	}

	public static List<Expression> simplifyMax(SetExtension expression) {
		return new MaxSimplifier(expression).simplify();
	}

	private SetExtensionSimplifier(SetExtension expression) {
		original = expression;
		originalMembers = original.getMembers();
	}

	private static class MinSimplifier extends SetExtensionSimplifier {

		MinSimplifier(SetExtension expression) {
			super(expression);
		}

		@Override
		protected boolean isNewExtremum(IntegerLiteral currentExtremum,
				IntegerLiteral intChild) {
			return currentExtremum.getValue().compareTo(intChild.getValue()) > 0;
		}

	}

	private static class MaxSimplifier extends SetExtensionSimplifier {

		MaxSimplifier(SetExtension expression) {
			super(expression);
		}

		@Override
		protected boolean isNewExtremum(IntegerLiteral currentExtremum,
				IntegerLiteral intChild) {
			return currentExtremum.getValue().compareTo(intChild.getValue()) < 0;
		}

	}

	protected List<Expression> simplify() {

		List<Expression> result = new ArrayList<Expression>();

		IntegerLiteral extremum = null;
		int extremumPosition = -1;
		int lastNonLiteral = -1;
		int potentialLastNonLiteral = -1;

		for (int i = 0; i < originalMembers.length; i++) {
			final Expression child = originalMembers[i];
			if (child.getTag() == INTLIT) {
				final IntegerLiteral intChild = (IntegerLiteral) child;
				if (extremum == null) {
					extremum = intChild;
					extremumPosition = i;
					lastNonLiteral = potentialLastNonLiteral;
				} else {
					if (isNewExtremum(extremum, intChild)) {
						extremum = intChild;
						extremumPosition = i;
						lastNonLiteral = potentialLastNonLiteral;
					}
				}
			} else {
				result.add(child);
				potentialLastNonLiteral++;
			}
		}

		if (extremumPosition == -1) {
			// no literal was found in the set extension, nothing to do
		} else if (lastNonLiteral == -1) {
			// only literals were found in the set extension, adding extremum
			result.add(extremum);
		} else {
			result.add(lastNonLiteral + 1, extremum);
		}

		return result;
	}

	// default behaviour that is to be overriden
	protected boolean isNewExtremum(IntegerLiteral currentExtremum,
			IntegerLiteral intChild) {
		return false;
	}

}