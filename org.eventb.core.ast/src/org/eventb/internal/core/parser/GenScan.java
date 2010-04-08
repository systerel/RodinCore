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
import static org.eventb.internal.core.parser.Parser._EOF;
import static org.eventb.internal.core.parser.Parser._IDENT;
import static org.eventb.internal.core.parser.Parser._INTLIT;
import static org.eventb.internal.core.parser.Parser._KPARTITION;
import static org.eventb.internal.core.parser.Parser._PREDVAR;

import java.util.HashMap;
import java.util.Map;
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
	 * Configuration table used to parameterize the scanner, with Rodin
	 * mathematical language tokens.
	 * 
	 */
	// TODO Retrieve this map from somewhere out here.
	static Map<String, Integer> basicConf = new HashMap<String, Integer>();
	static {
		basicConf.put("(", Parser._LPAR);
		basicConf.put(")", Parser._RPAR);
		basicConf.put("[", Parser._LBRACKET);
		basicConf.put("]", Parser._RBRACKET);
		basicConf.put("{", Parser._LBRACE);
		basicConf.put("}", Parser._RBRACE);
		basicConf.put(";", Parser._FCOMP);
		basicConf.put(",", Parser._COMMA);
		basicConf.put("+", Parser._PLUS);
		basicConf.put("\u005e", Parser._EXPN);
		basicConf.put("\u00ac", Parser._NOT);
		basicConf.put("\u00d7", Parser._CPROD);
		basicConf.put("\u00f7", Parser._DIV);
		basicConf.put("\u03bb", Parser._LAMBDA);
		basicConf.put("\u2025", Parser._UPTO);
		basicConf.put("\u2115", Parser._NATURAL);
		basicConf.put("\u21151", Parser._NATURAL1);
		basicConf.put("\u2119", Parser._POW);
		basicConf.put("\u21191", Parser._POW1);
		basicConf.put("\u2124", Parser._INTEGER);
		basicConf.put("\u2192", Parser._TFUN);
		basicConf.put("\u2194", Parser._REL);
		basicConf.put("\u21a0", Parser._TSUR);
		basicConf.put("\u21a3", Parser._TINJ);
		basicConf.put("\u21a6", Parser._MAPSTO);
		basicConf.put("\u21d2", Parser._LIMP);
		basicConf.put("\u21d4", Parser._LEQV);
		basicConf.put("\u21f8", Parser._PFUN);
		basicConf.put("\u2200", Parser._FORALL);
		basicConf.put("\u2203", Parser._EXISTS);
		basicConf.put("\u2205", Parser._EMPTYSET);
		basicConf.put("\u2208", Parser._IN);
		basicConf.put("\u2209", Parser._NOTIN);
		basicConf.put("\u2212", Parser._MINUS);
		basicConf.put("\u2216", Parser._SETMINUS);
		basicConf.put("\u2217", Parser._MUL);
		basicConf.put("\u2218", Parser._BCOMP);
		basicConf.put("\u2223", Parser._MID);
		basicConf.put("\u2225", Parser._PPROD);
		basicConf.put("\u2227", Parser._LAND);
		basicConf.put("\u2228", Parser._LOR);
		basicConf.put("\u2229", Parser._BINTER);
		basicConf.put("\u222a", Parser._BUNION);
		basicConf.put("\u223c", Parser._CONVERSE);
		basicConf.put("\u2254", Parser._BECEQ);
		basicConf.put(":\u2208", Parser._BECMO);
		basicConf.put(":\u2223", Parser._BECST);
		basicConf.put("=", Parser._EQUAL);
		basicConf.put("\u2260", Parser._NOTEQUAL);
		basicConf.put("<", Parser._LT);
		basicConf.put("\u2264", Parser._LE);
		basicConf.put(">", Parser._GT);
		basicConf.put("\u2265", Parser._GE);
		basicConf.put("\u2282", Parser._SUBSET);
		basicConf.put("\u2284", Parser._NOTSUBSET);
		basicConf.put("\u2286", Parser._SUBSETEQ);
		basicConf.put("\u2288", Parser._NOTSUBSETEQ);
		basicConf.put("\u2297", Parser._DPROD);
		basicConf.put("\u22a4", Parser._BTRUE);
		basicConf.put("\u22a5", Parser._BFALSE);
		basicConf.put("\u22c2", Parser._QINTER);
		basicConf.put("\u22c3", Parser._QUNION);
		basicConf.put("\u00b7", Parser._QDOT);
		basicConf.put("\u25b7", Parser._RANRES);
		basicConf.put("\u25c1", Parser._DOMRES);
		basicConf.put("\u2900", Parser._PSUR);
		basicConf.put("\u2914", Parser._PINJ);
		basicConf.put("\u2916", Parser._TBIJ);
		basicConf.put("\u2982", Parser._TYPING);
		basicConf.put("\u2a64", Parser._DOMSUB);
		basicConf.put("\u2a65", Parser._RANSUB);
		basicConf.put("\ue100", Parser._TREL);
		basicConf.put("\ue101", Parser._SREL);
		basicConf.put("\ue102", Parser._STREL);
		basicConf.put("\ue103", Parser._OVR);
		basicConf.put("BOOL", Parser._BOOL);
		basicConf.put("FALSE", Parser._FALSE);
		basicConf.put("TRUE", Parser._TRUE);
		basicConf.put("bool", Parser._KBOOL);
		basicConf.put("card", Parser._KCARD);
		basicConf.put("dom", Parser._KDOM);
		basicConf.put("finite", Parser._KFINITE);
		basicConf.put("id", Parser._KID);
		basicConf.put("inter", Parser._KINTER);
		basicConf.put("max", Parser._KMAX);
		basicConf.put("min", Parser._KMIN);
		basicConf.put("mod", Parser._MOD);
		basicConf.put("pred", Parser._KPRED);
		basicConf.put("prj1", Parser._KPRJ1);
		basicConf.put("prj2", Parser._KPRJ2);
		basicConf.put("ran", Parser._KRAN);
		basicConf.put("succ", Parser._KSUCC);
		basicConf.put("union", Parser._KUNION);
		basicConf.put("partition", Parser._KPARTITION);
		basicConf.put("partitition", Parser._KPARTITION);
		basicConf.put(".", Parser._DOT);
		basicConf.put("\u2024", Parser._DOT);
		basicConf.put("\u2982", Parser._TYPING);
	}

	/**
	 * Retrieves an instance of the parameterizable lexer, and initialize it
	 * with the given keywords map.
	 */
	public static GenScan getBasicLexer() {
		GenScan genScan = new GenScan();
		for (Entry<String, Integer> e : basicConf.entrySet()) {
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
					symbol(_EOF);
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
		symbol(_IDENT);
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
		symbol(_PREDVAR);
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
			symbol(_INTLIT);
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
	 * <code>Parser._KPARTITION</code> is scanned as a
	 * <code>Parser._IDENT</code>
	 * 
	 * @param kind
	 *            The type of the symbol
	 */
	public void symbol(int kind) {
		if (result != null && result.getLanguageVersion() == V1
				&& kind == _KPARTITION) {
			setToken(new Token(_IDENT, getStringToParse().substring(pos,
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
}
