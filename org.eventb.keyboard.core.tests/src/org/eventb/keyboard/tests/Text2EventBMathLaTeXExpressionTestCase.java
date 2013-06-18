/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 ******************************************************************************/
package org.eventb.keyboard.tests;

import org.junit.Test;
import org.rodinp.keyboard.tests.AbstractText2MathTestCase;

/**
 * @author htson
 *         <p>
 *         This class contains some expression test cases for Text2EventBMath
 *         translator. This tests the translation on some large expressions
 *         taken from Prof. Jean-Raymond Abrial's Marriage and SHWT
 *         developments.
 */
public class Text2EventBMathLaTeXExpressionTestCase extends AbstractText2MathTestCase {

	@Test
	public void testMarriageInvariants() {
		String input = "p \\subseteq P \\land\n" +
				"s \\in P \\pinj P \\land\n" +
				"s = s\\conv \\land\n" +
				"s \\binter id(P) = \\emptyset";
		String expected = "p \u2286 P \u2227\n" +
				"s \u2208 P \u2914 P \u2227\n" +
				"s = s\u223c \u2227\n" +
				"s \u2229 id(P) = \u2205";
		testTranslator("MarriageInvariant", input, expected);
	}

	@Test
	public void testBirthGuards() {
		String input = "x \\in P - p";
		String expect = "x \u2208 P \u2212 p";
		testTranslator("BirthGuards", input, expect);
	}

	@Test
	public void testBirthActions() {
		String input = "p \\bcmeq p \\bunion {x}";
		String expect = "p \u2254 p \u222a {x}";
		testTranslator("BirthActions", input, expect);
	}

	@Test
	public void testDeathGuards() {
		String input = "x \\in p";
		String expect = "x \u2208 p";
		testTranslator("DeathActions", input, expect);
	}

	@Test
	public void testDeathActions() {
		String input = "p \\bcmeq p - {x}\n" +
				"s \\bcmeq {x} \\domsub s \\ransub {x}";
		String expect = "p \u2254 p \u2212 {x}\n" +
				"s \u2254 {x} \u2a64 s \u2a65 {x}";
		testTranslator("MarriageActions", input, expect);
	}

	@Test
	public void testMarriageGuards() {
		String input = "x \\in P - dom(s) \\land\n" +
				"y \\in P - dom(s) \\land\n" +
				"x \\neq y";
		String expect = "x \u2208 P \u2212 dom(s) \u2227\ny \u2208 P \u2212 dom(s) \u2227\nx \u2260 y";
		testTranslator("MarriageActions", input, expect);
	}

	@Test
	public void testMarriageActions() {
		String input = "s \\bcmeq s \\ovl {x \\mapsto y} \\ovl {y \\mapsto x}";
		String expect = "s \u2254 s \ue103 {x \u21a6 y} \ue103 {y \u21a6 x}";
		testTranslator("MarriageActions", input, expect);
	}

	@Test
	public void testDivorceGuards() {
		String input = "x \\in dom(s)";
		String expect = "x \u2208 dom(s)";
		testTranslator("DivorceGuards", input, expect);
	}

	@Test
	public void testDivorceActions() {
		String input = "s \\bcmeq {x} \\domsub s \\ransub {x}";
		String expect = "s \u2254 {x} \u2a64 s \u2a65 {x}";
		testTranslator("DivorceActions", input, expect);
	}

	@Test
	public void testSHWTProperties() {
		String input = "rr \\in NODE \\rel NODE \\land\n"
				+ "cl \\in NODE \\rel NODE \\land\n"
				+ "tp \\in NODE \\land\n"
				+ "\n"
				+ "\\forallss\\qdot(ss \\subseteq NODE \\limp ss \\subseteq cl[ss]) \\land\n"
				+ "\n" + "\\forall(xx, yy, aa)\\qdot(xx \\in NODE \\land\n"
				+ "               yy \\in NODE \\land\n"
				+ "               aa \\subseteq NODE \\land\n"
				+ "               xx \\mapsto yy \\in rr \\land\n"
				+ "               xx \\in cl[aa]\n"
				+ "            \\limp\n"
				+ "               yy \\in cl[aa]\n"
				+ "               ) \\land\n"
				+ "\n"
				+ "\\forallss\\qdot(ss \\subseteq NODE \\land rr[ss] \\subseteq ss \\limp cl[ss] \\subseteq ss)";
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
