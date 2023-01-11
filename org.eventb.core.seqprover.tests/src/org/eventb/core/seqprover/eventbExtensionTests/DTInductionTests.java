/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.dtInducApplicable;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.junit.Test;

/**
 * @author Nicolas Beauger
 *
 */
public class DTInductionTests extends AbstractReasonerTests {

	public DTInductionTests() {
		super(DT_FAC);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.dtInduction";
	}

	private static final EmptyInput INPUT = new EmptyInput();

	private void assertReasonerSuccess(String sequent, String... newSequentImages) throws UntranslatableException {
		assertReasonerSuccess(genSeq(sequent, ff), INPUT, newSequentImages);
	}

	private void assertReasonerFailure(String sequentImage, String reason) throws UntranslatableException {
		assertReasonerFailure(genSeq(sequentImage, ff), INPUT, reason);
	}

	@Test
	public void testApplicable() {
		assertTrue(dtInducApplicable(genPred("∀ l ⦂ SD · l=l1", ff)));
		assertTrue(dtInducApplicable(genPred("∀ l ⦂ SD · destr1(l) = 0", ff)));
		assertTrue(dtInducApplicable(genPred("∀ l⦂Induc(ℤ) · l=l1", ff)));
		assertFalse(dtInducApplicable(genPred("destr1(l) = 0", ff)));
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", "{l=SD}[][][l=cons0] |- l=l1",
				"{l=SD; p_destr1=ℤ}[][][l=cons1(p_destr1)] |- l=l1",
				"{l=SD; p_destr2_0=ℤ; p_destr2_1=ℤ}[][][l=cons2(p_destr2_0, p_destr2_1)] |- l=l1");
		assertReasonerSuccess("|- ∀ l⦂SD, l1 · l=l1", "{l=SD}[][][l=cons0] |- ∀ l1 · l=l1",
				"{l=SD; p_destr1=ℤ}[][][l=cons1(p_destr1)] |- ∀ l1 · l=l1",
				"{l=SD; p_destr2_0=ℤ; p_destr2_1=ℤ}[][][l=cons2(p_destr2_0, p_destr2_1)] |- ∀ l1 · l=l1");
		assertReasonerSuccess("|- ∀ l⦂Induc(ℤ) · l=l1",
				"{l=Induc(ℤ)}[][][l=ind0] |- l=l1",
				"{l=Induc(ℤ); p_ind1_0=Induc(ℤ)}[][][l=ind1(p_ind1_0) ;; p_ind1_0=l1] |- l=l1",
				"{l=Induc(ℤ); p_ind2_0=Induc(ℤ); p_ind2_1=Induc(ℤ)}[][][l=ind2(p_ind2_0, p_ind2_1) ;; "
						+ "p_ind2_0=l1 ;; p_ind2_1=l1]|- l=l1");

	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure("|- destr1(l) = 0",
				"Inference " + getReasonerID() + " is not applicable for destr1(l)=0");
	}

}
