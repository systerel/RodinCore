/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static java.util.Collections.emptySet;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;

/**
 * Common implementation for tests related to proof trees.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractProofTreeTests implements IProofTreeChangedListener {

	ArrayList<IProofTreeDelta> deltas = null;


	/**
	 * Applies the given rule to the given node, expecting success.
	 * 
	 * @param node
	 *            a proof tree node
	 * @param rule
	 *            the rule to apply
	 */
	public void applyRule(IProofTreeNode node, IProofRule rule) {
		boolean applied = node.applyRule(rule);
		assertTrue(applied);
		assertSame(node.getRule(), rule);
		assertFalse(node.isOpen());
	}

	/**
	 * Checks that the first given node is an ancestor of the second one.
	 * 
	 * @param expectedAncestor
	 *            the expected ancestor
	 * @param node
	 *            the node to test
	 */
	public void assertAncestor(IProofTreeNode ancestor, IProofTreeNode node) {
		IProofTreeNode parent = node.getParent();
		while (parent != null) {
			if (parent == ancestor)
				return;
			node = parent;
			parent = parent.getParent();
		}
		fail("can't find expected ancestor among ancestors");
	}

	/**
	 * Checks that the specified delta has been produced since the last call to
	 * startDeltas() or flushDeltas(), whichever happened last.
	 * 
	 * @param expected
	 *            the expected delta
	 */
	public void assertDeltas(String expected) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IProofTreeDelta delta : deltas) {
			if (sep)
				builder.append('\n');
			builder.append(delta);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail("Unexpected delta:\n" + actual);
		}
	}

	/**
	 * Checks that the given array is empty.
	 * 
	 * @param array
	 *            the array to test for emptyness
	 */
	public final void assertEmpty(Object[] array) {
		assertEquals("array is empty", 0, array.length);
	}

	/**
	 * Checks that the given node is closed, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodeClosed(IProofTreeNode node) {
		// node.getChildren() is irrelevent
		assertNull(node.getFirstOpenDescendant());
		assertEmpty(node.getOpenDescendants());
		assertNotNull(node.getRule());
		// node.hasChildren() is irrelevent
		assertTrue(node.isClosed());
		assertFalse(node.isOpen());
	}
	
	/**
	 * Checks that the given node is reviewed, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodeReviewed(IProofTreeNode node) {
		assertNodeClosed(node);
		assertTrue(ProverLib.isReviewed(node.getConfidence()));
	}
	
	/**
	 * Checks that the given node is discharged, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodeDischarged(IProofTreeNode node) {
		assertNodeClosed(node);
		assertTrue(ProverLib.isDischarged(node.getConfidence()));
	}

	/**
	 * Checks that the given tree is discharged, using all available methods.
	 * 
	 * @param tree
	 *            the proof tree to test
	 */
	public void assertTreeDischarged(IProofTree tree) {
		assertNodeDischarged(tree.getRoot());
	}

	/**
	 * Checks that the given node is open, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodeOpen(IProofTreeNode node) {
		assertEmpty(node.getChildNodes());
		assertSame(node, node.getFirstOpenDescendant());
		assertSingleton(node, node.getOpenDescendants());
		assertNull(node.getRule());
		assertFalse(node.hasChildren());
		assertFalse(node.isClosed());
		assertTrue(node.isOpen());
	}

	/**
	 * Checks that the given node is pending, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodePending(IProofTreeNode node) {
		assertNotEmpty(node.getChildNodes());
		assertNotNull(node.getFirstOpenDescendant());
		assertNotSame(node, node.getFirstOpenDescendant());
		assertNotEmpty(node.getOpenDescendants());
		assertNotNull(node.getRule());
		assertTrue(node.hasChildren());
		assertFalse(node.isClosed());
		assertFalse(node.isOpen());
	}

	/**
	 * Checks that the given array is not empty.
	 * 
	 * @param array
	 *            the array to test for emptyness
	 */
	public final void assertNotEmpty(Object[] array) {
		assertFalse("array is not empty", array.length == 0);
	}

	/**
	 * Checks that the given array contains one and only one expected element.
	 * 
	 * @param expectedElement
	 *            the expected element of the given array
	 * @param array
	 *            the array to test for emptyness
	 */
	public final void assertSingleton(Object expectedElement, Object[] array) {
		assertEquals("array is not a singleton", 1, array.length);
		assertSame("wrong element", expectedElement, array[0]);
	}

	/**
	 * Checks that the information about open descendants of a given node is
	 * consistent: the nodes returned are actually descendants and are open.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void checkOpenDescendants(IProofTreeNode node) {
		IProofTreeNode[] openDescendants = node.getOpenDescendants();
		IProofTreeNode firstOpenDescendant = node.getFirstOpenDescendant();
		if (openDescendants.length == 0) {
			assertNull(firstOpenDescendant);
			return;
		}
		assertSame(firstOpenDescendant, openDescendants[0]);
		for (IProofTreeNode openDescendant : openDescendants) {
			assertNodeOpen(openDescendant);
			assertAncestor(node, openDescendant);
		}
	}

	/**
	 * Checks consistency of information on a proof tree and its root.
	 * 
	 * @param tree
	 *            the proof tree to test
	 * @param root
	 *            the expected root of the given tree
	 * @param sequent
	 *            the expected sequent of the proof tree
	 */
	public void checkTree(IProofTree tree, IProofTreeNode root,
			IProverSequent sequent) {

		// Tree and root node are properly connected.
		assertSame("Wrong root node", root, tree.getRoot());
		assertSame("wrong tree", tree, root.getProofTree());
		assertNull("Root node has a parent", root.getParent());

		// Tree and root node share the same factory.
		assertSame("Wrong tree factory", root.getFormulaFactory(),
				tree.getFormulaFactory());

		// Tree and root node share the same sequent.
		assertSame("Wrong tree sequent", sequent, tree.getSequent());
		assertSame("Wrong tree sequent", root.getSequent(), tree.getSequent());

		// Discharge information is consistent
		assertEquals("Inconsistency in discharged info", tree.isClosed(),
				root.isClosed());
	}

	/**
	 * Flushes the delta recorded. After this call, there is no delta pending,
	 * waiting for asserting them.
	 */
	public void flushDeltas() {
		deltas = new ArrayList<IProofTreeDelta>();
	}

	/**
	 * Returns a new prover sequent with no hypothesis and the given goal.
	 * 
	 * @param goal
	 *            goal of the sequent
	 * @return a new sequent with the given goal
	 */
	public static IProverSequent makeSimpleSequent(String goal) {
		final ITypeEnvironmentBuilder te = mTypeEnvironment();
		final Predicate goalPredicate = genPred(te, goal);
		final Set<Predicate> hypotheses = emptySet();
		return ProverFactory.makeSequent(te, hypotheses, goalPredicate);
	}

	/**
	 * Records a new delta notified by a proof tree.
	 * 
	 * Not to be called by test cases.
	 */
	public void proofTreeChanged(IProofTreeDelta delta) {
		assertTrue(deltas != null);
		deltas.add(delta);
	}

	/**
	 * Starts recording delta for the given proof tree.
	 * 
	 * @param tree
	 *            the proof tree for which deltas should get recorded
	 */
	public void startDeltas(IProofTree tree) {
		deltas = new ArrayList<IProofTreeDelta>();
		tree.addChangeListener(this);
	}

	/**
	 * Stops recording delta for the given proof tree.
	 * 
	 * @param tree
	 *            the proof tree for which deltas should not be recorded
	 */
	public void stopDeltas(IProofTree tree) {
		tree.removeChangeListener(this);
		deltas = new ArrayList<IProofTreeDelta>();
	}

}
