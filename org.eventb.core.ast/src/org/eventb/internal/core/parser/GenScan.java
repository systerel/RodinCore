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
package org.eventb.internal.core.parser;

import static org.eventb.core.ast.LanguageVersion.V1;

import java.util.Map.Entry;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;

/**
 * A parameterizable lexer.
 * 
 * @author Christophe Métayer
 * @since 2.0
 */
public class GenScan {

	/**
	 * The lambda character.
	 */
	private static final char LAMBDA = '\u03bb';

	/**
	 * The meta character for predicate variables.
	 */
	private static final char META = '$';

	/**
	 * Retrieves an instance of the parameterizable lexer, and initialize it
	 * with the given keywords map.
	 */
	public static GenScan getLexer(AbstractGrammar grammar) {
		GenScan genScan = new GenScan();
		for (Entry<String, Integer> e : grammar.getTokens().entrySet()) {
			genScan.addToken(e.getKey(), e.getValue());
		}
		return genScan;
	}

	/**
	 * The input string being analyzed.
	 */
	private String toParse;
	/**
	 * The current position of the cursor. (i.e. the character in the input
	 * string being analyzed).
	 */
	private int currentPos;
	/**
	 * The length of the input string <code>toParse</code>.
	 */
	private int length;

	/**
	 * Last token recognized.
	 */
	private Token token;

	/**
	 * Position after the last recognized token. This is the initial position in
	 * the string <code>toParse</code> that indicates the beginning of a lexem
	 * (i.e. after blanks have been skipped).
	 */
	private int pos;

	/**
	 * The last position where we are sure that a lexem was recognized.
	 */
	private int curPosSaved;

	public ParseResult result;

	/**
	 * The entry node used by the lexer to recognize tokens.
	 */
	final AbstractLexerNode entry = new AbstractLexerNode(this) {

		@Override
		public void progress() {
			// The cursor is placed just after the last token recognized
			setToken(null);
			setCurrentPos(getCurPosSaved());
			while (true) {
				if (getCurrentPos() >= getLength()) {
					setPos(getCurrentPos());
					symbol(BMath._EOF);
					return;
				}
				final int curPos = getCurrentPos();
				final char charAt = getStringToParse().charAt(curPos);
				// We skip all blank characters
				if (Character.isWhitespace(charAt)
						|| FormulaFactory.isEventBWhiteSpace(charAt)) {
					goForward();
				} else {
					// We skipped the blank characters, this is the beginning
					// of a lexem.
					setPos(curPos);

					// We look if the current node can be treated by one of the
					// registered nodes
					AbstractLexerNode n = null;
					if (nodes != null) {
						n = nodes.get(charAt);
					}
					if (n != null) {
						goForward();
						n.progress();
					} else {
						// We try to recognize an identifier
						if (!(searchForIdent() ||
						// We try to recognize a number
								searchForNumber() ||
						// We try to recognize a meta variable
						searchForPredVar())) {
							result.addProblem(new ASTProblem(
									new SourceLocation(curPos, curPos),
									ProblemKind.LexerError,
									ProblemSeverities.Warning, String
											.valueOf(charAt)));
							goForward();
							continue;
						}
					}
					return;
				}
			}
		}
	};

	/**
	 * Checks if the given character is valid for a first character in an
	 * identifier.
	 * 
	 * @param charAt
	 *            the character to check
	 * @return true iff a <code>charAt</code> can be the first character of an
	 *         identifier
	 */
	public final boolean firstIdent(Character charAt) {
		return Character.isJavaIdentifierStart((int) charAt)
				&& LAMBDA != charAt && META != charAt;
	}

	/**
	 * Checks if the given character is valid for a first character in a
	 * predicate variable.
	 * 
	 * @param charAt
	 *            the character to check
	 * @return true iff a <code>charAt</code> can be the first character of a
	 *         predicate variable
	 */
	public final boolean firstPredVar(Character charAt) {
		return META == charAt;
	}

	/**
	 * Method used to recognize an identifier after its first character as been
	 * recognized.
	 */
	public final void extendIdent() {
		while (currentPos < length) {
			final char charAt = toParse.charAt(currentPos);
			if (extendIdent(charAt)) {
				goForward();
			} else {
				break;
			}
		}
		if (currentPos < length && '\'' == toParse.charAt(currentPos)) {
			goForward();
		}
		symbol(BMath._IDENT);
	}

	/**
	 * Test if the given character is a valid character to be used inside an
	 * identifier
	 * 
	 * @param charAt
	 *            The character to be checked
	 * @return true if the character can be used to build an identifier
	 */
	public final boolean extendIdent(char charAt) {
		return (Character.isJavaIdentifierPart((int) charAt)
				&& LAMBDA != charAt && META != charAt);
	}

	/**
	 * Method used to recognize a predicate variable after its first character
	 * as been recognized.
	 */
	public final void extendPredVar() {
		while (currentPos < length) {
			final char charAt = toParse.charAt(currentPos);
			if (extendIdent(charAt)) {
				goForward();
			} else {
				break;
			}
		}
		symbol(BMath._PREDVAR);
	}

	/**
	 * Checks if we can go forward from the current position and recognize an
	 * identifier.
	 * 
	 * @return true iff the recognition initiated here led to an identifier
	 */
	public final boolean searchForIdent() {
		final Character charAt = new Character(getStringToParse().charAt(
				currentPos));
		if (!firstIdent(charAt)) {
			return false;
		}
		goForward();
		extendIdent();
		return true;
	}

	/**
	 * Checks if we can go forward from the current position and recognize a
	 * predicate variable.
	 * 
	 * @return true iff recognition initiated here led to predicate variable
	 */
	public final boolean searchForPredVar() {
		final Character charAt = new Character(getStringToParse().charAt(
				currentPos));
		if (!firstPredVar(charAt)) {
			return false;
		}
		goForward();
		extendPredVar();
		return true;
	}

	/**
	 * Checks if we can go forward from the current position and recognize a
	 * number.
	 * 
	 * @return true iff recognition initiated here led to a number
	 */
	public final boolean searchForNumber() {
		boolean isNumber = false;
		while (currentPos < length) {
			final char charAt = toParse.charAt(currentPos);
			if (charAt >= '0' && charAt <= '9') {
				goForward();
				isNumber = true;
			} else {
				break;
			}
		}
		if (isNumber) {
			symbol(BMath._INTLIT);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Increment the cursor pointing on the analyzed symbol. Handle the cases
	 * where the current character being analyzed is a supplementary character.
	 * 
	 * @return the new position of the cursor
	 */
	public int goForward() {
		int codePoint;
		do {
			codePoint = Character.codePointAt(toParse, currentPos);
			currentPos++;
		} while (Character.isSupplementaryCodePoint(codePoint));
		return currentPos;
	}

	public void parse(String string) {
		toParse = string;
		length = string.length();
		setPos(0);
		setCurrentPos(0);
	}

	/**
	 * Recognition of the next token.
	 * 
	 * @return the next token, <code>null</code> if the token can not be
	 *         recognized, <code>EOF</code> at the end of the stream
	 */
	public Token nextToken() {
		entry.progress();
		return token;
	}

	/**
	 * Add a token to the node map.
	 */
	public void addToken(String string, int lt) {
		entry.adopt(string, lt, true);
	}

	/**
	 * Create a symbol from the current token and its corresponding type. In
	 * case of <code>LanguageVersion.V1</code> the keyword corresponding to
	 * <code>GenParser._KPARTITION</code> is scanned as a
	 * <code>GenParser._IDENT</code>
	 * 
	 * @param kind
	 *            The type of the symbol
	 */
	public void symbol(int kind) {
		if (result != null && result.getLanguageVersion() == V1
				&& kind == BMath._KPARTITION) {
			setToken(new Token(BMath._IDENT, getStringToParse().substring(pos,
					currentPos), pos));
		} else {
			setToken(new Token(kind, getStringToParse().substring(pos,
					currentPos), pos));
		}
		this.curPosSaved = currentPos;
	}

	/*
	 * Getters and setters
	 */

	public int getCurrentPos() {
		return currentPos;
	}

	public void setCurrentPos(int pos) {
		this.currentPos = pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public int getCurPosSaved() {
		return curPosSaved;
	}

	public void setStringToParse(String toParse) {
		this.toParse = toParse;
	}

	public String getStringToParse() {
		return toParse;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}
	
	public ScanState save() {
		return new ScanState(pos, currentPos, curPosSaved);
	}
	
	public void restore(ScanState state) {
		this.pos = state.pos;
		this.currentPos = state.currentPos;
		this.curPosSaved = state.curPosSaved;
	}
	
	static class ScanState {
		final int pos;
		final int currentPos;
		final int curPosSaved;
		
		public ScanState(int pos, int currentPos, int curPosSaved) {
			this.pos = pos;
			this.currentPos = currentPos;
			this.curPosSaved = curPosSaved;
		}
	}
}
