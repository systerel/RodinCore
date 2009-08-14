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

package org.eventb.eventBKeyboard.internal.tests;

import junit.framework.TestCase;

import org.eventb.eventBKeyboard.Text2EventBMathTranslator;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Text2EventBMath
 *         translator. This tests the translation on all the symbols separately.
 */
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

	public void testNATLaTeX() {
		assertEquals("nat", "\u2115", Text2EventBMathTranslator
				.translate("nat"));
	}

	public void testNAT1() {
		assertEquals("NAT1", "\u2115\u2081", Text2EventBMathTranslator
				.translate("NAT1"));
		assertEquals("NAT1 ", "\u2115\u2081 ", Text2EventBMathTranslator
				.translate("NAT1 "));
		assertEquals(" NAT1", " \u2115\u2081", Text2EventBMathTranslator
				.translate(" NAT1"));
		assertEquals("NAT1 ", " \u2115\u2081 ", Text2EventBMathTranslator
				.translate(" NAT1 "));
	}

	public void testNAT1LaTeX() {
		assertEquals("natn", "\u2115\u2081", Text2EventBMathTranslator
				.translate("natn"));
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

	public void testPOWLaTeX() {
		assertEquals("pow", "\u2119", Text2EventBMathTranslator
				.translate("pow"));
		assertEquals("pow(a)", "\u2119(a)", Text2EventBMathTranslator
				.translate("pow(a)"));
		assertEquals("pow (a)", "\u2119 (a)", Text2EventBMathTranslator
				.translate("pow (a)"));
	}

	public void testPOW1() {
		assertEquals("POW1", "\u2119\u2081", Text2EventBMathTranslator
				.translate("POW1"));
		assertEquals("POW1 ", "\u2119\u2081 ", Text2EventBMathTranslator
				.translate("POW1 "));
		assertEquals(" POW1", " \u2119\u2081", Text2EventBMathTranslator
				.translate(" POW1"));
		assertEquals(" POW1 ", " \u2119\u2081 ", Text2EventBMathTranslator
				.translate(" POW1 "));
		assertEquals("POW1(a)", "\u2119\u2081(a)", Text2EventBMathTranslator
				.translate("POW1(a)"));
		assertEquals("POW1 (a)", "\u2119\u2081 (a)", Text2EventBMathTranslator
				.translate("POW1 (a)"));
	}

	public void testPOW1LaTeX() {
		assertEquals("pown", "\u2119\u2081", Text2EventBMathTranslator
				.translate("POW1"));
		assertEquals("pown(a)", "\u2119\u2081(a)", Text2EventBMathTranslator
				.translate("POW1(a)"));
		assertEquals("pown (a)", "\u2119\u2081 (a)",
				Text2EventBMathTranslator.translate("pown (a)"));
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

	public void testINTLaTeX() {
		assertEquals("intg", "\u2124", Text2EventBMathTranslator
				.translate("INT"));
	}

	public void testLogicalEquivalent() {
		assertEquals("<=>", "\u21d4", Text2EventBMathTranslator
				.translate("<=>"));
	}

	public void testLogicalEquivalentLaTeX() {
		assertEquals("leqv", "\u21d4", Text2EventBMathTranslator
				.translate("leqv"));
	}

	public void testImply() {
		assertEquals("=>", "\u21d2", Text2EventBMathTranslator.translate("=>"));
	}

	public void testImplyLaTeX() {
		assertEquals("limp", "\u21d2", Text2EventBMathTranslator
				.translate("limp"));
	}

	public void testAnd() {
		assertEquals("&", "\u2227", Text2EventBMathTranslator.translate("&"));
	}

	public void testAndLaTeX() {
		assertEquals("land", "\u2227", Text2EventBMathTranslator
				.translate("land"));
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

	public void testOrLaTeX() {
		assertEquals("lor", "\u2228", Text2EventBMathTranslator
				.translate("lor"));
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

	public void testNotLaTeX() {
		assertEquals("lnot", "\u00ac", Text2EventBMathTranslator
				.translate("lnot"));
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

	public void testTrueLaTeX() {
		assertEquals("btrue", "\u22a4", Text2EventBMathTranslator
				.translate("btrue"));
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

	public void testFalseLaTeX() {
		assertEquals("bfalse", "\u22a5", Text2EventBMathTranslator
				.translate("bfalse"));
	}

	public void testForall() {
		assertEquals("!", "\u2200", Text2EventBMathTranslator.translate("!"));
	}

	public void testForallLaTeX() {
		assertEquals("forall", "\u2200", Text2EventBMathTranslator
				.translate("forall"));
	}

	public void testThereExists() {
		assertEquals("#", "\u2203", Text2EventBMathTranslator.translate("#"));
	}

	public void testThereExistsLaTeX() {
		assertEquals("exists", "\u2203", Text2EventBMathTranslator
				.translate("exists"));
	}

	public void testMiddleDot() {
		assertEquals(".", "\u00b7", Text2EventBMathTranslator.translate("."));
	}

	public void testMiddleDotLaTeX() {
		assertEquals("qdot", "\u00b7", Text2EventBMathTranslator
				.translate("qdot"));
	}

	public void testNotEqual() {
		assertEquals("/=", "\u2260", Text2EventBMathTranslator.translate("/="));
	}

	public void testNotEqualLaTeX() {
		assertEquals("neq", "\u2260", Text2EventBMathTranslator
				.translate("neq"));
	}

	public void testLessThanEqual() {
		assertEquals("<=", "\u2264", Text2EventBMathTranslator.translate("<="));
	}

	public void testLessThanEqualLaTeX() {
		assertEquals("leq", "\u2264", Text2EventBMathTranslator
				.translate("leq"));
	}

	public void testGreaterThanEqual() {
		assertEquals(">=", "\u2265", Text2EventBMathTranslator.translate(">="));
	}

	public void testGreaterThanEqualLaTeX() {
		assertEquals("geq", "\u2265", Text2EventBMathTranslator
				.translate("geq"));
	}

	public void testElementOf() {
		assertEquals(":", "\u2208", Text2EventBMathTranslator.translate(":"));
	}

	public void testElementOfLaTeX() {
		assertEquals("in", "\u2208", Text2EventBMathTranslator
				.translate("in"));
	}

	public void testNotAnElementOf() {
		assertEquals("/:", "\u2209", Text2EventBMathTranslator.translate("/:"));
	}

	public void testNotAnElementOfLaTeX() {
		assertEquals("notin", "\u2209", Text2EventBMathTranslator
				.translate("notin"));
	}

	public void testSubsetOf() {
		assertEquals("<<:", "\u2282", Text2EventBMathTranslator
				.translate("<<:"));
	}

	public void testSubsetOfLaTeX() {
		assertEquals("subset", "\u2282", Text2EventBMathTranslator
				.translate("subset"));
	}

	public void testNotASubsetOf() {
		assertEquals("/<<:", "\u2284", Text2EventBMathTranslator
				.translate("/<<:"));
	}

	public void testNotASubsetOfLaTeX() {
		assertEquals("notsubset", "\u2284", Text2EventBMathTranslator
				.translate("notsubset"));
	}

	public void testSubsetOrEqualTo() {
		assertEquals("<:", "\u2286", Text2EventBMathTranslator.translate("<:"));
	}

	public void testSubsetOrEqualToLaTeX() {
		assertEquals("subseteq", "\u2286", Text2EventBMathTranslator
				.translate("subseteq"));
	}

	public void testNotASubsetOfNorEqualTo() {
		assertEquals("/<:", "\u2288", Text2EventBMathTranslator
				.translate("/<:"));
	}

	public void testNotASubsetOfNorEqualToLaTeX() {
		assertEquals("notsubseteq", "\u2288", Text2EventBMathTranslator
				.translate("notsubseteq"));
	}

	public void testRelation() {
		assertEquals("<->", "\u2194", Text2EventBMathTranslator
				.translate("<->"));
	}

	public void testRelationLaTeX() {
		assertEquals("rel", "\u2194", Text2EventBMathTranslator
				.translate("rel"));
	}

	public void testTotalRelation() {
		assertEquals(":", "\ue100", Text2EventBMathTranslator.translate("<<->"));
	}

	public void testTotalRelationLaTeX() {
		assertEquals("trel", "\ue100", Text2EventBMathTranslator
				.translate("trel"));
	}

	public void testSurjectiveRelation() {
		assertEquals("<->>", "\ue101", Text2EventBMathTranslator
				.translate("<->>"));
	}

	public void testSurjectiveRelationLaTeX() {
		assertEquals("srel", "\ue101", Text2EventBMathTranslator
				.translate("srel"));
	}

	public void testTotalSurjectiveRelation() {
		assertEquals("<<->>", "\ue102", Text2EventBMathTranslator
				.translate("<<->>"));
	}

	public void testTotalSurjectiveRelationLaTeX() {
		assertEquals("strel", "\ue102", Text2EventBMathTranslator
				.translate("strel"));
	}

	public void testPartialFunction() {
		assertEquals("+->", "\u21f8", Text2EventBMathTranslator
				.translate("+->"));
	}

	public void testPartialFunctionLaTeX() {
		assertEquals("pfun", "\u21f8", Text2EventBMathTranslator
				.translate("pfun"));
	}

	public void testTotalFunction() {
		assertEquals("-->", "\u2192", Text2EventBMathTranslator
				.translate("-->"));
	}

	public void testTotalFunctionLaTeX() {
		assertEquals("tfun", "\u2192", Text2EventBMathTranslator
				.translate("tfun"));
	}

	public void testPartialInjectiveFunction() {
		assertEquals(">+>", "\u2914", Text2EventBMathTranslator
				.translate(">+>"));
	}

	public void testPartialInjectiveFunctionLaTeX() {
		assertEquals("pinj", "\u2914", Text2EventBMathTranslator
				.translate("pinj"));
	}

	public void testTotalInjectiveFunction() {
		assertEquals(">->", "\u21a3", Text2EventBMathTranslator
				.translate(">->"));
	}

	public void testTotalInjectiveFunctionLaTeX() {
		assertEquals("tinj", "\u21a3", Text2EventBMathTranslator
				.translate("tinj"));
	}

	public void testPartialSurjectiveFunction() {
		assertEquals("+>>", "\u2900", Text2EventBMathTranslator
				.translate("+>>"));
	}

	public void testPartialSurjectiveFunctionLaTeX() {
		assertEquals("psur", "\u2900", Text2EventBMathTranslator
				.translate("psur"));
	}

	public void testTotalSurjectiveFunction() {
		assertEquals("->>", "\u21a0", Text2EventBMathTranslator
				.translate("->>"));
	}

	public void testTotalSurjectiveFunctionLaTeX() {
		assertEquals("tsur", "\u21a0", Text2EventBMathTranslator
				.translate("tsur"));
	}

	public void testBijectiveFunction() {
		assertEquals(">->>", "\u2916", Text2EventBMathTranslator
				.translate(">->>"));
	}

	public void testBijectiveFunctionLaTeX() {
		assertEquals("tbij", "\u2916", Text2EventBMathTranslator
				.translate("tbij"));
	}

	public void testMaplet() {
		assertEquals("|->", "\u21a6", Text2EventBMathTranslator
				.translate("|->"));
	}

	public void testMapletLaTeX() {
		assertEquals("mapsto", "\u21a6", Text2EventBMathTranslator
				.translate("mapsto"));
	}

	public void testEmptySet() {
		assertEquals("{}", "\u2205", Text2EventBMathTranslator.translate("{}"));
	}

	public void testEmptySetLaTeX() {
		assertEquals("emptyset", "\u2205", Text2EventBMathTranslator
				.translate("emptyset"));
	}

	public void testIntersection() {
		assertEquals("/\\", "\u2229", Text2EventBMathTranslator
				.translate("/\\"));
	}

	public void testIntersectionLaTeX() {
		assertEquals("binter", "\u2229", Text2EventBMathTranslator
				.translate("binter"));
	}

	public void testUnion() {
		assertEquals("\\/", "\u222a", Text2EventBMathTranslator
				.translate("\\/"));
	}

	public void testUnionLaTeX() {
		assertEquals("bunion", "\u222a", Text2EventBMathTranslator
				.translate("bunion"));
	}

	public void testSetMinus() {
		assertEquals("\\", "\u2216", Text2EventBMathTranslator.translate("\\"));
	}

	public void testSetMinusLaTeX() {
		assertEquals("setminus", "\u2216", Text2EventBMathTranslator
				.translate("setminus"));
	}

	public void testCartesianProduct() {
		assertEquals("**", "\u00d7", Text2EventBMathTranslator.translate("**"));
	}

	public void testCartesianProductLaTeX() {
		assertEquals("cprod", "\u00d7", Text2EventBMathTranslator
				.translate("cprod"));
	}

	public void testRelationOverriding() {
		assertEquals("<+", "\ue103", Text2EventBMathTranslator.translate("<+"));
	}

	public void testRelationOverridingLaTeX() {
		assertEquals("ovl", "\ue103", Text2EventBMathTranslator
				.translate("ovl"));
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

	public void testBackwardCompositionLaTeX() {
		assertEquals("bcomp", "\u2218", Text2EventBMathTranslator
				.translate("bcomp"));
	}

	public void testDirectProduct() {
		assertEquals("><", "\u2297", Text2EventBMathTranslator.translate("><"));
	}

	public void testDirectProductLaTeX() {
		assertEquals("dprod", "\u2297", Text2EventBMathTranslator
				.translate("dprod"));
	}

	public void testParallelProduct() {
		assertEquals("||", "\u2225", Text2EventBMathTranslator.translate("||"));
	}

	public void testParallelProductLaTeX() {
		assertEquals("pprod", "\u2225", Text2EventBMathTranslator
				.translate("pprod"));
	}

	public void testTildeOperator() {
		assertEquals("~", "\u223c", Text2EventBMathTranslator.translate("~"));
	}

	public void testDomainRestriction() {
		assertEquals("<|", "\u25c1", Text2EventBMathTranslator.translate("<|"));
	}

	public void testDomainRestrictionLaTeX() {
		assertEquals("domres", "\u25c1", Text2EventBMathTranslator
				.translate("domres"));
	}

	public void testDomainSubstraction() {
		assertEquals("<<|", "\u2a64", Text2EventBMathTranslator
				.translate("<<|"));
	}

	public void testDomainSubstractionLaTeX() {
		assertEquals("domsub", "\u2a64", Text2EventBMathTranslator
				.translate("domsub"));
	}

	public void testRangeRestriction() {
		assertEquals("|>", "\u25b7", Text2EventBMathTranslator.translate("|>"));
	}

	public void testRangeRestrictionLaTeX() {
		assertEquals("ranres", "\u25b7", Text2EventBMathTranslator
				.translate("ranres"));
	}

	public void testRangeSubstraction() {
		assertEquals("|>>", "\u2a65", Text2EventBMathTranslator
				.translate("|>>"));
	}

	public void testRangeSubstractionLaTeX() {
		assertEquals("ransub", "\u2a65", Text2EventBMathTranslator
				.translate("ransub"));
	}

	public void testLambda() {
		assertEquals("%", "\u03bb", Text2EventBMathTranslator.translate("%"));
	}

	public void testLambdaLaTeX() {
		assertEquals("lambda", "\u03bb", Text2EventBMathTranslator
				.translate("lambda"));
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

	public void testINTERLaTeX() {
		assertEquals("Inter", "\u22c2", Text2EventBMathTranslator
				.translate("Inter"));
		assertEquals("Inter()", "\u22c2()", Text2EventBMathTranslator
				.translate("Inter()"));
		assertEquals("Inter ()", "\u22c2 ()", Text2EventBMathTranslator
				.translate("Inter ()"));
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

	public void testUNIONLaTeX() {
		assertEquals("Union", "\u22c3", Text2EventBMathTranslator
				.translate("Union"));
		assertEquals("Union()", "\u22c3()", Text2EventBMathTranslator
				.translate("Union()"));
		assertEquals("Union ()", "\u22c3 ()", Text2EventBMathTranslator
				.translate("Union ()"));
	}

	public void testUptoOperator() {
		assertEquals("..", "\u2025", Text2EventBMathTranslator.translate(".."));
	}

	public void testUptoOperatorLaTeX() {
		assertEquals("upto", "\u2025", Text2EventBMathTranslator
				.translate("upto"));
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

	public void testDivisionLaTeX() {
		assertEquals("div", "\u00f7", Text2EventBMathTranslator
				.translate("div"));
	}

	public void testBecomesEqual() {
		assertEquals(":=", "\u2254", Text2EventBMathTranslator.translate(":="));
	}

	public void testBecomesEqualLaTeX() {
		assertEquals("bcmeq", "\u2254", Text2EventBMathTranslator
				.translate("bcmeq"));
	}

	public void testBecomesAnElementOf() {
		assertEquals("::", ":\u2208", Text2EventBMathTranslator.translate("::"));
	}

	public void testBecomesAnElementOfLaTeX() {
		assertEquals("bcmin", ":\u2208", Text2EventBMathTranslator
				.translate("bcmin"));
	}

	public void testBecomesSuchThat() {
		assertEquals(":|", ":\u2223", Text2EventBMathTranslator.translate(":|"));
	}

	public void testBecomesSuchThatLaTeX() {
		assertEquals("bcmsuch", ":\u2223", Text2EventBMathTranslator
				.translate("bcmsuch"));
	}

	public void testMid() {
		assertEquals("|", "\u2223", Text2EventBMathTranslator.translate("|"));
	}

	public void testMidLaTeX() {
		assertEquals("mid", "\u2223", Text2EventBMathTranslator
				.translate("mid"));
	}

	public void testOfType() {
		assertEquals("oftype", "\u2982", Text2EventBMathTranslator
				.translate("oftype"));
		assertEquals("oftype ", "\u2982 ", Text2EventBMathTranslator
				.translate("oftype "));
		assertEquals(" oftype", " \u2982", Text2EventBMathTranslator
				.translate(" oftype"));
		assertEquals(" oftype ", " \u2982 ", Text2EventBMathTranslator
				.translate(" oftype "));
	}

	public void testExpLaTeX() {
		assertEquals("expn", "\u005e", Text2EventBMathTranslator
				.translate("expn"));
	}

	public void testForwardCompositionLaTeX() {
		assertEquals("fcomp", "\u003b", Text2EventBMathTranslator
				.translate("fcomp"));
	}
}
