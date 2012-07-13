/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Collections.singletonList;
import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.InjectedDatatypeExtension.injectExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.internal.core.ast.extension.TypeMediator;
import org.eventb.internal.core.ast.extension.datatype.DatatypeTranslator;

/**
 * This test intends to check the additional predicates created by the datatype
 * translator in order to make the datatype translation consistent and provable.
 * 
 * @author "Thomas Muller"
 */
public class TestDatatypeTranslator extends AbstractTests {

	public void testRecordDatatypeTranslation() {
		final IDatatypeExtension record = injectExtension( //
		"Message[U,V] ::= " + //
				" message ; sender[U] ; receiver[U]; identifier[V]");
		final IDatatype datatype = ff.makeDatatype(record);
		final FormulaFactory factory = getInstance(datatype.getExtensions());
		final ParametricType type = mParametric(factory, datatype, "Agent",
				"Identifier");
		final DatatypeTranslator translator = getTranslator(factory, type,
				Collections.<String> emptySet());
		final ITypeEnvironment typeEnv = mTypeEnvironment( //
				"DT_Message", "ℙ(DT_Message)", //
				"Agent", "ℙ(Agent)", //
				"Identifier", "ℙ(Identifier)" //
		);
		// Adding the typing of "message" constructor to type check the
		// following parsed predicates
		checkPredicates(typeEnv, //
				translator.getAPredicates(), //
				"message ∈ Agent × Agent × Identifier ⤖ DT_Message");
		typeEnv.addName("message", getType("ℙ(Agent × Agent × Identifier × DT_Message)"));
		checkPredicates(typeEnv, //
				translator.getCPredicates(), //
				"sender ∈ ran(message) ↠ Agent", //
				"receiver ∈ ran(message) ↠ Agent", //
				"identifier ∈ ran(message) ↠ Identifier");
		// Adding the typing of "sender", "receiver" and "identifier" to type
		// check the following parsed predicates
		typeEnv.addName("sender", getType("ℙ(DT_Message × Agent)"));
		typeEnv.addName("receiver", getType("ℙ(DT_Message × Agent)"));
		typeEnv.addName("identifier", getType("ℙ(DT_Message × Identifier)"));
		checkPredicates(
				typeEnv,//
				translator.getDPredicates(),
				"((sender ⊗ receiver) ⊗ identifier) = message∼");
	}

	public void testRecursiveDatatypeTranslation() {
		final IDatatypeExtension dtExtension = injectExtension(" List[S] ::= nil || cons ; head[S] ; tail[List(S)]");
		final IDatatype datatype = ff.makeDatatype(dtExtension);
		final FormulaFactory factory = getInstance(datatype.getExtensions());
		final ParametricType type = mParametric(factory, datatype, "T");
		final DatatypeTranslator translator = getTranslator(factory, type,
				Collections.<String> emptySet());
		final ITypeEnvironment typeEnv = mTypeEnvironment( //
				"T", "ℙ(T)", //
				"DT_List", "ℙ(DT_List)" //
		);
		checkPredicates(typeEnv, //
				translator.getAPredicates(), //
				"nil ∈ DT_List", //
				"cons ∈ T × DT_List ↣ DT_List");
		// Adding the typing of "cons" and "nil" to type check the following
		// parsed predicates
		typeEnv.addName("cons", getType("ℙ(T × DT_List × DT_List)"));
		typeEnv.addName("nil", getType("DT_List"));
		checkPredicates(typeEnv, //
				singletonList(translator.getBPredicate()), //
				"ran(cons)∪{nil} = DT_List");
		checkPredicates(typeEnv, //
				translator.getCPredicates(), //
				"head  ∈ ran(cons) ↠ T", //
				"tail  ∈ ran(cons) ↠ DT_List");
		// Adding the typing of "head" and "tail" to type check the following
		// parsed predicates
		typeEnv.addName("head", getType("ℙ(DT_List × T)"));
		typeEnv.addName("tail", getType("ℙ(DT_List × DT_List)"));
		checkPredicates(typeEnv, //
				translator.getDPredicates(), //
				"(head ⊗ tail) = cons∼");
	}

	private Type getType(String typeStr) {
		return ff.parseType(typeStr, LATEST).getParsedType();
	}

	private void checkPredicates(ITypeEnvironment environment,
			List<Predicate> predicates, String... expectedPredStrs) {
		final int expectedSize = expectedPredStrs.length;
		assertTrue(expectedSize == predicates.size());
		assertPredicatesAreTypeChecked(predicates);
		for (String expPredStr : expectedPredStrs) {
			final Predicate parsed = ff
					.parsePredicate(expPredStr, LATEST, null)
					.getParsedPredicate();
			final ITypeCheckResult tcResult = parsed.typeCheck(environment);
			assertFalse(tcResult.getProblems().toString(),
					tcResult.hasProblem());
			assertTrue(parsed.toString() + " is not present",
					isMember(parsed, predicates));
		}
	}

	private void assertPredicatesAreTypeChecked(List<Predicate> predicates) {
		for (Predicate pred : predicates) {
			assertTrue("The predicate " + pred.toString()
					+ " is not type checked", pred.isTypeChecked());
		}
	}

	private boolean isMember(Predicate pred, List<Predicate> predicates) {
		for (Predicate p : predicates) {
			if (pred.equals(p))
				return true;
		}
		return false;
	}

	private DatatypeTranslator getTranslator(FormulaFactory factory,
			ParametricType parametricType, Set<String> usedNames) {
		return new DatatypeTranslator(factory, usedNames, parametricType);
	}

	private List<Type> getTypeParams(String[] typeParameters) {
		final TypeMediator mediator = new TypeMediator(ff);
		final List<Type> result = new ArrayList<Type>();
		for (String s : typeParameters) {
			result.add(mediator.makeGivenType(s));
		}
		return result;
	}

	private ParametricType mParametric(FormulaFactory factory,
			IDatatype datatype, String... typeParameters) {
		final List<Type> typeParams = getTypeParams(typeParameters);
		final IExpressionExtension tConstructor = datatype.getTypeConstructor();
		return factory.makeParametricType(typeParams, tConstructor);
	}

}
