/*******************************************************************************
 * Copyright (c) 2008, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation of RodinTests
 *     Systerel - created this class from RodinTests + some refactoring
 *     Systerel - mathematical language V2
 *     Systerel - clean up while adapting to XProver v2 API
 *     Systerel - used simple sequents
 *******************************************************************************/
package org.eventb.pp;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.internal.pp.CancellationChecker;
import org.eventb.internal.pp.PPTranslator;

/**
 * Implements a typed sequent for use in tests.
 */
public class TestSequent {

	private static final CancellationChecker NO_CHECKER = CancellationChecker
			.newChecker(null);

	private static List<Predicate> parseHypotheses(Iterable<String> hypotheses, FormulaFactory ff) {
		final List<Predicate> result = new ArrayList<Predicate>();
		for (final String string : hypotheses) {
			result.add(parsePredicate(string, ff));
		}
		return result;
	}

	private static Predicate parsePredicate(String pred, FormulaFactory ff) {
		final IParseResult result = ff.parsePredicate(pred, V2, null);
		assertFalse(pred, result.hasProblem());
		return result.getParsedPredicate();
	}

	public static ITypeEnvironmentBuilder parseTypeEnvironment(List<String> typenvList, FormulaFactory ff) {
		final ITypeEnvironmentBuilder result = ff.makeTypeEnvironment();
		for (int i = 0; i < typenvList.size(); i = i + 2) {
			String name = typenvList.get(i);
			String type = typenvList.get(i + 1);
			result.addName(name, ff.parseType(type, V2).getParsedType());
		}
		return result;
	}

	public static ISimpleSequent makeSequent(List<String> typenvList, Iterable<String> hypotheses,
				String goal, FormulaFactory ff) {
		final ITypeEnvironment tenv = parseTypeEnvironment(typenvList, ff);
		return makeSequent(tenv, hypotheses, goal, ff);
	}

	public static ISimpleSequent makeSequent(ITypeEnvironment initTypeEnv,
			Iterable<String> hypotheses, String goal, FormulaFactory ff) {
		final List<Predicate> pHyps = parseHypotheses(hypotheses, ff);
		final Predicate pGoal = parsePredicate(goal, ff);
		final ITypeEnvironmentBuilder typeEnv = initTypeEnv.makeBuilder();
		for (Predicate hyp : pHyps) {
			typeCheck(hyp, typeEnv);
		}
		typeCheck(pGoal, typeEnv);
		
		return SimpleSequents.make(pHyps, pGoal, ff);
	}
	
	private static void typeCheck(Predicate predicate, ITypeEnvironmentBuilder typEnv) {
		final ITypeCheckResult result = predicate.typeCheck(typEnv);
		assertTrue(predicate + " " + result.toString(), result.isSuccess());
		typEnv.addAll(result.getInferredEnvironment());
	}

	
	private final ISimpleSequent sequent;

	public TestSequent(ISimpleSequent sequent) {
		this.sequent = sequent;
	}
	
	public TestSequent(List<String> typenvList, Iterable<String> hypotheses,
				String goal, FormulaFactory ff) {
		this(makeSequent(typenvList, hypotheses, goal, ff));
	}
	
	public ITypeEnvironment typeEnvironment() {
		return sequent.getTypeEnvironment();
	}

	public void assertTranslatedSequentOf(ISimpleSequent input) {
		final ISimpleSequent actual = PPTranslator.translate(input, NO_CHECKER);
		assertEquals(sequent, actual);
	}

	public ISimpleSequent getSimpleSequent() {
		return sequent;
	}

}
