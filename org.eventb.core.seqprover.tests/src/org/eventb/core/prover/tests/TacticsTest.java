package org.eventb.core.prover.tests;


import junit.framework.TestCase;

import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.Tactics;

public class TacticsTest extends TestCase {
	IProofTreeNode pt;
	IProofTreeNode[] desc;
	
	// Globally applicable tactics
	
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
		assertEquals(desc.length,1);
	}

	// Tactics applicable on the goal
	
	public void testImpI(){
		pt = TestLib.genProofTreeNode( " ⊤|- ⊥⇒ ⊤");
		assertNull(Tactics.impI().apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,1);
		
		pt = TestLib.genProofTreeNode( " ⊤|- ⊤⇒ (⊥⇒ ⊤)");
		assertNull(Tactics.impI().apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,1);
	}
	
	public void testConjI(){
		pt = TestLib.genProofTreeNode( " ⊤|- 1=1∧2=2∧3=3");
		assertNull(Tactics.conjI().apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,3);
	}
	
	public void testAllI(){
		pt = TestLib.genProofTreeNode( " ⊤|- ∀x· x∈ℤ⇒ x=x ");
		assertNull(Tactics.allI().apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,1);
	}
	
	public void testExI(){
		pt = TestLib.genProofTreeNode( " ⊤|- ∃x·x∈ℤ");
		assertNull(Tactics.exI("0").apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,2);
		
		pt = TestLib.genProofTreeNode( " ⊤|- ∃x,y·x∈ℤ∧y∈ℕ");
		assertNull(Tactics.exI("0",null).apply(pt));
		desc = pt.getOpenDescendants();
		// System.out.println(pt);
		assertEquals(desc.length,2);
		
		pt = TestLib.genProofTreeNode( " ⊤|- ∃x,y·x∈ℤ∧y∈ℕ");
		assertNull(Tactics.exI(null,"0").apply(pt));
		desc = pt.getOpenDescendants();
		// System.out.println(pt);
		assertEquals(desc.length,2);
	}
	
	// Tactics applicable on a hypothesis
	
	public void testAllF(){
		pt = TestLib.genProofTreeNode( " ∀x·x∈ℤ ⇒ x=x |- 1=1");
		assertNull(Tactics.allF(TestLib.genHyp("∀x·x∈ℤ ⇒ x=x"),"0").apply(pt));
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,2);
		
		pt = TestLib.genProofTreeNode( " ∀x,y·x∈ℤ∧y∈ℕ ⇒ x=x∧y=y |- 1=1");
		assertNull(Tactics.allF(TestLib.genHyp(" ∀x,y·x∈ℤ∧y∈ℕ ⇒ x=x∧y=y "),"0",null).apply(pt));
		// System.out.println(pt);
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,2);
		
		pt = TestLib.genProofTreeNode( " ∀x,y·x∈ℤ∧y∈ℕ ⇒ x=x∧y=y |- 1=1");
		assertNull(Tactics.allF(TestLib.genHyp(" ∀x,y·x∈ℤ∧y∈ℕ ⇒ x=x∧y=y "),null,"0").apply(pt));
		// System.out.println(pt);
		desc = pt.getOpenDescendants();
		assertEquals(desc.length,2);
	}
	
	// TODO complete
}
