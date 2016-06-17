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
import org.eventb.internal.core.seqprover.eventbExtensions.FunCompImg;

/**
 * Unit tests for the Function Composition  image reasoner
 * {@link FunCompImg}
 * 
 * @author htson
 */
public class FunCompImgTests extends AbstractManualInferenceTests {

	String P1 = "({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x) = 3";
	
	String resultP1GoalA = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- (f;{x ↦ 3};{x ↦ 4})({x ↦ 1}(x))=3";
	
	String resultP1GoalB = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- x∈dom({x ↦ 1})∧{x ↦ 1}∈ℤ ⇸ ℤ∧{x ↦ 1}(x)∈dom(f;{x ↦ 3};{x ↦ 4})∧f;{x ↦ 3};{x ↦ 4}∈ℤ ⇸ ℤ";

	String resultP1HypA = "{f=ℤ↔ℤ; x=ℤ}[({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x)=3][][(f;{x ↦ 3};{x ↦ 4})({x ↦ 1}(x))=3] |- ⊤";
	
	String resultP1HypB = "{f=ℤ↔ℤ; x=ℤ}[][][({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x)=3] |- x∈dom({x ↦ 1})∧{x ↦ 1}∈ℤ ⇸ ℤ∧{x ↦ 1}(x)∈dom(f;{x ↦ 3};{x ↦ 4})∧f;{x ↦ 3};{x ↦ 4}∈ℤ ⇸ ℤ";

	String P2 = "({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x) = 3";
	
	String resultP2GoalA = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- ({x ↦ 3};{x ↦ 4})(({x ↦ 1};f)(x))=3";
	
	String resultP2GoalB = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- x∈dom({x ↦ 1};f)∧{x ↦ 1};f∈ℤ ⇸ ℤ∧({x ↦ 1};f)(x)∈dom({x ↦ 3};{x ↦ 4})∧{x ↦ 3};{x ↦ 4}∈ℤ ⇸ ℤ";

	String resultP2HypA = "{f=ℤ↔ℤ; x=ℤ}[({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x)=3][][({x ↦ 3};{x ↦ 4})(({x ↦ 1};f)(x))=3] |- ⊤";
	
	String resultP2HypB = "{f=ℤ↔ℤ; x=ℤ}[][][({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x)=3] |- x∈dom({x ↦ 1};f)∧{x ↦ 1};f∈ℤ ⇸ ℤ∧({x ↦ 1};f)(x)∈dom({x ↦ 3};{x ↦ 4})∧{x ↦ 3};{x ↦ 4}∈ℤ ⇸ ℤ";

	String P3 = "({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x) = 3";
	
	String resultP3GoalA = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- {x ↦ 4}(({x ↦ 1};f;{x ↦ 3})(x))=3";
	
	String resultP3GoalB = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- x∈dom({x ↦ 1};f;{x ↦ 3})∧{x ↦ 1};f;{x ↦ 3}∈ℤ ⇸ ℤ∧({x ↦ 1};f;{x ↦ 3})(x)∈dom({x ↦ 4})∧{x ↦ 4}∈ℤ ⇸ ℤ";

	String resultP3HypA = "{f=ℤ↔ℤ; x=ℤ}[({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x)=3][][{x ↦ 4}(({x ↦ 1};f;{x ↦ 3})(x))=3] |- ⊤";
	
	String resultP3HypB = "{f=ℤ↔ℤ; x=ℤ}[][][({x ↦ 1};f;{x ↦ 3};{x ↦ 4})(x)=3] |- x∈dom({x ↦ 1};f;{x ↦ 3})∧{x ↦ 1};f;{x ↦ 3}∈ℤ ⇸ ℤ∧({x ↦ 1};f;{x ↦ 3})(x)∈dom({x ↦ 4})∧{x ↦ 4}∈ℤ ⇸ ℤ";

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "0.0.1\n"+"0.0.2\n"+"0.0.3",
				P2, "0.0.1\n"+"0.0.2\n"+"0.0.3",
				P3, "0.0.1\n"+"0.0.2\n"+"0.0.3",
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.funCompImgGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funCompImg";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, null, "0.0.1", resultP1GoalA,
						resultP1GoalB),
				// P1 in hyp
				new SuccessfulTest(P1 + " |- ⊤ ", P1, "0.0.1", resultP1HypA,
						resultP1HypB),
				// P2 in goal
				new SuccessfulTest(" ⊤ |- " + P2, null, "0.0.2", resultP2GoalA,
						resultP2GoalB),
				// P2 in hyp
				new SuccessfulTest(P2 + " |- ⊤ ", P1, "0.0.2", resultP2HypA,
						resultP2HypB),
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "0.0.3", resultP3GoalA,
						resultP3GoalB),
				// P3 in hyp
				new SuccessfulTest(P3 + " |- ⊤ ", P3, "0.0.3", resultP3HypA,
						resultP3HypB),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in hyp
				P1 + " |- ⊤ ",
				P1,
				"0.0.0",
				// P1 in goal
				" ⊤ |- " + P1,
				null,
				"0.0.0",
				// P2 in hyp
				P2 + " |- ⊤ ",
				P2,
				"0.0.0",
				// P2 in goal
				" ⊤ |- " + P2,
				null,
				"0.0.0",
				// P3 in hyp
				P3 + " |- ⊤ ",
				P3,
				"0.0.0",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"0.0.0"
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
