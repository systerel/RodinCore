package org.eventb.core.prover.tests;


import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.externalReasoners.ExI;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.Tactic;
import org.eventb.core.prover.tactics.Tactics;

public class TacticTest extends TestCase {
	
	public void testNorm() {
		IProofTreeNode pt = SequentProver.makeProofTree(TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2")).getRoot();
		System.out.println(pt);
		Tactics.norm().apply(pt);
		System.out.println(pt);
		
		pt = SequentProver.makeProofTree(TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧(3=3 ⇒ 2=2 ∧3=3 ∧(∀x·x=1))")).getRoot();
		System.out.println(pt);
		Tactics.norm().apply(pt);
		System.out.println(pt);
	}
	
//	public void testCases(){
//		ProofTree pt = new ProofTree(TestLib.genSeq("x=1 ∨x=2 |- x < 3 "));
//		AssociativePredicate disjHyp = (AssociativePredicate)pt.getRootSeq().hypotheses().toArray()[0];
//		System.out.println(pt);
//		ProverPlugin plugin = new disjE();
//		PluginInput pluginInput = new disjE.Input(disjHyp);
//		(new Tactic.plugin(plugin,pluginInput)).apply(pt);
//		Tactics.norm().apply(pt);
//		System.out.println(pt);
//	}
//	
//	public void testConjE(){
//		ProofTree pt = new ProofTree(TestLib.genSeq("3<x ∧x<6 ∧x≠4 ;; y=3 ∧z=5 |-  x=5 "));
//		AssociativePredicate conjHyp = (AssociativePredicate)pt.getRootSeq().hypotheses().toArray()[0];
//		System.out.println(pt);
//		ProverPlugin plugin = new conjE();
//		PluginInput pluginInput = new conjE.Input(conjHyp);
//		(new Tactic.plugin(plugin,pluginInput)).apply(pt);
//		Tactics.norm().apply(pt);
//		System.out.println(pt);
//	}
//	
//	public void testConjE_auto(){
//		ProofTree pt = new ProofTree(TestLib.genSeq("3<x ∧x<6 ∧x≠4 ;; y=3 ∧z=5 |-  x=6 "));
//		Tactics.conjE_auto().apply(pt);
//		System.out.println(pt);
////		
////		AssociativePredicate conjHyp = (AssociativePredicate)pt.getRoot().hypotheses().toArray()[0];
////		System.out.println(pt);
////		ProverPlugin plugin = new conjE();
////		PluginInput pluginInput = new conjE.Input(conjHyp);
////		(new Tactic.plugin(plugin,pluginInput)).apply(pt);
////		Tactics.norm().apply(pt);
////		System.out.println(pt);
//	}
	
	public void testExI(){
		IProofTreeNode pt = SequentProver.makeProofTree(TestLib.genSeq("1=1 |-  ∃x·x=1")).getRoot();
		System.out.println(pt);
		IExternalReasoner plugin = new ExI();
		String[] witnesses = {"1"};
		IExtReasonerInput pluginInput = new ExI.Input(witnesses);
		(new Tactic.plugin(plugin,pluginInput)).apply(pt);
		Tactics.norm().apply(pt);
		System.out.println(pt);
	}

	public void testDoCase(){
		IProofTreeNode pt = SequentProver.makeProofTree(TestLib.genSeq("x=1 ∨x=2 |- x < 3 ")).getRoot();
		System.out.println(Tactics.doCase("x = 21").apply(pt));
		System.out.println(pt);
	}
	
	public void testDoCaseError(){
		IProofTreeNode pt = SequentProver.makeProofTree(TestLib.genSeq("x=1 ∨x=2 |- x < 3 ")).getRoot();
		System.out.println(Tactics.doCase("y = 21").apply(pt));
		System.out.println(pt);
	}
	
	public void testMngHyp(){	
		IProofTreeNode pt = SequentProver.makeProofTree(TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2")).getRoot();
		System.out.println(pt);
		Set<Hypothesis> h = Hypothesis.textSearch(pt.getSequent().hypotheses(),"1=1");
		System.out.println(h);
		Tactics.mngHyp(HypothesesManagement.ActionType.DESELECT,h).apply(pt);
		System.out.println(pt);
		
		Tactics.prune.apply(pt);
		System.out.println(pt);
		Tactics.mngHyp(HypothesesManagement.ActionType.HIDE,h).apply(pt);
		System.out.println(pt);
		Tactics.mngHyp(HypothesesManagement.ActionType.SHOW,h).apply(pt.getChildren()[0]);
		System.out.println(pt);
	}
	
	public void testLegacyProvers(){
		IProofTreeNode pt ;
		
		pt = SequentProver.makeProofTree(TestLib.genSeq("A ∈ℙ(ℤ) ;; B ∈ℙ(ℤ) ;; x∈A|- x∈A ∪B")).getRoot();
		System.out.println(Tactics.legacyProvers().apply(pt));
		System.out.println(pt);
		
		pt = SequentProver.makeProofTree(TestLib.genSeq("x=1 |- x ∈ℕ ")).getRoot();
		System.out.println(Tactics.legacyProvers().apply(pt));
		System.out.println(pt);
		
		pt = SequentProver.makeProofTree(TestLib.genSeq(" x ∈{1} |- x=1 ")).getRoot();
		System.out.println(Tactics.legacyProvers().apply(pt));
		System.out.println(pt);
		
		pt = SequentProver.makeProofTree(TestLib.genSeq(" 0 ≤ a ∧ 1 < b |- a mod b < b ")).getRoot();
		System.out.println(Tactics.legacyProvers().apply(pt));
		System.out.println(pt);
		
	}
	
}
