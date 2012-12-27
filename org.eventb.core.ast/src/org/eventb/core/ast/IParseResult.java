/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *******************************************************************************/
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
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IParseResult extends IResult {

	/**
	 * Returns whether parsing was successful. However, this method will return
	 * <code>true</code> in case of characters ignored by the lexical analyzer.
	 * Its usage is thus discouraged in favor of a test for
	 * <code>!hasProblem()</code> which will return <code>false</code> in case
	 * of lexical error.
	 * 
	 * @return <code>true</code> iff parsing succeeded
	 * @see #hasProblem()
	 * @deprecated Use <code>!hasProblem()</code> instead
	 */
	@Override
	@Deprecated
	boolean isSuccess();
	
	/**
	 * Returns the parsed assignment.
	 * <p>
	 * A <code>null</code> result is returned in the following cases
	 * (exhaustive list):
	 * <ul>
	 * <li>errors were encountered during parsing,</li>
	 * <li>the parser was not configured for parsing an assignment.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the parsed assignment or <code>null</code> in case of error
	 */
	Assignment getParsedAssignment();

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

	/**
	 * Returns the language version used for parsing.
	 * 
	 * @return the language version used
	 * @since 1.0
	 */
	LanguageVersion getLanguageVersion();

}
