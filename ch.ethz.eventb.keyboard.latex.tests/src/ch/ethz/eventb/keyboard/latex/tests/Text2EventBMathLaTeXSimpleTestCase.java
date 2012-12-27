/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package ch.ethz.eventb.keyboard.latex.tests;

import org.junit.Test;
import org.rodinp.keyboard.tests.AbstractText2MathTestCase;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Text2EventBMath
 *         translator. This tests the translation on all the symbols separately.
 */
public class Text2EventBMathLaTeXSimpleTestCase extends AbstractText2MathTestCase {

	@Test
	public void testConstants() {
		testTranslator(
				"\\nat", "\u2115",
				"\\natn", "\u2115\u0031",
				"\\intg", "\u2124"
		);
	}
	
	@Test
	public void testPropositionalCalculus() {
		testTranslator(
				"\\land", "\u2227",
				"\\limp", "\u21d2",
				"\\leqv", "\u21d4",
				"\\lnot", "\u00ac",
				"\\lor", "\u2228",
				"\\btrue", "\u22a4",
				"\\bfalse", "\u22a5"
		);
	}

	@Test
	public void testPredicateCalculus() {
		testTranslator(
				"\\forall", "\u2200",
				"\\exists", "\u2203",
				"\\qdot", "\u00b7"
		);
	}
	
	@Test
	public void testBasicSetTheory() {
		testTranslator(
				"\\in", "\u2208",
				"\\notin", "\u2209",
				"\\pow", "\u2119",
				"\\pown", "\u2119\u0031",
				"\\cprod", "\u00d7",
				"\\mapsto", "\u21a6",
				"\\mid", "\u2223",
				"\\subseteq", "\u2286",
				"\\notsubseteq", "\u2288",
				"\\subset", "\u2282",
				"\\notsubset", "\u2284",
				"\\neq", "\u2260"
		);
	}

	@Test
	public void testElementarySetTheory() {
		testTranslator(
				"\\bunion", "\u222a",
				"\\binter", "\u2229",
				"\\setminus", "\u2216",
				"\\emptyset", "\u2205",
				"\\Union", "\u22c3",
				"\\Inter", "\u22c2"				
		);
	}

	@Test
	public void testBinaryRelation() {
		testTranslator(
				"\\rel", "\u2194",
				"\\conv", "\u223c",
				"\\fcomp", "\u003b",
				"\\bcomp", "\u2218",
				"\\trel", "\ue100",
				"\\srel", "\ue101",
				"\\strel", "\ue102",
				"\\domres", "\u25c1",
				"\\ranres", "\u25b7",
				"\\domsub", "\u2a64",
				"\\ransub", "\u2a65",
				"\\ovl", "\ue103",
				"\\dprod", "\u2297",
				"\\pprod", "\u2225",
				"\\lambda", "\u03bb"
		);
	}


	@Test
	public void testFunction() {
		testTranslator(
				"\\pfun", "\u21f8",
				"\\tfun", "\u2192",
				"\\pinj", "\u2914",
				"\\tinj", "\u21a3",
				"\\psur", "\u2900",
				"\\tsur", "\u21a0",
				"\\tbij", "\u2916"
		);
	}

	@Test
	public void testArithmetics() {
		testTranslator(
				"\\upto", "\u2025",
				"\\leq", "\u2264",
				"\\geq", "\u2265",
				"-", "\u2212",
				"\\expn", "\u005e",
				"\\div", "\u00f7"
		);
	}

	@Test
	public void testAssignments() {
		testTranslator(
				"\\bcmeq", "\u2254",
				"\\bcmin", ":\u2208",
				"\\bcmsuch", ":\u2223"
		);
	}

	@Test
	public void testOther() {
		testTranslator("\\oftype", "\u2982");
	}
}
