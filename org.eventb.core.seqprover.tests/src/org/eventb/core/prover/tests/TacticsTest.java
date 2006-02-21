package org.eventb.core.prover.tests;


import java.util.Set;

import static junit.framework.Assert.*;
import junit.framework.TestCase;

import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.externalReasoners.ExI;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;

public class TacticsTest extends TestCase {
	IProofTreeNode pt;
	IProofTreeNode[] desc;
	
	public void testLegacyProvers(){	
		pt = TestLib.genProofTreeNode("A ∈ℙ(ℤ) ;; B ∈ℙ(ℤ) ;; x∈A|- x∈A ∪B");
		assertNull(Tactics.legacyProvers().apply(pt));
		assertTrue(pt.isDischarged());
		
		pt = TestLib.genProofTreeNode(" 0 ≤ a ∧ 1 < b |- a mod b < b ");
		assertNull(Tactics.legacyProvers().apply(pt));
		assertTrue(pt.isDischarged());
		
		pt = TestLib.genProofTreeNode(" ⊤|- ⊥");
		assertNotNull(Tactics.legacyProvers().apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,1);
		assertEquals(desc[0],pt);
		
	}
	
	public void testLemma() {		
		pt = TestLib.genProofTreeNode(" ⊤|- ⊤");
		assertNull(Tactics.lemma("⊥").apply(pt));
		desc = pt.getOpenDescendants();
		// System.out.println(pt);
		assertEquals(desc.length,3);
		
		pt = TestLib.genProofTreeNode(" ⊤|- ⊤");
		assertNotNull(Tactics.lemma("*UNPARSABLE*").apply(pt));
		desc = pt.getOpenDescendants();
		//System.out.println(pt);
		assertEquals(desc.length,1);
		assertEquals(desc[0],pt);
	}
	
	
	public void testNorm() {
		pt = TestLib.genProofTreeNode("1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2");
		assertNull(Tactics.norm().apply(pt));
		assertTrue(pt.isDischarged());
		
		pt = TestLib.genProofTreeNode("1=1 ;; 2=2 |- 1=1 ∧(3=3 ⇒ 2=2 ∧3=3 ∧(∀x·x=1))");
		Tactics.norm().apply(pt);
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,1);
	}
	
	public void testDoCase(){
		pt = TestLib.genProofTreeNode( "x=1 ∨x=2 |- x < 3 ");
		assertNull(Tactics.doCase("x = 21").apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,4);
	}
	
	public void testContradictGoal(){
		pt = TestLib.genProofTreeNode( " ⊤|- ⊤" );
		assertNull(Tactics.contradictGoal().apply(pt));
		desc = pt.getOpenDescendants();
		System.out.println(pt);
		assertEquals(desc.length,1);
	}

	// TODO complete
}
