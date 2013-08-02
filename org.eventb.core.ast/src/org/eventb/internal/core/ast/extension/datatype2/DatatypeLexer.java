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
package org.eventb.internal.core.ast.extension.datatype2;

import static org.eventb.core.ast.ProblemKind.DatatypeParsingError;
import static org.eventb.core.ast.ProblemSeverities.Error;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.IDENT;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.SourceLocation;
import org.eventb.internal.core.lexer.GenLexer;
import org.eventb.internal.core.lexer.GenLexer.LexState;
import org.eventb.internal.core.lexer.ILexer;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.ParseResult;

/**
 * This lexer proxy collapses any stream of tokens that represents a recursive
 * call to a datatype to a single token with the name of the datatype.
 * Otherwise, it delegates to the normal lexer.
 * <p>
 * For instance, when building a datatype <code>DT</code> with type parameters
 * <code>X</code> and <code>Y</code>, the stream of tokens
 * <code>"DT", "(", "X", ",", "Y", ")"</code> is replaced with just
 * <code>"DT"</code>.
 * </p>
 * <p>
 * This class is intended to be used only when the datatype has type parameters.
 * </p>
 * 
 * @author Vincent Monfort
 * @author Laurent Voisin
 */
public class DatatypeLexer implements ILexer {

	/*
	 * Creates a new scanner using this proxy lexer.
	 */
	public static Scanner makeDatatypeScanner(DatatypeBuilder dtBuilder,
			String str, ParseResult result, AbstractGrammar grammar) {
		final ILexer lexer = new DatatypeLexer(dtBuilder, str, result, grammar);
		return new Scanner(lexer);
	}

	// The lexer we're proxying
	private final GenLexer lexer;

	// The grammar (needed to produce tokens)
	private final AbstractGrammar grammar;

	// The token for the datatype identifier
	private final Token datatypeToken;

	// The stream of tokens to recognize
	private final List<Token> expectedTokens = new ArrayList<Token>();

	// The kind of identifiers (local cache)
	private final int identKind;

	private DatatypeLexer(DatatypeBuilder dtBuilder, String toLex,
			ParseResult result, AbstractGrammar grammar) {
		this.lexer = new GenLexer(toLex, result, grammar);
		this.grammar = grammar;
		this.identKind = grammar.getKind(IDENT);
		this.datatypeToken = mIdentToken(dtBuilder.getName());
		makeExpectedTokens(dtBuilder);
	}

	private void makeExpectedTokens(DatatypeBuilder dtBuilder) {
		final GivenType[] params = dtBuilder.getTypeParameters();
		assert params.length != 0;
		final Token comma = mToken(",");
		Token sep = mToken("(");
		for (final GivenType param : params) {
			expectedTokens.add(sep);
			sep = comma;
			expectedTokens.add(mIdentToken(param.getName()));
		}
		expectedTokens.add(mToken(")"));
	}

	private Token mToken(String image) {
		return new Token(grammar.getKind(image), image, -1);
	}

	private Token mIdentToken(String image) {
		return new Token(identKind, image, -1);
	}

	@Override
	public Token nextToken() {
		final Token tk = lexer.nextToken();
		if (tk.equals(datatypeToken)) {
			if (!skipExpectedTokens()) {
				// Give up on error
				return lexer.makeEOF();
			}
		}
		return tk;
	}

	/**
	 * Read tokens and compare them with the expected ones
	 * 
	 * @return <code>true</code> iff we got all the expected tokens
	 */
	private boolean skipExpectedTokens() {
		for (final Token expTk : this.expectedTokens) {
			final Token tk = lexer.nextToken();
			if (!tk.equals(expTk)) {
				addProblem(tk, expTk);
				return false;
			}
		}
		return true;
	}

	private void addProblem(Token tk, Token expected) {
		final int end = tk.pos + tk.val.length() - 1;
		final SourceLocation sloc = new SourceLocation(tk.pos, end);
		final ASTProblem problem = new ASTProblem(sloc, DatatypeParsingError,
				Error, expected.val);
		getResult().addProblem(problem);
	}

	@Override
	public LexState save() {
		return lexer.save();
	}

	@Override
	public void restore(LexState lexState) {
		lexer.restore(lexState);
	}

	@Override
	public int eofKind() {
		return lexer.eofKind();
	}

	@Override
	public ParseResult getResult() {
		return lexer.getResult();
	}

	@Override
	public Token makeEOF() {
		return lexer.makeEOF();
	}

}
