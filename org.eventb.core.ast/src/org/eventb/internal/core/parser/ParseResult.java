package org.eventb.internal.core.parser;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
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
		this.type = convertToType(this.expression);
	}

	// TODO move to Expression when it's hidden.
	// Ugly glitch, but can not use dynamic polymorphism, because
	// this class doesn't belong to the API package.
	private Type convertToType(Expression expr) {
		switch (expr.getTag()) {
		case Formula.FREE_IDENT:
			return factory.makeGivenType(((FreeIdentifier) expr).getName());
		case Formula.INTEGER:
			return factory.makeIntegerType();
		case Formula.BOOL:
			return factory.makeBooleanType();
		case Formula.POW:
			Type childType = convertToType(((UnaryExpression) expr).getChild());
			if (childType == null) return null; 
			return factory.makePowerSetType(childType);
		case Formula.CPROD:
			Type leftType = convertToType(((BinaryExpression) expr).getLeft());
			if (leftType == null) return null; 
			Type rightType = convertToType(((BinaryExpression) expr).getRight());
			if (rightType == null) return null; 
			return factory.makeProductType(leftType, rightType);
		case Formula.REL:
			leftType = convertToType(((BinaryExpression) expr).getLeft());
			if (leftType == null) return null; 
			rightType = convertToType(((BinaryExpression) expr).getRight());
			if (rightType == null) return null; 
			return factory.makeRelationalType(leftType, rightType);
		default:
			addProblem(new ASTProblem(
					expr.getSourceLocation(), 
					ProblemKind.InvalidTypeExpression, 
					ProblemSeverities.Error));
			return null;
		}
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
