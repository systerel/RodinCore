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
package org.eventb.keyboard.ui.tests;

import org.junit.Test;
import org.rodinp.keyboard.tests.AbstractRodinKeyboardTestCase;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Event-B Keyboard. This
 *         test all the symbols separately.
 */
public class EventBKeyboardLaTeXSimpleTestCase extends AbstractRodinKeyboardTestCase {

	@Test
	public void testConstants() {
		doTest("\\nat", "\u2115");
		doTest("\\natn", "\u2115\u0031");
		doTest("\\intg", "\u2124");
	}
	
	@Test
	public void testPropositionalCalculus() {
		doTest("\\land", "\u2227");
		doTest("\\limp", "\u21d2");
		doTest("\\leqv", "\u21d4");
		doTest("\\lnot", "\u00ac");
		doTest("\\lor", "\u2228");
		doTest("\\btrue", "\u22a4");
		doTest("\\bfalse", "\u22a5");		
	}
	
	@Test
	public void testPredicateCalculus() {
		doTest("\\forall", "\u2200");
		doTest("\\exists", "\u2203");
		doTest("\\qdot", "\u00b7");
	}

	@Test
	public void testBasicSetTheory() {
		doTest("\\in", "\u2208");
		doTest("\\notin", "\u2209");
		doTest("\\pow", "\u2119");
		doTest("\\pown", "\u2119\u0031");		
		doTest("\\cprod", "\u00d7");
		doTest("\\mapsto", "\u21a6");
		doTest("\\mid", "\u2223");
		doTest("\\subseteq", "\u2286");
		doTest("\\notsubseteq", "\u2288");
		doTest("\\subset", "\u2282");
		doTest("\\notsubset", "\u2284");
		doTest("\\neq", "\u2260");
	}

	@Test
	public void testElementarySetTheory() {
		doTest("\\bunion", "\u222a");
		doTest("\\binter", "\u2229");
		doTest("\\setminus", "\u2216");
		doTest("\\emptyset", "\u2205");
		doTest("\\Union", "\u22c3");
		doTest("\\Inter", "\u22c2");
	}
	
	@Test
	public void testBinaryRelation() {
		doTest("\\rel", "\u2194");
		doTest("\\conv", "\u223c");
		doTest("\\fcomp", "\u003b");
		doTest("\\bcomp", "\u2218");
		doTest("\\trel", "\ue100");
		doTest("\\srel", "\ue101");
		doTest("\\strel", "\ue102");
		doTest("\\domres", "\u25c1");
		doTest("\\ranres", "\u25b7");
		doTest("\\domsub", "\u2a64");
		doTest("\\ransub", "\u2a65");
		doTest("\\ovl", "\ue103");
		doTest("\\dprod", "\u2297");
		doTest("\\pprod", "\u2225");
		doTest("\\lambda", "\u03bb");
	}

	@Test
	public void testFunction() {
		doTest("\\pfun", "\u21f8");
		doTest("\\tfun", "\u2192");
		doTest("\\pinj", "\u2914");
		doTest("\\tinj", "\u21a3");
		doTest("\\psur", "\u2900");
		doTest("\\tsur", "\u21a0");
		doTest("\\tbij", "\u2916");
	}


	@Test
	public void testArithmetics() {
		doTest("\\upto", "\u2025");
		doTest("\\leq", "\u2264");
		doTest("\\geq", "\u2265");
		doTest("-", "\u2212");
		doTest("\\expn", "\u005e");
		doTest("\\div", "\u00f7");
	}

	@Test
	public void testAssignment() {
		doTest("\\bcmeq", "\u2254");
		doTest("\\bcmin", ":\u2208");
		doTest("\\bcmsuch", ":\u2223");
	}

	@Test
	public void testOther() {
		doTest("\\oftype", "\u2982");
	}

}
