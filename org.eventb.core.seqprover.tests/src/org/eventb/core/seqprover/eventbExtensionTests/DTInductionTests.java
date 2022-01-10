/*******************************************************************************
 * Copyright (c) 2010, 2022 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Université de Lorraine - refactor as extension of distinct case tests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

/**
 * @author Nicolas Beauger
 *
 */
public class DTInductionTests extends DTDistinctCaseTests {

	public DTInductionTests() {
		/*
		 * In one test case, the induction tactic adds more hypotheses than the distinct
		 * case tactic, so we update the expected sequents. In all other cases, they
		 * produce the same results.
		 */
		resultP3_1 = "{l1=Induc(ℤ); p_ind1_0=Induc(ℤ)}" +
				"[][][l1=ind1(p_ind1_0) ;; ∀ l⦂Induc(ℤ) · l=p_ind1_0] |- ∀ l⦂Induc(ℤ) · l=l1";
		resultP3_2 = "{l1=Induc(ℤ); p_ind2_0=Induc(ℤ); p_ind2_1=Induc(ℤ)}" +
				"[][][l1=ind2(p_ind2_0, p_ind2_1) ;; ∀ l⦂Induc(ℤ) · l=p_ind2_0 ;; ∀ l⦂Induc(ℤ) · l=p_ind2_1] |- ∀ l⦂Induc(ℤ) · l=l1";
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.dtInduction";
	}

}
