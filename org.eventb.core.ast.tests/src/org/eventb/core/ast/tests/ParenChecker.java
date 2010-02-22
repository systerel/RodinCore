/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.regex.Pattern;

/**
 * Parentheses checker for checking the result of the various toString() methods
 * of the AST library.
 * 
 * @author Laurent Voisin
 * 
 */
public class ParenChecker {

	/**
	 * Returns whether the given string is well-formed with respect to its
	 * parentheses.
	 * <p>
	 * The checks performed are:
	 * <ol>
	 * <li>No external matching parentheses.</li>
	 * <li>No duplicate matching parentheses.</li>
	 * </ol>
	 * </p>
	 * <p>
	 * For instance, the following strings are invalid:
	 * <ul>
	 * <li><code>(a)</code> (rule 1)</li>
	 * <li><code>((x)) + y</code> (rule 2)</li>
	 * </ul>
	 * while these examples are valid:
	 * <ul>
	 * <li><code>(a) + (b)</code></li>
	 * <li><code>((x) + (y)) + z</code></li>
	 * <li><code>x + (- (-y))</code></li>
	 * </ul>
	 * </p>
	 * 
	 * @param s
	 *            the string to verify
	 * @return <code>true</code> iff the given sting passes the check
	 */
	public static boolean check(String s) {

		s = s.trim();
		return checkUnneededParentheses(s) && checkDuplicateParen(s);
	}

	private static boolean checkUnneededParentheses(String input) {

		// This check ensures that there is no unnecessary external parenthesis
		// in
		// the given string
		if (input.charAt(0) != '(') {
			return true;
		}
		final int length = input.length();
		int count = 1;
		for (int i = 1; i < length; i++) {
			switch (input.charAt(i)) {
			case '(':
				++count;
				break;
			case ')':
				--count;
				if (count == 0) {
					if (i == length - 1) {
						System.err.println("'" + input + "'"
								+ " contains unnecessary"
								+ " external parentheses");
						return false;
					} else {
						return true;
					}
				}
				break;
			}
		}
		System.err.println("'" + input + "' contains unbalanced parentheses");
		return false;
	}

	private static Pattern anyParen = Pattern.compile("[()]");
	private static Pattern empty = Pattern.compile("\\(\\s*\\)");
	private static Pattern notEmpty = Pattern.compile("\\(" + "[^()]*"
			+ "[\\S&&[^()]]" + "[^()]*" + "\\)");
	private static Pattern fixFunctionCall = Pattern.compile("\\)\\(");

	private static boolean checkDuplicateParen(String input) {
		// On réduit systématiquement les parenthèses internes à rien
		// et on regarde s'il reste un pattern de la forme "()".
		String s = fixFunctionCall.matcher(input).replaceAll(").(");
		while (anyParen.matcher(s).find()) {
			if (empty.matcher(s).find()) {
				System.err.println("'" + input + "'"
						+ " contains duplicate parentheses");
				return false;
			}
			s = notEmpty.matcher(s).replaceAll("");
		}
		return true;
	}

	// For testing purposes
	public static void main(String[] args) {
		assertFalse(check("(a)"));
		assertFalse(check("((x)) + y"));

		assertTrue(check("(a) + (b)"));
		assertTrue(check("((x) + (y)) + z"));
		assertTrue(check("x + (- (-y))"));
		assertTrue(check("((a)(b)) + c"));
	}
}
