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

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.internal.core.seqprover.eventbExtensions.ExF;
import org.junit.Test;

/**
 * Acceptance tests for reasoner ExF.
 * 
 * @author Laurent Voisin
 */
public class ExFTests extends AbstractReasonerTests {
	
	@Override
	public String getReasonerID() {
		return ExF.REASONER_ID;
	}

	@Test
	public void success() throws Exception {
		// hyp not present: success but no effect
		assertReasonerSuccess(" |- ⊥ ", input("∃x·x=1"), "{}[][][] |- ⊥");
		// one bound variable
		assertReasonerSuccess("∃x·x=1 |- ⊥ ", input("∃x·x=1"), "{}[∃x·x=1][][x=1] |- ⊥");
		// two predicates generated
		assertReasonerSuccess("∃x·x=1∧x=2 |- ⊥ ", input("∃x·x=1∧x=2"), "{}[∃x·x=1∧x=2][][x=1;; x=2] |- ⊥");
		// two bound variables
		assertReasonerSuccess("∃x,y·x↦y=1↦2 |- ⊥ ", input("∃x,y·x↦y=1↦2"), "{}[∃x,y·x↦y=1↦2][][x↦y=1↦2] |- ⊥");
		// name collision
		assertReasonerSuccess("∃x·x=1;; x=3 |- ⊥ ", input("∃x·x=1"), "{}[∃x·x=1][][x0=1;; x=3] |- ⊥");
		// name collision, different type
		assertReasonerSuccess("∃x·x=1;; x=TRUE |- ⊥ ", input("∃x·x=1"), "{}[∃x·x=1][][x0=1;; x=TRUE] |- ⊥");
	}

	@Test
	public void failure() throws Exception {
		// hyp null
		assertReasonerFailure(" ⊤ |- ⊥ ", new ExF.Input(null), "Null hypothesis");
		// hyp not existentially quantified
		assertReasonerFailure(" ∀x·x = 1 |- ⊥ ", input("∀x·x=1"), "Predicate is not existentially quantified: ∀x·x=1");
	}

	private IReasonerInput input(String pred) {
		return new ExF.Input(genPred(pred));
	}

}
