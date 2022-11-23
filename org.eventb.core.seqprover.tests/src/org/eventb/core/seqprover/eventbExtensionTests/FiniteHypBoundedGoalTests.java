/*******************************************************************************
 * Copyright (c) 2009, 2022 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.core.seqprover.rewriterTests.AbstractAutomaticReasonerTests;
import org.junit.Test;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class FiniteHypBoundedGoalTests extends AbstractAutomaticReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteHypBoundedGoal";
	}

	@Test
	public void success() throws Exception {
		// all configurations
		assertReasonerSuccess("finite({1,2,3}) |- ∃n·(∀x·x ∈ {1,2,3} ⇒ n ≤ x)");
		assertReasonerSuccess("finite({1,2,3}) |- ∃n·(∀x·x ∈ {1,2,3} ⇒ x ≥ n)");
		assertReasonerSuccess("finite({1,2,3}) |- ∃n·(∀x·x ∈ {1,2,3} ⇒ n ≥ x)");
		assertReasonerSuccess("finite({1,2,3}) |- ∃n·(∀x·x ∈ {1,2,3} ⇒ x ≤ n)");
		// same as above, but with implicit finiteness of set extension
		assertReasonerSuccess(" |- ∃n·(∀x·x ∈ {1,2,3} ⇒ n ≤ x)");
		assertReasonerSuccess(" |- ∃n·(∀x·x ∈ {1,2,3} ⇒ x ≥ n)");
		assertReasonerSuccess(" |- ∃n·(∀x·x ∈ {1,2,3} ⇒ n ≥ x)");
		assertReasonerSuccess(" |- ∃n·(∀x·x ∈ {1,2,3} ⇒ x ≤ n)");
		// additional hypothesis
		assertReasonerSuccess("⊤ ;; finite({1,2,3}) |- ∃n·(∀x·x ∈ {1,2,3} ⇒ x ≥ n)");
		// comprehension set
		assertReasonerSuccess("finite({y∣y=10}) |- ∃n·(∀x·x ∈ {y∣y=10} ⇒ n ≤ x)");
		assertReasonerSuccess("finite({y·0≤y∧y≤10∣2∗y}) |- ∃n·(∀x·x ∈ {y·0≤y∧y≤10∣2∗y} ⇒ n ≤ x)");
	}

	@Test
	public void failure() throws Exception {
		// finite hyp on a different set
		assertReasonerFailure("S⊆ℤ ;; T⊆ℤ ;; finite(S) |- ∃n·(∀x·x ∈ T ⇒ n ≤ x)");
		// comprehension set with bound identifiers references
		assertReasonerFailure("finite({y·n≤y∧y≤10∣2∗y}) |- ∃n·(∀x·x ∈ {y·n≤y∧y≤10∣2∗y} ⇒ n ≤ x)");
		// malformed predicates
		assertReasonerFailure("finite({1,2,3}) |- ∃n,m⦂ℤ·(∀x·x ∈ {1,2,3} ⇒ n ≤ x)");
		assertReasonerFailure("finite({1,2,3}) |- ∃n·(∀x,y⦂ℤ·x ∈ {1,2,3} ⇒ n ≤ x)");
	}

}
