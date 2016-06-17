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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.InclusionSetMinusRightRewrites;

/**
 * Unit tests for the Rewrite inclusion with set minus on the right reasoner
 * {@link InclusionSetMinusRightRewrites}
 * 
 * @author htson
 */
public class InclusionSetMinusLeftTests extends AbstractManualRewriterTests {
	
	// A \ B <: S == A <: S \/ B
	private final String P1 = "A ∖ B ⊆ {x ∣ x > 0} ";

	private final String resultP1 = "A ⊆ {x ∣ x > 0} ∪ B";
	
	private final String P2 = "∀x·x = 1 ⇒ {x ↦ {2}, 2 ↦ {3}} ∖ {2 ↦ {x}} ⊆ {1 ↦ {x}}";

	private final String resultP2 = "∀x·x=1⇒{x ↦ {2}, 2 ↦ {3}} ⊆ {1 ↦ {x}} ∪ {2 ↦ {x}}";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.inclusionSetMinusLeftRewrites";
	}

	public String [] getTestGetPositions() {
		return new String[] {
				P1, "ROOT",
				P2, "1.1",
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.inclusionSetMinusLeftRewritesGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "1.0",
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
