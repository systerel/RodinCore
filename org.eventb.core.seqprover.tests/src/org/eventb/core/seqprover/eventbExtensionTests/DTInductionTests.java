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

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.dtInducGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.internal.core.seqprover.eventbExtensions.DTInduction;
import org.junit.Test;

/**
 * @author Nicolas Beauger
 *
 */
public class DTInductionTests extends AbstractManualReasonerTests {

	public DTInductionTests() {
		super(DT_FAC);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.dtInduction";
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return dtInducGetPositions(predicate);
	}

	protected IReasonerInput input(String input) {
		return new DTInduction.Input(null, makePosition(input));
	}

	public void assertReasonerSuccess(String sequentImage, IReasonerInput input, String... newSequents)
			throws UntranslatableException {
		assertReasonerSuccess(genSeq(sequentImage, ff), input, newSequents);
	}

	public void assertReasonerFailure(String sequentImage, IReasonerInput input, String reason)
			throws UntranslatableException {
		assertReasonerFailure(genSeq(sequentImage, ff), input, reason);
	}

	@Test
	public void testPositions() {
		assertGetPositions("∀ l ⦂ SD · l=l1", "0");
		assertGetPositions("∀ l⦂SD, l1 · l=l1", "0", "1");
		assertGetPositions("∀ l⦂SD, x⦂ℤ, l1 · l=l1", "0", "2");
		assertGetPositions("∀ l ⦂ SD · destr1(l) = 0", "0");
		assertGetPositions("∀ l⦂Induc(ℤ) · l=l1", "0");
		assertGetPositions("destr1(l) = 0");
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", input("0"), "{}[][][] |- cons0=l1",
				"{p_destr1=ℤ}[][][] |- cons1(p_destr1)=l1",
				"{p_destr2_0=ℤ; p_destr2_1=ℤ}[][][] |- cons2(p_destr2_0, p_destr2_1)=l1");
		assertReasonerSuccess("|- ∀ l⦂SD, l1 · l=l1", input("0"), "{}[][][] |- ∀ l1 · cons0=l1",
				"{p_destr1=ℤ}[][][] |- ∀ l1 · cons1(p_destr1)=l1",
				"{p_destr2_0=ℤ; p_destr2_1=ℤ}[][][] |- ∀ l1 · cons2(p_destr2_0, p_destr2_1)=l1");
		assertReasonerSuccess("|- ∀ l⦂SD, l1 · l=l1", input("1"), "{}[][][] |- ∀ l · l=cons0",
				"{p_destr1=ℤ}[][][] |- ∀ l · l=cons1(p_destr1)",
				"{p_destr2_0=ℤ; p_destr2_1=ℤ}[][][] |- ∀ l · l=cons2(p_destr2_0, p_destr2_1)");
		assertReasonerSuccess("|- ∀ l⦂Induc(ℤ) · l=l1", input("0"),
				"{l1=Induc(ℤ)}[][][] |- ind0=l1",
				"{p_ind1_0=Induc(ℤ)}[][][p_ind1_0=l1] |- ind1(p_ind1_0)=l1",
				"{p_ind2_0=Induc(ℤ); p_ind2_1=Induc(ℤ)}[][][p_ind2_0=l1 ;; p_ind2_1=l1]|- ind2(p_ind2_0, p_ind2_1)=l1");
	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure("|- destr1(l) = 0", input("0"),
				"Inference " + getReasonerID() + " is not applicable for destr1(l)=0");
	}

}
