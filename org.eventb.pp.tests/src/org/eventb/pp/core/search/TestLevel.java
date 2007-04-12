package org.eventb.pp.core.search;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.internal.pp.core.Level;


public class TestLevel extends TestCase {

	private static Level BASE = Level.base;
	private static Level ONE = new Level(BigInteger.ONE);
	private static Level TWO = new Level(BigInteger.valueOf(2));
	private static Level THREE = new Level(BigInteger.valueOf(3));
	private static Level FOUR = new Level(BigInteger.valueOf(4));
	private static Level FIVE = new Level(BigInteger.valueOf(5));
	private static Level SIX = new Level(BigInteger.valueOf(6));
	private static Level SEVEN = new Level(BigInteger.valueOf(7));
	
	
	public void testEquals() {
		assertTrue(BASE.equals(Level.base));
		assertTrue(ONE.equals(ONE));
		assertTrue(TWO.equals(TWO));
		assertFalse(ONE.equals(TWO));
	}
	
//	public void testSibling() {
//		assertTrue(ONE.getSibling().equals(TWO));
//		assertTrue(THREE.getSibling().equals(FOUR));
//	}
	
	public void testLeftBranch() {
		assertTrue(ONE.getLeftBranch().equals(THREE));
		assertTrue(TWO.getLeftBranch().equals(FIVE));
		assertTrue(BASE.getLeftBranch().equals(ONE));
	}
	
	public void testRightBranch() {
		assertTrue(ONE.getRightBranch().equals(FOUR));
		assertTrue(TWO.getRightBranch().equals(SIX));
		assertTrue(BASE.getRightBranch().equals(TWO));
	}
	
	public void testParent() {
		assertTrue(ONE.getParent().equals(BASE));
		assertTrue(TWO.getParent().equals(BASE));
		assertTrue(THREE.getParent().equals(ONE));
		assertTrue(FOUR.getParent().equals(ONE));
		assertTrue(FIVE.getParent().equals(TWO));
		assertTrue(SIX.getParent().equals(TWO));
	}
	
	public void testIsLeftBranch() {
		assertTrue(ONE.isLeftBranch());
		assertTrue(THREE.isLeftBranch());
		assertTrue(FIVE.isLeftBranch());
		assertFalse(TWO.isLeftBranch());
		assertFalse(FOUR.isLeftBranch());
		assertFalse(SIX.isLeftBranch());
	}
	
	public void testIsRightBranch() {
		assertFalse(ONE.isRightBranch());
		assertFalse(THREE.isRightBranch());
		assertFalse(FIVE.isRightBranch());
		assertTrue(TWO.isRightBranch());
		assertTrue(FOUR.isRightBranch());
		assertTrue(SIX.isRightBranch());
	}
	
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
}
