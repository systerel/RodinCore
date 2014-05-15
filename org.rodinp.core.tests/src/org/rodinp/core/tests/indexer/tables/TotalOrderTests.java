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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertPredecessors;
import static org.rodinp.core.tests.util.IndexTestsUtil.makeArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.sort.TotalOrder;

public class TotalOrderTests extends IndexTests {

	private static final TotalOrder<Integer> order = new TotalOrder<Integer>();

	private static void assertOrderedIteration(TotalOrder<Integer> iter,
			Integer... expectedOrder) {
		for (Integer i : expectedOrder) {
			assertNext(iter, i);
		}
		assertNoNext(iter);
	}

	private static void assertNext(TotalOrder<Integer> iter, Integer i) {
		final boolean hasNext = iter.hasNext();
		assertTrue("Iterator should have next", hasNext);
		final Integer next = iter.next();
		assertEquals("Bad next element", i, next);
	}

	private static void assertAllIteratedOnce(TotalOrder<Integer> iter,
			Integer... expected) {

		final Set<Integer> expSet = new HashSet<Integer>(Arrays
				.asList(expected));

		Integer next = Integer.MIN_VALUE;
		while (!expSet.isEmpty()) {
			final boolean hasNext = iter.hasNext();

			assertTrue("Iterator should have next after " + next, hasNext);

			next = iter.next();
			final boolean containsNext = expSet.contains(next);

			assertTrue("Unexpected iterated element " + next, containsNext);
			expSet.remove(next);
		}

	}

	private static void assertAllIteratedOnceToEnd(TotalOrder<Integer> iter,
			Integer... expected) {

		assertAllIteratedOnce(iter, expected);
		assertNoNext(iter);
	}

	private static void assertPartitionOrder(TotalOrder<Integer> iter,
			Integer[] before, Integer[] after) {

		assertAllIteratedOnce(iter, before);
		assertAllIteratedOnceToEnd(iter, after);
		assertNoNext(iter);
	}

	private static void assertNoNext(TotalOrder<Integer> iter) {

		final boolean hasNext = iter.hasNext();
		assertFalse("Should not have next", hasNext);
	}

	private static void setPreds(TotalOrder<Integer> iter, Integer label,
			Integer... preds) {

		setPreds(iter, true, label, preds);
	}

	private static void setPreds(TotalOrder<Integer> iter, boolean setToIter,
			Integer label, Integer... preds) {

		iter.setPredecessors(label, asList(preds));

		if (setToIter) {
			setToIter(iter, label);
			setToIter(iter, preds);
		}
	}

	private static void setToIter(TotalOrder<Integer> iter, Integer... ints) {

		for (Integer i : ints) {
			iter.setToIter(i);
		}
	}

	private int succModulo(int i, int mod) {
		return (i % mod) + 1;
	}

	@After
	public void tearDown() throws Exception {
		order.clear();
		super.tearDown();
	}

	@Test
	public void testSetPredecessors() {
		setPreds(order, 2, 1);

		assertOrderedIteration(order, 1, 2);
	}

	@Test
	public void testSetSeveralPreds() throws Exception {
		setPreds(order, 3, 1, 2);

		assertPartitionOrder(order, makeArray(1, 2), makeArray(3));
	}

	@Test
	public void testGetPredecessors() throws Exception {
		setPreds(order, 3, 1, 2);

		final List<Integer> predecessors = order.getPredecessors(3);

		assertPredecessors(predecessors, 1, 2);
	}

	@Test
	public void testClear() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 4, 3);

		order.clear();

		assertNoNext(order);
	}

	@Test
	public void testHasNext() throws Exception {
		setPreds(order, 2, 1);

		assertNext(order, 1);
		assertNext(order, 2);

		assertNoNext(order);
	}

	@Test
	public void testIterNext() {
		setPreds(order, 2, 1);

		assertNext(order, 1);
	}

	@Test
	public void testIterNoMoreNext() throws Exception {
		setPreds(order, 2, 1);
		order.next();
		order.next();

		assertNoNext(order);
		try {
			order.next();
			fail("expected NoSuchElementException");
		} catch (NoSuchElementException e) {
			// OK
		}
	}

	@Test
	public void testIterNoMoreMarked() throws Exception {
		setPreds(order, false, 2, 1);

		assertNoNext(order);
		try {
			order.next();
			fail("expected NoSuchElementException");
		} catch (NoSuchElementException e) {
			// OK
		}
	}

	@Test
	public void testSetToIter() throws Exception {
		setPreds(order, false, 2, 1);

		order.setToIter(2);

		assertOrderedIteration(order, 2);
	}

	@Test
	public void testSetToIterCreateNode() throws Exception {
		order.setToIter(314);

		assertOrderedIteration(order, 314);
	}

	@Test
	public void testSetToIterSeveralTimesTheSame() throws Exception {
		setPreds(order, false, 2, 1);

		order.setToIter(2);
		order.setToIter(2);

		assertOrderedIteration(order, 2);
	}

	@Test
	public void testSetToIterSuccessors() throws Exception {
		setPreds(order, false, 2, 1);
		setPreds(order, false, 3, 1);

		order.setToIter(1);
		order.next();

		order.setToIterSuccessors();

		assertAllIteratedOnceToEnd(order, 2, 3);
	}

	@Test
	public void testSTISuccCycle() throws Exception {
		setPreds(order, false, 2, 1);
		setPreds(order, false, 3, 2);
		setPreds(order, false, 4, 3);
		setPreds(order, false, 1, 4);

		int i = 3;
		order.setToIter(i);
		while (order.hasNext()) {
			assertNext(order, i);
			order.setToIterSuccessors();
			i = succModulo(i, 4);
		}
	}

	@Test
	public void testEnd() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);

		assertOrderedIteration(order, 1, 2, 3);

		order.end();

		setToIter(order, 1, 2, 3);
		assertOrderedIteration(order, 1, 2, 3);
	}

	@Test
	public void testSeveralEnd() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);

		assertOrderedIteration(order, 1, 2, 3);
		order.end();
		order.end();

		setToIter(order, 1, 2, 3);
		assertOrderedIteration(order, 1, 2, 3);
	}

	@Test
	public void testCycle1() throws Exception {
		try {
			setPreds(order, 1, 1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	@Test
	public void testCycle2() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 1, 2);

		assertAllIteratedOnceToEnd(order, 1, 2);
	}

	@Test
	public void testCycle3() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);
		setPreds(order, 1, 3);

		assertAllIteratedOnceToEnd(order, 1, 2, 3);
	}

	@Test
	public void testCycle4() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);
		setPreds(order, 4, 3);
		setPreds(order, 1, 4);

		assertAllIteratedOnceToEnd(order, 1, 2, 3, 4);
	}

	@Test
	public void testCycle4Iter24() throws Exception {
		setPreds(order, false, 2, 1);
		setPreds(order, false, 3, 2);
		setPreds(order, false, 4, 3);
		setPreds(order, false, 1, 4);

		setToIter(order, 2, 4);
		assertAllIteratedOnceToEnd(order, 2, 4);
	}

	@Test
	public void testTwoSeparateCycles() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);
		setPreds(order, 4, 3);
		setPreds(order, 1, 4);

		setPreds(order, 12, 11);
		setPreds(order, 13, 12);
		setPreds(order, 14, 13);
		setPreds(order, 11, 14);

		assertAllIteratedOnceToEnd(order, 1, 2, 3, 4, 11, 12, 13, 14);
	}

	@Test
	public void testTwoLinkedCycles() throws Exception {
		// linked by node 4 -> 11
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);
		setPreds(order, 4, 3);
		setPreds(order, 1, 4);

		setPreds(order, 12, 11);
		setPreds(order, 13, 12);
		setPreds(order, 14, 13);
		setPreds(order, 11, 14, 4);

		assertAllIteratedOnceToEnd(order, 1, 2, 3, 4, 11, 12, 13, 14);
	}

	@Test
	public void testTwoCyclesCommonNode() throws Exception {
		// 14 is the common node
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);
		setPreds(order, 14, 3, 13);
		setPreds(order, 1, 14);

		setPreds(order, 12, 11);
		setPreds(order, 13, 12);
		setPreds(order, 11, 14);

		assertAllIteratedOnceToEnd(order, 1, 2, 3, 11, 12, 13, 14);
	}

	@Test
	public void testJoinedCycles() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);
		setPreds(order, 4, 3, 2);
		setPreds(order, 1, 4);

		assertAllIteratedOnceToEnd(order, 1, 2, 3, 4);
	}

	@Test
	public void testModifyBeforeIter() throws Exception {
		setPreds(order, 2, 1);

		assertNext(order, 1);
		assertNext(order, 2);

		setPreds(order, 1, 0);

		// iteration should restart from 0
		assertOrderedIteration(order, 0, 1, 2);
	}

	@Test
	public void testModifyAtIterPred() throws Exception {
		setPreds(order, 3, 1);
		setPreds(order, 4, 3);

		assertNext(order, 1);
		assertNext(order, 3);

		setPreds(order, 3, 2, 1);
		setPreds(order, 2, 1);

		// iteration should restart from 2
		assertOrderedIteration(order, 2, 3, 4);
	}

	@Test
	public void testModifyAtIterSucc() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);

		assertNext(order, 1);
		assertNext(order, 2);

		setPreds(order, 4, 2);

		// iteration should not restart
		// assertNext(order, 2);
		assertAllIteratedOnceToEnd(order, 3, 4);
	}

	@Test
	public void testModifyAfterIter() throws Exception {
		setPreds(order, 2, 1);

		assertNext(order, 1);

		setPreds(order, 3, 2);
		assertOrderedIteration(order, 2, 3);
	}

	@Test
	public void testIterRemove() {
		setPreds(order, 2, 1);

		assertNext(order, 1);
		order.remove();

		assertOrderedIteration(order, 2);
	}

	@Test
	public void testIterSetToIterBefore() throws Exception {
		setPreds(order, false, 2, 1);
		setPreds(order, 3, 2);

		assertNext(order, 2);

		order.setToIter(1);

		assertOrderedIteration(order, 1, 2, 3);
	}

	@Test
	public void testIterSetToIterAtIter() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);

		assertNext(order, 1);
		assertNext(order, 2);

		order.setToIter(2); // should do nothing as 2 is already marked

		assertOrderedIteration(order, 3);
	}

	@Test
	public void testIterSetToIterAfter() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, false, 3, 2);

		assertNext(order, 1);
		assertNext(order, 2);

		order.setToIter(3);

		assertOrderedIteration(order, 3);
	}

	@Test
	public void testIterSetToIterCreateNode() throws Exception {
		setPreds(order, 2, 1);

		assertNext(order, 1);

		order.setToIter(3); // new element created => restart iteration

		List<Integer> iterated = new ArrayList<Integer>();
		while (order.hasNext()) {
			iterated.add(order.next());
		}
		assertTrue("Created node should have been iterated", iterated
				.contains(3));
	}

	@Test
	public void testIterSTISucc() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, false, 3, 2);
		setPreds(order, false, 4, 2);

		assertNext(order, 1);
		assertNext(order, 2);

		order.setToIterSuccessors();

		assertAllIteratedOnceToEnd(order, 3, 4);
	}

	@Test
	public void testIterEnd() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);

		assertNext(order, 1);
		assertNext(order, 2);

		order.end();

		assertNoNext(order);
	}

	@Test
	public void testIterBreakCycle() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, 3, 2);
		setPreds(order, 4, 3);
		setPreds(order, 1, 4);

		final int first = order.next();
		final int second = succModulo(first, 4);

		assertNext(order, second);

		// second is no more the successor of first => breaks the cycle
		setPreds(order, second); // no predecessors

		// so first becomes the last to iterate and second becomes the first
		final int new1 = second;
		final int new2 = succModulo(new1, 4);
		final int new3 = succModulo(new2, 4);
		final int new4 = first;

		assertOrderedIteration(order, new1, new2, new3, new4);
	}

	@Test
	public void testIterSeveralModifs() throws Exception {
		setPreds(order, 2, 1);
		setPreds(order, false, 3, 2);
		setPreds(order, false, 4, 3);

		assertNext(order, 1);
		assertNext(order, 2);

		order.setToIterSuccessors();
		order.remove();
		setPreds(order, 5, 4);
		setPreds(order, 3, 5, 4); // making a cycle
		order.setToIter(4);
		setPreds(order, 4, 1);
		order.setToIter(5);
		order.setToIter(6); // creating an independent node
		setPreds(order, 6, 5); // thus 6 is after 1

		// 2 was removed
		// 1 remains first but was already iterated
		assertAllIteratedOnceToEnd(order, 3, 4, 5, 6);
	}
}
