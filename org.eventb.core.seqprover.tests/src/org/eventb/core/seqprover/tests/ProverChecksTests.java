package org.eventb.core.seqprover.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
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
public class ProverChecksTests extends TestCase{

	private static final FormulaFactory factory = FormulaFactory.getDefault();
	
	IReasoner generatedBy = null;
	IReasonerInput generatedUsing = null;
	
	Predicate p1 = TestLib.genPred("1=1");
	
	Predicate px_int = TestLib.genPred("x=1");
	Predicate py_int = TestLib.genPred("y=1");
	Predicate px_bool = TestLib.genPred("x=TRUE");
	Set<Predicate> empty = Collections.emptySet();
	FreeIdentifier x_int = factory.makeFreeIdentifier("x", null, factory.makeIntegerType());
	FreeIdentifier y_int = factory.makeFreeIdentifier("y", null, factory.makeIntegerType());
	FreeIdentifier x_bool = factory.makeFreeIdentifier("x", null, factory.makeBooleanType());
	
	/**
	 * Test cases for the {@link ProverChecks#checkFormula(org.eventb.core.ast.Formula)} method.
	 */
	@Test
	public void testCheckFormula(){
		Formula<?> illFormed = factory.makeBoundIdentifier(0, null);
		Formula<?> unTyped = factory.makeFreeIdentifier("ident", null);
		assertFalse(ProverChecks.checkFormula(illFormed));
		assertFalse(ProverChecks.checkFormula(unTyped));
		assertTrue(ProverChecks.checkFormula(p1));
	}
	
	
	/**
	 * Tests the correct failure of the {@link ProverChecks#checkSequent(org.eventb.core.seqprover.IProverSequent)} method.
	 */
	@Test
	public void testCheckSequentFailure(){
		IProverSequent seq;
		ITypeEnvironment typeEnv;
		
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
		ITypeEnvironment typeEnv;
		
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
	 * Tests the correct failure of the {@link ProverChecks#checkRule(org.eventb.core.seqprover.IProofRule)} method.
	 */
	@Test
	public void testCheckRuleFailure(){
		IProofRule rule;
		IAntecedent[] antecedents;
		List<IHypAction> hypActions;
		
		// Antecedent is goal independent, but rule is not.
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, p1, "", antecedents);
		
		assertFalse(ProverChecks.checkRule(rule));
		
		// Goal and needed hyp do not agree on type environment
		antecedents = new IAntecedent[]{};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, px_int, px_bool, "", antecedents);
		assertFalse(ProverChecks.checkRule(rule));
		
		// Duplicate Added free idents with the same type in antecedent.
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(px_bool, null, new FreeIdentifier[] {x_bool, x_bool}, null)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, px_bool, "", antecedents);
		
		assertFalse(ProverChecks.checkRule(rule));

		// Duplicate Added free idents with different types in antecedent.
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(px_bool, null, new FreeIdentifier[] {x_bool, x_int}, null)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, p1, "", antecedents);
		
		assertFalse(ProverChecks.checkRule(rule));
		
		// Added free ident in antecedent is present in goal with a different type.
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(px_bool, null, new FreeIdentifier[] {x_bool}, null)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, px_int, "", antecedents);
		
		assertFalse(ProverChecks.checkRule(rule));
		
		// Added free ident in antecedent is present in goal with the same type.
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(px_int, null, new FreeIdentifier[] {x_int}, null)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, px_int, "", antecedents);
		
		assertFalse(ProverChecks.checkRule(rule));
		
		// Added free ident in antecedent is present in hyp action of another antecedent.
		hypActions = new ArrayList<IHypAction>();
		hypActions.add(ProverFactory.makeSelectHypAction(Collections.singleton(px_int)));
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, null),
				ProverFactory.makeAntecedent(null, null, null, hypActions)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, "", antecedents);
		
		assertFalse(ProverChecks.checkRule(rule));

		
		// Same free identifier added in antecedent and forward inference
		hypActions = new ArrayList<IHypAction>();
		hypActions.add(ProverFactory.makeForwardInfHypAction(empty, new FreeIdentifier[] {x_int}, empty));
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, hypActions)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, null, "", antecedents);

		assertFalse(ProverChecks.checkRule(rule));
		
		// Same free identifier added in consecutive forward inferences
		hypActions = new ArrayList<IHypAction>();
		hypActions.add(ProverFactory.makeForwardInfHypAction(empty, new FreeIdentifier[] {x_int}, empty));
		hypActions.add(ProverFactory.makeForwardInfHypAction(empty, new FreeIdentifier[] {x_int}, empty));
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, "", hypActions);
		
		assertFalse(ProverChecks.checkRule(rule));
		
		// Same free identifier added in consecutive forward inferences, but with different type
		hypActions = new ArrayList<IHypAction>();
		hypActions.add(ProverFactory.makeForwardInfHypAction(empty, new FreeIdentifier[] {x_bool}, empty));
		hypActions.add(ProverFactory.makeForwardInfHypAction(empty, new FreeIdentifier[] {x_int}, empty));
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, "", hypActions);
		
		assertFalse(ProverChecks.checkRule(rule));
		
		
	}
	
	/**
	 * Tests the correct success of the {@link ProverChecks#checkRule(org.eventb.core.seqprover.IProofRule)} method.
	 */
	@Test
	public void testCheckRuleSuccess(){
		IProofRule rule;
		IAntecedent[] antecedents;
		
		IReasoner generatedBy = null;
		IReasonerInput generatedUsing = null;
		
		// The identity rule
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, "", antecedents);
		
		assertTrue(ProverChecks.checkRule(rule));
		
		// Rule introducing an identical free identifier in two branches 
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, null),
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, null),
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, "", antecedents);
		
		assertTrue(ProverChecks.checkRule(rule));
	}
	
	/**
	 * Tests the correctness of the {@link ProverChecks#genRuleJustifications(org.eventb.core.seqprover.IProofRule)} method.
	 */
	@Test
	public void testGenRuleJustifications(){
		List<IProverSequent> justifications;
		
		IProofRule rule;
		IAntecedent[] antecedents;
		
		IReasoner generatedBy = null;
		IReasonerInput generatedUsing = null;
		
		// The identity rule
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null)
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, "", antecedents);
		justifications = ProverChecks.genRuleJustifications(rule);
		
		assertEquals("[{}[][][] |- ⊥⇒⊥]", justifications.toString());
		
		// A discharging rule
		antecedents = new IAntecedent[]{};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, p1, null, "", antecedents);
		justifications = ProverChecks.genRuleJustifications(rule);
		
		assertEquals("[{}[][][] |- 1=1]", justifications.toString());
		
		// Rule introducing an identical free identifier in two branches 
		antecedents = new IAntecedent[]{
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, null),
				ProverFactory.makeAntecedent(null, null, new FreeIdentifier[] {x_int}, null),
		};
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, null, "", antecedents);
		justifications = ProverChecks.genRuleJustifications(rule);
		
		assertEquals("[{}[][][] |- (∀x·⊥)∧(∀x·⊥)⇒⊥]", justifications.toString());
		
		// Rule with forward inferences
		ArrayList<IHypAction> hypActions = new ArrayList<IHypAction>();
		hypActions.add(ProverFactory.makeForwardInfHypAction(Collections.singleton(p1), new FreeIdentifier[] {x_int}, Collections.singleton(px_int)));
		hypActions.add(ProverFactory.makeForwardInfHypAction(Collections.singleton(p1), new FreeIdentifier[] {y_int}, Collections.singleton(py_int)));
		rule = ProverFactory.makeProofRule(generatedBy, generatedUsing, "", hypActions);
		justifications = ProverChecks.genRuleJustifications(rule);
		
		assertEquals("[{}[][][] |- ⊥⇒⊥, {}[][][1=1] |- ∃x·x=1, {}[][][1=1] |- ∃y·y=1]", justifications.toString());
		
	}
	
}
