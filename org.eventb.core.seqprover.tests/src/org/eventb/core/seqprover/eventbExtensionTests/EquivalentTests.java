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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.EqvRewrites;

/**
 * Unit tests for the Equivalence Rewrites reasoner
 * {@link EqvRewrites}
 * 
 * @author htson
 */
public class EquivalentTests extends AbstractManualRewriterTests {

	// P <=> Q == (P => Q) & (Q => P)
	String P1 = "(1 = x) ⇒ (y = 1 ⇔ x = y)";

	String resultP1 = "1=x⇒(y=1⇒x=y)∧(x=y⇒y=1)";

	String P2 = "∀x·x = 0 ⇒ (x = y ⇔ y = 1)";

	String resultP2 = "∀x·x=0⇒(x=y⇒y=1)∧(y=1⇒x=y)";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.eqvRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.eqvGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "1", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "1.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "1",
				P2, "1.1"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
