/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.indexer.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rodinp.internal.core.indexer.sort.Node;

import junit.framework.TestCase;

public class NodeTests extends TestCase {

	private static final Integer ONE = new Integer(1);
	private static final Integer TWO = new Integer(2);
	private static final Integer THREE = new Integer(3);
	private static final Node<Integer> NODE_ONE = new Node<Integer>(ONE);
	private static final Node<Integer> NODE_TWO = new Node<Integer>(TWO);
	private static final Node<Integer> NODE_THREE = new Node<Integer>(THREE);
	private static final List<Node<Integer>> EMPTY_NODE_LIST = Collections
			.emptyList();

	private static void assertNodes(String nodeType,
			List<Node<Integer>> expected, List<Node<Integer>> actual) {
		assertEquals("Bad " + nodeType + "s length in: " + actual, expected
				.size(), actual.size());
		for (Node<Integer> n : expected) {
			assertTrue("missing " + nodeType + " " + n.getLabel(), actual
					.contains(n));
		}

	}

	private static void assertPredecessors(List<Node<Integer>> expected,
			Node<Integer> node) {
		List<Node<Integer>> predecessors = node.getPredecessors();
		assertNodes("predecessor", expected, predecessors);
	}

	private static void assertSuccessors(List<Node<Integer>> expected,
			Node<Integer> node) {
		List<Node<Integer>> successors = node.getSuccessors();
		assertNodes("successor", expected, successors);
	}

	private static void assertNoPredecessors(Node<Integer> node) {
		assertPredecessors(EMPTY_NODE_LIST, node);
	}

	private void assertMark(boolean b, Node<Integer> nodeThree) {
		boolean marked = nodeThree.isMarked();
		assertEquals("Bad mark in node " + nodeThree, b, marked);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		NODE_ONE.clear();
		NODE_TWO.clear();
		NODE_THREE.clear();
	}

	public void testGetLabel() {
		final Node<Integer> n = new Node<Integer>(ONE);

		final Integer label = n.getLabel();

		assertEquals("Bad label", ONE, label);
	}

	public void testGetPredecessors() {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_THREE.addPredecessor(NODE_TWO);

		List<Node<Integer>> expected = new ArrayList<Node<Integer>>();
		expected.add(NODE_ONE);
		expected.add(NODE_TWO);

		assertPredecessors(expected, NODE_THREE);
	}

	public void testAddPredecessor() {
		NODE_THREE.addPredecessor(NODE_ONE);

		List<Node<Integer>> expected = Collections.singletonList(NODE_ONE);

		assertPredecessors(expected, NODE_THREE);
	}

	public void testGetSuccessors() {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_TWO.addPredecessor(NODE_ONE);

		List<Node<Integer>> expected = new ArrayList<Node<Integer>>();
		expected.add(NODE_TWO);
		expected.add(NODE_THREE);

		assertSuccessors(expected, NODE_ONE);
	}

	public void testRemovePredecessor() {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_THREE.addPredecessor(NODE_TWO);

		NODE_THREE.removePredecessor(NODE_ONE);

		List<Node<Integer>> expected = new ArrayList<Node<Integer>>();
		expected.add(NODE_TWO);

		assertPredecessors(expected, NODE_THREE);
	}

	public void testClear() {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_THREE.addPredecessor(NODE_TWO);
		NODE_THREE.setMark(true);
		NODE_THREE.setOrderPos(3);

		NODE_THREE.clear();

		assertNoPredecessors(NODE_THREE);
		final boolean marked = NODE_THREE.isMarked();
		final int orderPos = NODE_THREE.getOrderPos();

		assertFalse("mark not cleared", marked);
		assertEquals("orderPos not cleared", -1, orderPos);
	}

	public void testSetIsMarkTrue() {
		NODE_THREE.setMark(true);

		assertMark(true, NODE_THREE);
	}

	public void testSetIsMarkFalse() {
		NODE_THREE.setMark(false);

		assertMark(false, NODE_THREE);
	}

	public void testDegree() throws Exception {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_THREE.addPredecessor(NODE_TWO);

		final int degree = NODE_THREE.degree();

		assertEquals("Bad degree", 2, degree);
	}

	public void testGetOrderPosInit() throws Exception {
		final int orderPos = NODE_ONE.getOrderPos();

		assertEquals("Initial orderPos should be -1", -1, orderPos);
	}

	public void testSetGetOrderPos() throws Exception {
		final int pos = 123;

		NODE_ONE.setOrderPos(pos);

		final int actual = NODE_ONE.getOrderPos();

		assertEquals("Bad orderPos", pos, actual);
	}

	public void testSetGetOrderPosNegative() throws Exception {
		final int pos = -21;

		NODE_ONE.setOrderPos(pos);

		final int actual = NODE_ONE.getOrderPos();

		assertEquals("Bad orderPos", -1, actual);
	}

	public void testIsAfterInit() throws Exception {
		final boolean after = NODE_THREE.isAfter(NODE_ONE);

		assertFalse("isAfter should return false when orderPos is not set",
				after);
	}

	public void testIsAfter() throws Exception {
		NODE_ONE.setOrderPos(12);
		NODE_TWO.setOrderPos(45);

		final boolean afterTrue = NODE_TWO.isAfter(NODE_ONE);
		final boolean afterFalse = NODE_ONE.isAfter(NODE_TWO);

		assertTrue("Nodes not in proper order", afterTrue);
		assertFalse("Nodes not in proper order", afterFalse);
	}
}
