/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.junit.Assert.assertEquals;

import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.junit.Test;

/**
 * Unit tests for deltas fired when modifying proof trees.
 * 
 * @author Laurent Voisin
 */
public class ProofTreeDeltaTests extends AbstractProofTreeTests {

	
	/**
	 * Ensures that applying a rule to an open node fires a RULE & CHILDREN delta.
	 */
	@Test
	public void testApply() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		startDeltas(tree);
		Tactics.impI().apply(root, null);
		assertDeltas("⊤⇒⊤ [RULE|CHILDREN]");
	}

	/**
	 * Ensures that trying to apply a rule that fails doesn't produce any delta.
	 */
	@Test
	public void testApplyFailed() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		startDeltas(tree);
		Tactics.conjI().apply(root, null);
		assertDeltas("");
	}

	/**
	 * Ensures that pruning a non-open node fires a RULE & CHILDREN delta.
	 */
	@Test
	public void testPrune() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();
		Tactics.impI().apply(root, null);

		startDeltas(tree);
		root.pruneChildren();
		assertDeltas("⊤⇒⊤ [RULE|CHILDREN]");
	}

	/**
	 * Ensures that pruning a discharged node fires a RULE & CHILDREN delta.
	 */
	@Test
	public void testPruneDischarged() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();
		Tactics.impI().apply(root, null);
		assertEquals(1, root.getChildNodes().length);
		IProofTreeNode imp = root.getChildNodes()[0];
		Tactics.hyp().apply(imp, null);

		startDeltas(tree);
		imp.pruneChildren();
		assertDeltas(
				"⊤⇒⊤ [CONFIDENCE]\n" +
				"  ⊤ [RULE|CHILDREN|CONFIDENCE]"
		);
	}

	/**
	 * Ensures that discharging a node fires a CONFIDENCE delta for its parent.
	 */
	@Test
	public void testDischargeParent() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();
		Tactics.impI().apply(root, null);
		assertEquals(1, root.getChildNodes().length);
		IProofTreeNode imp = root.getChildNodes()[0];

		startDeltas(tree);
		Tactics.hyp().apply(imp, null);
		assertDeltas(
				"⊤⇒⊤ [CONFIDENCE]\n" +
				"  ⊤ [RULE|CHILDREN|CONFIDENCE]"
		);
	}

	/**
	 * Ensures that no CONFIDENCE delta is fired for an ancestor which doesn't get
	 * discharged, when discharging a node.
	 */
	@Test
	public void testNoDischargeAncestor() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertNotEmpty(root.getChildNodes());
		IProofTreeNode imp = root.getChildNodes()[0];
		Tactics.conjI().apply(imp, null);
		assertEquals(2, imp.getChildNodes().length);
		IProofTreeNode left = imp.getChildNodes()[0];

		startDeltas(tree);
		Tactics.hyp().apply(left, null);
		assertDeltas(
				"⊤⇒⊤∧⊥ []\n" +
				"  ⊤∧⊥ []\n" +
				"    ⊤ [RULE|CHILDREN|CONFIDENCE]"
		);
	}

	/**
	 * Ensures that no CONFIDENCE delta is fired for an ancestor which wasn't yet
	 * discharged, when pruning a node.
	 */
	@Test
	public void testDischargeBranch() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertNotEmpty(root.getChildNodes());
		IProofTreeNode imp = root.getChildNodes()[0];
		Tactics.conjI().apply(imp, null);
		assertEquals(2, imp.getChildNodes().length);
		IProofTreeNode left = imp.getChildNodes()[0];
		Tactics.hyp().apply(left, null);

		startDeltas(tree);
		left.pruneChildren();
		assertDeltas(
				"⊤⇒⊤∧⊥ []\n" +
				"  ⊤∧⊥ []\n" +
				"    ⊤ [RULE|CHILDREN|CONFIDENCE]"
		);
	}
	
	
	/**
	 * Ensures that setting a comment of a node produces a COMMENT delta.
	 */
	@Test
	public void testSetComment() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		startDeltas(tree);
		root.setComment("Test Comment");
		assertDeltas("⊤⇒⊤ [COMMENT]");
	}	

	/**
	 * Ensures that batching a job at a node produces a combined DELTA
	 */
	@Test
	public void testBatchRunNode() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode root = tree.getRoot();

		startDeltas(tree);
		tree.run(new Runnable() {

			public void run() {
				Tactics.impI().apply(root, null);
				root.setComment("Test Comment");
			}
			
		});
		assertDeltas("⊤⇒⊤ [RULE|CHILDREN|COMMENT]");
	}
	
	/**
	 * Ensures that batching a job at different set of nodes in a proof tree 
	 * produces a combined DELTA
	 */
	@Test
	public void testBatchRunTree() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertNotEmpty(root.getChildNodes());
		IProofTreeNode imp = root.getChildNodes()[0];
		Tactics.conjI().apply(imp, null);
		assertEquals(2, imp.getChildNodes().length);
		final IProofTreeNode left = imp.getChildNodes()[0];
		final IProofTreeNode right = imp.getChildNodes()[1];
		startDeltas(tree);
		
		tree.run(new Runnable() {

			public void run() {
				left.setComment("Test Left Comment");
				Tactics.hyp().apply(left, null);
				right.setComment("Test Right Comment");
			}
			
		});
		
		assertDeltas(
				"⊤⇒⊤∧⊥ []\n" + 
				"  ⊤∧⊥ []\n" + 
				"    ⊤ [RULE|CHILDREN|CONFIDENCE|COMMENT]\n" + 
				"    ⊥ [COMMENT]");
	}

}
