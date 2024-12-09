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

import static org.eventb.core.ast.tests.FastFactory.mDatatypeFactory;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.tests.AbstractTranslatorTests;
import org.junit.Test;

/**
 * Unit tests to check both datatype translated expressions and axioms created
 * by the datatype translator.
 * <p>
 * Following tests verify 3 types of translation :
 * <ul>
 * <li>a type constructor is well translated, and its axioms are correct</li>
 * <li>a value constructor is well translated</li>
 * <li>a destructor is well translated</li>
 * </ul>
 * Each test checks a same datatype instantiated with different type parameters.
 * Each type of translation is verified for two different datatype extensions
 * (i.e. Message and List).
 * </p>
 * <p>
 * Axiom testing is done only in type constructor tests as it would be redundant
 * to test it in the two other translation types.
 * </p>
 * 
 * @author "Thomas Muller"
 */
public class TestDatatypeTranslator extends AbstractTranslatorTests {

	@Test 
	public void testRecordTypeConstructorTranslation() {
		final String setsTypenv = "A=ℙ(Agent); I=ℙ(Identifier); "
				+ "P=ℙ(Person); S=ℙ(Stamp)";
		final TestTranslationSupport s = mSupport(MESSAGE__DT);
		s.addGivenTypes(MESSAGE_TPARAMS);
		s.addToSourceEnvironment(setsTypenv);
		s.setExpectedTypeEnvironment(MESSAGE_TYPE_ENV + ";" + setsTypenv);
		s.assertExprTranslation("Message(Agent, Identifier)", "Message_Type");
		s.assertExprTranslation("Message(A, I)", "Message(A ↦ I)");
		s.assertExprTranslation("Message(Person, Stamp)", "Message_Type0");
		s.assertExprTranslation("Message(P, S)", "Message0(P ↦ S)");
		s.assertAxioms(
				"message ∈ Agent × Agent × Identifier ⤖ Message_Type", //
				"sender ∈ ran(message) ↠ Agent", //
				"receiver ∈ ran(message) ↠ Agent", //
				"identifier ∈ ran(message) ↠ Identifier", //
				"((sender ⊗ receiver) ⊗ identifier) = message∼",
				"Message = (λU↦V· ⊤ ∣ message[U × U × V])", //
				"Message(Agent ↦ Identifier) = Message_Type", //
				"message0 ∈ Person × Person × Stamp ⤖ Message_Type0", //
				"sender0 ∈ ran(message0) ↠ Person", //
				"receiver0 ∈ ran(message0) ↠ Person", //
				"identifier0 ∈ ran(message0) ↠ Stamp", //
				"((sender0 ⊗ receiver0) ⊗ identifier0) = message0∼", //
				"Message0 = (λU↦V· ⊤ ∣ message0[U × U × V])", //
				"Message0(Person ↦ Stamp) = Message_Type0");
	}

	@Test 
	public void testRecursiveTypeConstructorTranslation() {
		final String setsTypenv = "O=ℙ(Object); T=ℙ(Thing)";
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addGivenTypes(LIST_TPARAMS);
		s.addToSourceEnvironment(setsTypenv);
		s.setExpectedTypeEnvironment(LIST_TYPE_ENV + ";" + setsTypenv);
		s.assertExprTranslation("List(Object)", "List_Type");
		s.assertExprTranslation("List(O)", "List(O)");
		s.assertExprTranslation("List(Thing)", "List_Type0");
		s.assertExprTranslation("List(T)", "List0(T)");
		s.assertAxioms(
				"cons ∈ Object × List_Type ↣ List_Type", //
				"head∈ran(cons) ↠ Object", //
				"tail∈ran(cons) ↠ List_Type", //
				"(head ⊗ tail) = cons∼", //
				"partition(List_Type, {nil}, ran(cons))",
				"List = (λS· ⊤ ∣ (⋂ List ∣ nil ∈ List ∧ cons[S × List] ⊆ List))", //
				"List(Object) = List_Type", //
				"cons0 ∈ Thing × List_Type0 ↣ List_Type0", //
				"head0∈ran(cons0) ↠ Thing", //
				"tail0∈ran(cons0) ↠ List_Type0", //
				"(head0 ⊗ tail0) = cons0∼", //
				"partition(List_Type0, {nil0}, ran(cons0))",
				"List0 = (λS· ⊤ ∣ (⋂ List ∣ nil0 ∈ List ∧ cons0[S × List] ⊆ List))", //
				"List0(Thing) = List_Type0");
	}

	@Test 
	public void testRecordConstructorTranslation() {
		final TestTranslationSupport s = mSupport(MESSAGE__DT);
		s.addGivenTypes(MESSAGE_TPARAMS);
		s.addToSourceEnvironment("a=Agent; b=Agent; c=Identifier"
				+ "; e=Person; f=Person; g=Stamp");
		s.setExpectedTypeEnvironment(MESSAGE_TYPE_ENV);
		s.assertExprTranslation("message(a,b,c)", "message(a ↦ b ↦ c)");
		s.assertExprTranslation("message(e,f,g)", "message0(e ↦ f ↦ g)");
	}

	@Test 
	public void testRecursiveConstructorTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addGivenTypes(LIST_TPARAMS);
		s.addToSourceEnvironment("obj=Object; l=List(Object)"
				+ "; thg=Thing; lt=List(Thing)");
		s.setExpectedTypeEnvironment(LIST_TYPE_ENV
				+ "; obj=Object; l=List_Type; thg=Thing; lt=List_Type0");
		s.assertExprTranslation("cons(obj, l)", "cons(obj ↦ l)");
		s.assertExprTranslation("cons(thg, lt)", "cons0(thg ↦ lt)");
	}

	@Test 
	public void testRecordDestructorTranslation() {
		final TestTranslationSupport s = mSupport(MESSAGE__DT);
		s.addGivenTypes(MESSAGE_TPARAMS);
		s.addToSourceEnvironment("d=Message(Agent, Identifier)"
				+ "; h=Message(Person, Stamp)");
		s.setExpectedTypeEnvironment(MESSAGE_TYPE_ENV
				+ "; d=Message_Type; h=Message_Type0");
		s.assertExprTranslation("sender(d)", "sender(d)");
		s.assertExprTranslation("sender(h)", "sender0(h)");
	}

	@Test 
	public void testRecursiveDestructorTranslation() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addGivenTypes(LIST_TPARAMS);
		s.addToSourceEnvironment("l=List(Object); lt=List(Thing)");
		s.setExpectedTypeEnvironment(LIST_TYPE_ENV
				+ "; l=List_Type; lt=List_Type0");
		s.assertExprTranslation("head(l)", "head(l)");
		s.assertExprTranslation("head(lt)", "head0(lt)");
	}

	/**
	 * Ensures that a datatype that occurs in an argument type of another
	 * datatype is correctly translated.
	 * 
	 * <pre>
	 *    A(ℤ)    --> A_Type
	 *    A(B(ℤ)) --> A_Type0
	 *    B(ℤ)    --> B_Type
	 * </pre>
	 */
	@Test 
	public void testDatatypeInDatatype() {
		final FormulaFactory fac = mDatatypeFactory(ff, "A[T] ::= a[d: T]");
		final TestTranslationSupport s = new TestTranslationSupport(fac,
				"B[U] ::= b[e: A(U)]");
		s.addToSourceEnvironment("x=A(B(ℤ))");
		s.setExpectedTypeEnvironment(""
				+ "A_Type=ℙ(A_Type); B_Type=ℙ(B_Type); A_Type0=ℙ(A_Type0); "
				+ "A=ℙ(ℙ(ℤ)×ℙ(A_Type)); B=ℙ(ℙ(ℤ)×ℙ(B_Type)); A0=ℙ(ℙ(B_Type)×ℙ(A_Type0)); "
				+ "a=ℙ(ℤ×A_Type); b=ℙ(A_Type×B_Type); a0=ℙ(B_Type×A_Type0); "
				+ "d=ℙ(A_Type×ℤ); e=ℙ(B_Type×A_Type); d0=ℙ(A_Type0×B_Type); "
				+ "x=A_Type0");
		s.assertPredTranslation("x ∈ A(B(ℤ))", "x ∈ A_Type0");
		s.assertPredTranslation("x ∈ A(B(1‥2))", "x ∈ A0(B(1‥2))");
		s.assertPredTranslation("a(1) ∈ A(ℤ)", "a(1) ∈ A_Type");
		s.assertPredTranslation("a(1) ∈ A(1‥2)", "a(1) ∈ A(1‥2)");
		s.assertPredTranslation("b(a(1)) ∈ B(ℤ)", "b(a(1)) ∈ B_Type");
		s.assertPredTranslation("b(a(1)) ∈ B(1‥2)", "b(a(1)) ∈ B(1‥2)");
		s.assertPredTranslation("d(x) ∈ B(ℤ)", "d0(x) ∈ B_Type");
		s.assertPredTranslation("d(x) ∈ B(1‥2)", "d0(x) ∈ B(1‥2)");
		s.assertPredTranslation("e(d(x)) ∈ A(ℤ)", "e(d0(x)) ∈ A_Type");
		s.assertPredTranslation("e(d(x)) ∈ A(1‥2)", "e(d0(x)) ∈ A(1‥2)");
		s.assertPredTranslation("d(e(d(x))) ∈ ℤ", "d(e(d0(x))) ∈ ℤ");
		s.assertPredTranslation("d(e(d(x))) ∈ 1‥2", "d(e(d0(x))) ∈ 1‥2");
		s.assertAxioms(//
				"a ∈ ℤ ⤖ A_Type", //
				"d ∈ ran(a) ↠ ℤ", //
				"d = a∼", //
				"A = (λT⦂ℙ(ℤ)· ⊤ ∣ a[T])", //
				"A(ℤ) = A_Type", //
				"b ∈ A_Type ⤖ B_Type", //
				"e ∈ ran(b) ↠ A_Type", //
				"e = b∼", //
				"B = (λU⦂ℙ(ℤ)· ⊤ ∣ b[A(U)])", //
				"B(ℤ) = B_Type", //
				"a0 ∈ B_Type ⤖ A_Type0", //
				"d0 ∈ ran(a0) ↠ B_Type", //
				"d0 = a0∼", //
				"A0 = (λT⦂ℙ(B_Type)· ⊤ ∣ a0[T])", //
				"A0(B_Type) = A_Type0");
	}

	/**
	 * Ensures that unnamed arguments give rise to the creation of a fresh name
	 * to represent them.
	 */
	@Test
	public void testUnnamedArgumentTranslation() {
		final TestTranslationSupport s = mSupport("Unnamed[S] ::= cons[S;S]");
		s.addGivenTypes("Object");
		final String setsTypenv = "O=ℙ(Object); d=Object; d1=Object";
		s.addToSourceEnvironment(setsTypenv);
		s.setExpectedTypeEnvironment("Unnamed_Type=ℙ(Unnamed_Type); "
				+ "cons=ℙ(Object×Object×Unnamed_Type); " //
				+ "d0=ℙ(Unnamed_Type×Object); " //
				+ "d2=ℙ(Unnamed_Type×Object); " //
				+ "Unnamed=ℙ(ℙ(Object)×ℙ(Unnamed_Type));" + setsTypenv);
		s.assertExprTranslation("Unnamed(Object)", "Unnamed_Type");
		s.assertExprTranslation("Unnamed(O)", "Unnamed(O)");
		s.assertAxioms(//
				"cons ∈ Object×Object ⤖ Unnamed_Type", //
				"d0 ∈ ran(cons) ↠ Object", //
				"d2 ∈ ran(cons) ↠ Object", //
				"(d0 ⊗ d2) = cons∼", //
				"Unnamed = (λS· ⊤ ∣ cons[S×S])", //
				"Unnamed(Object) = Unnamed_Type");
	}

}