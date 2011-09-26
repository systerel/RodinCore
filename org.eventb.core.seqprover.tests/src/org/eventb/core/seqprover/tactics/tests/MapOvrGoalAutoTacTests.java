/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.*;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertFailure;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertSuccess;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertTacticRegistered;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.mapOvrG;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit Tests for the auto-tactic MapOvrGoalAutoTac.
 * 
 * @author Emmanuel Billaud
 */
public class MapOvrGoalAutoTacTests {

	private static final String TAC_ID = "org.eventb.core.seqprover.mapOvrGoalTac";
	private static final ITactic TAC = new AutoTactics.MapOvrGoalAutoTac();

	private static final String goalString = "f{x↦y}∈A";
	private static final String hypString = "f∈A";
	private static final String stringB = "B";

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		assertTacticRegistered(TAC_ID, TAC);
	}

	@Test
	public void apply() {
		final ITypeEnvironment typeEnv = genTypeEnv("f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), x=ℤ, y=ℤ");
		assertSucceeded(typeEnv, rel, rel);
		assertSucceeded(typeEnv, rel, srel);
		assertSucceeded(typeEnv, rel, trel);
		assertSucceeded(typeEnv, rel, strel);
		assertSucceeded(typeEnv, rel, pfun);
		assertSucceeded(typeEnv, rel, tfun);
		assertSucceeded(typeEnv, rel, pinj);
		assertSucceeded(typeEnv, rel, tinj);
		assertSucceeded(typeEnv, rel, psur);
		assertSucceeded(typeEnv, rel, tsur);
		assertSucceeded(typeEnv, rel, tbij);
		assertSucceeded(typeEnv, trel, trel);
		assertSucceeded(typeEnv, trel, strel);
		assertSucceeded(typeEnv, trel, tfun);
		assertSucceeded(typeEnv, trel, tinj);
		assertSucceeded(typeEnv, trel, tsur);
		assertSucceeded(typeEnv, trel, tbij);
		assertSucceeded(typeEnv, pfun, pfun);
		assertSucceeded(typeEnv, pfun, tfun);
		assertSucceeded(typeEnv, pfun, pinj);
		assertSucceeded(typeEnv, pfun, tinj);
		assertSucceeded(typeEnv, pfun, psur);
		assertSucceeded(typeEnv, pfun, tsur);
		assertSucceeded(typeEnv, pfun, tbij);
		assertSucceeded(typeEnv, tfun, tfun);
		assertSucceeded(typeEnv, tfun, tinj);
		assertSucceeded(typeEnv, tfun, tsur);
		assertSucceeded(typeEnv, tfun, tbij);
	}

	private void assertSucceeded(final ITypeEnvironment typeEnv,
			final String opGoal, final String opHyp) {
		final Predicate goal = genPred(typeEnv, goalString + opGoal + stringB);
		final Predicate hyp = genPred(typeEnv, hypString + opHyp + stringB);
		final IProverSequent sequent = genSeq(hyp, goal);
		assertSuccess(sequent, mapOvrG(hyp, empty, empty), TAC);
	}

	@Test
	public void fail() {
		final ITypeEnvironment typeEnv = genTypeEnv("f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), x=ℤ, y=ℤ");
		// Goal is not an Inclusion
		assertFailed(typeEnv, "f{x ↦ y}∉A → B");
		// Left member is not an Overriding
		assertFailed(typeEnv, "f∖{x ↦ y}∈A → B");
		// The function is not override by a singleton
		assertFailed(typeEnv, "fg∈A → B");
		// The singleton is not a mapplet
		assertFailed(typeEnv, "f{a}∈A → B");
		assertFailedOnTypeRelation(typeEnv, srel, srel);
		assertFailedOnTypeRelation(typeEnv, strel, strel);
		assertFailedOnTypeRelation(typeEnv, pinj, pinj);
		assertFailedOnTypeRelation(typeEnv, tinj, tinj);
		assertFailedOnTypeRelation(typeEnv, psur, psur);
		assertFailedOnTypeRelation(typeEnv, tsur, tsur);
		assertFailedOnTypeRelation(typeEnv, tbij, tbij);
		assertFailedOnTypeRelation(typeEnv, trel, rel);
		assertFailedOnTypeRelation(typeEnv, trel, srel);
		assertFailedOnTypeRelation(typeEnv, trel, pfun);
		assertFailedOnTypeRelation(typeEnv, trel, pinj);
		assertFailedOnTypeRelation(typeEnv, trel, psur);
		assertFailedOnTypeRelation(typeEnv, pfun, rel);
		assertFailedOnTypeRelation(typeEnv, pfun, trel);
		assertFailedOnTypeRelation(typeEnv, pfun, srel);
		assertFailedOnTypeRelation(typeEnv, pfun, strel);
		assertFailedOnTypeRelation(typeEnv, tfun, rel);
		assertFailedOnTypeRelation(typeEnv, tfun, trel);
		assertFailedOnTypeRelation(typeEnv, tfun, srel);
		assertFailedOnTypeRelation(typeEnv, tfun, strel);
		assertFailedOnTypeRelation(typeEnv, tfun, pfun);
		assertFailedOnTypeRelation(typeEnv, tfun, pinj);
		assertFailedOnTypeRelation(typeEnv, tfun, psur);
	}

	private void assertFailedOnTypeRelation(final ITypeEnvironment typeEnv,
			final String opGoal, final String opHyp) {
		final Predicate goal = genPred(typeEnv, goalString + opGoal + stringB);
		final Predicate hyp = genPred(typeEnv, hypString + opHyp + stringB);
		final IProverSequent sequent = genSeq(hyp, goal);
		assertFailure(sequent, TAC);
	}

	private void assertFailed(final ITypeEnvironment typeEnv,
			final String goalStr) {
		final Predicate goal = genPred(typeEnv, goalStr);
		final IProverSequent sequent = genSeq(goal);
		assertFailure(sequent, TAC);
	}
}
