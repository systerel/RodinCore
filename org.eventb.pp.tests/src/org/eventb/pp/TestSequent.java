/*******************************************************************************
 * Copyright (c) 2008, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation of RodinTests
 *     Systerel - created this class from RodinTests + some refactoring
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.pp;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.internal.pp.core.elements.terms.AbstractPPTest.ff;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.sequent.InputPredicate;
import org.eventb.internal.pp.sequent.InputSequent;

/**
 * Implements a typed sequent for use in tests.
 */
public class TestSequent {

	private static List<Predicate> parseHypotheses(Iterable<String> hypotheses) {
		final List<Predicate> result = new ArrayList<Predicate>();
		for (final String string : hypotheses) {
			result.add(parsePredicate(string));
		}
		return result;
	}

	private static Predicate parsePredicate(String pred) {
		final IParseResult result = ff.parsePredicate(pred, V2, null);
		assertFalse(pred, result.hasProblem());
		return result.getParsedPredicate();
	}

	public static ITypeEnvironment parseTypeEnvironment(String... pairs) {
		return parseTypeEnvironment(Arrays.asList(pairs));
	}

	public static ITypeEnvironment parseTypeEnvironment(List<String> typenvList) {
		final ITypeEnvironment result = ff.makeTypeEnvironment();
		for (int i = 0; i < typenvList.size(); i = i + 2) {
			String name = typenvList.get(i);
			String type = typenvList.get(i + 1);
			result.addName(name, ff.parseType(type, V2).getParsedType());
		}
		return result;
	}

	private final ITypeEnvironment typenv;
	private final List<Predicate> hypotheses;
	private final Predicate goal;

	public TestSequent(ITypeEnvironment typeEnvironment,
			Iterable<String> hypotheses, String goal) {
		this.typenv = typeEnvironment.clone();
		this.hypotheses = parseHypotheses(hypotheses);
		this.goal = parsePredicate(goal);
		typeCheck();
	}

	public TestSequent(List<String> typenvList, Iterable<String> hypotheses,
			String goal) {
		this(parseTypeEnvironment(typenvList), hypotheses, goal);
	}

	private void typeCheck() {
		for (Predicate pred : hypotheses) {
			typeCheck(pred);
		}
		typeCheck(goal);
	}

	private void typeCheck(Predicate predicate) {
		final ITypeCheckResult result = predicate.typeCheck(typenv);
		assertTrue(predicate + " " + result.toString(), result.isSuccess());
		typenv.addAll(result.getInferredEnvironment());
	}

	public ITypeEnvironment typeEnvironment() {
		return typenv;
	}

	public List<Predicate> hypotheses() {
		return hypotheses;
	}

	public Predicate goal() {
		return goal;
	}

	public void assertTranslatedSequentOf(InputSequent is) {
		assertEquals(typenv, is.typeEnvironment());
		final InputPredicate[] preds = is.getPredicates();
		int i = 0;
		for (final Predicate hyp: hypotheses()) {
			assertEquals(hyp, preds[i++].translatedPredicate());
		}
		assertEquals(goal, preds[i].translatedPredicate());
	}

}
