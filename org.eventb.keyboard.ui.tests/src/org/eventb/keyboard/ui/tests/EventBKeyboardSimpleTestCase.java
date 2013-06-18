/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.keyboard.ui.tests;

import org.junit.Test;
import org.rodinp.keyboard.tests.AbstractRodinKeyboardTestCase;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Event-B Keyboard. This
 *         test all the symbols separately.
 */
public class EventBKeyboardSimpleTestCase extends AbstractRodinKeyboardTestCase {

	@Test
	public void testConstants() {
		doTest("NAT ", "\u2115 ");
		doTest("NAT1 ", "\u2115\u0031 ");
		doTest("INT ", "\u2124 ");
	}
	
	@Test
	public void testPropositionalCalculus() {
		doTest("&", "\u2227");
		doTest("=>", "\u21d2");
		doTest("<=>", "\u21d4");
		doTest("not ", "\u00ac ");
		doTest("or ", "\u2228 ");
		doTest("true ", "\u22a4 ");
		doTest("false ", "\u22a5 ");		
	}
	
	@Test
	public void testPredicateCalculus() {
		doTest("!", "\u2200");
		doTest("#", "\u2203");
		doTest(".", "\u00b7");
	}

	@Test
	public void testBasicSetTheory() {
		doTest(":", "\u2208");
		doTest("/:", "\u2209");
		doTest("POW ", "\u2119 ");
		doTest("POW1 ", "\u2119\u0031 ");		
		doTest("**", "\u00d7");
		doTest("|->", "\u21a6");
		doTest(",,", "\u21a6");
		doTest("|", "\u2223");
		doTest("<:", "\u2286");
		doTest("/<:", "\u2288");
		doTest("<<:", "\u2282");
		doTest("/<<:", "\u2284");
		doTest("/=", "\u2260");
	}

	@Test
	public void testElementarySetTheory() {
		doTest("\\/", "\u222a");
		doTest("/\\", "\u2229");
		doTest("\\", "\u2216");
		doTest("{}", "\u2205");
		doTest("UNION ", "\u22c3 ");
		doTest("INTER ", "\u22c2 ");
	}
	
	@Test
	public void testBinaryRelation() {
		doTest("<->", "\u2194");
		doTest("~", "\u223c");
		doTest("circ ", "\u2218 ");
		doTest("<<->", "\ue100");
		doTest("<->>", "\ue101");
		doTest("<<->>", "\ue102");
		doTest("<|", "\u25c1");
		doTest("|>", "\u25b7");
		doTest("<<|", "\u2a64");
		doTest("|>>", "\u2a65");
		doTest("<+", "\ue103");
		doTest("><", "\u2297");
		doTest("||", "\u2225");
		doTest("%", "\u03bb");
	}

	@Test
	public void testFunction() {
		doTest("+->", "\u21f8");
		doTest("-->", "\u2192");
		doTest(">+>", "\u2914");
		doTest(">->", "\u21a3");
		doTest("+>>", "\u2900");
		doTest("->>", "\u21a0");
		doTest(">->>", "\u2916");
	}


	@Test
	public void testArithmetics() {
		doTest("..", "\u2025");
		doTest("<=", "\u2264");
		doTest(">=", "\u2265");
		doTest("-", "\u2212");
		doTest("*", "\u2217");
		doTest("/", "\u00f7");
	}

	@Test
	public void testAssignment() {
		doTest(":=", "\u2254");
		doTest("::", ":\u2208");
		doTest(":|", ":\u2223");
	}

	@Test
	public void testOther() {
		doTest("oftype ", "\u2982 ");
	}

}
