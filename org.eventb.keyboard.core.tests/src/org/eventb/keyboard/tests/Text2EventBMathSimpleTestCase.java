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

package org.eventb.keyboard.tests;

import org.junit.Test;
import org.rodinp.keyboard.tests.AbstractText2MathTestCase;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Text2EventBMath
 *         translator. This tests the translation on all the symbols separately.
 */
public class Text2EventBMathSimpleTestCase extends AbstractText2MathTestCase {

	@Test
	public void testConstants() {
		testTranslator(
				"NAT", "\u2115",
				"NAT1", "\u2115\u0031",
				"INT", "\u2124"
		);
	}
	
	@Test
	public void testPropositionalCalculus() {
		testTranslator(
				"&", "\u2227",
				"=>", "\u21d2",
				"<=>", "\u21d4",
				"not", "\u00ac",
				"or", "\u2228",
				"true", "\u22a4",
				"false", "\u22a5"
		);
	}

	@Test
	public void testPredicateCalculus() {
		testTranslator(
				"!", "\u2200",
				"#", "\u2203",
				".", "\u00b7"
		);
	}
	
	@Test
	public void testBasicSetTheory() {
		testTranslator(
				":", "\u2208",
				"/:", "\u2209",
				"POW", "\u2119",
				"POW1", "\u2119\u0031",
				"**", "\u00d7",
				"|->", "\u21a6",
				",,", "\u21a6",
				"|", "\u2223",
				"<:", "\u2286",
				"/<:", "\u2288",
				"<<:", "\u2282",
				"/<<:", "\u2284",
				"/=", "\u2260"
		);
	}

	@Test
	public void testElementarySetTheory() {
		testTranslator(
				"\\/", "\u222a",
				"/\\", "\u2229",
				"\\", "\u2216",
				"{}", "\u2205",
				"UNION", "\u22c3",
				"INTER", "\u22c2"				
		);
	}

	@Test
	public void testBinaryRelation() {
		testTranslator(
				"<->", "\u2194",
				"~", "\u223c",
				"circ", "\u2218",
				"<<->", "\ue100",
				"<->>", "\ue101",
				"<<->>", "\ue102",
				"<|", "\u25c1",
				"|>", "\u25b7",
				"<<|", "\u2a64",
				"|>>", "\u2a65",
				"<+", "\ue103",
				"><", "\u2297",
				"||", "\u2225",
				"%", "\u03bb"
		);
	}


	@Test
	public void testFunction() {
		testTranslator(
				"+->", "\u21f8",
				"-->", "\u2192",
				">+>", "\u2914",
				">->", "\u21a3",
				"+>>", "\u2900",
				"->>", "\u21a0",
				">->>", "\u2916"
		);
	}

	@Test
	public void testArithmetics() {
		testTranslator(
				"..", "\u2025",
				"<=", "\u2264",
				">=", "\u2265",
				"-", "\u2212",
				"*", "\u2217",
				"/", "\u00f7"
		);
	}

	@Test
	public void testAssignments() {
		testTranslator(
				":=", "\u2254",
				"::", ":\u2208",
				":|", ":\u2223"
		);
	}

	@Test
	public void testOther() {
		testTranslator("oftype", "\u2982");
	}
}
