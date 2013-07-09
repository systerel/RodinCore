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
package org.eventb.keyboard.tests.core;

import org.junit.Test;
import org.rodinp.keyboard.core.tests.AbstractText2EventBMathTestCase;

/**
 * This class contains some simple test cases for Text2EventBMath translator.
 * This tests the translation on all the LaTeX symbols separately.
 * 
 * @author htson
 */
public class Text2EventBMathLaTeXSimpleTestCase extends
		AbstractText2EventBMathTestCase {
	
	/**
	 * Checks type LaTeX symbol translation.
	 */
	@Test
	public void testConstants() {
		testTranslation(//
				"\u2115", "\\nat", //
				"\u2115\u0031", "\\natn", //
				"\u2124", "\\intg" //
		);
	}
	
	/**
	 * Checks propositional calculus LaTeX symbol translation.
	 */
	@Test
	public void testPropositionalCalculus() {
		testTranslation(//
				"\u2227", "\\land", //
				"\u21d2", "\\limp", //
				"\u21d4", "\\leqv", //
				"\u00ac", "\\lnot", //
				"\u2228", "\\lor", //
				"\u22a4", "\\btrue", //
				"\u22a5", "\\bfalse" //
		);
	}
	
	/**
	 * Checks predicate calculus LaTeX symbol translation.
	 */
	@Test
	public void testPredicateCalculus() {
		testTranslation(//
				"\u2200", "\\forall",//
				"\u2203", "\\exists", //
				"\u00b7", "\\qdot" //
		);
	}
	
	/**
	 * Checks set theory LaTeX symbol translation.
	 */
	@Test
	public void testBasicSetTheory() {
		testTranslation(//
				"\u2208", "\\in", //
				"\u2209", "\\notin", //
				"\u2119", "\\pow", //
				"\u2119\u0031", "\\pown",//
				"\u00d7", "\\cprod", //
				"\u21a6", "\\mapsto", //
				"\u2223", "\\mid", //
				"\u2286", "\\subseteq", //
				"\u2288", "\\notsubseteq", //
				"\u2282", "\\subset", //
				"\u2284", "\\notsubset", //
				"\u2260", "\\neq"//
		);
	}
	
	/**
	 * Checks element related set theory LaTeX symbols
	 */
	@Test
	public void testElementarySetTheory() {
		testTranslation(//
				"\u222a", "\\bunion", //
				"\u2229", "\\binter", //
				"\u2216", "\\setminus", //
				"\u2205", "\\emptyset", //
				"\u22c3", "\\Union", //
				"\u22c2", "\\Inter" //
		);
	}
	
	/**
	 * Checks relational LaTeX symbol translation.
	 */
	@Test
	public void testBinaryRelation() {
		testTranslation(//
				"\u2194", "\\rel", //
				"\u223c", "\\conv", //
				"\u003b", "\\fcomp", //
				"\u2218", "\\bcomp", //
				"\ue100", "\\trel", //
				"\ue101", "\\srel", //
				"\ue102", "\\strel", //
				"\u25c1", "\\domres", //
				"\u25b7", "\\ranres", //
				"\u2a64", "\\domsub", //
				"\u2a65", "\\ransub", //
				"\ue103", "\\ovl", //
				"\u2297", "\\dprod", //
				"\u2225", "\\pprod", //
				"\u03bb", "\\lambda" //
		);
	}
	
	/**
	 * Checks functional LaTeX symbol translation.
	 */
	@Test
	public void testFunction() {
		testTranslation(//
				"\u21f8", "\\pfun", //
				"\u2192", "\\tfun", //
				"\u2914", "\\pinj", //
				"\u21a3", "\\tinj", //
				"\u2900", "\\psur", //
				"\u21a0", "\\tsur", //
				"\u2916", "\\tbij" //
		);
	}
	
	/**
	 * Checks arithmetical LaTeX symbol translation.
	 */
	@Test
	public void testArithmetics() {
		testTranslation(//
				"\u2025", "\\upto", //
				"\u2264", "\\leq", //
				"\u2265", "\\geq", //
				"\u2212", "-", //
				"\u005e", "\\expn", //
				"\u00f7", "\\div" //
		);
	}
	
	/**
	 * Checks assignment LaTeX symbol translation.
	 */
	@Test
	public void testAssignments() {
		testTranslation(//
				"\u2254", "\\bcmeq", //
				":\u2208", "\\bcmin", //
				":\u2223", "\\bcmsuch" //
		);
	}
	
	/**
	 * Checks other Event-B LaTeX symbol translation.
	 */
	@Test
	public void testOther() {
		testTranslation(//
				"\u2982", "\\oftype"//
		);
	}

}
