/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.pp.tests;

import static org.eventb.pp.tests.FastFactory.mList;
import static org.eventb.pp.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pp.Translator;

public class ReorganisationTests extends AbstractTranslationTests {
	
	private static final ITypeEnvironment defaultTe;
	static {
		defaultTe = ff.makeTypeEnvironment();
		defaultTe.addGivenSet("S");
		defaultTe.addGivenSet("T");
		defaultTe.addGivenSet("U");
		defaultTe.addGivenSet("V");
	}

	private static void doTest(String input, String expected,
			boolean transformExpected, ITypeEnvironment te) {

		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		if (transformExpected) {
			pexpected = Translator.reduceToPredicateCalulus(pexpected, ff);
		}
		doTest(pinput, pexpected);
	}
	
	private static void doTest(Predicate input, Predicate expected) {
		assertTypeChecked(input);
		assertTypeChecked(expected);

		Predicate actual = Translator.reduceToPredicateCalulus(input, ff);

		assertTypeChecked(actual);
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	
	public void testFuncExtraction() {
		
		doTest( "1 < f(10)",
				"∀x·(∃y·y=10∧y↦x∈f) ⇒ 1<x",
				false,
				mTypeEnvironment());
	}
	
	public void testFuncExtraction2() {
		
		doTest( "1 < f(10+f(20))",
				"1 < f(10+f(20))",
				true,
				mTypeEnvironment());
	}
	
	public void testFuncExtraction3() {
		
		doTest( "f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20)))))))))) < f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20))))))))))",
				"f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20)))))))))) < f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20))))))))))",
				true,
				mTypeEnvironment());
	}
	
	public void testCardinality2() {
		
		doTest( "card({1}) > 1",
				"∀x·x=card({1})⇒x>1", 
				true,
				mTypeEnvironment());
	}
	
	public void testCardinality3() {
		
		doTest( "card({card({1})}) = card({2})",
				"card({card({1})}) = card({2})", 
				true,
				mTypeEnvironment());
	}
	
	public void testCardinality4() {
		
		doTest( "∀s·card(S)>card({t·t>s∧t<card({t,t})∣t})",
				"∀s·card(S)>card({t·t>s∧t<card({t,t})∣t})", 
				true, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
	}
	
	public void testComplex() {
		doTest( "card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)}) < 20",
				"∀x·x=card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)})⇒x<20", 
				true, 
				mTypeEnvironment(mList("r", "f"), mList(REL(INT_SET, INT), REL(INT, INT))));
	}
	
	public void testComplex2() {
		doTest( "card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)}) < 20",
				"∀x·x=card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)})⇒x<20", 
				true, 
				mTypeEnvironment(mList("r", "f"), mList(REL(INT_SET, INT), REL(INT, INT))));

	}
	
	public void testComplex3() {
		doTest( "card({1,2}) + f(20) + min({1,2,3}) + max({2,1}) > 2",
				"∀x4,x3,x2,x1·x1=card({1,2}) ∧ x2=f(20) ∧ x3=min({1,2,3}) ∧ x4=max({2,1}) ⇒ x1+x2+x3+x4 > 2", 
				true, 
				mTypeEnvironment(mList("r", "f"), mList(REL(INT_SET, INT), REL(INT, INT))));

	}

	/**
	 * Ensures that no extraction is performed when it's not needed.
	 */
	public void testMin0() {
		doTest( "x=min(S)",
				"x∈S ∧ (∀y·y∈S ⇒ x≤y)", 
				false, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
		doTest( "x≤min(S)",
				"∀y·y∈S ⇒ x≤y", 
				false, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
		doTest( "x+1≤min(S)",
				"∀y·y∈S ⇒ x+1≤y", 
				false, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
	}
	
	/**
	 * Ensures that an extraction is performed when needed.
	 */
	public void testMin1() {
		doTest( "x+1=min(S)",
				"∀y·y∈S ∧ (∀z·z∈S ⇒ y≤z) ⇒ x+1=y", 
				false, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
	}
	
	/**
	 * Ensures that extraction is compatible with bound identifiers.
	 */
	public void testMin2() {
		doTest( "∃x·x+1=min(S)",
				"∃x·∀y·y∈S ∧ (∀z·z∈S ⇒ y≤z) ⇒ x+1=y", 
				false, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
		doTest( "∃x·min(S)=x+1",
				"∃x·∀y·y∈S ∧ (∀z·z∈S ⇒ y≤z) ⇒ y=x+1", 
				false, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
		doTest( "∃x·min(S)≤x+1",
				"∃x·∃y·y∈S ∧ y≤x+1", 
				false, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
	}
	
}
