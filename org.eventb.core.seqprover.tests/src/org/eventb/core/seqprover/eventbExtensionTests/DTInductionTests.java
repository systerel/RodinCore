/*******************************************************************************
 * Copyright (c) 2010, 2024 Systerel and others.
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

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
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
				"{}[][][] |- ∀ p_destr1 · cons1(p_destr1)=l1",
				"{}[][][] |- ∀ p_destr2_0, p_destr2_1 · cons2(p_destr2_0, p_destr2_1)=l1");
		assertReasonerSuccess("|- ∀ l⦂SD, l1 · l=l1", input("0"), "{}[][][] |- ∀ l1 · cons0=l1",
				"{}[][][] |- ∀ p_destr1 · ∀ l1 · cons1(p_destr1)=l1",
				"{}[][][] |- ∀ p_destr2_0, p_destr2_1 · ∀ l1 · cons2(p_destr2_0, p_destr2_1)=l1");
		assertReasonerSuccess("|- ∀ l⦂SD, l1 · l=l1", input("1"), "{}[][][] |- ∀ l · l=cons0",
				"{}[][][] |- ∀ p_destr1 · ∀ l · l=cons1(p_destr1)",
				"{}[][][] |- ∀ p_destr2_0, p_destr2_1 · ∀ l · l=cons2(p_destr2_0, p_destr2_1)");
		assertReasonerSuccess("|- ∀ l⦂Induc(ℤ) · l=l1", input("0"),
				"{l1=Induc(ℤ)}[][][] |- ind0=l1",
				"{}[][][] |- ∀ p_ind1_0⦂Induc(ℤ) · p_ind1_0=l1 ⇒ ind1(p_ind1_0)=l1",
				"{}[][][]|- ∀ p_ind2_0⦂Induc(ℤ), p_ind2_1 · p_ind2_0=l1 ∧ p_ind2_1=l1 ⇒ ind2(p_ind2_0, p_ind2_1)=l1");
		assertReasonerSuccess("|- ∀ l⦂Induc(ℤ), l1 · l=l1", input("0"),
				"{}[][][] |- ∀ l1⦂Induc(ℤ) · ind0=l1",
				"{}[][][] |- ∀ p_ind1_0⦂Induc(ℤ) · (∀ l1 · p_ind1_0=l1) ⇒ (∀ l1 · ind1(p_ind1_0)=l1)",
				"{}[][][]|- ∀ p_ind2_0⦂Induc(ℤ), p_ind2_1 · (∀ l1 · p_ind2_0=l1) ∧ (∀ l1 · p_ind2_1=l1) ⇒ (∀ l1 · ind2(p_ind2_0, p_ind2_1)=l1)");
		assertReasonerSuccess("|- ∀ l⦂Induc(ℤ), l1 · l=l1", input("1"),
				"{}[][][] |- ∀ l⦂Induc(ℤ) · l=ind0",
				"{}[][][] |- ∀ p_ind1_0⦂Induc(ℤ) · (∀ l · l=p_ind1_0) ⇒ (∀ l · l=ind1(p_ind1_0))",
				"{}[][][]|- ∀ p_ind2_0⦂Induc(ℤ), p_ind2_1 · (∀ l · l=p_ind2_0) ∧ (∀ l · l=p_ind2_1) ⇒ (∀ l · l=ind2(p_ind2_0, p_ind2_1))");

		// Induction on a predicate with a guard
		assertReasonerSuccess("|- ∀ x · x ∈ Induc(ℕ) ⇒ x ∈ S", input("0"), //
				"{S=ℙ(Induc(ℤ))}[][][] |- ind0 ∈ Induc(ℕ) ⇒ ind0 ∈ S", //
				"{S=ℙ(Induc(ℤ))}[][][] |- ∀ a ·" //
						+ "   (a ∈ Induc(ℕ) ⇒ a ∈ S)" //
						+ "⇒ (ind1(a) ∈ Induc(ℕ) ⇒ ind1(a) ∈ S)",
				"{S=ℙ(Induc(ℤ))}[][][] |- ∀ a, b · " //
						+ "   (a ∈ Induc(ℕ) ⇒ a ∈ S)" //
						+ " ∧ (b ∈ Induc(ℕ) ⇒ b ∈ S)" //
						+ "⇒ (ind2(a, b) ∈ Induc(ℕ) ⇒ ind2(a, b) ∈ S)");
	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure("|- destr1(l) = 0", input("0"),
				"Inference " + getReasonerID() + " is not applicable for destr1(l)=0");
	}

}
