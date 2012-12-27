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
package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteMax;

/**
 * Unit tests for the Existence of maximum using Finiteness reasoner
 * {@link FiniteMax}
 * 
 * @author htson
 */
public class FiniteMaxTests extends AbstractEmptyInputReasonerTests {

	String P1 = "(x = 2) ⇒ (∃n·(∀x·x ∈ S ⇒ x ≤ n))";

	String P2 = "∀x· x = 2 ⇒ (∃n·(∀x·x ∈ S ⇒ x ≤ n))";

	String P3 = "∃n·(∀x·x ∈ S ⇒ x ≤ n)";

	String resultP3Goal = "{S=ℙ(ℤ)}[][][⊤] |- finite(S)";
	
	String P4 = "∃n·(∀x·x ∈ S ⇒ 2 ∗ x ≤ n)";
	
	String P5 = "∃n·(∀x·x ∈ {2, 3} ⇒ 3 ≤ n)";
	
	String P6 = "∃n·(∀x·x ∈ {x} ⇒ x ≤ n)";
	
	String P7 = "(x = 2) ⇒ (∃n·(∀x·x ∈ S ⇒ n ≥ x))";
	
	String P8 = "∀x· x = 2 ⇒ (∃n·(∀x·x ∈ S ⇒ n ≥ x))";

	String P9 = "∃n·(∀x·x ∈ S ⇒ n ≥ x)";

	String resultP9Goal = "{S=ℙ(ℤ)}[][][⊤] |- finite(S)";
	
	String P10 = "∃n·(∀x·x ∈ S ⇒ n ≥ 2 ∗ x)";
	
	String P11 = "∃n·(∀x·x ∈ {2, 3} ⇒ n ≥ 3)";
	
	String P12 = "∃n·(∀x·x ∈ {x} ⇒ n ≥ x)";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteMaxGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteMax";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, resultP3Goal),
				// P9 in goal
				new SuccessfulTest(" ⊤ |- " + P9, resultP9Goal)
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in goal
				" ⊤ |- " + P1,
				// P2 in goal
				" ⊤ |- " + P2,
				// P4 in goal
				" ⊤ |- " + P4,
				// P5 in goal
				" ⊤ |- " + P5,
				// P6 in goal
				" ⊤ |- " + P6,
				// P7 in goal
				" ⊤ |- " + P7,
				// P8 in goal
				" ⊤ |- " + P8,
				// P10 in goal
				" ⊤ |- " + P10,
				// P11 in goal
				" ⊤ |- " + P11,
				// P12 in goal
				" ⊤ |- " + P12
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "ROOT",
				P4, "",
				P5, "",
				P6, "",
				P7, "",
				P8, "",
				P9, "ROOT",
				P10, "",
				P11, "",
				P12, "",
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
