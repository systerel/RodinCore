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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AndOrDistRewrites;

/**
 * Unit tests for the And/Or Distribution Rewrites reasoner
 * {@link AndOrDistRewrites}
 * 
 * @author htson
 */
public class AndOrDistTests extends AbstractManualRewriterTests {

	// P or ... or (Q & ... & R) or ... or S ==
	//          (P or ... or Q or ... or S) & ... & (P or ... or R or ... or S)
	String P1 = "x = 1 ∨ x = 2 ∨ (x = 3 ∧ x = 4 ∧ x = 5) ∨ x = 6 ∨ x = 7";

	String resultP1A = "x=1∨x=2∨x=3∨x=6∨x=7";

	String resultP1B = "x=1∨x=2∨x=4∨x=6∨x=7";

	String resultP1C = "x=1∨x=2∨x=5∨x=6∨x=7";

	String P2 = "1 = x ⇒ (x = 1 ∨ x = 2 ∨ (x = 3 ∧ x = 4 ∧ x = 5) ∨ x = 6 ∨ x = 7)";

	String resultP2 = "1=x⇒(x=1∨x=2∨x=3∨x=6∨x=7)∧(x=1∨x=2∨x=4∨x=6∨x=7)∧(x=1∨x=2∨x=5∨x=6∨x=7)";

	String P3 = "∀x·x = 0 ⇒ (x = 1 ∨ x = 2 ∨ (x = 3 ∧ x = 4 ∧ x = 5) ∨ x = 6 ∨ x = 7)";

	String resultP3 = "∀x·x=0⇒(x=1∨x=2∨x=3∨x=6∨x=7)∧(x=1∨x=2∨x=4∨x=6∨x=7)∧(x=1∨x=2∨x=5∨x=6∨x=7)";


	// P & ... & (Q or ... or R) & ... & S ==
	//          (P & ... & Q & ... & S) or ... or (P & ... & R & ... & S)
	String P4 = "x = 1 ∧ x = 2 ∧ (x = 3 ∨ x = 4 ∨ x = 5) ∧ x = 6 ∧ x = 7";

	String resultP4 = "(x=1∧x=2∧x=3∧x=6∧x=7)∨(x=1∧x=2∧x=4∧x=6∧x=7)∨(x=1∧x=2∧x=5∧x=6∧x=7)";

	String P5 = "1 = x ⇒ x = 1 ∧ x = 2 ∧ (x = 3 ∨ x = 4 ∨ x = 5) ∧ x = 6 ∧ x = 7";

	String resultP5 = "1=x⇒(x=1∧x=2∧x=3∧x=6∧x=7)∨(x=1∧x=2∧x=4∧x=6∧x=7)∨(x=1∧x=2∧x=5∧x=6∧x=7)";

	String P6 = "∀x·x = 0 ⇒ (x = 1 ∧ x = 2 ∧ (x = 3 ∨ x = 4 ∨ x = 5) ∧ x = 6 ∧ x = 7)";

	String resultP6 = "∀x·x=0⇒(x=1∧x=2∧x=3∧x=6∧x=7)∨(x=1∧x=2∧x=4∧x=6∧x=7)∨(x=1∧x=2∧x=5∧x=6∧x=7)";


	// Failure
	String P7 = "x = 1 ∨ x = 2 ∨ (x = 3 ∨ x = 4 ∨ x = 5) ∨ x = 6 ∨ x = 7";

	String P8 = "1 = x ⇒ (x = 1 ∨ x = 2 ∨ (x = 3 ∨ x = 4 ∨ x = 5) ∨ x = 6 ∨ x = 7)";

	String P9 = "∀x·x = 0 ⇒ (x = 1 ∨ x = 2 ∨ (x = 3 ∨ x = 4 ∨ x = 5) ∨ x = 6 ∨ x = 7)";
	
	String P10 = "x = 1 ∧ x = 2 ∧ (x = 3 ∧ x = 4 ∧ x = 5) ∧ x = 6 ∧ x = 7";

	String P11 = "1 = x ⇒ (x = 1 ∧ x = 2 ∧ (x = 3 ∧ x = 4 ∧ x = 5) ∧ x = 6 ∧ x = 7)";

	String P12 = "∀x·x = 0 ⇒ (x = 1 ∧ x = 2 ∧ (x = 3 ∧ x = 4 ∧ x = 5) ∧ x = 6 ∧ x = 7)";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.andOrDistRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.andOrDistGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "2", resultP1A, resultP1B, resultP1C),
				new SuccessfulTest(P2, "1.2", resultP2),
				new SuccessfulTest(P3, "1.1.2", resultP3),
				new SuccessfulTest(P4, "2", resultP4),
				new SuccessfulTest(P5, "1.2", resultP5),
				new SuccessfulTest(P6, "1.1.2", resultP6),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "3",
				P2, "1.3",
				P3, "1.1.3",
				P4, "3",
				P5, "1.3",
				P6, "1.1.3",
				P7, "2",
				P8, "1.2",
				P9, "1.1.2",
				P10, "2",
				P11, "1.2",
				P12, "1.1.2"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "2",
				P2, "1.2",
				P3, "1.1.2",
				P4, "2",
				P5, "1.2",
				P6, "1.1.2",
				P7, "",
				P8, "",
				P9, "",
				P10, "",
				P11, "",
				P12, ""
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
