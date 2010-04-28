/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FunOvr;

/**
 * Unit tests for the Function override reasoner {@link FunOvr}
 * 
 * @author htson
 */
public class FunOvrTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ (f  g  {x ↦ 3})(y) = 3";

	String P2 = "∀x· x = 2 ⇒ (f  g  {x ↦ 3})(y) = 3";

	String P3 = "(f  g  {2 ↦ 3})(y) = 3";

	String resultP3GoalA = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤;; y=2] |- 3=3";
	String resultP3GoalB = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤;; ¬y=2] |- ({2} ⩤ (fg))(y)=3";

	String resultP3HypA = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[(fg{2 ↦ 3})(y)=3][][y=2;; 3=3] |- ⊤";

	String resultP3HypB = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[(fg{2 ↦ 3})(y)=3][][¬y=2;; ({2} ⩤ (fg))(y)=3] |- ⊤";

	String P4 = "3 = (f  {2 ↦ 3}  h)(y)";

	String resultP4GoalA = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤;; y∈dom(h)] |- 3=h(y)";

	String resultP4GoalB = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤;; ¬y∈dom(h)] |- 3=(dom(h)⩤(f{2 ↦ 3}))(y)";

	String resultP4HypA = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[3=(f{2 ↦ 3}h)(y)][][y∈dom(h);; 3=h(y)] |- ⊤";

	String resultP4HypB = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[3=(f{2 ↦ 3}h)(y)][][¬y∈dom(h);; 3=(dom(h)⩤(f{2 ↦ 3}))(y)] |- ⊤";
	
	String P5 = "¬((f  g  {2 ↦ 3})(y) = 3)";

	String resultP5GoalA = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤;; y=2] |- ¬3=3";

	String resultP5GoalB = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤;; ¬y=2] |- ¬({2} ⩤ (fg))(y)=3";

	String resultP5HypA = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[¬(fg{2 ↦ 3})(y)=3][][y=2;; ¬3=3] |- ⊤";

	String resultP5HypB = "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[¬(fg{2 ↦ 3})(y)=3][][¬y=2;; ¬({2} ⩤ (fg))(y)=3] |- ⊤";

	String P6 = "¬3 = (f  {2 ↦ 3}  h)(y)";

	String resultP6GoalA = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤;; y∈dom(h)] |- ¬3=h(y)";

	String resultP6GoalB = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤;; ¬y∈dom(h)] |- ¬3=(dom(h) ⩤ (f{2 ↦ 3}))(y)";

	String resultP6HypA = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[¬3=(f{2 ↦ 3}h)(y)][][y∈dom(h);; ¬3=h(y)] |- ⊤";

	String resultP6HypB = "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[¬3=(f{2 ↦ 3}h)(y)][][¬y∈dom(h);; ¬3=(dom(h) ⩤ (f{2 ↦ 3}))(y)] |- ⊤";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.funOvrGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funOvr";
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
				// P3 in hyp
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
				"0.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "0",
				P4, "1",
				P5, "0.0",
				P6, "0.1"
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
