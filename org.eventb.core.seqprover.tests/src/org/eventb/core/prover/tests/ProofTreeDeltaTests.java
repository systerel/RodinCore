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
import org.eventb.core.prover.rules.RuleFactory;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Unit tests for deltas fired when modifying proof trees.
 * 
 * @author Laurent Voisin
 */
public class ProofTreeDeltaTests extends AbstractProofTreeTests {

	RuleFactory rf = new RuleFactory();
	
	/**
	 * Ensures that applying a rule to an open node fires a CHILDREN delta.
	 */
	public void testApply() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		startDeltas(tree);
		applyRule(root, rf.impI());
		assertDeltas("⊤⇒⊤ [CHILDREN]");
	}

	/**
	 * Ensures that pruning a node fires a CHILDREN delta.
	 */
	public void testPrune() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();
		applyRule(root, rf.impI());

		startDeltas(tree);
		root.pruneChildren();
		assertDeltas("⊤⇒⊤ [CHILDREN]");
	}

	/**
	 * Ensures that pruning a discharged node fires a CHILDREN delta.
	 */
	public void testPruneDischarged() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();
		applyRule(root, rf.impI());
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];
		applyRule(imp, rf.hyp());

		startDeltas(tree);
		imp.pruneChildren();
		assertDeltas(
				"⊤⇒⊤ [STATUS]\n" +
				"  ⊤ [STATUS|CHILDREN]"
		);
	}

	/**
	 * Ensures that discharging a node fires a STATUS delta for its parent.
	 */
	public void testDischargeParent() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();
		applyRule(root, rf.impI());
		assertEquals(1, root.getChildren().length);
		IProofTreeNode imp = root.getChildren()[0];

		startDeltas(tree);
		applyRule(imp, rf.hyp());
		assertDeltas(
				"⊤⇒⊤ [STATUS]\n" +
				"  ⊤ [STATUS|CHILDREN]"
		);
	}

	/**
	 * Ensures that no STATUS delta is fired for an ancestor which doesn't get
	 * discharged, when discharging a node.
	 */
	public void testNoDischargeAncestor() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		applyRule(root, rf.impI());
		assertNotEmpty(root.getChildren());
		IProofTreeNode imp = root.getChildren()[0];
		applyRule(imp, rf.conjI());
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];

		startDeltas(tree);
		applyRule(left, rf.hyp());
		assertDeltas(
				"⊤⇒⊤∧⊥ []\n" +
				"  ⊤∧⊥ []\n" +
				"    ⊤ [STATUS|CHILDREN]"
		);
	}

	/**
	 * Ensures that no STATUS delta is fired for an ancestor which wasn't yet
	 * discharged, when pruning a node.
	 */
	public void testDischargeBranch() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = SequentProver.makeProofTree(sequent);
		IProofTreeNode root = tree.getRoot();

		applyRule(root, rf.impI());
		assertNotEmpty(root.getChildren());
		IProofTreeNode imp = root.getChildren()[0];
		applyRule(imp, rf.conjI());
		assertEquals(2, imp.getChildren().length);
		IProofTreeNode left = imp.getChildren()[0];
		applyRule(left, rf.hyp());

		startDeltas(tree);
		left.pruneChildren();
		assertDeltas(
				"⊤⇒⊤∧⊥ []\n" +
				"  ⊤∧⊥ []\n" +
				"    ⊤ [STATUS|CHILDREN]"
		);
	}

}
