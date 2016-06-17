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
public class CardUpToTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ card(1‥x) = 2";

	String P2 = "∀x· x = 2 ⇒ card(1‥x) = 2";

	String P3 = "card(1‥x) = 2";

	String resultP3GoalA = "{x=ℤ}[][][⊤;; 1≤x] |- x − 1+1=2";

	String resultP3GoalB = "{x=ℤ}[][][⊤;; x<1] |- 0=2";

	String resultP3HypA = "{x=ℤ}[card(1 ‥ x)=2][][1≤x;; x − 1+1=2] |- ⊤";

	String resultP3HypB = "{x=ℤ}[card(1 ‥ x)=2][][x<1;; 0=2] |- ⊤";

	String P4 = "2 = card(1‥x)";

	String resultP4GoalA = "{x=ℤ}[][][⊤;; 1≤x] |- 2=x − 1+1";

	String resultP4GoalB = "{x=ℤ}[][][⊤;; x<1] |- 2=0";

	String resultP4HypA = "{x=ℤ}[2=card(1 ‥ x)][][1≤x;; 2=x − 1+1] |- ⊤";

	String resultP4HypB = "{x=ℤ}[2=card(1 ‥ x)][][x<1;; 2=0] |- ⊤";

	String P5 = "¬card(1‥x) = 2";

	String resultP5GoalA = "{x=ℤ}[][][⊤;; 1≤x] |- ¬x − 1+1=2";

	String resultP5GoalB = "{x=ℤ}[][][⊤;; x<1] |- ¬0=2";

	String resultP5HypA = "{x=ℤ}[¬card(1 ‥ x)=2][][1≤x;; ¬x − 1+1=2] |- ⊤";

	String resultP5HypB = "{x=ℤ}[¬card(1 ‥ x)=2][][x<1;; ¬0=2] |- ⊤";

	String P6 = "¬2 = card(1‥x)";

	String resultP6GoalA = "{x=ℤ}[][][⊤;; 1≤x] |- ¬2=x − 1+1";

	String resultP6GoalB = "{x=ℤ}[][][⊤;; x<1] |- ¬2=0";

	String resultP6HypA = "{x=ℤ}[¬2=card(1 ‥ x)][][1≤x;; ¬2=x − 1+1] |- ⊤";

	String resultP6HypB = "{x=ℤ}[¬2=card(1 ‥ x)][][x<1;; ¬2=0] |- ⊤";

	
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
		return Tactics.cardUpToGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.cardUpTo";
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
				"0.0"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
