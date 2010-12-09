package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.MAPSTO;

import java.util.Arrays;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.QuantifiedExpression;

public class LambdaCheck {

	public static boolean lambdaCheck(QuantifiedExpression expression) {
		return new LambdaCheck(expression).verify();
	}

	private final QuantifiedExpression original;
	private final BoundIdentDecl[] quantifiedIdentifiers;

	private LambdaCheck(QuantifiedExpression expression) {
		original = expression;
		quantifiedIdentifiers = original.getBoundIdentDecls();
	}

	private boolean verify() {
		final Expression expression = original.getExpression();
		if (expression.getTag() == MAPSTO) {
			final Expression left = ((BinaryExpression) expression).getLeft();
			final Expression right = ((BinaryExpression) expression).getRight();
			if (isWellFormed(left)) {
				return checkIdents(left, right);
			}
		}
		return false;
	}

	private boolean isWellFormed(Expression pattern) {
		switch (pattern.getTag()) {
		case MAPSTO:
			final BinaryExpression maplet = (BinaryExpression) pattern;
			return isWellFormed(maplet.getLeft())
					&& isWellFormed(maplet.getRight());
		case BOUND_IDENT:
			// TODO Benoit : something else to do ?
			return true;
		default:
			return !containsQuantifiedIdent(pattern);
		}
	}

	private boolean containsQuantifiedIdent(Expression expression) {
		final BoundIdentifier[] boundIdents = expression.getBoundIdentifiers();

		for (int i = 0; i < boundIdents.length; i++) {
			if (boundIdents[i].getBoundIndex() < quantifiedIdentifiers.length) {
				return true;
			}
		}
			return false;
		}

	private boolean checkIdents(Expression left, Expression right) {
		final BoundIdentifier[] lbids = left.getBoundIdentifiers();
		final BoundIdentifier[] rbids = right.getBoundIdentifiers();
		for (int i = 0; i < rbids.length; i++) {
			if (Arrays.asList(lbids).contains(rbids[i])) {
				// ok, identifier is in both expressions
			} else {
				return false;
			}
		}
		return true;
	}
}