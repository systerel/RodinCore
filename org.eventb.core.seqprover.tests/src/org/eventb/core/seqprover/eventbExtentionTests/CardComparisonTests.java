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
import org.eventb.internal.core.seqprover.eventbExtensions.CardComparison;

/**
 * Unit tests for the Cardinal Comparison Rewrites reasoner
 * {@link CardComparison}
 * 
 * @author htson
 */
public class CardComparisonTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ card(1‥x) = card(x‥3)";

	String P2 = "∀x· x = 2 ⇒ card(1‥x) = card(x‥3)";

	String P3 = "card(1‥x) = card(x‥3)";

	String resultP3Goal = "{x=ℤ}[][][⊤] |- 1 ‥ x=x ‥ 3";

	String P4 = "(x = 2) ⇒ card(1‥x) ≤ card(x‥3)";

	String P5 = "∀x· x = 2 ⇒ card(1‥x) ≤ card(x‥3)";

	String P6 = "card(1‥x) ≤ card(x‥3)";

	String resultP6Goal = "{x=ℤ}[][][⊤] |- 1 ‥ x⊆x ‥ 3";
	
	String P7 = "(x = 2) ⇒ card(1‥x) < card(x‥3)";

	String P8 = "∀x· x = 2 ⇒ card(1‥x) < card(x‥3)";

	String P9 = "card(1‥x) < card(x‥3)";

	String resultP9Goal = "{x=ℤ}[][][⊤] |- 1 ‥ x⊂x ‥ 3";
	
	String P10 = "(x = 2) ⇒ card(1‥x) ≥ card(x‥3)";

	String P11 = "∀x· x = 2 ⇒ card(1‥x) ≥ card(x‥3)";

	String P12 = "card(1‥x) ≥ card(x‥3)";

	String resultP12Goal = "{x=ℤ}[][][⊤] |- x ‥ 3⊆1 ‥ x";
	
	String P13 = "(x = 2) ⇒ card(1‥x) > card(x‥3)";

	String P14 = "∀x· x = 2 ⇒ card(1‥x) > card(x‥3)";

	String P15 = "card(1‥x) > card(x‥3)";

	String resultP15Goal = "{x=ℤ}[][][⊤] |- x ‥ 3⊂1 ‥ x";
	
	String P16 = "¬ card(1‥x) > card(x‥3)";

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "",
				P2, "",
				P3, "ROOT",
				P4, "",
				P5, "",
				P6, "ROOT",
				P7, "",
				P8, "",
				P9, "ROOT",
				P10, "",
				P11, "",
				P12, "ROOT",
				P13, "",
				P14, "",
				P15, "ROOT",
				P16, ""
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.cardComparisonGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.cardComparison";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "", resultP3Goal),
				// P6 in goal
				new SuccessfulTest(" ⊤ |- " + P6, null, "", resultP6Goal),
				// P9 in goal
				new SuccessfulTest(" ⊤ |- " + P9, null, "", resultP9Goal),
				// P12 in goal
				new SuccessfulTest(" ⊤ |- " + P12, null, "", resultP12Goal),
				// P15 in goal
				new SuccessfulTest(" ⊤ |- " + P15, null, "", resultP15Goal),
			};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P3 in hyp
				P3 + " |- ⊤ ",
				P3,
				"",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"1",
				// P6 in hyp
				P6 + " |- ⊤ ",
				P6,
				"",
				// P6 in goal
				" ⊤ |- " + P6,
				null,
				"1",
				// P9 in hyp
				P9 + " |- ⊤ ",
				P9,
				"",
				// P9 in goal
				" ⊤ |- " + P9,
				null,
				"1",
				// P16 in goal
				" ⊤ |- " + P16,
				null,
				"0",
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4FreeCore.externalPP(false);
//	}

}
