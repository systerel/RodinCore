/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.pfun;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.pinj;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.psur;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.rel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.srel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.strel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tbij;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tfun;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tinj;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.trel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tsur;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.mapOvrG;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit Tests for the auto-tactic MapOvrGoalAutoTac.
 * 
 * @author Emmanuel Billaud
 */
public class MapOvrGoalAutoTacTests extends AbstractTacticTests {

	public MapOvrGoalAutoTacTests() {
		super(new AutoTactics.MapOvrGoalAutoTac(),
				"org.eventb.core.seqprover.mapOvrGoalTac");
	}

	@Test
	public void assertSuccessfulApplication() {
		addToTypeEnvironment("f=ℤ↔ℤ; A=ℙ(ℤ); B=ℙ(ℤ); x=ℤ; y=ℤ");
		assertSucceeded(rel, rel);
		assertSucceeded(srel, rel);
		assertSucceeded(trel, rel);
		assertSucceeded(strel, rel);
		assertSucceeded(pfun, rel);
		assertSucceeded(tfun, rel);
		assertSucceeded(pinj, rel);
		assertSucceeded(tinj, rel);
		assertSucceeded(psur, rel);
		assertSucceeded(tsur, rel);
		assertSucceeded(tbij, rel);
		assertSucceeded(trel, trel);
		assertSucceeded(strel, trel);
		assertSucceeded(tfun, trel);
		assertSucceeded(tinj, trel);
		assertSucceeded(tsur, trel);
		assertSucceeded(tbij, trel);
		assertSucceeded(pfun, pfun);
		assertSucceeded(tfun, pfun);
		assertSucceeded(pinj, pfun);
		assertSucceeded(tinj, pfun);
		assertSucceeded(psur, pfun);
		assertSucceeded(tsur, pfun);
		assertSucceeded(tbij, pfun);
		assertSucceeded(tfun, tfun);
		assertSucceeded(tinj, tfun);
		assertSucceeded(tsur, tfun);
		assertSucceeded(tbij, tfun);
	}

	private void assertSucceeded(String opHyp, String opGoal) {
		final String hypStr = "f ∈ A" + opHyp + "B";
		final Predicate hyp = parsePredicate(hypStr);
		final String goalStr = "f{x↦y} ∈ A" + opGoal + "B";
		assertSuccess(" ;H; ;S; " + hypStr + " |- " + goalStr,
				mapOvrG(hyp, empty, empty));
	}

	@Test
	public void assertFailure() {
		addToTypeEnvironment("f=ℤ↔ℤ; A=ℙ(ℤ); B=ℙ(ℤ); x=ℤ; y=ℤ");
		// Goal is not an Inclusion
		assertFailure(" ;H; ;S; |- f{x ↦ y}∉A → B");
		// Left member is not an Overriding
		assertFailure(" ;H; ;S; |- f∖{x ↦ y}∈A → B");
		// The function is not override by a singleton
		assertFailure(" ;H; ;S; |- fg∈A → B");
		// The singleton is not a mapplet
		assertFailure(" ;H; ;S; |- f{a}∈A → B");
		assertFailedOnTypeRelation(srel, srel);
		assertFailedOnTypeRelation(strel, strel);
		assertFailedOnTypeRelation(pinj, pinj);
		assertFailedOnTypeRelation(tinj, tinj);
		assertFailedOnTypeRelation(psur, psur);
		assertFailedOnTypeRelation(tsur, tsur);
		assertFailedOnTypeRelation(tbij, tbij);
		assertFailedOnTypeRelation(trel, rel);
		assertFailedOnTypeRelation(trel, srel);
		assertFailedOnTypeRelation(trel, pfun);
		assertFailedOnTypeRelation(trel, pinj);
		assertFailedOnTypeRelation(trel, psur);
		assertFailedOnTypeRelation(pfun, rel);
		assertFailedOnTypeRelation(pfun, trel);
		assertFailedOnTypeRelation(pfun, srel);
		assertFailedOnTypeRelation(pfun, strel);
		assertFailedOnTypeRelation(tfun, rel);
		assertFailedOnTypeRelation(tfun, trel);
		assertFailedOnTypeRelation(tfun, srel);
		assertFailedOnTypeRelation(tfun, strel);
		assertFailedOnTypeRelation(tfun, pfun);
		assertFailedOnTypeRelation(tfun, pinj);
		assertFailedOnTypeRelation(tfun, psur);
	}

	private void assertFailedOnTypeRelation(String opGoal, String opHyp) {
		final String hypStr = "f ∈ A" + opHyp + "B";
		final String goalStr = "f{x↦y} ∈ A " + opGoal + "B";
		assertFailure(" ;H; ;S; " + hypStr + " |- " + goalStr);
	}

}
