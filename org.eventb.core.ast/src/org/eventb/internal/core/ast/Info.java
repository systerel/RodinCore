/**
 * 
 */
package org.eventb.internal.core.ast;

import org.eventb.core.ast.Expression;

public class Info {
	private final Expression expr;
	final boolean indexClosed;
	
	public Info(Expression expression) {
		expr = expression;
		indexClosed = expr.isWellFormed();
	}
	
	/**
	 * When substituting we must keep de Bruijn indices up-to-date.
	 * This is only necessary, if there are unmatched deBruijn indices
	 * in the expression.
	 * @return True, if there are no unmatched deBuijn indices in the expression
	 */
	public boolean isIndexClosed() {
		return indexClosed;
	}
	
	/**
	 * Return the expression of this substitute.
	 * @return The expression of this substitute
	 */
	public Expression getExpression() {
		return expr;
	}
	
	@Override
	public String toString() {
		return (indexClosed ? "CLOSED " : "OPEN ") + expr.toString();
	}
}