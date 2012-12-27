/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed rules FIN_FUN_*
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunction;

/**
 * Unit tests for the Finite of function reasoner {@link FiniteFunction}
 * 
 * @author htson
 */
public class FiniteFunConvTests extends AbstractPFunSetInputReasonerTests {

	private static final String P1 = "finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)})";
	private static final String P1Input = "ℕ × ℕ ⇸ ℕ";

	private static final String[] P1Result = {
			"{x=ℤ}[][][⊤] |- ⊤", //
			"{x=ℤ}[][][⊤] |- {0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∼ ∈ ℕ × ℕ ⇸ ℕ", //
			"{x=ℤ}[][][⊤] |- finite(ℕ × ℕ)", //
	};

	private static final String P2 = "x = 1 ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)})";

	private static final String P3 = "finite({0 ↦ 3,1 ↦ x,1 ↦ 2}[{x}])";
	
	private static final String P4 = "finite({0 ↦ 1})";
	private static final String P4Input = "{1÷2} ⇸ {3÷4}";
	
	private static final String[] P4Result = {
		"{}[][][⊤] |- 2≠0∧4≠0", //
		"{}[][][⊤] |- {0 ↦ 1}∼ ∈ {1÷2} ⇸ {3÷4}", //
		"{}[][][⊤] |- finite({1÷2})", //
	};

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "ROOT",
				P2, "",
				P3, ""
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteFunConvGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteFunConv";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, P1Input, P1Result),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, P4Input, P4Result),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// correct goal, wrong input
				" ⊤ |- " + P1,
				null,
				"(ℕ × ℕ) ↔ ℕ",
				"Expected a set of all partial functions S ⇸ T",
				// Correct goal, type-check error with input
				" ⊤ |- " + P1,
				null,
				"ℕ ⇸ ℕ × ℕ",
				"Type check failed for " + "{0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∼∈ℕ ⇸ ℕ × ℕ",
				// incorrect goal
				" ⊤ |- " + P2,
				null,
				"ℕ",
				"Goal is not a finiteness",
				// incorrect goal, local check
				" ⊤ |- " + P3,
				null,
				"ℕ ⇸ ℕ × ℕ",
				"Goal is not a finiteness of a relation",
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
