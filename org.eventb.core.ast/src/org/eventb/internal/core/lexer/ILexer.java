/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.lexer;

import org.eventb.internal.core.lexer.GenLexer.LexState;
import org.eventb.internal.core.parser.ParseResult;

public interface ILexer {

	/**
	 * Returns the next token from the stream.
	 * 
	 * @return a token
	 */
	Token nextToken();

	/**
	 * Save the state of this lexer.
	 * 
	 * @return the saved state of this lexer
	 */
	LexState save();

	/**
	 * Restore from a previously saved state
	 * 
	 * @param lexState
	 *            state to restore from
	 */
	void restore(LexState lexState);

	/**
	 * Returns the kind of end-of-file tokens.
	 * 
	 * @return the kind of EOF tokens
	 */
	int eofKind();

	/**
	 * Returns the parse result where this lexer has stored its error messages.
	 * 
	 * @return the parse result of this lexer
	 */
	ParseResult getResult();

	/**
	 * Returns an end-of-file token at the current position. Useful when lexing
	 * is broken and we cannot recover from previous errors.
	 * 
	 * @return an end-of-file token
	 */
	Token makeEOF();

}