/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - adapted to common test framework
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstrExpr;

/**
 * Unit tests for the Abstract expression reasoner
 * 
 * TODO : test that WD lemmas are also added to the hyps
 * 
 * @author Farhad Mehta
 */
public class AbstrExprTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return new AbstrExpr().getReasonerID();
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Expression not parsable
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- ⊤ "),
						new SingleExprInput("@unparsable@", ff.makeTypeEnvironment())
				),
				// Expression not typecheckable
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- ⊤ "),
						new SingleExprInput("x", ff.makeTypeEnvironment())
				),	
		};
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final IProverSequent seq = TestLib.genSeq(" x=1 ;; x+1 = 2 |- (x+1)+1 = 3 ");
		return new SuccessfullReasonerApplication[]{
				// hyp not univ quantified implication, but still univ quantified
				new SuccessfullReasonerApplication(seq, 
						new SingleExprInput("x+1",seq.typeEnvironment()),
						"{x=ℤ}[][][x=1;; x+1=2] |- ⊤",
						"{ae=ℤ; x=ℤ}[][][x=1;; x+1=2;; ae=x+1] |- (x+1)+1=3"
				),
		};
	}
	
}
