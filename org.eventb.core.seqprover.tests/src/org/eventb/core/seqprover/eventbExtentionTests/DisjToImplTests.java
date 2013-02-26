/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - port to AbstractManualRewriterTests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.disjToImplGetPositions;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjunctionToImplicationRewrites;

/**
 * Unit tests for the disjunction to implication reasoner
 * 
 * @author htson
 */
public class DisjToImplTests extends AbstractManualRewriterTests {

	@Override
	public String getReasonerID() {
		return DisjunctionToImplicationRewrites.REASONER_ID;
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				//
				new SuccessfulTest(//
						"x=1 ∨ x=2 ∨ x=3",//
						"",//
						"¬x=1 ⇒ (x=2 ∨ x=3)"),//
				new SuccessfulTest(//
						"x = 1 ⇒ x = 2 ∨ x = 3 ∨ x = 4",//
						"1",//
						"x=1 ⇒ (¬x=2 ⇒ x=3 ∨ x=4)"),//
				new SuccessfulTest(//
						"∀x· x=0 ⇒ x=2 ∨ x=3 ∨ x=4",//
						"1.1",//
						"∀x· x=0 ⇒ (¬x=2 ⇒ x=3 ∨ x=4)"),//
				new SuccessfulTest(//
						"(x=1 ∨ x=2) ∧ x=3",//
						"0",//
						"¬x=1 ⇒ x=2", "x=3"), };
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// Not applicable
				"⊤", "",//
				// Incorrect position
				"x=1 ∨ x=2 ∨ x=3", "0",//
				"x=1 ⇒ x=2 ∨ x=3 ∨ x=4", "0.1",//
				"∀x· x=0 ⇒ x=2 ∨ x=3 ∨ x=4", "0",//
				"(x=1 ∨ x=2) ∧ x=3", "1",//
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				//
				"x=1 ∨ x=2 ∨ x=3", "ROOT",//
				"x=1 ⇒ x=2 ∨ x=3 ∨ x=4", "1",//
				"∀x· x=0 ⇒ x=2 ∨ x=3 ∨ x=4", "1.1",//
				"(x=1 ∨ x=2) ∧ x=3", "0",//
		};
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return disjToImplGetPositions(predicate);
	}

}
