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
import org.eventb.internal.core.seqprover.eventbExtensions.ContrL1;
import org.junit.Test;

/**
 * Acceptance tests for reasoner ContrL1.
 * 
 * Shares most tests with the previous level of the reasoner.
 * 
 * @author Laurent Voisin
 */
public class ContrL1Tests extends ContrTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.contrL1";
	}

	@Test
	public void success() throws Exception {

		// Hypothesis given, positive goal
		assertReasonerSuccess("z=3 |- x=1", input("z=3"), //
				"{}[z=3][][¬x=1] |- ¬z=3");

		// Hypothesis given, negative goal
		assertReasonerSuccess("z=3 |- ¬x=1", input("z=3"), //
				"{}[z=3][][x=1] |- ¬z=3");

		// Hypothesis given, negated conjunctive goal
		assertReasonerSuccess("z=3 |- ¬(x=1 ∧ y=2)", input("z=3"), //
				"{}[z=3][][x=1 ;; y=2] |- ¬z=3");
	}

	protected IReasonerInput input(String predImage) {
		return new ContrL1.Input(predImage == null ? null : genPred(predImage));
	}

}
