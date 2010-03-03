/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed tests about LaTeX translation
 *******************************************************************************/
package org.eventb.eventBKeyboard.internal.tests;

import junit.framework.TestCase;

import org.eventb.eventBKeyboard.Text2EventBMathTranslator;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Text2EventBMath
 *         translator. This tests the translation on all the symbols separately.
 * @deprecated use org.eventb.keyboard.tests instead
 */
@Deprecated
public class Text2EventBMathSimpleTestCase extends TestCase {

	public void testNAT() {
		assertEquals("NAT", "\u2115", Text2EventBMathTranslator
				.translate("NAT"));
		assertEquals("NAT ", "\u2115 ", Text2EventBMathTranslator
				.translate("NAT "));
		assertEquals(" NAT", " \u2115", Text2EventBMathTranslator
				.translate(" NAT"));
		assertEquals(" NAT ", " \u2115 ", Text2EventBMathTranslator
				.translate(" NAT "));
	}

	public void testNAT1() {
		assertEquals("NAT1", "\u2115\u0031", Text2EventBMathTranslator
				.translate("NAT1"));
		assertEquals("NAT1 ", "\u2115\u0031 ", Text2EventBMathTranslator
				.translate("NAT1 "));
		assertEquals(" NAT1", " \u2115\u0031", Text2EventBMathTranslator
				.translate(" NAT1"));
		assertEquals("NAT1 ", " \u2115\u0031 ", Text2EventBMathTranslator
				.translate(" NAT1 "));
	}

	public void testPOW() {
		assertEquals("POW", "\u2119", Text2EventBMathTranslator
				.translate("POW"));
		assertEquals("POW ", "\u2119 ", Text2EventBMathTranslator
				.translate("POW "));
		assertEquals(" POW", " \u2119", Text2EventBMathTranslator
				.translate(" POW"));
		assertEquals(" POW ", " \u2119 ", Text2EventBMathTranslator
				.translate(" POW "));
		assertEquals("POW(a)", "\u2119(a)", Text2EventBMathTranslator
				.translate("POW(a)"));
		assertEquals("POW (a)", "\u2119 (a)", Text2EventBMathTranslator
				.translate("POW (a)"));
	}

	public void testPOW1() {
		assertEquals("POW1", "\u2119\u0031", Text2EventBMathTranslator
				.translate("POW1"));
		assertEquals("POW1 ", "\u2119\u0031 ", Text2EventBMathTranslator
				.translate("POW1 "));
		assertEquals(" POW1", " \u2119\u0031", Text2EventBMathTranslator
				.translate(" POW1"));
		assertEquals(" POW1 ", " \u2119\u0031 ", Text2EventBMathTranslator
				.translate(" POW1 "));
		assertEquals("POW1(a)", "\u2119\u0031(a)", Text2EventBMathTranslator
				.translate("POW1(a)"));
		assertEquals("POW1 (a)", "\u2119\u0031 (a)", Text2EventBMathTranslator
				.translate("POW1 (a)"));
	}

	public void testINT() {
		assertEquals("INT", "\u2124", Text2EventBMathTranslator
				.translate("INT"));
		assertEquals("INT ", "\u2124 ", Text2EventBMathTranslator
				.translate("INT "));
		assertEquals(" INT", " \u2124", Text2EventBMathTranslator
				.translate(" INT"));
		assertEquals(" INT ", " \u2124 ", Text2EventBMathTranslator
				.translate(" INT "));
	}

	public void testLogicalEquivalent() {
		assertEquals("<=>", "\u21d4", Text2EventBMathTranslator
				.translate("<=>"));
	}

	public void testImply() {
		assertEquals("=>", "\u21d2", Text2EventBMathTranslator.translate("=>"));
	}

	public void testAnd() {
		assertEquals("&", "\u2227", Text2EventBMathTranslator.translate("&"));
	}

	public void testOr() {
		assertEquals("or", "\u2228", Text2EventBMathTranslator.translate("or"));
		assertEquals("or ", "\u2228 ", Text2EventBMathTranslator
				.translate("or "));
		assertEquals(" or", " \u2228", Text2EventBMathTranslator
				.translate(" or"));
		assertEquals(" or ", " \u2228 ", Text2EventBMathTranslator
				.translate(" or "));
	}

	public void testNot() {
		assertEquals("not", "\u00ac", Text2EventBMathTranslator
				.translate("not"));
		assertEquals("not ", "\u00ac ", Text2EventBMathTranslator
				.translate("not "));
		assertEquals(" not", " \u00ac", Text2EventBMathTranslator
				.translate(" not"));
		assertEquals(" not ", " \u00ac ", Text2EventBMathTranslator
				.translate(" not "));
	}

	public void testTrue() {
		assertEquals("true", "\u22a4", Text2EventBMathTranslator
				.translate("true"));
		assertEquals("true ", "\u22a4 ", Text2EventBMathTranslator
				.translate("true "));
		assertEquals(" true", " \u22a4", Text2EventBMathTranslator
				.translate(" true"));
		assertEquals(" true ", " \u22a4 ", Text2EventBMathTranslator
				.translate(" true "));
	}

	public void testFalse() {
		assertEquals("false", "\u22a5", Text2EventBMathTranslator
				.translate("false"));
		assertEquals("false ", "\u22a5 ", Text2EventBMathTranslator
				.translate("false "));
		assertEquals(" false", " \u22a5", Text2EventBMathTranslator
				.translate(" false"));
		assertEquals(" false ", " \u22a5 ", Text2EventBMathTranslator
				.translate(" false "));
	}

	public void testForall() {
		assertEquals("!", "\u2200", Text2EventBMathTranslator.translate("!"));
	}

	public void testThereExists() {
		assertEquals("#", "\u2203", Text2EventBMathTranslator.translate("#"));
	}

	public void testMiddleDot() {
		assertEquals(".", "\u00b7", Text2EventBMathTranslator.translate("."));
	}

	public void testNotEqual() {
		assertEquals("/=", "\u2260", Text2EventBMathTranslator.translate("/="));
	}

	public void testLessThanEqual() {
		assertEquals("<=", "\u2264", Text2EventBMathTranslator.translate("<="));
	}

	public void testGreaterThanEqual() {
		assertEquals(">=", "\u2265", Text2EventBMathTranslator.translate(">="));
	}

	public void testElementOf() {
		assertEquals(":", "\u2208", Text2EventBMathTranslator.translate(":"));
	}

	public void testNotAnElementOf() {
		assertEquals("/:", "\u2209", Text2EventBMathTranslator.translate("/:"));
	}

	public void testSubsetOf() {
		assertEquals("<<:", "\u2282", Text2EventBMathTranslator
				.translate("<<:"));
	}

	public void testNotASubsetOf() {
		assertEquals("/<<:", "\u2284", Text2EventBMathTranslator
				.translate("/<<:"));
	}

	public void testSubsetOrEqualTo() {
		assertEquals("<:", "\u2286", Text2EventBMathTranslator.translate("<:"));
	}

	public void testNotASubsetOfNorEqualTo() {
		assertEquals("/<:", "\u2288", Text2EventBMathTranslator
				.translate("/<:"));
	}

	public void testRelation() {
		assertEquals("<->", "\u2194", Text2EventBMathTranslator
				.translate("<->"));
	}

	public void testTotalRelation() {
		assertEquals(":", "\ue100", Text2EventBMathTranslator.translate("<<->"));
	}

	public void testSurjectiveRelation() {
		assertEquals("<->>", "\ue101", Text2EventBMathTranslator
				.translate("<->>"));
	}

	public void testTotalSurjectiveRelation() {
		assertEquals("<<->>", "\ue102", Text2EventBMathTranslator
				.translate("<<->>"));
	}

	public void testPartialFunction() {
		assertEquals("+->", "\u21f8", Text2EventBMathTranslator
				.translate("+->"));
	}

	public void testTotalFunction() {
		assertEquals("-->", "\u2192", Text2EventBMathTranslator
				.translate("-->"));
	}

	public void testPartialInjectiveFunction() {
		assertEquals(">+>", "\u2914", Text2EventBMathTranslator
				.translate(">+>"));
	}

	public void testTotalInjectiveFunction() {
		assertEquals(">->", "\u21a3", Text2EventBMathTranslator
				.translate(">->"));
	}

	public void testPartialSurjectiveFunction() {
		assertEquals("+>>", "\u2900", Text2EventBMathTranslator
				.translate("+>>"));
	}

	public void testTotalSurjectiveFunction() {
		assertEquals("->>", "\u21a0", Text2EventBMathTranslator
				.translate("->>"));
	}

	public void testBijectiveFunction() {
		assertEquals(">->>", "\u2916", Text2EventBMathTranslator
				.translate(">->>"));
	}

	public void testMaplet() {
		assertEquals("|->", "\u21a6", Text2EventBMathTranslator
				.translate("|->"));
	}

	public void testEmptySet() {
		assertEquals("{}", "\u2205", Text2EventBMathTranslator.translate("{}"));
	}

	public void testIntersection() {
		assertEquals("/\\", "\u2229", Text2EventBMathTranslator
				.translate("/\\"));
	}

	public void testUnion() {
		assertEquals("\\/", "\u222a", Text2EventBMathTranslator
				.translate("\\/"));
	}

	public void testSetMinus() {
		assertEquals("\\", "\u2216", Text2EventBMathTranslator.translate("\\"));
	}

	public void testCartesianProduct() {
		assertEquals("**", "\u00d7", Text2EventBMathTranslator.translate("**"));
	}

	public void testRelationOverriding() {
		assertEquals("<+", "\ue103", Text2EventBMathTranslator.translate("<+"));
	}

	public void testBackwardComposition() {
		assertEquals("circ", "\u2218", Text2EventBMathTranslator
				.translate("circ"));
		assertEquals("circ ", "\u2218 ", Text2EventBMathTranslator
				.translate("circ "));
		assertEquals(" circ", " \u2218", Text2EventBMathTranslator
				.translate(" circ"));
		assertEquals(" circ ", " \u2218 ", Text2EventBMathTranslator
				.translate(" circ "));
	}

	public void testDirectProduct() {
		assertEquals("><", "\u2297", Text2EventBMathTranslator.translate("><"));
	}

	public void testParallelProduct() {
		assertEquals("||", "\u2225", Text2EventBMathTranslator.translate("||"));
	}

	public void testTildeOperator() {
		assertEquals("~", "\u223c", Text2EventBMathTranslator.translate("~"));
	}

	public void testDomainRestriction() {
		assertEquals("<|", "\u25c1", Text2EventBMathTranslator.translate("<|"));
	}

	public void testDomainSubstraction() {
		assertEquals("<<|", "\u2a64", Text2EventBMathTranslator
				.translate("<<|"));
	}

	public void testRangeRestriction() {
		assertEquals("|>", "\u25b7", Text2EventBMathTranslator.translate("|>"));
	}

	public void testRangeSubstraction() {
		assertEquals("|>>", "\u2a65", Text2EventBMathTranslator
				.translate("|>>"));
	}

	public void testLambda() {
		assertEquals("%", "\u03bb", Text2EventBMathTranslator.translate("%"));
	}

	public void testINTER() {
		assertEquals("INTER", "\u22c2", Text2EventBMathTranslator
				.translate("INTER"));
		assertEquals("INTER ", "\u22c2 ", Text2EventBMathTranslator
				.translate("INTER "));
		assertEquals(" INTER", " \u22c2", Text2EventBMathTranslator
				.translate(" INTER"));
		assertEquals(" INTER ", " \u22c2 ", Text2EventBMathTranslator
				.translate(" INTER "));
		assertEquals("INTER()", "\u22c2()", Text2EventBMathTranslator
				.translate("INTER()"));
		assertEquals("INTER ()", "\u22c2 ()", Text2EventBMathTranslator
				.translate("INTER ()"));
	}

	public void testUNION() {
		assertEquals("UNION", "\u22c3", Text2EventBMathTranslator
				.translate("UNION"));
		assertEquals("UNION ", "\u22c3 ", Text2EventBMathTranslator
				.translate("UNION "));
		assertEquals(" UNION", " \u22c3", Text2EventBMathTranslator
				.translate(" UNION"));
		assertEquals(" UNION ", " \u22c3 ", Text2EventBMathTranslator
				.translate(" UNION "));
		assertEquals("UNION()", "\u22c3()", Text2EventBMathTranslator
				.translate("UNION()"));
		assertEquals("UNION ()", "\u22c3 ()", Text2EventBMathTranslator
				.translate("UNION ()"));
	}

	public void testUptoOperator() {
		assertEquals("..", "\u2025", Text2EventBMathTranslator.translate(".."));
	}

	public void testMinus() {
		assertEquals("-", "\u2212", Text2EventBMathTranslator.translate("-"));
	}

	public void testAsterisk() {
		assertEquals("*", "\u2217", Text2EventBMathTranslator.translate("*"));
	}

	public void testDivision() {
		assertEquals("/", "\u00f7", Text2EventBMathTranslator.translate("/"));
	}

	public void testBecomesEqual() {
		assertEquals(":=", "\u2254", Text2EventBMathTranslator.translate(":="));
	}

	public void testBecomesAnElementOf() {
		assertEquals("::", ":\u2208", Text2EventBMathTranslator.translate("::"));
	}

	public void testBecomesSuchThat() {
		assertEquals(":|", ":\u2223", Text2EventBMathTranslator.translate(":|"));
	}

	public void testMid() {
		assertEquals("|", "\u2223", Text2EventBMathTranslator.translate("|"));
	}

}
