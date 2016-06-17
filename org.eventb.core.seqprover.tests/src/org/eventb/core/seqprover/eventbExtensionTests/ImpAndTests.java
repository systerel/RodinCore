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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ImpAndRewrites;

/**
 * Unit tests for the {@link ImpAndRewrites} reasoner
 * 
 * @author htson
 *
 */
public class ImpAndTests extends AbstractManualRewriterTests {

	String P1 = "x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3";

	String resultP1a = "x=0⇒x=1";
	
	String resultP1b = "x=0⇒x=2";
	
	String resultP1c = "x=0⇒x=3";

	String P2 = "x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3)";

	String resultP2 = "x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)";

	String P3 = "∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3)";
	
	String resultP3 = "∀x·x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)";

	String P4 = "∀x·x = 4 ⇒ ((x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) ∧ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3))";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.impAndRewrites";
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "", resultP1a, resultP1b, resultP1c),
				new SuccessfulTest(P2, "1", resultP2),
				new SuccessfulTest(P3, "1.1", resultP3)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "0",
				P3, "0.1"
		};
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.impAndGetPositions(predicate);
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "ROOT",
				P2, "1",
				P3, "1.1",
				P4, "1\n" + "1.1.0\n" + "1.1.1" 
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}