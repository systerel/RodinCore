/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.keyboard.tests;

import org.rodinp.keyboard.tests.AbstractText2MathTestCase;

/**
 * @author htson
 *         <p>
 *         This class contains some expression test cases for Text2EventBMath
 *         translator. This tests the translation on some large expressions
 *         taken from Prof. Jean-Raymond Abrial's Marriage and SHWT
 *         developments.
 */
public class Text2EventBMathExpressionTestCase extends AbstractText2MathTestCase {

	public void testMarriageInvariants() {
		String input = "p <: P &\ns : P >+> P &\ns = s~ &\ns /\\ id(P) = {}";
		String expect = "p \u2286 P \u2227\ns \u2208 P \u2914 P \u2227\ns = s\u223c \u2227\ns \u2229 id(P) = \u2205";
		testTranslator("MarriageInvariant", input, expect);
	}

	public void testBirthGuards() {
		String input = "x : P - p";
		String expect = "x \u2208 P \u2212 p";
		testTranslator("BirthGuards", input, expect);
	}

	public void testBirthActions() {
		String input = "p := p \\/ {x}";
		String expect = "p \u2254 p \u222a {x}";
		testTranslator("BirthActions", input, expect);
	}

	public void testDeathGuards() {
		String input = "x : p";
		String expect = "x \u2208 p";
		testTranslator("DeathActions", input, expect);
	}

	public void testDeathActions() {
		String input = "p := p - {x} ||\ns := {x} <<| s |>> {x}";
		String expect = "p \u2254 p \u2212 {x} \u2225\ns \u2254 {x} \u2a64 s \u2a65 {x}";
		testTranslator("MarriageActions", input, expect);
	}

	public void testMarriageGuards() {
		String input = "x : P - dom(s) &\ny : P - dom(s) &\nx /= y";
		String expect = "x \u2208 P \u2212 dom(s) \u2227\ny \u2208 P \u2212 dom(s) \u2227\nx \u2260 y";
		testTranslator("MarriageActions", input, expect);
	}

	public void testMarriageActions() {
		String input = "s := s <+ {x |-> y} <+ {y |-> x}";
		String expect = "s \u2254 s \ue103 {x \u21a6 y} \ue103 {y \u21a6 x}";
		testTranslator("MarriageActions", input, expect);
	}

	public void testDivorceGuards() {
		String input = "x : dom(s)";
		String expect = "x \u2208 dom(s)";
		testTranslator("DivorceGuards", input, expect);
	}

	public void testDivorceActions() {
		String input = "s := {x} <<| s |>> {x}";
		String expect = "s \u2254 {x} \u2a64 s \u2a65 {x}";
		testTranslator("DivorceActions", input, expect);
	}

	public void testSHWTProperties() {
		String input = "rr : NODE <-> NODE &\n" + "cl : NODE <-> NODE &\n"
				+ "tp : NODE &\n" + "\n"
				+ "!ss.(ss <: NODE => ss <: cl[ss]) &\n" + "\n"
				+ "!(xx, yy, aa).(xx : NODE &\n"
				+ "               yy : NODE &\n"
				+ "               aa <: NODE &\n"
				+ "               xx |-> yy : rr &\n"
				+ "               xx : cl[aa]\n" + "            =>\n"
				+ "               yy : cl[aa]\n" + "               ) &\n"
				+ "\n" + "!ss.(ss <: NODE & rr[ss] <: ss => cl[ss] <: ss)";
		String expect = "rr \u2208 NODE \u2194 NODE \u2227\n"
				+ "cl \u2208 NODE \u2194 NODE \u2227\n"
				+ "tp \u2208 NODE \u2227\n"
				+ "\n"
				+ "\u2200ss\u00b7(ss \u2286 NODE \u21d2 ss \u2286 cl[ss]) \u2227\n"
				+ "\n"
				+ "\u2200(xx, yy, aa)\u00b7(xx \u2208 NODE \u2227\n"
				+ "               yy \u2208 NODE \u2227\n"
				+ "               aa \u2286 NODE \u2227\n"
				+ "               xx \u21a6 yy \u2208 rr \u2227\n"
				+ "               xx \u2208 cl[aa]\n"
				+ "            \u21d2\n"
				+ "               yy \u2208 cl[aa]\n"
				+ "               ) \u2227\n"
				+ "\n"
				+ "\u2200ss\u00b7(ss \u2286 NODE \u2227 rr[ss] \u2286 ss \u21d2 cl[ss] \u2286 ss)";
		testTranslator("SHWTProperties", input, expect);
	}

}
