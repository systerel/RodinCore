/*
 * Created on 06-jul-2005
 *
 */
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
	 * Argument 0 is the name of the variable, argument 1 is the location
	 */
	FreeIdentifierHasBoundOccurences ("%1$s appears bound at %2$d"),
	/**
	 * Argument 0 is the name of the variable, argument 1 is the location
	 */
	BoundIdentifierHasFreeOccurences ("%1$s appears free at %2$d"),
	/**
	 * Argument 0 is the name of the variable, argument 1 is the location
	 */
	BoundIdentifierIsAlreadyBound ("%1$s is bound twice at %2$d"),
	/**
	 * Argument 1 is the location, argument 0 is unused
	 */
	BoundIdentifierIndexOutOfBounds ("Bound ident has no corresponding quantifier at %2d"),
	
	
	/**
	 * Argument 0 is the name of the variable, argument 1 is the location start,
	 * argument 2 is the location end.
	 */
	SyntaxError ("Syntax error at %2$d:%3$d: %1$s"),
	/**
	 * Argument 0 is the name of the variable, argument 1 is the location
	 */
	SemanticError ("Semantic error at %2$d: %1$s"),
	/**
	 * Argument 0 is the name of the variable
	 */
	ParserException ("Parser exception: %1$s"),
	
	/**
	 * Argument 0 is the name of the variable, argument 1 is the location
	 */
	LexerError ("Lexer error at %2$d, token not recognized: %1$s"),
	
	/**
	 * No arguments.
	 */
	LexerException ("Lexer exception"),
	
	
	/**
	 * Argument 0 is the name of the first type, argument 1 is the name of the second type.
	 * Argument 2 and 3 is the start and the end of the expression where the conflict appears.
	 */
	TypesDoNotMatch ("Type: %1$s does not match type: %2$s. Expression between: %3$d and %4$d"),
	
	/**
	 * Arguments 0 and 1 is the name of the location of the expression causing the conflict
	 */
	Circularity ("Types do not match. Expression between: %1$d and %2$d"),
	
	/**
	 * Argument 0 is the location (i.e. start) of the variable causing the problem
	 */
	TypeUnknown ("Variable has an unknown type at: %1$d");
	
	
	private String message;
	private ProblemKind(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return message;
	}
}
