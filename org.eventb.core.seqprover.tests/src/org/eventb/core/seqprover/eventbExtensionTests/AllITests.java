/*******************************************************************************
 * Copyright (c) 2012, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;
import org.junit.Test;

/**
 * Acceptance tests for reasoner AllI.
 * 
 * @author Laurent Voisin
 */
public class AllITests extends AbstractReasonerTests {
	
	private static final EmptyInput NO_INPUT = new EmptyInput();

	@Override
	public String getReasonerID() {
		return AllI.REASONER_ID;
	}

	@Test
	public void success() throws Exception {
		// one bound variable
		assertReasonerSuccess("|- ∀x·x=1", NO_INPUT, "{}[][][] |- x=1");
		// two predicates generated
		assertReasonerSuccess("|- ∀x·x=1 ∧ x=2", NO_INPUT, "{}[][][] |- x=1 ∧ x=2");
		// two bound variables
		assertReasonerSuccess("|- ∀x,y·x↦y = 1↦2", NO_INPUT, "{}[][][] |- x↦y = 1↦2");
		// name collision
		assertReasonerSuccess("x=3 |- ∀x·x=1", NO_INPUT, "{}[][][x=3] |- x0=1");
		// name collision, different type
		assertReasonerSuccess("x=TRUE |- ∀x·x=1", NO_INPUT, "{}[][][x=TRUE] |- x0=1");
	}

	@Test
	public void failure() throws Exception {
		// goal not quantified
		assertReasonerFailure(" ⊤ |- ⊥ ", NO_INPUT, "Goal is not universally quantified");
		// goal not universally quantified
		assertReasonerFailure(" ⊤ |- ∃x·x=1 ", NO_INPUT, "Goal is not universally quantified");
	}

}
