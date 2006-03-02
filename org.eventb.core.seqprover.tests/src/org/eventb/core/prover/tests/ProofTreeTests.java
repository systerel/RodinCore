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
import org.eventb.core.prover.rules.ProofRule;
import org.eventb.core.prover.rules.RuleFactory;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Unit tests for classes ProofTree and ProofTreeNode: basic manipulations of
 * proof trees.
 * 
 * @author Laurent Voisin
 */
public class ProofTreeTests extends AbstractProofTreeTests {

	RuleFactory rf = new RuleFactory();
	
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

		applyRule(root, rf.impI());
		assertNodePending(root);
		assertNotEmpty(root.getChildren());

		IProofTreeNode imp = root.getChildren()[0];
		assertSingleton(imp, root.getChildren());
		assertNodeOpen(imp);
		
		applyRule(imp, rf.hyp());
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

		applyRule(root, rf.impI());
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

		applyRule(root, rf.impI());
		assertNotEmpty(root.getChildren());
		IProofTreeNode imp = root.getChildren()[0];
		assertSingleton(imp, root.getChildren());
		assertNodeOpen(imp);
		assertNodePending(root);

		applyRule(imp, rf.conjI());
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		IProofTreeNode right = imp.getChildren()[1];
		assertNodeOpen(left);
		assertNodeOpen(right);
		assertNodePending(imp);
		assertNodePending(root);
		
		applyRule(left, rf.hyp());
		assertEmpty(left.getChildren());
		assertNodeDischarged(left);
		assertNodeOpen(right);
		assertNodePending(imp);
		assertNodePending(root);
		
		assertFalse("Tree is discharged", tree.isDischarged());
		checkTree(tree, root, sequent);
	}
	
	/**
	 * Checks consistency after applying a rule that fails.
	 */
	public void testApplyRuleFailure() {
		IProverSequent sequent = makeSimpleSequent("⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		boolean applied = root.applyRule((ProofRule) rf.hyp());
		assertFalse(applied);
		assertNodeOpen(root);
	}

	/**
	 * Checks consistency after pruning a subtree on a pending node.
	 */
	public void testPrunePending() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		applyRule(root, rf.impI());
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];

		applyRule(imp, rf.conjI());
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		IProofTreeNode right = imp.getChildren()[1];

		assertNodePending(root);
		root.pruneChildren();
		assertNodeOpen(root);
		// the pruned nodes are not part of the tree anymore.
		assertNull(imp.getParent());
		assertNull(imp.getProofTree());
		assertNull(left.getProofTree());
		assertNull(right.getProofTree());
	}

	/**
	 * Checks consistency after pruning a subtree on a discharged node.
	 */
	public void testPruneDischarged() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		applyRule(root, rf.impI());
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];

		applyRule(imp, rf.conjI());
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		IProofTreeNode right = imp.getChildren()[1];
		
		applyRule(left, rf.hyp());
		applyRule(right, rf.hyp());
		
		assertNodeDischarged(imp);
		imp.pruneChildren();
		assertNodeOpen(imp);
		assertNodePending(root);
		// the pruned nodes are not part of the tree anymore.
		assertNull(left.getProofTree());
		assertNull(right.getProofTree());
	}


}
