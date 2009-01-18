/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.util.tests;

import org.rodinp.internal.core.util.Util;

import junit.framework.TestCase;

public class UtilTests extends TestCase {

	// Non-breaking Space
	private final char NBS = '\u00A0';

	// Line Separator
	private final char LS = '\u2028';

	// Paragraph Separator
	private final char PS = '\u2029';

	// MATHEMATICAL SANS-SERIF BOLD CAPITAL B
	private final int B = 0x1D5D5;

	private static void assertWhitespace(int value) {
		assertTrue(Character.isWhitespace(value)
				|| Character.isSpaceChar(value));
	}

	private static void assertTrimmed(String expected, String input) {
		assertEquals(expected, Util.trimSpaceChars(input));
	}

	private static String st(int... cps) {
		final StringBuilder b = new StringBuilder(cps.length);
		for (int i : cps) {
			b.appendCodePoint(i);
		}
		return b.toString();
	}

	/**
	 * Exercises {@link Character#isWhitespace(int)}
	 */
	public void testIsWhitespace() throws Exception {
		assertWhitespace('\t');
		assertWhitespace('\n');
		assertWhitespace('\f');
		assertWhitespace('\r');
		assertWhitespace(' ');
		assertWhitespace(NBS);
		assertWhitespace(LS);
		assertWhitespace(PS);
	}

	/**
	 * Ensures trimming works in very simple cases
	 */
	public void testTrimSimple() throws Exception {
		assertTrimmed("", "");
		assertTrimmed("a", " a");
		assertTrimmed("a", "a ");
		assertTrimmed("a", " a ");
	}

	/**
	 * Ensures trimming works with several space characters
	 */
	public void testTrimMulti() throws Exception {
		final String spaces = "\t\n\f\r " + NBS + LS + PS;
		assertTrimmed("a", spaces + "a");
		assertTrimmed("a", "a" + spaces);
		assertTrimmed("a", spaces + "a" + spaces);
	}

	/**
	 * Ensures trimming works with high surrogates (32-bits characters)
	 */
	public void testTrimHigh() throws Exception {
		final String b = st(B);
		assertTrimmed(b, st('\t', '\n', '\f', '\r', NBS, LS, PS, B));
		assertTrimmed(b, st(B, '\t', '\n', '\f', '\r', NBS, LS, PS));
		assertTrimmed(b, st('\t', '\n', '\f', '\r', NBS, LS, PS, B, '\t', '\n',
				'\f', '\r', NBS, LS, PS));
	}

}
