/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - remove formula files
 *******************************************************************************/
package org.eventb.rubin.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;
import org.eventb.rubin.Sequent;

public class AbstractPPTests {

	public static final String PLUGIN_ID = "org.eventb.rubin.tests";

	public static final FormulaFactory ff = FormulaFactory.getDefault();

	private static final boolean PERF = false;

	protected final ITypeEnvironment typeCheck(Sequent sequent) {
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		for (final Predicate hyp : sequent.getHypotheses()) {
			typeCheck(hyp, typenv);
		}
		typeCheck(sequent.getGoal(), typenv);
		return typenv;
	}

	/**
	 * TypeChecks the given predicate, augmenting the given type environment
	 * with the inferred types.
	 * 
	 * @param pred
	 *            the predicate to typeCheck
	 * @param typenv
	 *            initial type environment. Will be extended with inferred types
	 */
	protected final void typeCheck(Predicate pred,
			ITypeEnvironmentBuilder typenv) {
		final ITypeCheckResult result = pred.typeCheck(typenv);
		assertTrue("TypeChecker failed on predicate " + pred,
				result.isSuccess());
		typenv.addAll(result.getInferredEnvironment());
		assertTrue("PredicateFormula should be type-checked",
				pred.isTypeChecked());
	}

	protected static interface IProblemFilter {
		IProblem filter(IProblem problem);
	}

	protected void testProblems(IProblemFilter filter, int maxSteps,
			IProblem[] problems) {
		for (final IProblem problem : problems) {
			final IProblem toRun = filter.filter(problem);
			if (toRun != null) {
				testProblem(maxSteps, toRun);
			}
		}
	}

	private void testProblem(int maxSteps, IProblem problem) {
		final String name = problem.name();
		final Sequent sequent = problem.sequent();
		final long start, end;
		if (PERF) {
			start = System.currentTimeMillis();
			System.out.println("-------------------");
			System.out.println("Proving: " + name);
		}

		typeCheck(sequent);
		final ISimpleSequent ss = SimpleSequents.make(sequent.getHypotheses(),
				sequent.getGoal(), ff);
		final PPProof proof = new PPProof(ss, null);
		proof.translate();
		proof.load();
		proof.prove(maxSteps);
		final PPResult ppr = proof.getResult();

		switch (problem.status()) {
		case VALID:
		case VALIDPPFAILS:
			assertEquals("Should have succeeded" + sequent, Result.valid,
					ppr.getResult());
			break;
		case INVALID:
			assertFalse("Should have failed" + sequent,
					ppr.getResult().equals(Result.valid));
			break;
		default:
			fail("Invalid status for problem " + name);
			break;
		}
		if (PERF) {
			end = System.currentTimeMillis();
			System.out.println("Time: " + (end - start) + " ms");
		}
	}

}
