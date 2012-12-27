/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AllD;
import org.eventb.internal.core.seqprover.eventbExtensions.AllmtD;

/**
 * Unit tests for the AllmtD reasoner
 */
public class AllmtDTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new AllmtD()).getReasonerID();
	}
	
	final static Predicate hyp = TestLib.genPred(" ∀x,y· x ∈ ℕ ∧ y ∈ ℕ  ⇒ x ∈ P ∧ y ∈ Q ");
	final static IProverSequent seq = TestLib.genSeq(" ∀x,y· x ∈ ℕ ∧ y ∈ ℕ  ⇒ x ∈ P ∧ y ∈ Q  |- z∈P ");
	final static IProverSequent seq2 = TestLib.genSeq(" (∀x,y· x ∈ ℕ ∧ y ∈ ℕ  ⇒ x ∈ P ∧ y ∈ Q) ;; z≠0 |- z∈P ");
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// without WD condition
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0","1"}),
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q][] |- ⊤",
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q][] |- ¬(0∈P∧1∈Q)",
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q][¬(0∈ℕ∧1∈ℕ)] |- z∈P"
				),
				// with WD condition
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"z","1÷z"}),
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q][] |- z≠0",
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q;; z≠0][] |- ¬(z∈P∧1 ÷ z∈Q)",
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q;; z≠0][¬(z∈ℕ∧1 ÷ z∈ℕ)] |- z∈P"
				),
				// with WD condition already selected
				new SuccessfullReasonerApplication(
						seq2,
						new AllD.Input(hyp,seq2.typeEnvironment(),new String[]{"z","1÷z"}),
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q][z≠0] |- z≠0",
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q][z≠0] |- ¬(z∈P∧1 ÷ z∈Q)",
						"{z=ℤ; P=ℙ(ℤ); Q=ℙ(ℤ)}[][∀x,y·x∈ℕ∧y∈ℕ⇒x∈P∧y∈Q][z≠0;; ¬(z∈ℕ∧1 ÷ z∈ℕ)] |- z∈P"
				)
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// hyp not present
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- ⊥ "),
						new AllD.Input(hyp, lib.makeTypeEnvironment(),new String[]{})
				),
				// hyp not univ quantified
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- ⊥ "),
						new AllD.Input(lib.True(), lib.makeTypeEnvironment(),new String[]{})
				),
				// hyp not univ quantified implication
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ∀x· x=0 |- ⊥ "),
						new AllD.Input(TestLib.genPred("∀x· x=0"), lib.makeTypeEnvironment(),new String[]{})
				),
				// not all bound idents instantiated
				new UnsuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0"}),
						"Missing instantiation for y"
				)
		};
	}

}
