package org.rodinp.internal.core.index.tables.tests;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.internal.core.index.tables.Node;

import junit.framework.TestCase;

public class NodeTests extends TestCase {

	private static final Integer ONE = new Integer(1);
	private static final Integer TWO = new Integer(2);
	private static final Integer THREE = new Integer(3);
	private static final Node<Integer> NODE_ONE = new Node<Integer>(ONE);
	private static final Node<Integer> NODE_TWO = new Node<Integer>(TWO);
	private static final Node<Integer> NODE_THREE = new Node<Integer>(THREE);
	private static final List<Node<Integer>> EMPTY_NODE_LIST = new ArrayList<Node<Integer>>();

	private static void assertNodes(String nodeType,
			List<Node<Integer>> expected, List<Node<Integer>> actual) {
		assertEquals("Bad " + nodeType + "s length", expected.size(), actual
				.size());
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
	}

	public void testGetLabel() {
		final Node<Integer> n = new Node<Integer>(ONE);

		final Integer label = n.getLabel();

		assertEquals("Bad label", ONE, label);
	}

	public void testAddPredecessor() {
		NODE_THREE.addPredecessor(NODE_ONE);

		List<Node<Integer>> expected = new ArrayList<Node<Integer>>();
		expected.add(NODE_ONE);

		assertPredecessors(expected, NODE_THREE);
	}

	public void testGetPredecessors() {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_THREE.addPredecessor(NODE_TWO);

		List<Node<Integer>> expected = new ArrayList<Node<Integer>>();
		expected.add(NODE_ONE);
		expected.add(NODE_TWO);

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

		NODE_THREE.clear();

		assertNoPredecessors(NODE_THREE);
	}

	public void testSetIsMarkTrue() {
		NODE_THREE.setMark(true);

		assertMark(true, NODE_THREE);
	}

	public void testSetIsMarkFalse() {
		NODE_THREE.setMark(false);

		assertMark(false, NODE_THREE);
	}

	public void testMarkSuccessors() throws Exception {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_TWO.addPredecessor(NODE_ONE);

		NODE_ONE.markSuccessors();

		assertMark(true, NODE_THREE);
		assertMark(true, NODE_TWO);
	}

	public void testDegree() throws Exception {
		NODE_THREE.addPredecessor(NODE_ONE);
		NODE_THREE.addPredecessor(NODE_TWO);

		final int degree = NODE_THREE.degree();

		assertEquals("Bad degree", 2, degree);
	}
	
}
