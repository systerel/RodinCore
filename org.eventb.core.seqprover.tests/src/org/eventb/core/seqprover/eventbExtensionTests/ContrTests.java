/*******************************************************************************
 * Copyright (c) 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.internal.core.seqprover.eventbExtensions.Contr;
import org.junit.Test;

/**
 * Acceptance tests for reasoner Contr.
 * 
 * @author Laurent Voisin
 */
public class ContrTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.contr";
	}

	@Test
	public void successCommon() throws Exception {

		// No predicate given, positive goal
		assertReasonerSuccess("|- x=1", input(null), //
				"{}[][][¬x=1] |- ⊥");

		// No predicate given, negative goal
		assertReasonerSuccess("|- ¬x=1", input(null), //
				"{}[][][x=1] |- ⊥");

		// No predicate given, negated conjunctive goal
		assertReasonerSuccess("|- ¬(x=1 ∧ y=2)", input(null), //
				"{}[][][x=1 ;; y=2] |- ⊥");

	}

	@Test
	public void success() throws Exception {

		// Hypothesis given, positive goal
		assertReasonerSuccess("z=3 |- x=1", input("z=3"), //
				"{}[][z=3][¬x=1] |- ¬z=3");

		// Hypothesis given, negative goal
		assertReasonerSuccess("z=3 |- ¬x=1", input("z=3"), //
				"{}[][z=3][x=1] |- ¬z=3");

		// Hypothesis given, negated conjunctive goal
		assertReasonerSuccess("z=3 |- ¬(x=1 ∧ y=2)", input("z=3"), //
				"{}[][z=3][x=1 ;; y=2] |- ¬z=3");
	}

	@Test
	public void failure() throws Exception {
		// Predicate is not a hypothesis
		assertReasonerFailure("|- ⊥", input("y=2"),
				"Nonexistent hypothesis: y=2");
	}

	protected IReasonerInput input(String predImage) {
		return new Contr.Input(predImage == null ? null : genPred(predImage));
	}

}
