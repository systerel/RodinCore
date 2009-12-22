/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
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
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunction;

/**
 * Unit tests for the Finite of function reasoner {@link FiniteFunction}
 * 
 * @author htson
 */
public class FiniteFuntionTests extends AbstractSingleExpressionInputReasonerTests {

	String P1 = "finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)})";

	String resultP1GoalA = "{x=ℤ}[][][⊤] |- {0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∈ℕ ⇸ ℕ × ℕ";

	String resultP1GoalB = "{x=ℤ}[][][⊤] |- finite(ℕ)";
		
	String P2 = "x = 1 ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)})";

	String P3 = "finite({0 ↦ 3,1 ↦ x,1 ↦ 2}[{x}])";

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "ROOT",
				P2, "",
				P3, ""
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteFunctionGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteFunction";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, "ℕ⇸ℕ × ℕ", resultP1GoalA, resultP1GoalB),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in goal
				" ⊤ |- " + P1,
				null,
				"ℕ ↔ BOOL × ℕ",
				"Expected a set of all partial functions S ⇸ T",
				// P1 in goal
				" ⊤ |- " + P1,
				null,
				"ℕ ⇸ BOOL × ℕ",
				"Type check failed for " + "{0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∈ℕ ⇸ BOOL × ℕ",
				// P2 in goal
				" ⊤ |- " + P2,
				null,
				"ℕ",
				"Goal is not a finiteness",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"ℕ",
				"Goal is not a finiteness of a relation"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
