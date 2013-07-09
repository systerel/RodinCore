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
 * This class contains some simple test cases for Text2EventBMath translator.
 * <p>
 * In particular, it checks that all symbols are individually correctly
 * translated.
 * </p>
 * 
 * @author htson
 */
public class Text2EventBMathSimpleTestCase extends
		AbstractText2EventBMathTestCase {

	/**
	 * Checks type symbol translation.
	 */
	@Test
	public void testConstants() {
		testTranslation(//
				"\u2115", "NAT", //
				"\u2115\u0031", "NAT1", //
				"\u2124", "INT" //
		);
	}

	/**
	 * Checks propositional calculus symbol translation.
	 */
	@Test
	public void testPropositionalCalculus() {
		testTranslation( //
				"\u2227", "&", //
				"\u21d2", "=>", //
				"\u21d4", "<=>",//
				"\u00ac", "not",//
				"\u2228", "or",//
				"\u22a4", "true", //
				"\u22a5", "false"//
		);
	}

	/**
	 * Checks predicate calculus symbol translation.
	 */
	@Test
	public void testPredicateCalculus() {
		testTranslation( //
				"\u2200", "!", //
				"\u2203", "#", //
				"\u00b7", "." //
		);
	}

	/**
	 * Checks set theory symbol translation.
	 */
	@Test
	public void testBasicSetTheory() {
		testTranslation( //
				"\u2208", ":",//
				"\u2209", "/:",//
				"\u2119", "POW",//
				"\u2119\u0031", "POW1",//
				"\u00d7", "**", //
				"\u21a6", "|->", //
				"\u21a6", ",,", //
				"\u2223", "|", //
				"\u2286", "<:", //
				"\u2288", "/<:", //
				"\u2282", "<<:", //
				"\u2284", "/<<:", //
				"\u2260", "/="//
		);
	}

	/**
	 * Checks element related set theory symbols
	 */
	@Test
	public void testElementarySetTheory() {
		testTranslation( //
				"\u222a", "\\/", //
				"\u2229", "/\\", //
				"\u2216", "\\", //
				"\u2205", "{}", //
				"\u22c3", "UNION", //
				"\u22c2", "INTER" //
		);
	}

	/**
	 * Checks relational symbol translation.
	 */
	@Test
	public void testBinaryRelation() {
		testTranslation( //
				"\u2194", "<->", //
				"\u223c", "~", //
				"\u2218", "circ", //
				"\ue100", "<<->", //
				"\ue101", "<->>", //
				"\ue102", "<<->>", //
				"\u25c1", "<|", //
				"\u25b7", "|>", //
				"\u2a64", "<<|", //
				"\u2a65", "|>>", //
				"\ue103", "<+", //
				"\u2297", "><", //
				"\u2225", "||", //
				"\u03bb", "%"//
		);
	}

	/**
	 * Checks functional symbol translation.
	 */
	@Test
	public void testFunction() {
		testTranslation("\u21f8", "+->",//
				"\u2192", "-->", //
				"\u2914", ">+>", //
				"\u21a3", ">->", //
				"\u2900", "+>>", //
				"\u21a0", "->>", //
				"\u2916", ">->>" //
		);
	}

	/**
	 * Checks arithmetical symbol translation.
	 */
	@Test
	public void testArithmetics() {
		testTranslation( //
				"\u2025", "..", //
				"\u2264", "<=", //
				"\u2265", ">=", //
				"\u2212", "-", //
				"\u2217", "*", //
				"\u00f7", "/"//
		);
	}

	/**
	 * Checks assignment symbol translation.
	 */
	@Test
	public void testAssignments() {
		testTranslation( //
				"\u2254", ":=", //
				":\u2208", "::", //
				":\u2223", ":|" //
		);
	}

	/**
	 * Checks other Event-B symbol translation.
	 */
	@Test
	public void testOther() {
		testTranslation( //
				"\u2982", "oftype");
	}

}
