/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.GeneralizedModusPonensL1;

/**
 * Unit tests for the reasoner GeneralizedModusPonensL1.
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonensTestsL1 extends GeneralizedModusPonensTests {

	public GeneralizedModusPonensTestsL1() {
		super(new GeneralizedModusPonensL1());
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Apply once in the hypothesis 1/2 (FALSE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P⇒2∈P |- 1∈P "), new EmptyInput(),
						"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒2∈P] |- 1∈P"),
				// Apply once in the hypothesis 2/2 (FALSE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" ¬1∈P⇒2∈P |- 1∈P "), new EmptyInput(),
						"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][¬⊥⇒2∈P] |- 1∈P"),
				// Apply once in the hypothesis 1/2 (TRUE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P⇒2∈P |- ¬1∈P "), new EmptyInput(),
						"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊤⇒2∈P] |- ¬1∈P"),
				// Apply once in the hypothesis 2/2 (TRUE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" ¬1∈P⇒2∈P |- ¬1∈P "), new EmptyInput(),
						"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][¬⊤⇒2∈P] |- ¬1∈P"),
				// Apply many in the hypothesis 1/2
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P⇒2∈P |- 1∈P∨2∈P "),
						new EmptyInput(), "{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒⊥] |- 1∈P∨2∈P"),
				// Apply many in the hypothesis 2/2
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P⇒2∈P |- 1∈P∨¬2∈P "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒⊤] |- 1∈P∨¬2∈P"),
				// Re-writing is prioritary proceeded using hypotheses
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; 1∈P⇒2∈P |- 1∈P∨2∈P "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒⊥] |- ⊤∨2∈P"),
				// Sequent (P⊢P) is not re-written (⊥⊢P) or (P⊢⊤), event when
				// the goal denotes a disjunction.
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P∨2∈P |- 1∈P∨2∈P "),
						new EmptyInput(), "{P=ℙ(ℤ)}[1∈P∨2∈P][][⊥∨⊥] |- 1∈P∨2∈P") };
	}

}
