/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

/**
 * @author Nicolas Beauger
 *
 */
public class DTInductionTests extends AbstractManualInferenceTests {

	private static final String P1 = "∀ l⦂SD · l=l1";
	private static final String resultP1_0 = "{l1=SD}[][][l1=cons0] |- ∀ l⦂SD · l=l1";
	private static final String resultP1_1 = "{l1=SD; p_destr1=ℤ}[][][l1=cons1(p_destr1)] |- ∀ l⦂SD · l=l1";
	private static final String resultP1_2 = "{l1=SD; p_destr2_0=ℤ; p_destr2_1=ℤ}" +
			"[][][l1=cons2(p_destr2_0, p_destr2_1)] |- ∀ l⦂SD · l=l1";
	
	private static final String P2 = "∀ l ⦂ SD · destr1(l) = 0";
		
	private static final String P3 = "∀ l⦂Induc(ℤ) · l=l1";
	private static final String resultP3_0 = "{l1=Induc(ℤ)}[][][l1=ind0] |- ∀ l⦂Induc(ℤ) · l=l1";
	private static final String resultP3_1 = "{l1=Induc(ℤ); p_ind1_0=Induc(ℤ)}" +
			"[][][l1=ind1(p_ind1_0) ;; ∀ l⦂Induc(ℤ) · l=p_ind1_0] |- ∀ l⦂Induc(ℤ) · l=l1";
	private static final String resultP3_2 = "{l1=Induc(ℤ); p_ind2_0=Induc(ℤ); p_ind2_1=Induc(ℤ)}" +
			"[][][l1=ind2(p_ind2_0, p_ind2_1) ;; ∀ l⦂Induc(ℤ) · l=p_ind2_0 ;; ∀ l⦂Induc(ℤ) · l=p_ind2_1] |- ∀ l⦂Induc(ℤ) · l=l1";

	public DTInductionTests() {
		super(DT_FAC);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.dtInduction";
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.dtDCInducGetPositions(predicate);
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String [] {
				P1, "1.1",
				P2, "",
		};
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(" |- " + P1, null, "1.1", resultP1_0, resultP1_1, resultP1_2),
				new SuccessfulTest(" |- " + P3, null, "1.1", resultP3_0, resultP3_1, resultP3_2),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in hyp
				P1 + " |- ⊤ ",
				P1,
				"1.0",
				// P1 in goal
				" |- " + P1,
				null,
				"1.0",
				// P2 in goal
				" |- " + P2,
				null,
				"1.0.0",
		};
	}


}
