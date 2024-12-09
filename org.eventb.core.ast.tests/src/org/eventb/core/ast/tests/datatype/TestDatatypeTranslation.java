/*******************************************************************************
 * Copyright (c) 2012, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.datatype;

import static org.eventb.core.ast.Formula.BOOL;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.tests.AbstractTranslatorTests;
import org.eventb.internal.core.ast.datatype.DatatypeTranslation;
import org.junit.Test;

/**
 * Class checking special cases of datatypes translation.
 * 
 * @author "Thomas Muller"
 */
public class TestDatatypeTranslation extends AbstractTranslatorTests {

	/**
	 * Datatype in powerset translation
	 */
	@Test 
	public void testDatatypeInPowersetTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addGivenTypes("Elements", "Things");
		s.setExpectedTypeEnvironment(LIST_TYPE_ENV);
		s.assertExprTranslation("ℙ(List(Elements))", "ℙ(List_Type)");
		s.assertExprTranslation("ℙ(List(Things))", "ℙ(List_Type0)");
	}

	/**
	 * Datatype in a product type translation
	 */
	@Test 
	public void testDatatypeInProductSetTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addGivenTypes("Elements", "Things");
		s.setExpectedTypeEnvironment(LIST_TYPE_ENV);
		s.assertExprTranslation("(List(Elements)×List(Things))",
				"List_Type×List_Type0");
		s.assertExprTranslation("(List(Things)×List(Things))",
				"List_Type0×List_Type0");
	}

	/**
	 * Datatype in a product type translation
	 */
	@Test 
	public void testDatatypeIntegerTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addToSourceEnvironment("l=List(ℤ)");
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); cons=ℙ(ℤ × List_Type × List_Type);"
				+ " l=List_Type");
		s.assertExprTranslation("cons(2, l)", "cons(2 ↦ l)");
		s.assertExprTranslation("cons(3, cons(2, l))", "cons(3 ↦ cons(2 ↦ l))");
	}

	/**
	 * Datatype of Boolean type translation
	 */
	@Test 
	public void testDatatypeBoolTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addToSourceEnvironment("l=List(BOOL)");
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); cons=ℙ(BOOL × List_Type × List_Type);"
				+ "nil=List_Type; head=ℙ(List_Type × BOOL); l=List_Type");
		s.assertExprTranslation("head(l)", "head(l)");
		s.assertExprTranslation("head(cons(FALSE, cons(TRUE, nil)))",
				"head(cons(FALSE ↦ (cons(TRUE ↦ nil))))");
	}

	/**
	 * Recursive datatype translation
	 */
	@Test 
	public void testRecursiveDatatypeTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); "
				+ "List=ℙ(ℙ(ℤ)×ℙ(List_Type)); "
				+ "List_Type0=ℙ(List_Type0); "
				+ "List0=ℙ(ℙ(List_Type)×ℙ(List_Type0))");
		s.assertExprTranslation("List(List(ℤ))", "List_Type0");
		s.assertExprTranslation("List(List({1}))", "List0(List({1}))");
	}

	/**
	 * Mixed datatypes translation
	 */
	@Test 
	public void testMixedDatatypesTranslation() {
		final String setsTypenv = "A=ℙ(Agent); I=ℙ(Identifier)";
		final TestTranslationSupport s = mSupport(LIST__DT, MESSAGE__DT);
		s.addGivenTypes("Agent", "Identifier");
		s.addToSourceEnvironment(setsTypenv);
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); "
				+ "List=ℙ(ℙ(Message_Type)×ℙ(List_Type)); "
				+ "Message_type=ℙ(Message_Type); "
				+ "Message=ℙ(ℙ(Agent) × ℙ(Identifier) × ℙ(Message_Type)); " + setsTypenv);
		s.assertExprTranslation("List(Message(Agent, Identifier))", "List_Type");
		s.assertExprTranslation("List(Message(A, I))", "List(Message(A↦I))");
	}

	/**
	 * Mixed datatypes translation
	 */
	@Test 
	public void testMixedDatatypesValuesTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT, MESSAGE__DT);
		s.addGivenTypes("Agent", "Identifier");
		s.addToSourceEnvironment("a=Agent; b=Agent; c=Identifier");
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); "
				+ "List=ℙ(ℙ(Message_Type)×ℙ(List_Type)); " + "nil=List_Type; "
				+ "cons=ℙ(Message_Type×List_Type×List_Type); "
				+ "head=ℙ(List_Type×Message_Type); "
				+ "tail=ℙ(List_Type×List_Type); "
				+ "Message_Type=ℙ(Message_Type); "
				+ "Message=ℙ(ℙ(Agent) × ℙ(Identifier) × ℙ(Message_Type)); "
				+ "message=ℙ(Agent×Agent×Identifier×Message_Type); "
				+ "sender=ℙ(Message_Type×Agent); "
				+ "receiver=ℙ(Message_Type×Agent); "
				+ "identifier=ℙ(Message_Type×Identifier); "
				+ "a=Agent; b=Agent; c=Identifier");
		s.assertExprTranslation("cons(message(a, b, c), nil)",
				"cons(message(a ↦ b ↦ c) ↦ nil)");
		s.assertAxioms(
				"message ∈ Agent × Agent × Identifier ⤖ Message_Type", //
				"sender ∈ ran(message) ↠ Agent", //
				"receiver ∈ ran(message) ↠ Agent", //
				"identifier ∈ ran(message) ↠ Identifier", //
				"((sender ⊗ receiver) ⊗ identifier) = message∼", //
				"Message = (λU↦V· ⊤ ∣ message[U × U × V])", //
				"Message(Agent ↦ Identifier) = Message_Type", //
				"cons ∈ Message_Type × List_Type ↣ List_Type", //
				"head∈ran(cons) ↠ Message_Type", //
				"tail∈ran(cons) ↠ List_Type",//
				"(head ⊗ tail) = cons∼",//
				"partition(List_Type, {nil}, ran(cons))", //
				"List = (λS· ⊤ ∣ (⋂ List ∣ nil ∈ List ∧ cons[S × List] ⊆ List))", //
				"List(Message_Type) = List_Type"); //
	}

	/**
	 * Composed enum and datatypes translation
	 */
	@Test 
	public void testEnumAndDatatypeTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT,
				"Directions ::= North || East || South || West");
		s.setExpectedTypeEnvironment("Directions=ℙ(Directions); "
				+ "List_Type=ℙ(List_Type); List=ℙ(ℙ(ℤ × Directions) × ℙ(List_Type)); "
				+ "cons=ℙ((ℤ×Directions)×List_Type×List_Type); "
				+ "nil=List_Type; head=ℙ(List_Type×(ℤ×Directions)) ;"
				+ " tail=ℙ(List_Type×List_Type)");
		s.assertExprTranslation("cons(1 ↦ West, nil)", "cons(1 ↦ West ↦ nil)");
		s.assertAxioms(
				"partition(Directions, {North}, {East}, {South}, {West})",
				"cons ∈ ℤ × Directions × List_Type ↣ List_Type", //
				"head∈ran(cons) ↠ ℤ × Directions", //
				"tail∈ran(cons) ↠ List_Type",//
				"(head ⊗ tail) = cons∼", //
				"partition(List_Type, {nil}, ran(cons))", //
				"List = (λS· ⊤ ∣ (⋂ List ∣ nil ∈ List ∧ cons[S × List] ⊆ List))", //
				"List(ℤ × Directions) = List_Type");
	}

	/**
	 * Non inductive datatype with set constructor translation
	 */
	@Test 
	public void testNonInductiveDatatypeTranslation() {
		final TestTranslationSupport s = mSupport("Values[S] ::= None || One[one : S] || Many[many : ℙ(S)]");
		s.setExpectedTypeEnvironment("Values_Type=ℙ(Values_Type); Values=ℙ(ℙ(ℤ) × ℙ(Values_Type));"
				+ "None=Values_Type; One=ℙ(ℤ×Values_Type); Many=ℙ(ℙ(ℤ)×Values_Type);"
				+ "one=ℙ(Values_Type×ℤ); many=ℙ(Values_Type×ℙ(ℤ))");
		s.assertExprTranslation("Many({0, 1, 2})", "Many({0, 1, 2})");
		s.assertAxioms(
				"One ∈ ℤ ↣ Values_Type", //
				"one ∈ ran(One) ↠ ℤ", //
				"one = One∼", //
				"Many ∈ ℙ(ℤ) ↣ Values_Type", //
				"many ∈ ran(Many) ↠ ℙ(ℤ)", //
				"many = Many∼", //
				"partition(Values_Type, {None}, ran(One), ran(Many))", //
				"Values = (λS· ⊤ ∣ {None} ∪ One[S] ∪ Many[ℙ(S)])", //
				"Values(ℤ) = Values_Type");
	}

	public static class DatatypeTranslationErrors {

		final TestTranslationSupport s = mSupport(ff, MESSAGE__DT);
		final DatatypeTranslation trans = s.getTranslation();
		final AtomicExpression untyped = mEmptySet(null);
		final AtomicExpression badFactory = LIST_FAC.makeAtomicExpression(BOOL,
				null);
		final Type ty_S = ff.makeGivenType("S");
		final Expression empty_S = mEmptySet(POW(ty_S));

		@Test(expected = IllegalStateException.class)
		public void notTyped() throws Exception {
			assertFalse(untyped.isTypeChecked());
			untyped.translateDatatype(trans);
		}

		@Test(expected = IllegalArgumentException.class)
		public void invalidFactory() throws Exception {
			assertTrue(badFactory.isTypeChecked());
			badFactory.translateDatatype(trans);
		}

		// The given set S is not in the source type environment
		@Test(expected = IllegalArgumentException.class)
		public void notInSourceTypenv() throws Exception {
			assertTrue(empty_S.isTypeChecked());
			empty_S.translateDatatype(trans);
		}

		// The given set S is not in the source type environment and adding it
		// after starting the translation does not work.
		@Test(expected = IllegalArgumentException.class)
		public void notInSourceTypenvModifiedTooLate() throws Exception {
			assertTrue(empty_S.isTypeChecked());
			s.addGivenTypes("S");
			empty_S.translateDatatype(trans);
		}

	}

}
