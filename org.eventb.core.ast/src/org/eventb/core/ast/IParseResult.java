package org.eventb.core.ast;

/**
 * Common protocol for reporting results of the AST parser.
 * <p>
 * The method described below give access to the result of the parser, i.e., the
 * tree that represents the parsed formula.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IParseResult extends IResult {

	/**
	 * Returns the parsed expression.
	 * <p>
	 * A <code>null</code> result is returned in the following cases
	 * (exhaustive list):
	 * <ul>
	 * <li>errors were encountered during parsing,</li>
	 * <li>the parser was not configured for parsing an expression.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the parsed expression or <code>null</code> in case of error
	 */
	Expression getParsedExpression();

	/**
	 * Returns the parsed predicate.
	 * <p>
	 * A <code>null</code> result is returned in the following cases
	 * (exhaustive list):
	 * <ul>
	 * <li>errors were encountered during parsing,</li>
	 * <li>the parser was not configured for parsing a predicate.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the parsed predicate or <code>null</code> in case of error
	 */
	Predicate getParsedPredicate();

	/**
	 * Returns the parsed type.
	 * <p>
	 * A <code>null</code> result is returned in the following cases
	 * (exhaustive list):
	 * <ul>
	 * <li>errors were encountered during parsing,</li>
	 * <li>the parser was not configured for parsing a type.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the parsed type or <code>null</code> in case of error
	 */
	Type getParsedType();

}