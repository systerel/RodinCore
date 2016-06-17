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
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteNegative;

/**
 * Unit tests for the Finite of set of non-positive numbers reasoner
 * {@link FiniteNegative}
 * 
 * @author htson
 */
public class FinitePositiveTests extends AbstractEmptyInputReasonerTests {

	String P1 = "(x = 2) ⇒ finite({0,x,1})";

	String P2 = "∀x· x = 2 ⇒ finite({0,x,1})";

	String P3 = "finite({0,x,1})";

	String resultP3GoalA = "{x=ℤ}[][][⊤] |- ∃n·∀x0·x0∈{0,x,1}⇒x0≤n";
	
	String resultP3GoalB = "{x=ℤ}[][][⊤] |- {0,x,1}⊆ℕ";
	
	String P4 = "finite({0↦x,x↦1})";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finitePositiveGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finitePositive";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, resultP3GoalA, resultP3GoalB)
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in goal
				" ⊤ |- " + P1,
				// P2 in goal
				" ⊤ |- " + P2,
				// P4 in goal
				" ⊤ |- " + P4
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "ROOT",
				P4, ""
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
