/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
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
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteInter;

/**
 * Unit tests for the Finite of intersection reasoner {@link FiniteInter}
 * 
 * @author htson
 */
public class FiniteInterTests extends AbstractEmptyInputReasonerTests {

	String P1 = "(x = 2) ⇒ finite(S ∩ {0 ↦ 3} ∩ T)";

	String P2 = "∀x· x = 2 ⇒ finite(S ∩ {0 ↦ 3} ∩ T)";

	String P3 = "finite(S ∩ {0 ↦ 3} ∩ T)";

	String resultP3GoalA = "{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- finite(S)∨finite({0 ↦ 3})∨finite(T)";

	String P4 = "finite(inter({S, {0 ↦ 3}, T}))";

	String resultP4GoalA = "{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- ∃s· s ∈ {S, {0 ↦ 3}, T} ∧ finite(s)";

	String P5 = "finite(⋂s·s ∈ {S, {0 ↦ 3}, T} ∣ s)";

	String resultP5GoalA = "{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- ∃s· s ∈ {S, {0 ↦ 3}, T} ∧ finite(s)";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteInterGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteInter";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, resultP3GoalA),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, resultP4GoalA),
				// P5 in goal
				new SuccessfulTest(" ⊤ |- " + P5, resultP5GoalA),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in goal
				" ⊤ |- " + P1,
				// P2 in goal
				" ⊤ |- " + P2
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "ROOT",
				P4, "ROOT",
				P5, "ROOT",
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
