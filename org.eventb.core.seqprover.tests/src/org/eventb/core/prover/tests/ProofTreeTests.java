/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.tests;

import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.tactics.Tactics;

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
		// applyRule(imp, rf.hyp());
		assertNodeDischarged(imp);
		assertNodeDischarged(root);
		assertTrue("Tree is not discharged", tree.isDischarged());
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
		// applyRule(root, rf.impI());
		assertNotEmpty(root.getChildren());
		IProofTreeNode imp = root.getChildren()[0];
		assertSingleton(imp, root.getChildren());
		assertNodeOpen(imp);
		assertNodePending(root);
		
		assertFalse("Tree is discharged", tree.isDischarged());
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
		// applyRule(root, rf.impI());
		assertNotEmpty(root.getChildren());
		IProofTreeNode imp = root.getChildren()[0];
		assertSingleton(imp, root.getChildren());
		assertNodeOpen(imp);
		assertNodePending(root);

		Tactics.conjI().apply(imp);
		// applyRule(imp, rf.conjI());
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		IProofTreeNode right = imp.getChildren()[1];
		assertNodeOpen(left);
		assertNodeOpen(right);
		assertNodePending(imp);
		assertNodePending(root);
		
		Tactics.hyp().apply(left);
		// applyRule(left, rf.hyp());
		assertEmpty(left.getChildren());
		assertNodeDischarged(left);
		assertNodeOpen(right);
		assertNodePending(imp);
		assertNodePending(root);
		
		assertFalse("Tree is discharged", tree.isDischarged());
		checkTree(tree, root, sequent);
	}
	
//	/**
//	 * Checks consistency after applying a rule that fails.
//	 */
//	// TODO : rewrite check since rules no longer used so
//	public void testApplyRuleFailure() {
//		IProverSequent sequent = makeSimpleSequent("⊥");
//		IProofTree tree = SequentProver.makeProofTree(sequent);
//		IProofTreeNode root = tree.getRoot();
//
//		boolean applied = root.applyRule((ProofRule) rf.hyp());
//		assertFalse(applied);
//		assertNodeOpen(root);
//	}

	/**
	 * Checks consistency after pruning a subtree on a pending node.
	 */
	public void testPrunePending() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root);
		// applyRule(root, rf.impI());
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];

		Tactics.conjI().apply(imp);
		// applyRule(imp, rf.conjI());
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
		// applyRule(root, rf.impI());
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];

		Tactics.conjI().apply(imp);
		// applyRule(imp, rf.conjI());
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		IProofTreeNode right = imp.getChildren()[1];
		
		Tactics.hyp().apply(left);
		Tactics.hyp().apply(right);
//		applyRule(left, rf.hyp());
//		applyRule(right, rf.hyp());
		
		// the nodes to prune are part of the same proof tree.
		assertSame(left.getProofTree(),tree);
		assertSame(right.getProofTree(),tree);
		// their parent node is discharged
		assertNodeDischarged(imp);

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
		// applyRule(graftRoot, rf.hyp());
		assertNodeDischarged(graftRoot);
		
		boolean success = treeRoot.graft(graft);
		assertFalse(success);
		
		// Grafted tree is still discharged
		assertNodeDischarged(graftRoot);
		
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
		// applyRule(graftRoot, rf.conjI());
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
		// applyRule(graftRoot, rf.conjI());
		assertEquals(2, graftRoot.getChildren().length);
		IProofTreeNode ch1 = graftRoot.getChildren()[0];
		IProofTreeNode ch2 = graftRoot.getChildren()[1];
		Tactics.tautology().apply(ch1);
		Tactics.tautology().apply(ch2);
		// applyRule(ch1, rf.hyp());
		// applyRule(ch2, rf.hyp());
		assertNodeDischarged(graftRoot);
		
		treeRoot.graft(graft);
		
		// Grafted tree is pruned
		assertNodeOpen(graftRoot);
		
		// Children have been grafted
		assertNotSame(ch1.getProofTree(),graft);
		assertNotSame(ch2.getProofTree(),graft);
		assertSame(ch1.getProofTree(),tree);
		assertSame(ch2.getProofTree(),tree);
		assertNodeDischarged(treeRoot);

		
	}


}
