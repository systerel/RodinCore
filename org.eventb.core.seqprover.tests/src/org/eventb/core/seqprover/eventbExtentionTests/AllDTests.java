/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deselect WD predicate and used hypothesis
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AllD;

/**
 * Unit tests for the AllD reasoner
 * 
 * @author Farhad Mehta
 *
 * TODO : make these tests independant of the integer type (use an arbitrary carrier set instead)
 */
public class AllDTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new AllD()).getReasonerID();
	}
	
	final static IProverSequent seq = TestLib.genSeq(" ∀x,y· x∈ℤ ⇒ x∈P ∧ y∈P  |- ⊥ ");
	final static Predicate hyp = TestLib.getFirstHyp(seq);
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {

		return new SuccessfullReasonerApplication[]{
				// hyp not univ quantified implication, but still univ quantified
				new SuccessfullReasonerApplication(TestLib.genSeq(" ∀x· x=0 |- ⊥ "), 
						new AllD.Input(TestLib.genPred("∀x· x=0"), Lib.makeTypeEnvironment(),new String[]{"0"}),
						"[{}[][][∀x·x=0] |- ⊤," +
						" {}[][∀x·x=0][0=0] |- ⊥]"
				),
				// without WD condition
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0","1"}),
						"[{P=ℙ(ℤ)}[][][∀x,y·x∈ℤ⇒x∈P∧y∈P] |- ⊤," +
						" {P=ℙ(ℤ)}[][∀x,y·x∈ℤ⇒x∈P∧y∈P][0∈ℤ⇒0∈P∧1∈P] |- ⊥]"
				),
				// with WD condition
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0","1÷0"}),
						"[{P=ℙ(ℤ)}[][][∀x,y·x∈ℤ⇒x∈P∧y∈P] |- 0≠0," +
						" {P=ℙ(ℤ)}[][∀x,y·x∈ℤ⇒x∈P∧y∈P, 0≠0][0∈ℤ⇒0∈P∧1 ÷ 0∈P] |- ⊥]"
				),
				// not all bound idents instantiated
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0"}),
						"[{P=ℙ(ℤ)}[][][∀x,y·x∈ℤ⇒x∈P∧y∈P] |- ⊤," +
						" {P=ℙ(ℤ)}[][∀x,y·x∈ℤ⇒x∈P∧y∈P][∀y·0∈ℤ⇒0∈P∧y∈P] |- ⊥]"
				),
				// only first bound ident instantiated
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0", null}),
						"[{P=ℙ(ℤ)}[][][∀x,y·x∈ℤ⇒x∈P∧y∈P] |- ⊤," +
						" {P=ℙ(ℤ)}[][∀x,y·x∈ℤ⇒x∈P∧y∈P][∀y·0∈ℤ⇒0∈P∧y∈P] |- ⊥]"
				),
				// only second bound ident instantiated
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{null, "0"}),
						"[{P=ℙ(ℤ)}[][][∀x,y·x∈ℤ⇒x∈P∧y∈P] |- ⊤," +
						" {P=ℙ(ℤ)}[][∀x,y·x∈ℤ⇒x∈P∧y∈P][∀x·x∈ℤ⇒x∈P∧0∈P] |- ⊥]"
				),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// hyp not present
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- ⊥ "),
						new AllD.Input(hyp, Lib.makeTypeEnvironment(),new String[]{})
				),
				// hyp not univ quantified
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- ⊥ "),
						new AllD.Input(Lib.True, Lib.makeTypeEnvironment(),new String[]{})
				),	
		};
	}

}
