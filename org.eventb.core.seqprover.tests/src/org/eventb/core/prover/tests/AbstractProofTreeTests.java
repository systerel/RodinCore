/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.tests;

import java.util.HashSet;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.rules.IProofRule;
import org.eventb.core.prover.rules.ProofRule;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

/**
 * Common implementation for tests related to proof trees.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractProofTreeTests extends TestCase {

	FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * Returns a new prover sequent with no hypothesis and the given goal.
	 * 
	 * @param goal
	 *            goal of the sequent
	 * @return a new sequent with the given goal
	 */
	public IProverSequent makeSimpleSequent(String goal) {
		IParseResult parseResult = ff.parsePredicate(goal);
		Predicate goalPredicate = parseResult.getParsedPredicate();
		assertNotNull("Can't parse predicate: " + goal, goalPredicate);
		ITypeEnvironment te = ff.makeTypeEnvironment();
		ITypeCheckResult tr = goalPredicate.typeCheck(te);
		assertTrue("Can't typecheck predicate" + goalPredicate,
				goalPredicate.isTypeChecked());
		return new SimpleProverSequent(
				tr.getInferredEnvironment(),
				new HashSet<Hypothesis>(),
				goalPredicate);
	}
	
	public final void assertEmpty(Object[] array) {
		assertEquals("array is empty", 0, array.length);
	}
	
	public final void assertNotEmpty(Object[] array) {
		assertFalse("array is not empty", array.length == 0);
	}
	
	public final void assertSingleton(Object expected, Object[] array) {
		assertEquals("array is not a singleton", 1, array.length);
		assertSame("wrong element", expected, array[0]);
	}
	
	/**
	 * Checks that the given node is open, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodeOpen(IProofTreeNode node) {
		assertEmpty(node.getChildren());
		assertSame(node, node.getFirstOpenDescendant());
		assertSingleton(node, node.getOpenDescendants());
		assertNull(node.getRule());
		assertFalse(node.hasChildren());
		assertFalse(node.isDischarged());
		assertTrue(node.isOpen());
	}

	/**
	 * Checks that the given node is pending, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodePending(IProofTreeNode node) {
		assertNotEmpty(node.getChildren());
		assertNotNull(node.getFirstOpenDescendant());
		assertNotSame(node, node.getFirstOpenDescendant());
		assertNotEmpty(node.getOpenDescendants());
		assertNotNull(node.getRule());
		assertTrue(node.hasChildren());
		assertFalse(node.isDischarged());
		assertFalse(node.isOpen());
	}

	/**
	 * Checks that the given node is discharged, using all available methods.
	 * 
	 * @param node
	 *            the node to test
	 */
	public void assertNodeDischarged(IProofTreeNode node) {
		// node.getChildren() is irrelevent
		assertNull(node.getFirstOpenDescendant());
		assertEmpty(node.getOpenDescendants());
		assertNotNull(node.getRule());
		// node.hasChildren() is irrelevent
		assertTrue(node.isDischarged());
		assertFalse(node.isOpen());
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

		// Tree and root node share the same sequent.
		assertSame("Wrong tree sequent", sequent, tree.getSequent());
		assertSame("Wrong tree sequent", root.getSequent(), tree.getSequent());

		// Discharge information is consistent
		assertEquals("Inconsistency in discharged info", tree.isDischarged(),
				root.isDischarged());
	}

	/**
	 * Applies the given rule to the given node, expecting success.
	 * 
	 * @param node
	 *            a proof tree node
	 * @param rule
	 *            the rule to apply
	 */
	public void applyRule(IProofTreeNode node, IProofRule rule) {
		boolean applied = node.applyRule((ProofRule) rule);
		assertTrue(applied);
		assertSame(node.getRule(), rule);
		assertFalse(node.isOpen());
	}
	
}
