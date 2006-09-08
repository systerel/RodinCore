/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.seqprover.tests;

import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.tactics.Tactics;

/**
 * Unit tests for classes ProofTree and ProofTreeNode: basic manipulations of
 * proof trees.
 * 
 * @author Laurent Voisin
 */
public class ProofTreeTests extends AbstractProofTreeTests {	
	
	/**
	 * Ensures that an initial proof tree is open.
	 */
	public void testInitialTree() {
		IProverSequent sequent = makeSimpleSequent("⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		
		assertNodeOpen(tree.getRoot());
	}

	/**
	 * Checks consistency of a discharged proof tree.
	 */
	public void testDischargedTree() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root);
		assertNodePending(root);
		assertNotEmpty(root.getChildren());

		IProofTreeNode imp = root.getChildren()[0];
		assertSingleton(imp, root.getChildren());
		assertNodeOpen(imp);
		
		Tactics.hyp().apply(imp);
		assertNodeClosed(imp);
		assertNodeClosed(root);
		assertTrue("Tree is not discharged", tree.isClosed());
		checkTree(tree, root, sequent);
	}

	/**
	 * Checks consistency of a pending proof tree.
	 */
	public void testPendingTree() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root);
		assertNotEmpty(root.getChildren());
		IProofTreeNode imp = root.getChildren()[0];
		assertSingleton(imp, root.getChildren());
		assertNodeOpen(imp);
		assertNodePending(root);
		
		assertFalse("Tree is discharged", tree.isClosed());
		checkTree(tree, root, sequent);
	}

	/**
	 * Checks consistency of a mixed pending proof tree.
	 */
	public void testMixedTree() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root);
		assertNotEmpty(root.getChildren());
		IProofTreeNode conj = root.getChildren()[0];
		assertSingleton(conj, root.getChildren());
		assertNodeOpen(conj);
		assertNodePending(root);

		Tactics.conjI().apply(conj);
		assertEquals(2, conj.getChildren().length);
		IProofTreeNode left = conj.getChildren()[0];
		IProofTreeNode right = conj.getChildren()[1];
		assertNodeOpen(left);
		assertNodeOpen(right);
		assertNodePending(conj);
		assertNodePending(root);
		
		Tactics.hyp().apply(left);
		assertEmpty(left.getChildren());
		assertNodeDischarged(left);
		assertNodeOpen(right);
		assertNodePending(conj);
		assertNodePending(root);
		assertFalse("Tree is closed", tree.isClosed());
		
		Tactics.review().apply(right);
		assertNodeReviewed(right);
		assertNodeDischarged(left);
		assertNodeReviewed(conj);
		assertNodeReviewed(root);
		
		
		assertTrue("Tree is pending", tree.isClosed());
		checkTree(tree, root, sequent);
	}
	
	/**
	 * Checks consistency after applying a rule that fails.
	 */
	public void testApplyRuleFailure() {
		IProverSequent sequent = makeSimpleSequent("⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();
		
		Object error = Tactics.tautology().apply(root);
		assertNotNull(error);
		assertNodeOpen(root);
	}

	/**
	 * Checks consistency after pruning a subtree on a pending node.
	 */
	public void testPrunePending() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root);
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];

		Tactics.conjI().apply(imp);
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		IProofTreeNode right = imp.getChildren()[1];

		// the nodes to prune are part of the same proof tree.
		assertSame(imp.getProofTree(),tree);
		assertSame(left.getProofTree(),tree);
		assertSame(right.getProofTree(),tree);
		
		assertNodePending(root);
		IProofTree[] pruned = root.pruneChildren();
		assertEquals(1, pruned.length);
		assertNodeOpen(root);
		
		// the pruned node is the root of the pruned subtree.
		assertNull(imp.getParent());
		assertSame(imp.getProofTree(),pruned[0]);
		assertSame(left.getProofTree(),imp.getProofTree());
		assertSame(right.getProofTree(),imp.getProofTree());
		// the pruned nodes are not in the original tree
		assertNotSame(imp.getProofTree(),tree);
		assertNotSame(left.getProofTree(),tree);
		assertNotSame(right.getProofTree(),tree);
		
	}

	/**
	 * Checks consistency after pruning a subtree on a discharged node.
	 */
	public void testPruneDischarged() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root);
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];

		Tactics.conjI().apply(imp);
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		IProofTreeNode right = imp.getChildren()[1];
		
		Tactics.hyp().apply(left);
		Tactics.hyp().apply(right);
		
		// the nodes to prune are part of the same proof tree.
		assertSame(left.getProofTree(),tree);
		assertSame(right.getProofTree(),tree);
		// their parent node is discharged
		assertNodeClosed(imp);

		IProofTree[] pruned = imp.pruneChildren();
		assertEquals(2, pruned.length);
		assertNodeOpen(imp);
		assertNodePending(root);
		// the pruned nodes are not part of the tree anymore.
		assertNotSame(left.getProofTree(),tree);
		assertNotSame(right.getProofTree(),tree);
		// Pruned nodes are part of some proof tree.
		assertNotNull(left.getProofTree());
		assertNotNull(right.getProofTree());
		
		// they are roots of their own proof trees.
		assertNull(left.getParent());
		assertNull(right.getParent());
		assertSame(left.getProofTree(),pruned[0]);
		assertSame(right.getProofTree(),pruned[1]);
	}
	
	/**
	 * Checks that grafting a tree with a un-identical sequent results in failure.
	 */
	public void testGraftFailure() {
		IProverSequent sequent = makeSimpleSequent("⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode treeRoot = tree.getRoot();

		sequent = makeSimpleSequent("⊤");
		IProofTree graft = SequentProver.makeProofTree(sequent);
		IProofTreeNode graftRoot = graft.getRoot();
		
		Tactics.tautology().apply(graftRoot);
		assertNodeClosed(graftRoot);
		
		boolean success = treeRoot.graft(graft);
		assertFalse(success);
		
		// Grafted tree is still discharged
		assertNodeClosed(graftRoot);
		
		// Original tree is still open
		assertNodeOpen(treeRoot);		
	}
	
	
	/**
	 * Checks consistency after grafting a pending subtree on an open node.
	 */
	public void testGraftPending() {
		IProverSequent sequent = makeSimpleSequent("⊤ ∧ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode treeRoot = tree.getRoot();

		sequent = makeSimpleSequent("⊤ ∧ ⊥");
		IProofTree graft = SequentProver.makeProofTree(sequent);
		IProofTreeNode graftRoot = graft.getRoot();
		
		Tactics.conjI().apply(graftRoot);
		assertEquals(2, graftRoot.getChildren().length);
		IProofTreeNode ch1 = graftRoot.getChildren()[0];
		IProofTreeNode ch2 = graftRoot.getChildren()[1];
				
		treeRoot.graft(graft);
		
		// Grafted tree is pruned
		assertNodeOpen(graftRoot);
		
		// Children have been grafted
		assertNotSame(ch1.getProofTree(),graft);
		assertNotSame(ch2.getProofTree(),graft);
		assertSame(ch1.getProofTree(),tree);
		assertSame(ch2.getProofTree(),tree);
		
	}

	/**
	 * Checks consistency after grafting a discharged subtree on an open node.
	 */
	public void testGraftDischarged() {
		IProverSequent sequent = makeSimpleSequent("⊤ ∧ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode treeRoot = tree.getRoot();

		sequent = makeSimpleSequent("⊤ ∧ ⊤");
		IProofTree graft = SequentProver.makeProofTree(sequent);
		IProofTreeNode graftRoot = graft.getRoot();
		
		Tactics.conjI().apply(graftRoot);
		assertEquals(2, graftRoot.getChildren().length);
		IProofTreeNode ch1 = graftRoot.getChildren()[0];
		IProofTreeNode ch2 = graftRoot.getChildren()[1];
		Tactics.tautology().apply(ch1);
		Tactics.tautology().apply(ch2);
		assertNodeClosed(graftRoot);
		
		treeRoot.graft(graft);
		
		// Grafted tree is pruned
		assertNodeOpen(graftRoot);
		
		// Children have been grafted
		assertNotSame(ch1.getProofTree(),graft);
		assertNotSame(ch2.getProofTree(),graft);
		assertSame(ch1.getProofTree(),tree);
		assertSame(ch2.getProofTree(),tree);
		assertNodeClosed(treeRoot);
	}
	
	/**
	 * Checks that proof dependency information has been properly generated.
	 */
	public void testProofDependencies() {
		IProverSequent sequent;
		IProofTree proofTree;
		IProofDependencies proofDependencies;
		
		// test getUsedHypotheses
		sequent = TestLib.genSeq("y=2;; x=1 |- x=1");
		proofTree = SequentProver.makeProofTree(sequent);
		Tactics.hyp().apply(proofTree.getRoot());
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(Lib.proofReusable(proofDependencies,sequent));
		assertTrue(proofDependencies.getGoal().equals(TestLib.genPredicate("x=1")));
		assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genHyps("x=1")));
		assertTrue(proofDependencies.getUsedFreeIdents().equals(TestLib.genTypeEnv("x","ℤ")));
		assertTrue(proofDependencies.getIntroducedFreeIdents().equals(TestLib.genTypeEnv()));
		
		// test getUsedHypotheses
		sequent = TestLib.genSeq("y=2 ;; x=1 |- x=1 ⇒ x=1");
		proofTree = SequentProver.makeProofTree(sequent);
		Tactics.impI().apply(proofTree.getRoot());
		Tactics.hyp().apply(proofTree.getRoot().getFirstOpenDescendant());
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(Lib.proofReusable(proofDependencies,sequent));
		assertTrue(proofDependencies.getGoal().equals(TestLib.genPredicate("x=1 ⇒ x=1")));
		// TODO : uncomment next line once usedHyps better implemented
		// assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genHyps()));
		assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genHyps("x=1")));
		assertTrue(proofDependencies.getUsedFreeIdents().equals(TestLib.genTypeEnv("x","ℤ")));
		assertTrue(proofDependencies.getIntroducedFreeIdents().equals(TestLib.genTypeEnv()));
		
		
		// test getUsedFreeIdents
		sequent = TestLib.genSeq("y=2;; 1=1 |- 1=1");
		proofTree = SequentProver.makeProofTree(sequent);
		Tactics.lemma("y=2").apply(proofTree.getRoot());
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(Lib.proofReusable(proofDependencies,sequent));
		assertTrue(proofDependencies.getGoal().equals(TestLib.genPredicate("1=1")));
		assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genHyps()));
		assertTrue(proofDependencies.getUsedFreeIdents().equals(TestLib.genTypeEnv("y","ℤ")));
		assertTrue(proofDependencies.getIntroducedFreeIdents().equals(TestLib.genTypeEnv()));
		
		//	 test getIntroducedFreeIdents
		sequent = TestLib.genSeq("y=2 |- ∀ x· x∈ℤ");
		proofTree = SequentProver.makeProofTree(sequent);
		Tactics.allI().apply(proofTree.getRoot());
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(Lib.proofReusable(proofDependencies,sequent));
		assertTrue(proofDependencies.getGoal().equals(TestLib.genPredicate("∀ x· x∈ℤ")));
		assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genHyps()));
		assertTrue(proofDependencies.getUsedFreeIdents().equals(TestLib.genTypeEnv()));
		assertTrue(proofDependencies.getIntroducedFreeIdents().equals(TestLib.genTypeEnv("x","ℤ")));
	}

}
