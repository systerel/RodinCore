/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use new test methods
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.inclusionSetMinusLeftRewritesGetPositions;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.InclusionSetMinusRightRewrites;
import org.junit.Test;

/**
 * Unit tests for the Rewrite inclusion with set minus on the right reasoner
 * {@link InclusionSetMinusRightRewrites}
 * 
 * @author htson
 */
public class InclusionSetMinusLeftTests extends AbstractManualRewriterTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.inclusionSetMinusLeftRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return inclusionSetMinusLeftRewritesGetPositions(predicate);
	}

	@Test
	public void testPositions() {
		// Applicable positions
		assertGetPositions("A ∖ B ⊆ {x ∣ x > 0}", "ROOT");
		assertGetPositions("∀x·x = 1 ⇒ A ∖ B ⊆ {1 ↦ {x}}", "1.1");

		// Not applicable
		assertGetPositions("1 ∈ ∅");
	}

	/*
	 * Ensures that the reasoner behaves as expected when succeeding.
	 */
	@Test
	public void testSuccess() throws Exception {
		// DERIV_SUBSETEQ_SETMINUS_L
		// A \ B <: S == A <: S \/ B
		assertReasonerSuccess("A ∖ B ⊆ {x ∣ x > 0}", "", "A ⊆ {x ∣ x > 0} ∪ B");
		assertReasonerSuccess( //
				"∀x·x = 1 ⇒ {x ↦ {2}, 2 ↦ {3}} ∖ {2 ↦ {x}} ⊆ {1 ↦ {x}}", //
				"1.1", //
				"∀x·x=1⇒{x ↦ {2}, 2 ↦ {3}} ⊆ {1 ↦ {x}} ∪ {2 ↦ {x}}");
	}

	/*
	 * Ensures that the reasoner behaves as expected when failing.
	 */
	@Test
	public void testFailure() throws Exception {
		assertReasonerFailure("A ∖ B ⊆ {x ∣ x > 0}", "0");
		assertReasonerFailure("∀x·x = 1 ⇒ {x ↦ {2}, 2 ↦ {3}} ∖ {2 ↦ {x}} ⊆ {1 ↦ {x}}", "1.0");

	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
