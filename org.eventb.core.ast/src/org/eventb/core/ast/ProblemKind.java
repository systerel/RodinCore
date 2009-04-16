/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added upgrade to mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * This enum contains all the problems that can be encountered by the various
 * checkers.
 * <p>
 * When a sub-class of class ASTProblem is instantiated, it expects as
 * parameters an unfixed number of arguments, depending on the problem you
 * instanciate. Indicated here is the arguments needed with each problem. The
 * index after the argument indicates its position in the arguments array.
 * 
 * @author François Terrier
 * 
 */
public enum ProblemKind {
	
	/**
	 * Argument 0 is the name of the variable.
	 */
	FreeIdentifierHasBoundOccurences ("%1$s appears bound"),
	/**
	 * Argument 0 is the name of the variable.
	 */
	BoundIdentifierHasFreeOccurences ("%1$s appears free"),
	/**
	 * Argument 0 is the name of the variable.
	 */
	BoundIdentifierIsAlreadyBound ("%1$s is bound twice"),
	/**
	 * No argument.
	 */
	BoundIdentifierIndexOutOfBounds ("Bound ident has no corresponding quantifier"),
	
	
	/**
	 * Argument 0 is the name of the variable.
	 */
	SyntaxError ("Syntax error: %1$s"),
	/**
	 * No argument.
	 */
	UnexpectedLPARInDeclList("Unexpected left parenthesis "
			+ "in bound identifier declaration"),	
	/**
	 * Argument 0 is the name of the variable.
	 */
	ParserException ("Parser exception: %1$s"),
	
	/**
	 * Argument 0 is the contents of the token.
	 */
	LexerError ("Lexer error, character '%1$s' has been ignored."),
	
	/**
	 * No argument.
	 */
	LexerException ("Lexer exception"),
	
	
	/**
	 * Argument 0 is the name of the first type, argument 1 is the name of the second type.
	 */
	TypesDoNotMatch ("Type: %1$s does not match type: %2$s."),
	
	/**
	 * No argument.
	 */
	Circularity ("Types do not match."),
	
	/**
	 * No argument.
	 */
	TypeUnknown ("Variable has an unknown type"),
	
	/**
	 * No argument.
	 */
	InvalidTypeExpression("Expression doesn't denote a type"),
	
	/**
	 * Unlocated failure when type-checking.
	 */
	TypeCheckFailure("Couldn't infer some types"),
	
	/**
	 * No argument.
	 */
	MinusAppliedToSet ("Arithmetic subtraction applied to a set expression."),
	
	/**
	 * No argument.
	 */
	MulAppliedToSet ("Arithmetic multiplication applied to a set expression."),

	/**
	 * No argument.
	 */
	NotUpgradableError ("Formula cannot be upgraded."),
	;
		
	private String message;
	private ProblemKind(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return message;
	}
}
