/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved all type-checking code to class TypeChecker
 *     Systerel - added checks about predicate variables
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.tests.TestLib.FAKE_HYP_ACTION;
import static org.eventb.core.seqprover.tests.Util.TEST_PLUGIN_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.ProverChecks;
import org.junit.Test;

/**
 * Test cases for the static methods in {@link ProverChecks}. 
 * 
 * <p>
 * These checks are rather minimal since the methods to be tested are themselves test methods.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class ProverChecksTests {

	private static final FormulaFactory factory = FormulaFactory.getDefault();
	
	private static final Predicate p1 = TestLib.genPred("1=1");
	
	private static final Predicate px_int = TestLib.genPred("x=1");
	private static final Predicate py_int = TestLib.genPred("y=1");
	private static final Predicate px_bool = TestLib.genPred("x=TRUE");
	private static final FreeIdentifier x_int = factory.makeFreeIdentifier("x", null, factory.makeIntegerType());
	private static final FreeIdentifier y_int = factory.makeFreeIdentifier("y", null, factory.makeIntegerType());
	private static final Predicate pv_P = factory.makePredicateVariable("$P", null);
	
	/**
	 * Tests the correct failure of the {@link ProverChecks#checkSequent(org.eventb.core.seqprover.IProverSequent)} method.
	 */
	@Test
	public void testCheckSequentFailure(){
		IProverSequent seq;
		ITypeEnvironmentBuilder typeEnv;
		
		// Goal with undeclared free ident  
		typeEnv = factory.makeTypeEnvironment();
		seq = ProverFactory.makeSequent(typeEnv, null, px_int);
		
		assertFalse(ProverChecks.checkSequent(seq));
		
		// Hypothesis with undeclared free ident  
		typeEnv = factory.makeTypeEnvironment();
		seq = ProverFactory.makeSequent(typeEnv, Collections.singleton(px_int), p1);
		
		assertFalse(ProverChecks.checkSequent(seq));
		
		// Goal with declared free ident, but not of the correct type. 
		typeEnv = factory.makeTypeEnvironment();
		seq = ProverFactory.makeSequent(typeEnv, Collections.singleton(px_int), px_bool);
		
		assertFalse(ProverChecks.checkSequent(seq));

		
		// Selected hypothesis not in hypotheses  
		typeEnv = factory.makeTypeEnvironment();
		typeEnv.addName("x", factory.makeIntegerType());
		seq = ProverFactory.makeSequent(typeEnv, Collections.singleton(p1),Collections.singleton(px_int), p1);
		
		assertFalse(ProverChecks.checkSequent(seq));
	}
	
	/**
	 * Tests the correct success of the {@link ProverChecks#checkSequent(org.eventb.core.seqprover.IProverSequent)} method.
	 */
	@Test
	public void testCheckSequentSuccess(){
		IProverSequent seq;
		ITypeEnvironmentBuilder typeEnv;
		
		// Goal and hypothesis with declared free ident  
		typeEnv = factory.makeTypeEnvironment();
		typeEnv.addName("x", factory.makeIntegerType());
		seq = ProverFactory.makeSequent(typeEnv, Collections.singleton(px_int), px_int);
		
		assertTrue(ProverChecks.checkSequent(seq));
		
		// Selected hypothesis in hypotheses  
		typeEnv = factory.makeTypeEnvironment();
		typeEnv.addName("x", factory.makeIntegerType());
		seq = ProverFactory.makeSequent(typeEnv, Collections.singleton(px_int),Collections.singleton(px_int), p1);
		
		assertTrue(ProverChecks.checkSequent(seq));
	}

	/**
	 * Tests the correctness of the {@link ProverChecks#genRuleJustifications(org.eventb.core.seqprover.IProofRule)} method.
	 */
	@Test
	public void testGenRuleJustifications(){
		List<IProverSequent> justifications;
		
		IProofRule rule;
		IAntecedent[] antecedents;
		
		final IReasonerDesc fakeDesc = SequentProver
		.getReasonerRegistry().getReasonerDesc(TEST_PLUGIN_ID + ".noId");
		IReasoner generatedBy =  fakeDesc.getInstance();
		IReasonerInput generatedUsing = new EmptyInput();
		
		// The identity rule
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null,null,FAKE_HYP_ACTION)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, "", antecedents);
		justifications = ProverChecks.genRuleJustifications(rule, factory);
		
		assertEquals("[{}[][][] |- ⊥⇒⊥]", justifications.toString());
		
		// A discharging rule
		antecedents = new IAntecedent[]{};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, p1, (Predicate)null, "", antecedents);
		justifications = ProverChecks.genRuleJustifications(rule, factory);
		
		assertEquals("[{}[][][] |- 1=1]", justifications.toString());
		
		// Rule introducing an identical free identifier in two branches 
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, null),
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, null),
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, "", antecedents);
		justifications = ProverChecks.genRuleJustifications(rule, factory);
		
		assertEquals("[{}[][][] |- (∀x·⊥)∧(∀x·⊥)⇒⊥]", justifications.toString());
		
		// Rule with forward inferences
		ArrayList<IHypAction> hypActions = new ArrayList<IHypAction>();
		hypActions.add(ProverFactory.makeForwardInfHypAction(Collections.singleton(p1), new FreeIdentifier[] {x_int}, Collections.singleton(px_int)));
		hypActions.add(ProverFactory.makeForwardInfHypAction(Collections.singleton(p1), new FreeIdentifier[] {y_int}, Collections.singleton(py_int)));
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, "", hypActions);
		justifications = ProverChecks.genRuleJustifications(rule, factory);
		
		assertEquals("[{}[][][] |- ⊥⇒⊥, {}[][][1=1] |- ∃x·x=1, {}[][][1=1] |- ∃y·y=1]", justifications.toString());
		
	}

	/**
	 * Tests the correctness of the
	 * {@link ProverChecks#checkNoPredicateVariable(Predicate)} and
	 * {@link ProverChecks#checkNoPredicateVariable(Collection)} methods.
	 */
	@Test
	public void testCheckNoPredicateVariable() throws Exception {
		final Predicate null_pred = null;
		assertTrue(ProverChecks.checkNoPredicateVariable(null_pred));
		assertTrue(ProverChecks.checkNoPredicateVariable(py_int));
		assertFalse(ProverChecks.checkNoPredicateVariable(pv_P));
		
		final Collection<Predicate> null_preds = null;
		final Collection<Predicate> no_preds = Collections.emptyList();
		assertTrue(ProverChecks.checkNoPredicateVariable(null_preds));
		assertTrue(ProverChecks.checkNoPredicateVariable(no_preds));
		assertTrue(ProverChecks.checkNoPredicateVariable(Arrays.asList(py_int)));
		assertFalse(ProverChecks.checkNoPredicateVariable(Arrays.asList(pv_P)));
		assertFalse(ProverChecks.checkNoPredicateVariable(Arrays.asList(py_int, pv_P)));
	}
}
