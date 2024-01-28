/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use new test methods
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.InclusionSetMinusRightRewrites;

/**
 * Unit tests for the Rewrite inclusion with set minus on the right reasoner
 * {@link InclusionSetMinusRightRewrites}
 * 
 * @author htson
 */
public class InclusionSetMinusRightTests extends AbstractManualRewriterTests {
	
	// S <: A \ B == S <: A & S /\ B = {} 
	String P1 = "{x} ⊆ {x+1} ∖ {x+2}";

	String resultP1A = "{x} ⊆ {x+1}";

	String resultP1B = "{x} ∩ {x+2} = ∅";

	String P2 = "0=1 ⇒ {x} ⊆ {x+1} ∖ {x+2}";

	String resultP2 = "0=1 ⇒ {x} ⊆ {x+1} ∧ {x} ∩ {x+2} = ∅";

	String P3 = "∀x· x=1 ⇒ {x} ⊆ {x+1} ∖ {x+2}";

	String resultP3 = "∀x· x=1 ⇒ {x} ⊆ {x+1} ∧ {x} ∩ {x+2} = ∅";
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.inclusionSetMinusRightRewrites";
	}

	public String [] getTestGetPositions() {
		return new String[] {
				P1, "ROOT",
				P2, "1",
				P3, "1.1",
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.inclusionSetMinusRightRewritesGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "", resultP1A, resultP1B),
				new SuccessfulTest(P2, "1", resultP2),
				new SuccessfulTest(P3, "1.1", resultP3),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "0",
				P3, "1.0",
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
