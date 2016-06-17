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
import org.eventb.internal.core.seqprover.eventbExtensions.FunInterImg;

/**
 * Unit tests for the Function apply to intersection image reasoner
 * {@link FunInterImg}
 * 
 * @author htson
 */
public class FunInterImgTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ f[{x,3} ∩ {2}] = {3}";

	String P2 = "∀x· x = 2 ⇒ {3} = f[{x,3} ∩ {2}]";

	String P3 = "f[{x,3} ∩ {2}] = {3}";

	String resultP3GoalA = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";

	String resultP3GoalB = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- f[{x,3}]∩f[{2}]={3}";

	String resultP3HypA = "{f=ℤ↔ℤ; x=ℤ}[][][f[{x,3}∩{2}]={3}] |- f∼∈ℤ ⇸ ℤ";

	String resultP3HypB = "{f=ℤ↔ℤ; x=ℤ}[f[{x,3}∩{2}]={3}][][f[{x,3}]∩f[{2}]={3}] |- ⊤";

	String P4 = "{3} = f[{x,3} ∩ {2}]";

	String resultP4GoalA = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";

	String resultP4GoalB = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- {3}=f[{x,3}]∩f[{2}]";

	String resultP4HypA = "{f=ℤ↔ℤ; x=ℤ}[][][{3}=f[{x,3}∩{2}]] |- f∼∈ℤ ⇸ ℤ";

	String resultP4HypB = "{f=ℤ↔ℤ; x=ℤ}[{3}=f[{x,3}∩{2}]][][{3}=f[{x,3}]∩f[{2}]] |- ⊤";

	String P5 = "¬f[{x,3} ∩ {2}] = {3}";

	String resultP5GoalA = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";

	String resultP5GoalB = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- ¬f[{x,3}]∩f[{2}]={3}";

	String resultP5HypA = "{f=ℤ↔ℤ; x=ℤ}[][][¬f[{x,3}∩{2}]={3}] |- f∼∈ℤ ⇸ ℤ";

	String resultP5HypB = "{f=ℤ↔ℤ; x=ℤ}[¬f[{x,3}∩{2}]={3}][][¬f[{x,3}]∩f[{2}]={3}] |- ⊤";

	String P6 = "¬{3} = f[{x,3} ∩ {2}]";

	String resultP6GoalA = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";

	String resultP6GoalB = "{f=ℤ↔ℤ; x=ℤ}[][][⊤] |- ¬{3}=f[{x,3}]∩f[{2}]";

	String resultP6HypA = "{f=ℤ↔ℤ; x=ℤ}[][][¬{3}=f[{x,3}∩{2}]] |- f∼∈ℤ ⇸ ℤ";

	String resultP6HypB = "{f=ℤ↔ℤ; x=ℤ}[¬{3}=f[{x,3}∩{2}]][][¬{3}=f[{x,3}]∩f[{2}]] |- ⊤";

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "",
				P2, "",
				P3, "0",
				P4, "1",
				P5, "0.0",
				P6, "0.1"
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.funInterImgGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funInterImg";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "0", resultP3GoalA,
						resultP3GoalB),
				// P3 in hyp
				new SuccessfulTest(P3 + " |- ⊤ ", P3, "0", resultP3HypA,
						resultP3HypB),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, null, "1", resultP4GoalA,
						resultP4GoalB),
				// P4 in hyp
				new SuccessfulTest(P4 + " |- ⊤ ", P4, "1", resultP4HypA,
						resultP4HypB),
				// P5 in goal
				new SuccessfulTest(" ⊤ |- " + P5, null, "0.0", resultP5GoalA,
						resultP5GoalB),
				// P5 in hyp
				new SuccessfulTest(P5 + " |- ⊤ ", P5, "0.0", resultP5HypA,
						resultP5HypB),
				// P6 in goal
				new SuccessfulTest(" ⊤ |- " + P6, null, "0.1", resultP6GoalA,
						resultP6GoalB),
				// P6 in hyp
				new SuccessfulTest(P6 + " |- ⊤ ", P6, "0.1", resultP6HypA,
						resultP6HypB), };
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P3 in hyp
				P3 + " |- ⊤ ",
				P3,
				"1",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"1",
				// P4 in hyp
				P4 + " |- ⊤ ",
				P4,
				"0",
				// P4 in goal
				" ⊤ |- " + P4,
				null,
				"0",
				// P5 in hyp
				P5 + " |- ⊤ ",
				P5,
				"0.1",
				// P5 in goal
				" ⊤ |- " + P5,
				null,
				"0.1",
				// P6 in hyp
				P6 + " |- ⊤ ",
				P6,
				"0.0",
				// P6 in goal
				" ⊤ |- " + P6,
				null,
				"0.0",
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
