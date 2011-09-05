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

import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertFailure;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertSuccess;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertTacticRegistered;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.mapOvrG;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;

import java.util.Collections;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
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
		// Basic test : REL -- REL
		assertSucceeded(typeEnv, "↔", "↔");
		// Basic test : REL -- SREL
		assertSucceeded(typeEnv, "↔", "");
		// Basic test : REL -- TREL
		assertSucceeded(typeEnv, "↔", "");
		// Basic test : REL -- STREL
		assertSucceeded(typeEnv, "↔", "");
		// Basic test : REL -- PFUN
		assertSucceeded(typeEnv, "↔", "⇸");
		// Basic test : REL -- TFUN
		assertSucceeded(typeEnv, "↔", "→");
		// Basic test : REL -- PINJ
		assertSucceeded(typeEnv, "↔", "⤔");
		// Basic test : REL -- TINJ
		assertSucceeded(typeEnv, "↔", "↣");
		// Basic test : REL -- PSUR
		assertSucceeded(typeEnv, "↔", "⤀");
		// Basic test : REL -- TSUR
		assertSucceeded(typeEnv, "↔", "↠");
		// Basic test : REL -- TBIJ
		assertSucceeded(typeEnv, "↔", "⤖");
		// Basic test : TREL -- TREL
		assertSucceeded(typeEnv, "", "");
		// Basic test : TREL -- STREL
		assertSucceeded(typeEnv, "", "");
		// Basic test : TREL -- TFUN
		assertSucceeded(typeEnv, "", "→");
		// Basic test : TREL -- TINJ
		assertSucceeded(typeEnv, "", "↣");
		// Basic test : TREL -- TSUR
		assertSucceeded(typeEnv, "", "↠");
		// Basic test : TREL -- TBIJ
		assertSucceeded(typeEnv, "", "⤖");
		// Basic test : PFUN -- PFUN
		assertSucceeded(typeEnv, "⇸", "⇸");
		// Basic test : PFUN -- TFUN
		assertSucceeded(typeEnv, "⇸", "→");
		// Basic test : PFUN -- PINJ
		assertSucceeded(typeEnv, "⇸", "⤔");
		// Basic test : PFUN -- TINJ
		assertSucceeded(typeEnv, "⇸", "↣");
		// Basic test : PFUN -- PSUR
		assertSucceeded(typeEnv, "⇸", "⤀");
		// Basic test : PFUN -- TSUR
		assertSucceeded(typeEnv, "⇸", "↠");
		// Basic test : PFUN -- TBIJ
		assertSucceeded(typeEnv, "⇸", "↠");
		// Basic test : TFUN -- TFUN
		assertSucceeded(typeEnv, "→", "→");
		// Basic test : TFUN -- TINJ
		assertSucceeded(typeEnv, "→", "↣");
		// Basic test : TFUN -- TSUR
		assertSucceeded(typeEnv, "→", "↠");
		// Basic test : TFUN -- TBIJ
		assertSucceeded(typeEnv, "→", "↠");
	}

	private void assertSucceeded(final ITypeEnvironment typeEnv,
			final String opGoal, final String opHyp) {
		final Predicate goal = genPred(typeEnv, goalString + opGoal + stringB);
		final Predicate hyp = genPred(typeEnv, hypString + opHyp + stringB);
		final IProverSequent sequent = ProverFactory.makeSequent(typeEnv,
				Collections.singleton(hyp), goal);
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
		// Fails on type relation : SREL
		assertFailedOnTypeRelation(typeEnv, "", "");
		// Fails on type relation : STREL
		assertFailedOnTypeRelation(typeEnv, "", "");
		// Fails on type relation : PINJ
		assertFailedOnTypeRelation(typeEnv, "⤔", "⤔");
		// Fails on type relation : TINJ
		assertFailedOnTypeRelation(typeEnv, "↣", "↣");
		// Fails on type relation : PSUR
		assertFailedOnTypeRelation(typeEnv, "⤀", "⤀");
		// Fails on type relation : TSUR
		assertFailedOnTypeRelation(typeEnv, "↠", "↠");
		// Fails on type relation : TBIJ
		assertFailedOnTypeRelation(typeEnv, "⤖", "⤖");
		// Fails on type relation : TREL -- REL
		assertFailedOnTypeRelation(typeEnv, "", "↔");
		// Fails on type relation : TREL -- SREL
		assertFailedOnTypeRelation(typeEnv, "", "");
		// Fails on type relation : TREL -- PFUN
		assertFailedOnTypeRelation(typeEnv, "", "⇸");
		// Fails on type relation : TREL -- PINJ
		assertFailedOnTypeRelation(typeEnv, "", "⤔");
		// Fails on type relation : TREL -- PSUR
		assertFailedOnTypeRelation(typeEnv, "", "⤀");
		// Fails on type relation : PFUN -- REL
		assertFailedOnTypeRelation(typeEnv, "⇸", "↔");
		// Fails on type relation : PFUN -- TREL
		assertFailedOnTypeRelation(typeEnv, "⇸", "");
		// Fails on type relation : PFUN -- SREL
		assertFailedOnTypeRelation(typeEnv, "⇸", "");
		// Fails on type relation : PFUN -- STREL
		assertFailedOnTypeRelation(typeEnv, "⇸", "");
		// Fails on type relation : TFUN -- REL
		assertFailedOnTypeRelation(typeEnv, "→", "↔");
		// Fails on type relation : TFUN -- TREL
		assertFailedOnTypeRelation(typeEnv, "→", "");
		// Fails on type relation : TFUN -- SREL
		assertFailedOnTypeRelation(typeEnv, "→", "");
		// Fails on type relation : TFUN -- STREL
		assertFailedOnTypeRelation(typeEnv, "→", "");
		// Fails on type relation : TFUN -- PFUN
		assertFailedOnTypeRelation(typeEnv, "→", "⇸");
		// Fails on type relation : TFUN -- PINJ
		assertFailedOnTypeRelation(typeEnv, "→", "⤔");
		// Fails on type relation : TFUN -- PSUR
		assertFailedOnTypeRelation(typeEnv, "→", "⤀");
	}

	private void assertFailedOnTypeRelation(final ITypeEnvironment typeEnv,
			final String opGoal, final String opHyp) {
		final Predicate goal = genPred(typeEnv, goalString + opGoal + stringB);
		final Predicate hyp = genPred(typeEnv, hypString + opHyp + stringB);
		final IProverSequent sequent = ProverFactory.makeSequent(typeEnv,
				Collections.singleton(hyp), goal);
		assertFailure(sequent, TAC);
	}

	private void assertFailed(final ITypeEnvironment typeEnv,
			final String goalStr) {
		final Predicate goal = genPred(typeEnv, goalStr);
		final Predicate hyp = genPred("⊤");
		final IProverSequent sequent = ProverFactory.makeSequent(typeEnv,
				Collections.singleton(hyp), goal);
		assertFailure(sequent, TAC);
	}
}
