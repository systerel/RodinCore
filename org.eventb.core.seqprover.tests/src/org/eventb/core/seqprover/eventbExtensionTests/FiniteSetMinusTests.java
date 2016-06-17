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

/**
 * Unit tests for the Finite of intersection reasoner {@link FiniteInter}
 * 
 * @author htson
 */
public class FiniteSetMinusTests extends AbstractEmptyInputReasonerTests {

	String P1 = "(x = 2) ⇒ finite(S ∖ {0 ↦ 3})";

	String P2 = "∀x· x = 2 ⇒ finite(S ∖ {0 ↦ 3})";

	String P3 = "finite(S ∖ {0 ↦ 3})";

	String resultP3Goal = "{S=ℙ(ℤ×ℤ)}[][][⊤] |- finite(S)";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteSetMinusGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteSetMinus";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, resultP3Goal)
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in goal
				" ⊤ |- " + P1,
				// P2 in goal
				" ⊤ |- " + P2
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "ROOT",
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
