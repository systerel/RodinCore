/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added child indexes
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.eventb.core.ast.FormulaFactory.makePosition;

import org.eventb.core.ast.IPosition;
import org.junit.Test;

/**
 * Tests for interface {@link IPosition} and its standard implementation.
 * 
 * @author Laurent Voisin
 */
public class TestPosition{

	private void assertSameSign(int expected, int actual) {
		if (expected == 0 && actual != 0)
			fail("Expected zero, was " + actual);
		else if (expected < 0 && actual >= 0)
			fail("Expected negative, was " + actual);
		else if (expected > 0 && actual <= 0)
			fail("Expected positive, was " + actual);
	}
	
	/*
	 * Ensures that the total order on position is implemented correctly, and is
	 * compatible with equality.
	 */
	private void assertComparison(int expected, IPosition left, IPosition right) {
		assertSameSign(expected, left.compareTo(right));
		assertSameSign(- expected, right.compareTo(left));
		if (expected == 0) {
			assertEquals(left, right);
			assertEquals(right, left);
		}
	}
	
	private IPosition mPos(String image) {
		final IPosition pos = makePosition(image);
		assertEquals(image, pos.toString());
		return pos;
	}
	
	@Test 
	public final void testCompareTo() {
		assertComparison(0, mPos(""),      mPos(""));
		assertComparison(0, mPos("1"),     mPos("1"));
		assertComparison(0, mPos("1.2.3"), mPos("1.2.3"));
		assertComparison(0, mPos("3.1.2"), mPos("3.1.2"));
		
		assertComparison(-1, mPos(""),      mPos("0"));
		assertComparison(-1, mPos(""),      mPos("1.2"));
		assertComparison(-1, mPos("0"),     mPos("0.0"));
		assertComparison(-1, mPos("0"),     mPos("0.1"));
		assertComparison(-1, mPos("0"),     mPos("1"));
		assertComparison(-1, mPos("1.1"),   mPos("1.2"));
		assertComparison(-1, mPos("1.2"),   mPos("1.2.0"));
		assertComparison(-1, mPos("1.2"),   mPos("1.2.1"));
		assertComparison(-1, mPos("1.2"),   mPos("1.2.3.4"));
	}

	private void assertFirstChild(String image) {
		final IPosition pos = mPos(image);
		final IPosition expect = mPos(image.length() == 0 ? "0" : image + ".0");
		final IPosition actual = pos.getFirstChild();
		assertEquals(expect, actual);
		
		// Compatibility with other methods
		assertEquals(pos, actual.getParent());
		assertFalse(actual.isRoot());
		assertTrue(actual.isFirstChild());
		assertEquals(0, actual.getChildIndex());
		assertEquals(expect, pos.getChildAtIndex(0));
	}
	
	@Test 
	public final void testGetFirstChild() {
		assertFirstChild("");
		assertFirstChild("0");
		assertFirstChild("2");
		assertFirstChild("1.2.3");
	}

	private void assertChildIndex(IPosition pos) {
		assertEquals(pos, pos.getParent().getChildAtIndex(pos.getChildIndex()));
	}

	private void assertNextSibling(String image, String expected) {
		final IPosition pos = mPos(image);
		assertFalse(pos.isRoot());

		final IPosition expect = mPos(expected);
		final IPosition actual = pos.getNextSibling();
		assertEquals(expect, actual);
		
		// Compatibility with other methods
		assertEquals(pos.getParent(), actual.getParent());
		assertEquals(pos, actual.getPreviousSibling());
		assertFalse(actual.isRoot());
		assertFalse(actual.isFirstChild());
		assertChildIndex(pos);
		assertChildIndex(actual);
	}
	
	@Test 
	public final void testGetNextSibling() {
		assertNextSibling("0", "1");
		assertNextSibling("1", "2");
		assertNextSibling("0.0", "0.1");
		assertNextSibling("1.2.3", "1.2.4");
		
		try {
			IPosition.ROOT.getNextSibling();
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

	@Test 
	public final void testGetParent() {
		assertEquals(mPos(""), mPos("0").getParent());
		assertEquals(mPos(""), mPos("1").getParent());
		assertEquals(mPos("0"), mPos("0.0").getParent());
		assertEquals(mPos("0"), mPos("0.2").getParent());
		assertEquals(mPos("1.2"), mPos("1.2.3").getParent());
		
		try {
			IPosition.ROOT.getParent();
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

	private void failPreviousSibling(String image) {
		IPosition pos = mPos(image);
		assertTrue(pos.isRoot() || pos.isFirstChild());

		try {
			pos.getPreviousSibling();
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

	@Test 
	public final void testGetPreviousSibling() {
		// Tests for regular values already done with testGetNextSibling.

		failPreviousSibling("");
		failPreviousSibling("0");
		failPreviousSibling("1.2.0");
	}

	@Test 
	public final void testIsFirstChild() {
		assertFalse(mPos("").isFirstChild());
		assertTrue(mPos("0").isFirstChild());
		assertFalse(mPos("1").isFirstChild());
		assertFalse(mPos("2").isFirstChild());
		assertTrue(mPos("0.0").isFirstChild());
		assertFalse(mPos("0.1").isFirstChild());
		assertFalse(mPos("0.2").isFirstChild());
		assertTrue(mPos("1.2.3.0").isFirstChild());
		assertFalse(mPos("1.2.3.4").isFirstChild());
	}

	@Test 
	public final void testIsRoot() {
		assertTrue(mPos("").isRoot());
		assertFalse(mPos("0").isRoot());
		assertFalse(mPos("2").isRoot());
		assertFalse(mPos("0.0").isRoot());
		assertFalse(mPos("0.2").isRoot());
		assertFalse(mPos("1.2.3.0").isRoot());
		assertFalse(mPos("1.2.3.4").isRoot());
	}
	
	private void assertMementoFailure(final String image) {
		try {
			mPos(image);
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
	
	@Test 
	public final void testMemento() {
		mPos("");
		mPos("1");
		mPos("1000.200.300");
		
		assertMementoFailure(".");
		assertMementoFailure("0.");
		assertMementoFailure(".0");
		assertMementoFailure("1..2");
		assertMementoFailure("-1");
		assertMementoFailure("1.-2");
	}

	@Test 
	public final void testRoot() {
		final IPosition root = IPosition.ROOT;
		assertTrue(root.isRoot());
		assertEquals("", root.toString());
	}

	private void failNthChild(String image, int n) {
		IPosition pos = mPos(image);

		try {
			pos.getChildAtIndex(n);
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

	@Test 
	public final void testNthChild() {
		// Tests for regular values already done with other queries.

		failNthChild("", -1);
		failNthChild("0", -23);
		failNthChild("1.2", -5);
	}

	@Test 
	public final void testChildIndex() {
		// Tests for regular values already done with other queries.

		try {
			IPosition.ROOT.getChildIndex();
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

}
