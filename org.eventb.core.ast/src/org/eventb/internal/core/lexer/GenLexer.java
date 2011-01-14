/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.lexer;

import static org.eventb.core.ast.LanguageVersion.V1;
import static org.eventb.internal.core.lexer.LexicalClass.IDENTIFIER;
import static org.eventb.internal.core.lexer.LexicalClass.SYMBOL;
import static org.eventb.internal.core.lexer.LexicalClass.WHITESPACE;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.IndexedSet;
import org.eventb.internal.core.parser.ParseResult;

/**
 * Generic lexer for any {@link BMath} grammar (potentially extended).
 * 
 * @author Nicolas Beauger
 * 
 */
public class GenLexer {

	/**
	 * The lambda character.
	 */
	public static final int LAMBDA = '\u03bb';

	/**
	 * The meta character for predicate variables.
	 */
	public static final int META = '\u0024';

	/**
	 * The prime character for primed identifiers.
	 */
	public static final int PRIME = '\'';

	/**
	 * A restorable snapshot of a lexer state.
	 */
	public static class LexState {
		public final int tokenStart;
		public final int curPos;

		public LexState(int tokenStart, int curPos) {
			this.curPos = curPos;
			this.tokenStart = tokenStart;
		}

	}

	/**
	 * Reads a single lexem.
	 */
	public static class LexemReader {

		private final AbstractGrammar grammar;

		public LexemReader(AbstractGrammar grammar) {
			this.grammar = grammar;
		}

		/**
		 * Reads a single lexem from the given stream.
		 * <p>
		 * The stream must be set up so that stream.getCurPos() is the index of
		 * the first code point of the lexem to read.
		 * </p>
		 * <p>
		 * The stream is modified so that when the method returns:
		 * <li><code>stream.getTokenStart()</code> is the index of the first
		 * code point of the lexem</li>
		 * <li><code>stream.getCurPos()</code> is the index immediately
		 * following the index of the last code point of the lexem</li>
		 * </p>
		 * 
		 * @param stream
		 *            a stream
		 * @return a lexical class, or <code>null</code> if recognized lexem is
		 *         erroneous
		 */
		public LexicalClass read(LexStream stream) {
			stream.startNext();
			final int codePoint = stream.codePointAt(stream.getTokenStart());
			final LexicalClass lexClass = recognize(codePoint);
			final boolean success = lexClass.read(stream, grammar);
			if (!success) {
				return null;
			}
			return lexClass;
		}

		// Recognizes the lexical class, given the first code point of a lexem.
		private static LexicalClass recognize(int codePoint) {
			// TODO optimize using smart test order (context-dependent)
			for (LexicalClass lexClass : LexicalClass.values()) {
				if (lexClass.isStart(codePoint)) {
					return lexClass;
				}
			}
			throw new IllegalStateException(
					"LEXER: code point with no lexical class: " + codePoint);
		}

	}

	/**
	 * Tells whether the given string looks like an identifier.
	 * 
	 * @param str
	 *            a string of characters
	 * @return <code>true</code> if the given string could be scanned as an
	 *         identifier
	 */
	public static boolean isIdent(String str) {
		return IDENTIFIER.contains(str);
	}

	/**
	 * Tells whether the given string is purely symbolic.
	 * 
	 * @param str
	 *            a string of characters
	 * @return <code>true</code> if the given string is purely symbolic
	 */
	public static boolean isSymbol(String str) {
		return SYMBOL.contains(str);
	}

	public final ParseResult result;

	private final LexStream stream;
	private final AbstractGrammar grammar;
	private final LexemReader reader;

	public GenLexer(String toLex, ParseResult result, AbstractGrammar grammar) {
		this.result = result;
		this.stream = new LexStream(toLex);
		this.grammar = grammar;
		this.reader = new LexemReader(grammar);
	}

	public AbstractGrammar getGrammar() {
		return grammar;
	}
	
	public LexState save() {
		return new LexState(stream.getTokenStart(), stream.getCurPos());
	}

	public void restore(LexState lexState) {
		stream.setTokenStart(lexState.tokenStart);
		stream.setCurPos(lexState.curPos);
	}

	/**
	 * Returns the next token from the stream.
	 * 
	 * @return a token
	 */
	public Token nextToken() {
		if (stream.isEOF()) {
			return makeEOF();
		}
		final LexicalClass lexClass = reader.read(stream);
		if (lexClass == null) {
			addProblem(stream.getLexem());
			return nextToken();
		}
		if (lexClass == WHITESPACE) {
			return nextToken();
		}

		String lexem = stream.getLexem();
		final int kind = lexClass.getKind(lexem, grammar);
		if (kind == IndexedSet.NOT_AN_INDEX) {
			addProblem(lexem);
			return makeEOF();
		}
		if (kind == grammar.getIDENT() && isPrime(stream)) {
			stream.goForward();
			lexem = stream.getLexem();
		}
		return makeToken(kind, lexem);
	}

	private Token makeToken(int kind, String val) {
		if (result.getLanguageVersion() == V1 && kind == grammar.getPARTITION()) {
			return new Token(grammar.getIDENT(), val, stream.getTokenStart());
		} else {
			return new Token(kind, val, stream.getTokenStart());
		}
	}

	private Token makeEOF() {
		return new Token(grammar.getEOF(), "", stream.getCurPos());
	}

	private void addProblem(String tokenImage) {
		final int pos = stream.getCurPos() - 1;
		result.addProblem(new ASTProblem(new SourceLocation(pos, pos),
				ProblemKind.LexerError, ProblemSeverities.Warning, tokenImage));
	}

	private static boolean isPrime(LexStream stream) {
		return !stream.isEOF() && stream.curCodePoint() == PRIME;
	}

}
