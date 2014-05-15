/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.indexer.tables;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertPredecessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.sort.Graph;
import org.rodinp.internal.core.indexer.sort.Node;

public class GraphTests extends IndexTests {

	private static Graph<Integer> graph = new Graph<Integer>();

	@After
	public void tearDown() throws Exception {
		graph.clear();
		super.tearDown();
	}

	private static void setPreds(Graph<Integer> iter, Integer label,
			Integer... preds) {

		iter.setPredecessors(label, asList(preds));
	}

	private static void remove(Graph<Integer> iter, Integer label) {
		final Node<Integer> node = iter.getOrCreateNode(label);
		iter.remove(node);
	}

	private static List<Integer> getLabels(Collection<Node<Integer>> nodes) {
		List<Integer> result = new ArrayList<Integer>();
		for (Node<Integer> node : nodes) {
			result.add(node.getLabel());
		}
		return result;
	}

	private void assertLabels(Graph<Integer> gr, Integer... labels) {
		final List<Integer> expected = Arrays.asList(labels);
		final Collection<Node<Integer>> nodes = gr.getNodes();
		final List<Integer> actual = getLabels(nodes);

		assertEquals("Bad length for: " + nodes, expected.size(), actual.size());

		assertTrue("Not all present in: " + nodes, actual.containsAll(expected));
	}

	private void assertEmptyPreds(Graph<Integer> gr, Integer label) {
		final List<Integer> preds = gr.getPredecessors(label);
		assertTrue("should be empty", preds.isEmpty());
	}

	@Test
	public void testRemoveFirst() throws Exception {
		setPreds(graph, 2, 1);
		remove(graph, 1);

		assertLabels(graph, 2);
		assertEmptyPreds(graph, 2);
	}

	@Test
	public void testRemoveLast() throws Exception {
		setPreds(graph, 2, 1);
		remove(graph, 2);

		assertLabels(graph, 1);
	}

	@Test
	public void testRemoveInner() throws Exception {
		setPreds(graph, 2, 1);
		setPreds(graph, 3, 2);

		remove(graph, 2);

		assertLabels(graph, 1, 3);
		assertEmptyPreds(graph, 3);
	}

	@Test
	public void testSetGetPredecessors() {
		graph.setPredecessors(2, asList(1));
		final List<Integer> predecessors = graph.getPredecessors(2);

		assertPredecessors(predecessors, 1);
	}

	@Test
	public void testSetGetSeveralPredecessors() {
		graph.setPredecessors(3, asList(1, 2));
		final List<Integer> predecessors = graph.getPredecessors(3);

		assertPredecessors(predecessors, 1, 2);
	}

	@Test
	public void testGetNoSuchLabel() throws Exception {
		try {
			graph.getPredecessors(1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	@Test
	public void testClear() {
		setPreds(graph, 3, 1, 2);

		graph.clear();
		final Collection<Node<Integer>> nodes = graph.getNodes();

		assertTrue("graph not properly cleared", nodes.isEmpty());
	}

	@Test
	public void testGetNodes() {
		setPreds(graph, 3, 1, 2);

		final Collection<Node<Integer>> nodes = graph.getNodes();
		assertEquals("bad nodes size in: " + nodes, 3, nodes.size());

		for (Node<Integer> node : nodes) {
			switch (node.getLabel()) {
			case 1:
				assertEmptyPreds(graph, 1);
			case 2:
				assertEmptyPreds(graph, 2);
			case 3:
				assertPredecessors(graph.getPredecessors(3), 1, 2);
			}
		}
	}

	@Test
	public void testGetOrCreateNodeCreate() {
		final Node<Integer> node = graph.getOrCreateNode(1);
		
		assertEquals("bad node label", Integer.valueOf(1), node.getLabel());
		assertLabels(graph, 1);
		assertEmptyPreds(graph, 1);
	}

	@Test
	public void testGetOrCreateNodeGet() {
		graph.getOrCreateNode(1);
		
		final Node<Integer> node = graph.getOrCreateNode(1);
		
		assertEquals("bad node label", Integer.valueOf(1), node.getLabel());
		assertLabels(graph, 1);
		assertEmptyPreds(graph, 1);
	}

}
