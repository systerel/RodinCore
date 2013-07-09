/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored using a translation tester
 *******************************************************************************/
package org.eventb.keyboard.tests.core;

import org.rodinp.keyboard.core.tests.AbstractText2EventBMathTestCase;

/**
 * This class contains some expression test cases for Text2EventBMath
 * translator.
 * <p>
 * This tests the symbol translation on some large expressions taken from Prof.
 * Jean-Raymond Abrial's Marriage and SHWT developments.
 * </p>
 * 
 * @author htson
 */
public class Text2EventBMathExpressionTestCase extends
		AbstractText2EventBMathTestCase {

	public void testMarriageInvariants() {
		testTranslation(
				"MarriageInvariant",
				"p <: P &\ns : P >+> P &\ns = s~ &\ns /\\ id(P) = {}",
				"p \u2286 P \u2227\ns \u2208 P \u2914 P \u2227\ns = s\u223c \u2227\ns \u2229 id(P) = \u2205");
	}

	public void testBirthGuards() {
		testTranslation( //
				"BirthGuards", //
				"x \u2208 P \u2212 p", //
				"x : P - p" //
		);
	}

	public void testBirthActions() {
		testTranslation( //
				"BirthActions", //
				"p \u2254 p \u222a {x}", //
				"p := p \\/ {x}" //
		);
	}

	public void testDeathGuards() {
		testTranslation( //
				"DeathActions", //
				"x \u2208 p", //
				"x : p" //
		);
	}

	public void testDeathActions() {
		testTranslation(
				"MarriageActions", //
				"p \u2254 p \u2212 {x} \u2225\ns \u2254 {x} \u2a64 s \u2a65 {x}", //
				"p := p - {x} ||\ns := {x} <<| s |>> {x}" //
		);
	}

	public void testMarriageGuards() {
		testTranslation(
				"MarriageActions", //
				"x \u2208 P \u2212 dom(s) \u2227\ny \u2208 P \u2212 dom(s) \u2227\nx \u2260 y", //
				"x : P - dom(s) &\ny : P - dom(s) &\nx /= y" //
		);
	}

	public void testMarriageActions() {
		testTranslation( //
				"MarriageActions", //
				"s \u2254 s \ue103 {x \u21a6 y} \ue103 {y \u21a6 x}", //
				"s := s <+ {x |-> y} <+ {y |-> x}" //
		);
	}

	public void testDivorceGuards() {
		testTranslation( //
				"DivorceGuards", //
				"x \u2208 dom(s)", //
				"x : dom(s)" //
		);
	}

	public void testDivorceActions() {
		testTranslation( //
				"DivorceActions", //
				"s \u2254 {x} \u2a64 s \u2a65 {x}", //
				"s := {x} <<| s |>> {x}" //
		);
	}

	public void testSHWTProperties() {
		testTranslation(
				"SHWTProperties",
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
						+ "\u2200ss\u00b7(ss \u2286 NODE \u2227 rr[ss] \u2286 ss \u21d2 cl[ss] \u2286 ss)",
				"rr : NODE <-> NODE &\n" + "cl : NODE <-> NODE &\n"
						+ "tp : NODE &\n" + "\n"
						+ "!ss.(ss <: NODE => ss <: cl[ss]) &\n" + "\n"
						+ "!(xx, yy, aa).(xx : NODE &\n"
						+ "               yy : NODE &\n"
						+ "               aa <: NODE &\n"
						+ "               xx |-> yy : rr &\n"
						+ "               xx : cl[aa]\n" + "            =>\n"
						+ "               yy : cl[aa]\n"
						+ "               ) &\n" + "\n"
						+ "!ss.(ss <: NODE & rr[ss] <: ss => cl[ss] <: ss)"

		);
	}

}
