package org.eventb.pp.core.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.junit.Test;


public class TestLevel extends AbstractPPTest {

	
    @Test
	public void testEquals() {
		assertTrue(BASE.equals(Level.BASE));
		assertTrue(ONE.equals(ONE));
		assertTrue(TWO.equals(TWO));
		assertFalse(ONE.equals(TWO));
	}
	
//	public void testSibling() {
//		assertTrue(ONE.getSibling().equals(TWO));
//		assertTrue(THREE.getSibling().equals(FOUR));
//	}
	
    @Test
	public void testLeftBranch() {
		assertTrue(ONE.getLeftBranch().equals(THREE));
		assertTrue(TWO.getLeftBranch().equals(FIVE));
		assertTrue(BASE.getLeftBranch().equals(ONE));
	}
	
    @Test
	public void testRightBranch() {
		assertTrue(ONE.getRightBranch().equals(FOUR));
		assertTrue(TWO.getRightBranch().equals(SIX));
		assertTrue(BASE.getRightBranch().equals(TWO));
	}
	
    @Test
	public void testParent() {
		assertTrue(BASE.getParent().equals(BASE));
		assertTrue(ONE.getParent().equals(BASE));
		assertTrue(TWO.getParent().equals(BASE));
		assertTrue(THREE.getParent().equals(ONE));
		assertTrue(FOUR.getParent().equals(ONE));
		assertTrue(FIVE.getParent().equals(TWO));
		assertTrue(SIX.getParent().equals(TWO));
	}
	
    @Test
	public void testIsLeftBranch() {
		assertFalse(BASE.isLeftBranch());
		assertTrue(ONE.isLeftBranch());
		assertTrue(THREE.isLeftBranch());
		assertTrue(FIVE.isLeftBranch());
		assertFalse(TWO.isLeftBranch());
		assertFalse(FOUR.isLeftBranch());
		assertFalse(SIX.isLeftBranch());
	}
	
    @Test
	public void testIsRightBranch() {
		assertFalse(BASE.isRightBranch());
		assertFalse(ONE.isRightBranch());
		assertFalse(THREE.isRightBranch());
		assertFalse(FIVE.isRightBranch());
		assertTrue(TWO.isRightBranch());
		assertTrue(FOUR.isRightBranch());
		assertTrue(SIX.isRightBranch());
	}
	
    @Test
	public void testHeight() {
		assertTrue(BASE.getHeight() == 0);
		assertTrue(ONE.getHeight() == 1);
		assertTrue(TWO.getHeight() == 1);
		assertTrue(THREE.getHeight() == 2);
		assertTrue(FOUR.getHeight() == 2);
		assertTrue(FIVE.getHeight() == 2);
		assertTrue(SIX.getHeight() == 2);
		assertTrue(SEVEN.getHeight() == 3);
		
		assertTrue(SEVEN.getLeftBranch().getHeight() == 4);
		assertTrue(SEVEN.getRightBranch().getHeight() == 4);
	}
	
    @Test
	public void testComparable() {
		assertTrue(BASE.compareTo(ONE) <= 1);
		assertTrue(BASE.compareTo(BASE) == 0);
		assertTrue(ONE.compareTo(BASE) >= 1);
		assertTrue(ONE.compareTo(ONE) == 0);
		assertTrue(ONE.compareTo(TWO) <= 1);
		assertTrue(TWO.compareTo(ONE) >= 1);
		
		assertEquals(Level.getHighest(ONE, BASE), ONE);
	}
	
	
    @Test
	public void testAncestorInSameTree() {
		assertTrue(BASE.isAncestorInSameTree(ONE));
		assertTrue(BASE.isAncestorInSameTree(TWO));
		assertTrue(BASE.isAncestorInSameTree(THREE));
		assertTrue(BASE.isAncestorInSameTree(FOUR));
		
		assertFalse(BASE.isAncestorInSameTree(BASE));
		assertFalse(ONE.isAncestorInSameTree(ONE));
		assertFalse(ONE.isAncestorInSameTree(BASE));
		assertFalse(TWO.isAncestorInSameTree(BASE));
		assertFalse(THREE.isAncestorInSameTree(BASE));
		
		
		assertTrue(ONE.isAncestorInSameTree(THREE));
		assertFalse(ONE.isAncestorInSameTree(FIVE));
		assertTrue(ONE.isAncestorInSameTree(FOUR));
		assertFalse(ONE.isAncestorInSameTree(SIX));
		
		assertFalse(ONE.isAncestorInSameTree(BASE));
		assertFalse(FOUR.isAncestorInSameTree(ONE));
	}
}
