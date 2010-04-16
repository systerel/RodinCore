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

import static org.eventb.internal.core.parser.BMath._IDENT;

import java.util.HashMap;

/**
 * A concrete lexer node in the recognition graph used by the parameterizable
 * lexer.
 * 
 * @author Christophe Métayer
 * @since 2.0
 */
public class LexerNode extends AbstractLexerNode {

	/**
	 * List of characters recognized at this state
	 */
	final char[] chars;
	/**
	 * Length of the char array
	 */
	final int l;
	/**
	 * Flag indicating if this state is terminal and thus allows recognition of
	 * a keyword lexem
	 */
	private final boolean term;
	/**
	 * Lexem type recognized at this state
	 */
	private final int kind;

	/**
	 * Index in the char array from which it is possible to recognize an
	 * identifier. Property : indIdent : -1..l <br>
	 * <li>
	 * <ul>
	 * If indIdent =-1 then all characters recognized by previous states does
	 * not make an identifier. Hence, from this current state NO identifier will
	 * be recognized.
	 * </ul>
	 * <ul>
	 * If indIdent =0 then the recognition of an identifier can happen if the
	 * first character of the array is not recognized.
	 * </ul>
	 * <ul>
	 * If indIdent : 1..l then all characters recognized until the index
	 * indIdent make an identifier.
	 * </ul>
	 * </li>
	 */
	private final int indIdent;

	/**
	 * @param text
	 * @param kind
	 * @param fident
	 *            indicate if the characters recognized by previous states make
	 *            an identifier
	 */
	public LexerNode(GenScan owner, String text, int kind, boolean fident,
			boolean term) {
		super(owner);
		this.kind = kind;
		chars = text.toCharArray();
		if (fident) {
			int i;
			for (i = 0; i < chars.length && genScan.extendIdent(chars[i]); i++) {
				// skip
			}
			indIdent = i;
		} else {
			indIdent = -1;
		}
		l = text.length();
		this.term = term;

	}

	public LexerNode add(String substring, int kind2, boolean firstIdent) {
		int i;
		final int length2 = substring.length();
		for (i = 0; i < length2 && i < l && substring.charAt(i) == chars[i]; i++) {
			// skip
		}
		// Let i :
		// case A : i=l if all characters in the array have been recognized
		// case B : i<l in this case, characters preceding the character at
		// index i have been recognized but the character at index i is not
		// recognized.

		// i is equal to length2, in this case the token to take into
		// account is a prefix of the current token
		if (i == l) {
			assert i != length2;
			super.adopt(substring.substring(l), kind2, indIdent <= l);
			return this;
		} else if (i == length2) {
			// The new token is a prefix of tokens recognized at this state.
			// We build a new state recognizing this token
			LexerNode nI = new LexerNode(genScan, substring, kind2, firstIdent,
					true);
			// We create a new node for the end of the current node
			nI.nodes = new HashMap<Character, LexerNode>();
			LexerNode nThis = new LexerNode(genScan, new String(chars, i + 1, l
					- i - 1), kind, i < indIdent, term);
			nI.nodes.put(chars[i], nThis);
			if (nodes != null) {
				nThis.nodes = new HashMap<Character, LexerNode>();
				nThis.nodes.putAll(nodes);
			}
			return nI;
		} else {
			// We create a non terminal node
			LexerNode res = new LexerNode(genScan, substring.substring(0, i),
					-1, indIdent >= 0, false);
			res.nodes = new HashMap<Character, LexerNode>();
			// We create a new node for the end of the current lexem
			LexerNode nI = new LexerNode(genScan, new String(chars, i + 1, l
					- i - 1), kind, indIdent >= i, term);
			nI.nodes = new HashMap<Character, LexerNode>();
			if (nodes != null) {
				nI.nodes.putAll(nodes);
			}
			res.nodes.put(chars[i], nI);
			res.nodes.put(substring.charAt(i), new LexerNode(genScan, substring
					.substring(i + 1, length2), kind2, indIdent >= i, true));
			return res;
		}
	}

	/**
	 * Try to recognize the characters of the stream
	 */
	@Override
	public void progress() {
		int i;
		int curPos = genScan.getCurrentPos();
		// We try to recognize the characters that this state can recognize
		for (i = 0; i < l && curPos < genScan.getLength(); i++) {
			curPos = genScan.getCurrentPos();
			if (genScan.getStringToParse().charAt(curPos) == chars[i]) {
				genScan.goForward();
			} else {
				break;
			}
		}
		if (i <= indIdent) {
			// An identifier has been recognized
			genScan.symbol(_IDENT);
			if (i < l) {
				genScan.extendIdent();
			}
		}
		if (i == l) {
			// We recognized all characters. If it is a terminal, we
			// recognized the associated token
			if (term) {
				genScan.symbol(kind);
			}
			curPos = genScan.getCurrentPos();
			// Check if we can continue the recognition
			if (curPos < genScan.getLength()) {
				final char charAt = genScan.getStringToParse().charAt(curPos);
				LexerNode n = null;
				if (nodes != null) {
					n = nodes.get(charAt);
				}
				if (n != null) {
					genScan.goForward();
					n.progress();
				} else {
					// Check if we can recognize an identifier (i.e. if all
					// characters of the token correspond to an identifier,
					// we continue the recognition.
					if (l == indIdent && genScan.extendIdent(charAt)) { // TODO
						// check if
						// we can
						// avoid the
						// test on
						// the
						// current
						// character
						genScan.extendIdent();
					}
				}
			}
		}
	}

	@Override
	public void toString(StringBuilder b, int i) {
		for (int j = 0; j < i; j++) {
			b.append('\t');
		}
		b.append(chars);
		b.append(' ');
		b.append(term);
		b.append('\n');
		super.toString(b, i);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		toString(b, 0);
		return b.toString();
	}
}
