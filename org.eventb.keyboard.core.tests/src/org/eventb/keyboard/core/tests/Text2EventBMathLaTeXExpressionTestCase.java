/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.keyboard.core.tests;

import org.junit.Test;
import org.rodinp.keyboard.core.tests.AbstractText2EventBMathTestCase;

/**
 * This class contains some expression test cases for Text2EventBMath
 * translator.
 * <p>
 * This tests the LaTeX symbol translation on some large expressions taken from
 * Prof. Jean-Raymond Abrial's Marriage and SHWT developments.
 * </p>
 * 
 * @author htson
 */
public class Text2EventBMathLaTeXExpressionTestCase extends
		AbstractText2EventBMathTestCase {

	@Test
	public void testMarriageInvariants() {
		testTranslation("MarriageInvariant", //
				"p \u2286 P \u2227\n" + "s \u2208 P \u2914 P \u2227\n"
						+ "s = s\u223c \u2227\n" + "s \u2229 id(P) = \u2205", //
				"p \\subseteq P \\land\n" + "s \\in P \\pinj P \\land\n"
						+ "s = s\\conv \\land\n"
						+ "s \\binter id(P) = \\emptyset" //
		);
	}

	@Test
	public void testBirthGuards() {
		testTranslation("BirthGuards", //
				"x \u2208 P \u2212 p", //
				"x \\in P - p" //
		);
	}

	@Test
	public void testBirthActions() {
		testTranslation("BirthActions", //
				"p \u2254 p \u222a {x}", //
				"p \\bcmeq p \\bunion {x}" //
		);
	}

	@Test
	public void testDeathGuards() {
		testTranslation("DeathActions", //
				"x \u2208 p", //
				"x \\in p" //
		);
	}

	@Test
	public void testDeathActions() {
		testTranslation(
				"MarriageActions", //
				"p \u2254 p \u2212 {x}\n" + "s \u2254 {x} \u2a64 s \u2a65 {x}",
				"p \\bcmeq p - {x}\n" + "s \\bcmeq {x} \\domsub s \\ransub {x}"//
		);
	}

	@Test
	public void testMarriageGuards() {
		testTranslation(
				"MarriageActions", //
				"x \u2208 P \u2212 dom(s) \u2227\ny \u2208 P \u2212 dom(s) \u2227\nx \u2260 y",
				"x \\in P - dom(s) \\land\ny \\in P - dom(s) \\land\nx \\neq y" //
		);
	}

	@Test
	public void testMarriageActions() {
		testTranslation(
				"MarriageActions", //
				"s \u2254 s \ue103 {x \u21a6 y} \ue103 {y \u21a6 x}",
				"s \\bcmeq s \\ovl {x \\mapsto y} \\ovl {y \\mapsto x}" //
		);
	}

	@Test
	public void testDivorceGuards() {
		testTranslation("DivorceGuards", //
				"x \u2208 dom(s)", //
				"x \\in dom(s)"//
		);
	}

	@Test
	public void testDivorceActions() {
		testTranslation("DivorceActions", //
				"s \u2254 {x} \u2a64 s \u2a65 {x}", //
				"s \\bcmeq {x} \\domsub s \\ransub {x}"//
		);
	}

	@Test
	public void testSHWTProperties() {
		testTranslation(
				"SHWTProperties", //
				"rr \u2208 NODE \u2194 NODE \u2227\n"
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
						+ "\u2200ss\u00b7(ss \u2286 NODE \u2227 rr[ss] \u2286 ss \u21d2 cl[ss] \u2286 ss)", //
				"rr \\in NODE \\rel NODE \\land\n"
						+ "cl \\in NODE \\rel NODE \\land\n"
						+ "tp \\in NODE \\land\n"
						+ "\n"
						+ "\\forallss\\qdot(ss \\subseteq NODE \\limp ss \\subseteq cl[ss]) \\land\n"
						+ "\n"
						+ "\\forall(xx, yy, aa)\\qdot(xx \\in NODE \\land\n"
						+ "               yy \\in NODE \\land\n"
						+ "               aa \\subseteq NODE \\land\n"
						+ "               xx \\mapsto yy \\in rr \\land\n"
						+ "               xx \\in cl[aa]\n"
						+ "            \\limp\n"
						+ "               yy \\in cl[aa]\n"
						+ "               ) \\land\n"
						+ "\n"
						+ "\\forallss\\qdot(ss \\subseteq NODE \\land rr[ss] \\subseteq ss \\limp cl[ss] \\subseteq ss)" //
		);
	}

}
