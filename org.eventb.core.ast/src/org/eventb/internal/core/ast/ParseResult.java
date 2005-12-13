/*
 * Created on 06-jul-2005
 *
 */
package org.eventb.internal.core.ast;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;

/**
 * Result of a Parsed formula.
 * 
 * @author François Terrier
 *
 */
public class ParseResult extends AbstractResult implements IParseResult {
	
	// Parsed predicate
	private Predicate predicate = null;
	// Parsed expression
	private Expression expression = null;
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedPredicate()
	 */
	public Predicate getParsedPredicate () {
		if (! isSuccess()) return null;
		return predicate;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedExpression()
	 */
	public Expression getParsedExpression () {
		if (! isSuccess()) return null;
		return expression;
	}
	
	/**
	 * Sets the parsed formula to null.
	 */
	public void resetParsedFormula () {
		this.predicate = null;
		this.expression = null;
	}
	
	/**
	 * Sets the parsed predicate.
	 * 
	 * @param formula the formula being parsed
	 */
	public void setParsedPredicate (Predicate formula) {
		this.predicate = formula;
	}
	
	/**
	 * Sets the parsed expression.
	 * 
	 * @param formula the expression being parsed
	 */
	public void setParsedExpression (Expression formula) {
		this.expression = formula;
	}
	
}
