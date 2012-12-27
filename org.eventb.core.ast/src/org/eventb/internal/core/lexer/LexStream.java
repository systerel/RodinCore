/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.lexer;

/**
 * Stream used by the lexer to read its input.
 * 
 * @author Nicolas Beauger
 * 
 */
public class LexStream {

	private final String toLex;
	private final int length;

	/**
	 * The current position of the cursor. (i.e. the character in the input
	 * string being analyzed).
	 */
	private int curPos;

	/**
	 * Position after the last recognized token. This is the initial position in
	 * the string <code>toParse</code> that indicates the beginning of a lexem
	 * (i.e. after blanks have been skipped).
	 */
	private int tokenStart;

	public LexStream(String toLex) {
		this.toLex = toLex;
		this.length = toLex.length();
		this.tokenStart = -1;
		this.curPos = 0;
	}

	public int getTokenStart() {
		return tokenStart;
	}

	public void setTokenStart(int tokenStart) {
		this.tokenStart = tokenStart;
	}

	public int getCurPos() {
		return curPos;
	}

	public void setCurPos(int curPos) {
		this.curPos = curPos;
	}

	/**
	 * Returns the code point at current position. It must be ensured that
	 * <code>!{@link #isEOF()}</code> before calling this method.
	 * 
	 * @return a code point
	 */
	public int curCodePoint() {
		return codePointAt(curPos);
	}

	/**
	 * Returns the code point at given position.
	 * 
	 * @param index
	 *            a valid position in the lexed string
	 * @return a code point
	 */
	public int codePointAt(int index) {
		return toLex.codePointAt(index);
	}

	public String getLexem() {
		return toLex.substring(tokenStart, curPos);
	}

	public void startNext() {
		tokenStart = curPos;
		goForward();
	}

	public boolean isEOF() {
		return curPos >= length;
	}

	public void goForward() {
		curPos = nextCodePoint(toLex, curPos);
	}

	public void resetCurPos() {
		curPos = tokenStart;
		goForward();
	}

	// use instead of index++
	public static int nextCodePoint(String str, int index) {
		int codePoint;
		do {
			codePoint = Character.codePointAt(str, index);
			index++;
		} while (Character.isSupplementaryCodePoint(codePoint));
		return index;
	}

}