/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteSet;

/**
 * Unit tests for the Finite Set reasoner
 * {@link FiniteSet}
 * 
 * @author htson
 */
public class FiniteSetTests extends AbstractSingleExpressionInputReasonerTests {

	private static final String P1 = "finite({x ∣ x ∈ ℕ})";
	private static final String[] P1Result = new String[] { //
			"{}[][][⊤] |- ⊤", //
			"{}[][][⊤] |- finite(ℕ)", //
			"{}[][][⊤] |- {x ∣ x∈ℕ}⊆ℕ", //
	};
		
	private static final String P2 = "finite({x ↦ (y ↦ z) ∣ x ∈ ℕ ∧ y ∈ BOOL ∧ z ∈ ℕ})";
	private static final String[] P2Result = new String[] { //
			"{}[][][⊤] |- ⊤", //
			"{}[][][⊤] |- finite(ℕ × (BOOL × ℕ))", //
			"{}[][][⊤] |- {x ↦ (y ↦ z) ∣ x∈ℕ∧y∈BOOL∧z∈ℕ}⊆ℕ × (BOOL × ℕ)", //
	};

	private static final String P3 = //
	"a = 1 ⇒ finite({x ↦ (y ↦ z) ∣ x ∈ ℕ ∧ y ∈ BOOL ∧ z ∈ ℕ})";

	private static final String P4 = "finite({x ∣ x = max(S)})";
	private static final String[] P4Result = new String[] { //
			"{}[][][⊤] |- S≠∅ ∧ (∃b·∀x·x∈S⇒b≤x)", //
			"{}[][][⊤] |- finite({x ∣ x = min(S)})", //
			"{}[][][⊤] |- {x ∣ x = max(S)} ⊆ {x ∣ x = min(S)}", //
	};

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "ROOT",
				P2, "ROOT",
				P3, "",
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteSetGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteSet";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, "ℕ", P1Result),
				// P2 in goal
				new SuccessfulTest(" ⊤ |- " + P2, "ℕ × (BOOL × ℕ)", P2Result),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, "{x ∣ x = min(S)}", P4Result),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P2 in goal
				" ⊤ |- " + P2,
				null,
				"ℕ × BOOL × ℕ",
				"Incorrect input type",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"ℕ",
				"Goal is not a finiteness"
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
