/*******************************************************************************
 * Copyright (c) 2010, 2025 Systerel and others.
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
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.DTDistinctCase;
import org.junit.Test;

/**
 * @author Nicolas Beauger
 *
 */
public class DTDistinctCaseTests extends AbstractManualReasonerTests {

	public DTDistinctCaseTests() {
		super(DT_FAC);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.dtDistinctCase";
	}

	// Make an input from a position (in the goal)
	protected IReasonerInput input(String position) {
		return new DTDistinctCase.Input(null, makePosition(position));
	}

	// Make an input from a position (in the goal) and an input
	protected IReasonerInput inputWithNames(String position, String input) {
		return new DTDistinctCase.Input(null, makePosition(position), input, ff);
	}

	// Make an input from a position in a given hypothesis
	protected IReasonerInput input(String hypothesis, String position) {
		return new DTDistinctCase.Input(genPred(hypothesis, ff), makePosition(position));
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.dtDCGetPositions(predicate);
	}

	@Test
	public void testGetPositions() {
		assertGetPositions("∀ l⦂SD · l=l1", "1.1");
		assertGetPositions("∀ l ⦂ SD · destr1(l) = 0");
	}

	@Test
	public void success() throws Exception {
		// Applied to the goal
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", input("1.1"),
				"{l1=SD}[][][l1=cons0] |- ∀ l⦂SD · l=l1",
				"{l1=SD; p_destr1=ℤ}[][][l1=cons1(p_destr1)] |- ∀ l⦂SD · l=l1",
				"{l1=SD; p_destr2_0=ℤ; p_destr2_1=ℤ}[][][l1=cons2(p_destr2_0, p_destr2_1)] |- ∀ l⦂SD · l=l1");
		// Applied to an hypothesis
		assertReasonerSuccess("l1∈SD |- ⊥", input("l1∈SD", "0"),
				"{l1=SD}[][][l1∈SD;;l1=cons0] |- ⊥",
				"{l1=SD; p_destr1=ℤ}[][][l1∈SD;;l1=cons1(p_destr1)] |- ⊥",
				"{l1=SD; p_destr2_0=ℤ; p_destr2_1=ℤ}[][][l1∈SD;;l1=cons2(p_destr2_0, p_destr2_1)] |- ⊥");
		// Applied to an inductive datatype
		assertReasonerSuccess("|- ∀ l⦂Induc(ℤ) · l=l1", input("1.1"),
				"{l1=Induc(ℤ); p_ind0_0=ℤ}[][][l1=ind0(p_ind0_0)] |- ∀ l⦂Induc(ℤ) · l=l1",
				"{l1=Induc(ℤ); p_ind1_0=Induc(ℤ)}[][][l1=ind1(p_ind1_0)] |- ∀ l⦂Induc(ℤ) · l=l1",
				"{l1=Induc(ℤ); p_ind2_0=Induc(ℤ); p_ind2_1=Induc(ℤ)}[][][l1=ind2(p_ind2_0, p_ind2_1)] |- ∀ l⦂Induc(ℤ) · l=l1");
		// Applied to an inductive datatype in an hypothesis
		assertReasonerSuccess("l1∈Induc(ℕ) |- ⊥", input("l1∈Induc(ℕ)", "0"),
				"{l1=Induc(ℤ); p_ind0_0=ℤ}[][][l1∈Induc(ℕ);; p_ind0_0∈ℕ;; l1=ind0(p_ind0_0)] |- ⊥",
				"{l1=Induc(ℤ); p_ind1_0=Induc(ℤ)}[][][l1∈Induc(ℕ);;p_ind1_0∈Induc(ℕ);;l1=ind1(p_ind1_0)] |- ⊥",
				"{l1=Induc(ℤ); p_ind2_0=Induc(ℤ); p_ind2_1=Induc(ℤ)}[][][l1∈Induc(ℕ);;p_ind2_0∈Induc(ℕ);;"
						+ "p_ind2_1∈Induc(ℕ);;l1=ind2(p_ind2_0, p_ind2_1)] |- ⊥");
		// Applied to the goal with user-provided names
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", inputWithNames("1.1", "a, b, c"),
				"{l1=SD}[][][l1=cons0] |- ∀ l⦂SD · l=l1",
				"{l1=SD; a=ℤ}[][][l1=cons1(a)] |- ∀ l⦂SD · l=l1",
				"{l1=SD; b=ℤ; c=ℤ}[][][l1=cons2(b, c)] |- ∀ l⦂SD · l=l1");
		// Applied to the goal with user-provided names (too few)
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", inputWithNames("1.1", "a"),
				"{l1=SD}[][][l1=cons0] |- ∀ l⦂SD · l=l1",
				"{l1=SD; a=ℤ}[][][l1=cons1(a)] |- ∀ l⦂SD · l=l1",
				"{l1=SD; p_destr2_0=ℤ; p_destr2_1=ℤ}[][][l1=cons2(p_destr2_0, p_destr2_1)] |- ∀ l⦂SD · l=l1");
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", inputWithNames("1.1", "a, b"),
				"{l1=SD}[][][l1=cons0] |- ∀ l⦂SD · l=l1",
				"{l1=SD; a=ℤ}[][][l1=cons1(a)] |- ∀ l⦂SD · l=l1",
				"{l1=SD; b=ℤ; p_destr2_1=ℤ}[][][l1=cons2(b, p_destr2_1)] |- ∀ l⦂SD · l=l1");
		// Applied to the goal with user-provided names (too many)
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", inputWithNames("1.1", "a, b, c, d"),
				"{l1=SD}[][][l1=cons0] |- ∀ l⦂SD · l=l1",
				"{l1=SD; a=ℤ}[][][l1=cons1(a)] |- ∀ l⦂SD · l=l1",
				"{l1=SD; b=ℤ; c=ℤ}[][][l1=cons2(b, c)] |- ∀ l⦂SD · l=l1");
		// Applied to the goal with user-provided names (conflicts)
		assertReasonerSuccess("|- ∀ l⦂SD · l=l1", inputWithNames("1.1", "l1, p_destr2_1"),
				"{l1=SD}[][][l1=cons0] |- ∀ l⦂SD · l=l1",
				"{l1=SD; l2=ℤ}[][][l1=cons1(l2)] |- ∀ l⦂SD · l=l1",
				"{l1=SD; p_destr2_1=ℤ; p_destr2_2=ℤ}[][][l1=cons2(p_destr2_1, p_destr2_2)] |- ∀ l⦂SD · l=l1");
	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure("∀ l⦂SD · l=l1 |- ⊤", input("∀ l⦂SD · l=l1", "1.0"),
				"Inference " + getReasonerID() + " is not applicable for ∀l·l=l1 at position 1.0");
		assertReasonerFailure("|- ∀ l⦂SD · l=l1", input("1.0"),
				"Inference " + getReasonerID() + " is not applicable for ∀l·l=l1 at position 1.0");
		assertReasonerFailure("|- ∀ l ⦂ SD · destr1(l) = 0", input("1.0.0"),
				"Inference " + getReasonerID() + " is not applicable for ∀l·destr1(l)=0 at position 1.0.0");
		assertReasonerFailure("|- ∀ l⦂SD · l=l1", inputWithNames("1.1", "x, +, 1, y'"),
				"Input \"+\" is not a valid identifier: global input should be empty or have a list of comma-separated identifiers");
	}

}
