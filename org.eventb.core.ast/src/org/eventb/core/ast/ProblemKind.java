/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added upgrade to mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - added kinds related to parser 2.0
 *	   Systerel - added kind for name conflict between a given type and a free identifier
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Collection;
import java.util.Iterator;

/**
 * This enumeration contains all the problems that can be encountered by the
 * various checkers.
 * <p>
 * When a sub-class of class ASTProblem is instantiated, it expects as
 * parameters an unfixed number of arguments, depending on the problem you
 * instantiate. Indicated here is the arguments needed with each problem. The
 * index after the argument indicates its position in the arguments array.
 * <p>
 * 
 * @author François Terrier
 * 
 * @since 1.0
 */
public enum ProblemKind {
	// TODO externalize strings
	
	/**
	 * Argument 1 is the name of the variable.
	 */
	FreeIdentifierHasBoundOccurences ("%1$s appears bound"),
	/**
	 * Argument 1 is the name of the variable.
	 */
	BoundIdentifierHasFreeOccurences ("%1$s appears free"),
	/**
	 * Argument 1 is the name of the variable.
	 */
	BoundIdentifierIsAlreadyBound ("%1$s is bound twice"),
	
	/**
	 * Argument 1 is the contents of the token.
	 */
	LexerError ("Lexer error, character '%1$s' has been ignored"),
	
	/**
	 * Argument 1 is the name, argument 2 the alternative type.
	 * 
	 * @since 3.0
	 */
	TypeNameUsedForRegularIdentifier(
			"Name %1$s is used both as a given type and as a free identifier with type %2$s"),
	
	/**
	 * Argument 1 is the name of the first type, argument 2 is the name of the second type.
	 */
	TypesDoNotMatch ("Type: %1$s does not match type: %2$s"),
	
	/**
	 * Argument 1 is the name of the expected sub-formula kind, argument 2 is the name of the actual sub-formula kind.
	 * @since 2.0
	 */
	UnexpectedSubFormulaKind ("Unexpected sub-formula, expected: %1$s but was: %2$s"),
	
	/**
	 * Argument 1 is the image of the expected symbol, argument 2 is the name of the actual symbol.
	 * @since 2.0
	 */
	UnexpectedSymbol ("Expected: %1$s but was: %2$s"),
	
	/**
	 * Argument 1 is the image of the unknown operator.
	 * @since 2.0
	 */
	UnknownOperator ("Unknown operator: %1$s"),
	
	/**
	 * No argument.
	 * @since 2.0
	 */
	UnmatchedTokens ("Tokens have been ignored"),
	
	/**
	 * Argument 1 is the image of the expected symbol, argument 2 is the name of the actual symbol.
	 * @since 2.0
	 */
	IncompatibleOperators ("Operator: %1$s is not compatible with: %2$s, parentheses are required"),
	
	/**
	 * No argument.
	 */
	Circularity ("Types do not match"),
	
	/**
	 * Argument 1 is the image of the misplaced operator.
	 * @since 2.0
	 */
	MisplacedNudOperator ("Operator: %1$s should appear at the beginning of a sub-formula"),
	
	/**
	 * Argument 1 is the image of the misplaced operator.
	 * @since 2.0
	 */
	MisplacedLedOperator ("Operator: %1$s should appear with a sub-formula on its left"),

	/**
	 * Argument 1 is the compound message with all errors.
	 * @since 2.0
	 */
	VariousPossibleErrors ("Parse failed because either:\n%1$s"),
	
	/**
	 * No argument.
	 * @since 2.0
	 */
	InvalidAssignmentToImage ("Assignment to function image applies to exactly one function"),
	
	/**
	 * Argument 1 is the number of assigned identifiers, argument 2 is the numbers of expressions.
	 * @since 2.0
	 * 
	 */
	IncompatibleIdentExprNumbers ("Incompatible number of arguments: %1$s identifiers and %2$s expressions"),
	
	/**
	 * No argument.
	 * @since 2.0
	 */
	BECMOAppliesToOneIdent("\'Becomes Member Of\' applies to only one identifier"),
	
	/**
	 * No argument.
	 * @since 2.0
	 */
	FreeIdentifierExpected ("Expected a free identifier"),

	/**
	 * No argument.
	 * @since 2.0
	 */
	IntegerLiteralExpected ("Expected an integer literal"),

	/**
	 * Argument 1 is the expected type for the generic operator.
	 * @since 2.0
	 */
	InvalidGenericType("Invalid type for generic operator, expected %1$s"),

	/**
	 * No argument.
	 * @since 2.0
	 */
	UnexpectedOftype("Oftype is not expected here"),

	/**
	 * No argument.
	 * @since 2.0
	 */
	ExtensionPreconditionError("Preconditions for this extended operator are not fulfilled"),
	
	/**
	 * No argument.
	 * @since 2.0
	 */
	PrematureEOF("Premature End Of Formula"),
	
	/**
	 * Argument 1 is the name of the duplicate identifier.
	 * @since 2.0
	 */
	DuplicateIdentifierInPattern("Duplicate identifier in pattern: %1$s"),
	
	/**
	 * No argument.
	 */
	TypeUnknown ("Variable has an unknown type"),
	
	/**
	 * No argument.
	 */
	InvalidTypeExpression("Expression doesn't denote a type"),
	
	/**
	 * Non-located failure when type-checking.
	 */
	TypeCheckFailure("Couldn't infer some types"),
	
	/**
	 * No argument.
	 */
	MinusAppliedToSet ("Arithmetic subtraction applied to a set expression"),
	
	/**
	 * No argument.
	 */
	MulAppliedToSet ("Arithmetic multiplication applied to a set expression"),

	/**
	 * No argument.
	 */
	NotUpgradableError ("Formula cannot be upgraded"),
	
	/**
	 * Argument 1 is the illegal predicate variable.
	 * @since 1.2
	 */
	PredicateVariableNotAllowed("Predicate variable %1$s is not allowed here"), 

	/**
	 * No argument.
	 * @since 2.2
	 */
	ExpressionNotBinding("Expression not binding any variable in quantified expression"), 
	;

	/**
	 * Returns a compound message from the given list of messages. Every single
	 * message is placed on a new line, indented with 2 spaces.
	 * 
	 * @param problems
	 *            a list of messages
	 * @return a string
	 * @since 2.0
	 */
	public static String makeCompoundMessage(Collection<ASTProblem> problems) {
		if (problems.size() < 2) {
			throw new IllegalArgumentException(
					"expected at least 2 error messages, but was " + problems);
		}
		final String spaces = "  ";
		final StringBuilder compound = new StringBuilder();
		final Iterator<ASTProblem> problem = problems.iterator();
		compound.append(spaces);
		compound.append(problem.next());
		while(problem.hasNext()) {
			compound.append("\n");
			compound.append(spaces);
			compound.append(problem.next());
		}
		return compound.toString();
	}

	private String message;
	private ProblemKind(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return message;
	}
}
