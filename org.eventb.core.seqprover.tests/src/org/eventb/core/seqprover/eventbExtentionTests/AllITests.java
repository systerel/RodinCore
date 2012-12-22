/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;

/**
 * Acceptance tests for reasoner AllI.
 * 
 * @author Laurent Voisin
 */
public class AllITests extends AbstractReasonerTests {
	
	private static final EmptyInput NO_INPUT = new EmptyInput();

	@Override
	public String getReasonerID() {
		return AllI.REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				// one bound variable
				new SuccessfullReasonerApplication(genSeq("|- ∀x·x=1"), 
						NO_INPUT,
						"{}[][][] |- x=1"
				),
				// two predicates generated
				new SuccessfullReasonerApplication(genSeq("|- ∀x·x=1 ∧ x=2"), 
						NO_INPUT,
						"{}[][][] |- x=1 ∧ x=2"
				),
				// two bound variables
				new SuccessfullReasonerApplication(genSeq("|- ∀x,y·x↦y = 1↦2"), 
						NO_INPUT,
						"{}[][][] |- x↦y = 1↦2"
				),
				// name collision
				new SuccessfullReasonerApplication(genSeq("x=3 |- ∀x·x=1"), 
						NO_INPUT,
						"{}[][][x=3] |- x0=1"
				),
				// name collision, different type
				new SuccessfullReasonerApplication(genSeq("x=TRUE |- ∀x·x=1"), 
						NO_INPUT,
						"{}[][][x=TRUE] |- x0=1"
				),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// goal not quantified
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- ⊥ "),
						NO_INPUT,
						"Goal is not universally quantified"
				),	
				// goal not universally quantified
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- ∃x·x=1 "),
						NO_INPUT,
						"Goal is not universally quantified"
				),	
		};
	}

}
