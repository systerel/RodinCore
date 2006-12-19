package org.eventb.core.ast.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.SourceLocation;

public class TestSourceLocation extends TestCase {

	public final void testContains() {
		SourceLocation s11 = new SourceLocation(1, 1);
		SourceLocation s12 = new SourceLocation(1, 2);
		SourceLocation s13 = new SourceLocation(1, 3);
		SourceLocation s22 = new SourceLocation(2, 2);
		SourceLocation s23 = new SourceLocation(2, 3);
		
		assertTrue(s11.contains(s11));
		assertFalse(s11.contains(s12));
		assertFalse(s11.contains(s13));
		assertFalse(s11.contains(s22));
		assertFalse(s11.contains(s23));
		
		assertTrue(s12.contains(s11));
		assertTrue(s12.contains(s12));
		assertFalse(s12.contains(s13));
		assertTrue(s12.contains(s22));
		assertFalse(s12.contains(s23));
		
		assertTrue(s13.contains(s11));
		assertTrue(s13.contains(s12));
		assertTrue(s13.contains(s13));
		assertTrue(s13.contains(s22));
		assertTrue(s13.contains(s23));
		
		assertFalse(s22.contains(s11));
		assertFalse(s22.contains(s12));
		assertFalse(s22.contains(s13));
	}

	public final void testEqualsObject() {
		SourceLocation s11 = new SourceLocation(1, 1);
		SourceLocation s11b = new SourceLocation(1, 1);
		SourceLocation s12 = new SourceLocation(1, 2);
		SourceLocation s01 = new SourceLocation(0, 1);
		
		assertFalse(s11.equals(null));
		assertTrue(s11.equals(s11));
		assertTrue(s11.equals(s11b));
		assertFalse(s11.equals(s12));
		assertFalse(s11.equals(s01));
	}

}
