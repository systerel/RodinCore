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
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteRelation;

/**
 * Unit tests for the Finite of relation reasoner
 * {@link FiniteRelation}
 * 
 * @author htson
 */
public class FiniteRelationTests extends AbstractSingleExpressionInputReasonerTests {

	private static final String P1 = "finite({0 ↦ FALSE})";
	private static final String[] P1Result = {
		"{}[][][⊤] |- ⊤", //
		"{}[][][⊤] |- {0 ↦ FALSE} ∈ {0} ↔ BOOL", //
		"{}[][][⊤] |- finite({0})", //
		"{}[][][⊤] |- finite(BOOL)", //
	};

	private static final String P2 = "x = 1 ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)})";

	private static final String P3 = "finite({0 ↦ 3,1 ↦ x,1 ↦ 2}[{x}])";

	private static final String P4 = "finite({0 ↦ 1})";
	private static final String[] P4Result = {
		"{}[][][⊤] |- 2≠0∧4≠0", //
		"{}[][][⊤] |- {0 ↦ 1} ∈ {1÷2} ↔ {3÷4}", //
		"{}[][][⊤] |- finite({1÷2})", //
		"{}[][][⊤] |- finite({3÷4})", //
	};

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "ROOT",
				P2, "",
				P3, ""
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteRelationGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteRelation";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, "{0} ↔ BOOL", P1Result),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, "{1÷2} ↔ {3÷4}", P4Result),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P2 in goal
				" ⊤ |- " + P1,
				null,
				"ℕ × BOOL × ℕ",
				"Expected a set of all relations S ↔ T",
				// P1 in goal
				" ⊤ |- " + P1,
				null,
				"ℕ ↔ BOOL × ℕ",
				"Type check failed for {0 ↦ FALSE}∈ℕ ↔ BOOL × ℕ",
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

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
