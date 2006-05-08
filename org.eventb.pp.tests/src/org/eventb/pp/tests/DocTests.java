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

public class DocTests extends AbstractTranslationTests {
	
	private static void doTransTest(String input, String expected, boolean transformExpected, ITypeEnvironment te) {
		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		if(transformExpected) {
			pexpected = Translator.simplifyPredicate(pexpected, ff);
			pexpected = Translator.reduceToPredicateCalulus(pexpected, ff);
		}
		doTransTest(pinput, pexpected);
	}
	
	private static void doTransTest(Predicate input, Predicate expected) {
		assertTrue("Input is not typed: " + input, input.isTypeChecked());
		assertTrue("Expected result is not typed: " + expected, 
				expected.isTypeChecked());

		Predicate actual = Translator.reduceToPredicateCalulus(input, ff);
		actual = Translator.simplifyPredicate(actual, ff);

		assertTrue("Actual result is not typed: " + actual,
				actual.isTypeChecked());
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	
	private void doDecompTest(String inputString, String expectedString, ITypeEnvironment te) {
		final Predicate input = parse(inputString, te);
		final Predicate expected = parse(expectedString, te);
		final Predicate actual = Translator.decomposeIdentifiers(input, ff);
		assertTrue("Actual result is not typed: " + actual,
				actual.isTypeChecked());
		assertEquals("Wrong identifier decomposition", expected, actual);
	}

	
	public void testDoc1() {
		
		doDecompTest( 	"∀x·10↦(20↦30)=x",
						"∀x,x0,x1·10↦(20↦30)=x↦(x0↦x1)",
						mTypeEnvironment());
	}

	public void testDoc2() {
		
		doDecompTest(	"a=b ∧ a ∈ S",
						"∀x0,x1,x2,x3·(a=x0↦x1 ∧ b = x2 ↦x3)⇒(x0↦x1=x2↦x3 ∧ x0↦x1 ∈ S)",
						mTypeEnvironment(
								mList("a", "b", "S"),
								mList(CPROD(INT, INT), CPROD(INT, INT), REL(INT, INT))));
	}
	
	public void testDoc3() {
		
		doTransTest(	"p⊆S ∧ q⊆S ⇒ (p⊆q ⇔ S∖q ⊆ S∖p)",
						"(∀x·x∈p⇒x∈S)∧(∀y·y∈q⇒y∈S)⇒((∀z·z∈p⇒z∈q)⇔(∀t·t∈S∧¬t∈q⇒t∈S∧¬t∈p))",
						false,
						mTypeEnvironment(
								mList("p", "S", "q"),
								mList(INT_SET, INT_SET, INT_SET)));
	}
	
	public void testDoc4() {
		
		doTransTest(	"u ≠ ∅ ⇒ (∀t·t∈u ⇒ inter(u) ⊆ t)",
						"¬(∀x·¬x∈u) ⇒ (∀t·t∈u ⇒ (∀x·(∀s·s∈u ⇒ x∈s)⇒x∈t))",
						false,
						mTypeEnvironment(
								mList("u", "t"),
								mList(POW(INT_SET), INT_SET)));
	}

	public void testDoc5() {
		
		doTransTest(	"(S ◁ r)∼  =r∼  ▷ S",
						"(∀x,y·y↦x ∈ r ∧ y∈S  ⇒ y↦x ∈ r ∧ y∈S) ∧ (∀x,y·y↦x ∈ r ∧ y∈S ⇒ y↦x ∈ r ∧ y ∈ S )",
						false,
						mTypeEnvironment(
								mList("S", "r"),
								mList(INT_SET, REL(INT, BOOL))));
	}
	
	public void testDoc6() {
		
		doTransTest(	"a ⊆ b ⇒ r[a] ⊆ r[b]",
						"(∀x·x∈a ⇒ x∈b) ⇒ (∀y·(∃z·z∈a ∧ z↦y∈r) ⇒ (∃t·t∈b∧t↦y∈r))",
						false,
						mTypeEnvironment(
								mList("a", "b", "r"),
								mList(INT_SET, INT_SET, REL(INT, BOOL))));
	}
}
