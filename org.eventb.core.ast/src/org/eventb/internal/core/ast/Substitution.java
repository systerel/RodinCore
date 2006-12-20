package org.eventb.internal.core.ast;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Abstract super class for all kinds of substitutions operated on formulas.
 * 
 * @author Stefan Hallerstede
 */
public abstract class Substitution implements IFormulaRewriter {

	protected final FormulaFactory ff;
	
	// Number of bound variables between current point and the root of the
	// formula to which this substitution is applied.
	protected int nbOfInternallyBound = 0;

	public Substitution(FormulaFactory ff) {
		this.ff = ff;
	}

	public final FormulaFactory getFactory() {
		return this.ff;
	}

	public final void enteringQuantifier(int nbOfBoundIdentDecls) {
		nbOfInternallyBound += nbOfBoundIdentDecls;
	}
	
	public final void leavingQuantifier(int nbOfBoundIdentDecls) {
		nbOfInternallyBound -= nbOfBoundIdentDecls;
	}

	public final Expression rewrite(AssociativeExpression expression) {
		return expression;
	}

	public final Predicate rewrite(AssociativePredicate predicate) {
		return predicate;
	}

	public final Expression rewrite(AtomicExpression expression) {
		return expression;
	}

	public final Expression rewrite(BinaryExpression expression) {
		return expression;
	}

	public final Predicate rewrite(BinaryPredicate predicate) {
		return predicate;
	}

	public final Expression rewrite(BoolExpression expression) {
		return expression;
	}

	public final BoundIdentDecl rewrite(BoundIdentDecl declaration) {
		return declaration;
	}

	public final Expression rewrite(IntegerLiteral literal) {
		return literal;
	}

	public final Predicate rewrite(LiteralPredicate predicate) {
		return predicate;
	}

	public final Expression rewrite(QuantifiedExpression expression) {
		return expression;
	}

	public final Predicate rewrite(QuantifiedPredicate predicate) {
		return predicate;
	}

	public final Predicate rewrite(RelationalPredicate predicate) {
		return predicate;
	}

	public final Expression rewrite(SetExtension expression) {
		return expression;
	}

	public final Predicate rewrite(SimplePredicate predicate) {
		return predicate;
	}

	public final Expression rewrite(UnaryExpression expression) {
		return expression;
	}

	public final Predicate rewrite(UnaryPredicate predicate) {
		return predicate;
	}
	
}
