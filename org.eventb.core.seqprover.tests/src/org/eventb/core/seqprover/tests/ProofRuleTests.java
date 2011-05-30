/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added used reasoners to proof dependencies
 *******************************************************************************/

package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.tests.Util.TEST_PLUGIN_ID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.junit.Test;

/**
 * Unit tests for Proof Rules and their application
 *
 * Also tests that sequent modification and hypothesis actions behave as expected
 * from an external point of view
 * 
 * @author Farhad Mehta
 */
public class ProofRuleTests {	
	
	public final static FormulaFactory factory = FormulaFactory.getDefault();
	public final static Predicate True = factory.makeLiteralPredicate(Formula.BTRUE,null);
	public final static Predicate False = factory.makeLiteralPredicate(Formula.BFALSE,null);
	private final static IAntecedent[] NO_ANTECEDENTS = new IAntecedent[0];
	private final static Set<Predicate> NO_HYPS = Collections.emptySet();
	
	protected static final IReasonerDesc fakeDesc = SequentProver
			.getReasonerRegistry().getReasonerDesc(TEST_PLUGIN_ID + ".noId");
	private static final IReasoner fakeReas = fakeDesc.getInstance();
	private static final IReasonerInput emptyInput = new EmptyInput();
	
	/**
	 * Rule discharging a sequent with a true goal.
	 * 
	 * Discharging.
	 * Goal dependent.
	 */
	private final static IProofRule trueGoal = 
		ProverFactory.makeProofRule(fakeDesc, emptyInput, True, NO_HYPS, IConfidence.DISCHARGED_MAX, "trueGoal", NO_ANTECEDENTS);

	/**
	 * Rule discharging a sequent with a false hypothesis.
	 * 
	 * Discharging
	 * Goal independent.
	 */
	private final static IProofRule  falseHyp= 
		ProverFactory.makeProofRule(fakeReas, emptyInput, null, False, "falseHyp", NO_ANTECEDENTS);

	/**
	 * Generates a cut rule with the given predicate
	 * 
	 * Non-discharging
	 * Goal independent.
	 * 
	 * @param pred
	 * 		The predicate to first prove, and then assume.
	 * @return
	 * 		The resulting cut rule
	 */
	private final IProofRule cut(Predicate pred){
		IAntecedent[] anticidents = new IAntecedent[2];
		anticidents[0] = ProverFactory.makeAntecedent(pred);
		anticidents[1] = ProverFactory.makeAntecedent(
				null,
				Collections.singleton(pred),
				null);
		return ProverFactory.makeProofRule(
				fakeReas,emptyInput,
				null,
				"cut ("+pred.toString()+")",
				anticidents);
	}
	
	/**
	 * Generates a rule introducing the given free variable
	 * 
	 * Non-discharging
	 * Goal independent.
	 * 
	 * @param freeIdent
	 * 		The free identifier to introduce.
	 * @return
	 * 		The resulting free identifier introduction rule
	 */
	private final IProofRule introFreeIdent(FreeIdentifier freeIdent){
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				null,null,new FreeIdentifier[] {freeIdent},null);
		return ProverFactory.makeProofRule(
				fakeReas,emptyInput,
				null,
				"introFreeIdent ("+freeIdent.toString()+")",
				anticidents);
	}
	
	/**
	 * Generates an ill formed rule.
	 * 
	 * Non-discharging.
	 * Goal dependent.
	 */
	private final static IProofRule illFormed(){ 
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(null);
		return ProverFactory.makeProofRule(
				fakeReas, emptyInput,
				True, NO_HYPS,
				IConfidence.DISCHARGED_MAX,
				"illFormed", anticidents);
	}
	
	/**
	 * Generates a hypothesis selection rule with the given predicate
	 * 
	 * Non-discharging
	 * Goal independent.
	 * 
	 * @param pred
	 * 		The hypothesis predicate to select.
	 * @return
	 * 		The resulting selection rule
	 */
	private final IProofRule selectHyp(Predicate pred){
		IHypAction selectHypAction = 
			ProverFactory.makeSelectHypAction(Collections.singleton(pred));
		return ProverFactory.makeProofRule(
				fakeReas,emptyInput,
				"select ("+pred.toString()+")",
				Collections.singletonList(selectHypAction));
	}
	
	/**
	 * Generates a hypothesis hide rule with the given predicate
	 * 
	 * Non-discharging
	 * Goal independent.
	 * 
	 * @param pred
	 * 		The hypothesis predicate to hide.
	 * @return
	 * 		The resulting hide rule
	 */
	private final IProofRule hideHyp(Predicate pred){
		IHypAction selectHypAction = 
			ProverFactory.makeHideHypAction(Collections.singleton(pred));
		return ProverFactory.makeProofRule(
				fakeReas,emptyInput,
				"hide ("+pred.toString()+")",
				Collections.singletonList(selectHypAction));
	}
	
	/**
	 * Generates a forward inference rule with the given predicates and 
	 * free identifier.
	 * 
	 * Non-discharging
	 * Goal independent.
	 * 
	 * @param pred1
	 * 		The predicate hypothesis needed for the forward inference.
	 * @param freeIdent
	 * 		The free identifier introduced.
	 * @param pred2
	 * 		The predicate hypothesis inferred.
	 * @return
	 * 		The resulting forward inference rule
	 */
	private final IProofRule fwdInf(Predicate pred1, FreeIdentifier freeIdent, Predicate pred2){
		IHypAction fwdInfHypAction = 
			ProverFactory.makeForwardInfHypAction(
					Collections.singleton(pred1),
					new FreeIdentifier[] {freeIdent},
					Collections.singleton(pred2));
		return ProverFactory.makeProofRule(
				fakeReas,emptyInput,
				"fwdInf ("+pred1.toString()+")",
				Collections.singletonList(fwdInfHypAction));
	}
	
	/**
	 * Tests for rule application for discharging rules
	 */
	@Test
	public void testDischargingRuleApplication(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		
		// Rule with goal dependence
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		newSeqs = trueGoal.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 0);
		
		seq = TestLib.genSeq(" ⊤ |- ⊥ ");
		newSeqs = trueGoal.apply(seq);
		assertNull(newSeqs);
		
		// Rule without goal dependence
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeqs = falseHyp.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 0);
		
		seq = TestLib.genSeq(" ⊤ |- ⊥ ");
		newSeqs = falseHyp.apply(seq);
		assertNull(newSeqs);
	}
	
	/**
	 * Tests for rule application for ill-formed rules
	 */
	@Test
	public void testIllFormedRuleApplication(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		
		IProofRule illFormed = illFormed();

		seq = TestLib.genSeq(" ⊤ |- ⊤");
		newSeqs = illFormed.apply(seq);
		assertNull(newSeqs);
	}

	/**
	 * Tests for rule application for non-discharging rules
	 */
	@Test
	public void testNonDischargingRuleApplication(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		
		Predicate cutPred = TestLib.genPred("1=1");
		IProofRule cut = cut(cutPred);
		
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeqs = cut.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 2);
		newSeqs[0].goal().equals(cutPred);
		newSeqs[0].containsHypothesis(False);
		newSeqs[1].goal().equals(False);
		newSeqs[1].containsHypothesis(cutPred);
		newSeqs[1].containsHypothesis(False);
	}
	
	/**
	 * Tests for rule application for rules introducing Free Identifiers
	 */
	@Test
	public void testFreeIdentIntroRuleApplication(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		
		FreeIdentifier freeIdent_x = factory.makeFreeIdentifier("x", null, factory.makeIntegerType());
		IProofRule introFreeIdent_x = introFreeIdent(freeIdent_x);
		
		// Succesful application
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeqs = introFreeIdent_x.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 1);
		newSeqs[0].typeEnvironment().contains(freeIdent_x.getName());
		
		// Unsuccesful application; clashing name and type
		seq = TestLib.genSeq(" x=2 |- ⊥ ");
		newSeqs = introFreeIdent_x.apply(seq);
		assertNull(newSeqs);
	}
	
	
	// Hypothesis action tests
	
	/**
	 * Tests for selection rule application
	 */
	@Test
	public void testSelectionRuleApplication(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		
		Predicate pred = TestLib.genPred("1=1");
		IProofRule hide = hideHyp(pred);
		IProofRule select = selectHyp(pred);
		
		// unmodifying
		// hyp absent
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeqs = hide.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 1);
		assertTrue(ProverLib.deepEquals(seq, newSeqs[0]));
		
		// modifying + hidden, selected are disjoint
		seq = TestLib.genSeq(" 1=1 ;; ⊥ |- ⊥ ");
		newSeqs = hide.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 1);
		assertFalse(ProverLib.deepEquals(seq, newSeqs[0]));
		assertTrue(newSeqs[0].isHidden(pred));
		assertFalse(newSeqs[0].isSelected(pred));
		newSeqs = select.apply(newSeqs[0]);
		assertFalse(newSeqs[0].isHidden(pred));
		assertTrue(newSeqs[0].isSelected(pred));
	}
	
	/**
	 * Tests for forward inference rule application
	 */
	@Test
	public void testFwdInfRuleApplication(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		
		Predicate pred1 = TestLib.genPred("1=1");
		FreeIdentifier freeIdent_x = factory.makeFreeIdentifier("x", null, factory.makeIntegerType());
		Predicate pred2 = TestLib.genPred("x=1");
		IProofRule fwdInf = fwdInf(pred1, freeIdent_x, pred2);
		
		// unmodifying
		// hyp absent
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeqs = fwdInf.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 1);
		assertTrue(ProverLib.deepEquals(seq, newSeqs[0]));
		
		// Free Ident clash
		seq = TestLib.genSeq(" x=1 ;; ⊥ |- ⊥ ");
		newSeqs = fwdInf.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 1);
		assertTrue(ProverLib.deepEquals(seq, newSeqs[0]));
		
		// modifying
		seq = TestLib.genSeq(" 1=1 ;; ⊥ |- ⊥ ");
		newSeqs = fwdInf.apply(seq);
		assertNotNull(newSeqs);
		assertTrue(newSeqs.length == 1);
		assertFalse(ProverLib.deepEquals(seq, newSeqs[0]));
		assertTrue(newSeqs[0].containsHypothesis(pred2));
		assertTrue(newSeqs[0].typeEnvironment().contains(freeIdent_x.getName()));
		
	}

	/**
	 * Ensure that proof rules constructed with an unregistered reasoner allow
	 * retrieving this very reasoner.
	 */
	@Test
	public void testProofRuleReasoner() throws Exception {
		final IReasoner original = new EmptyInputReasoner() {
		
			public String getReasonerID() {
				return TEST_PLUGIN_ID + ".testProofRuleReasoner_noId";
			}
		
			public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
					IProofMonitor pm) {
				return null;
			}
		};

		final IProofRule rule = ProverFactory.makeProofRule(original, emptyInput, "no display", null);
		final IReasoner generatedBy = rule.generatedBy(); 
		assertSame(original, generatedBy);

		final IReasonerDesc desc = rule.getReasonerDesc();
		final IReasoner instance = desc.getInstance();
		assertSame(generatedBy, instance);
	}
}
