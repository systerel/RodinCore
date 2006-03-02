package org.eventb.internal.core.parser;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.AbstractResult;

/**
 * Result of a Parsed formula.
 * 
 * @author François Terrier
 * @author Laurent Voisin 
 */
public class ParseResult extends AbstractResult implements IParseResult {

	// Formula factory to use for building formulas
	protected final FormulaFactory factory;
	
	// Parsed assignment
	private Assignment assignment = null;

	// Parsed expression
	private Expression expression = null;

	// Parsed predicate
	private Predicate predicate = null;

	// Parsed type
	private Type type = null;

	public ParseResult(FormulaFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Converts the parsed expression to a type.
	 */
	public void convertToType() {
		assert this.isSuccess();
		try {
			type = expression.toType(factory);
		} catch (InvalidExpressionException e) {
			addProblem(new ASTProblem(
					expression.getSourceLocation(), 
					ProblemKind.InvalidTypeExpression, 
					ProblemSeverities.Error));
		}
		expression = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedAssignment()
	 */
	public Assignment getParsedAssignment() {
		if (!isSuccess())
			return null;
		return assignment;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedExpression()
	 */
	public Expression getParsedExpression() {
		if (!isSuccess())
			return null;
		return expression;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedPredicate()
	 */
	public Predicate getParsedPredicate() {
		if (!isSuccess())
			return null;
		return predicate;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedType()
	 */
	public Type getParsedType() {
		if (!isSuccess())
			return null;
		return type;
	}

	/**
	 * Sets the parsed formula to null.
	 */
	public void resetParsedFormula() {
		this.predicate = null;
		this.expression = null;
		this.assignment = null;
	}

	/**
	 * Sets the parsed assignment.
	 * 
	 * @param formula
	 *            the assignment being parsed
	 */
	public void setParsedAssignment(Assignment formula) {
		this.assignment = formula;
	}

	/**
	 * Sets the parsed expression.
	 * 
	 * @param formula
	 *            the expression being parsed
	 */
	public void setParsedExpression(Expression formula) {
		this.expression = formula;
	}

	/**
	 * Sets the parsed predicate.
	 * 
	 * @param formula
	 *            the formula being parsed
	 */
	public void setParsedPredicate(Predicate formula) {
		this.predicate = formula;
	}

}
