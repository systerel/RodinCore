package org.eventb.core.ast.tests;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

public class TestTypeEnvironment extends TestCase {

	static FormulaFactory ff = FormulaFactory.getDefault();
	
	private static Type t_S = ff.makeGivenType("S"); 
	private static Type t_T = ff.makeGivenType("T"); 
	private static Type INT = ff.makeIntegerType();
	private static Type BOOL = ff.makeBooleanType();
	
	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addAll(ITypeEnvironment)'
	 */
	public void testAddAllTypeEnv() {
		ITypeEnvironment te1 = ff.makeTypeEnvironment();
		ITypeEnvironment te2 = ff.makeTypeEnvironment();
		ITypeEnvironment empty = ff.makeTypeEnvironment();
		
		te2.addAll(empty);
		assertEquals(te1, te2);
		
		te1.addGivenSet("S");
		te2.addAll(te1);
		assertEquals(te1, te2);
		
		ITypeEnvironment te3 = ff.makeTypeEnvironment();
		te3.addName("x", INT);
		te2.addAll(te3);
		te1.addName("x", INT);
		assertEquals(te1, te2);
		
		te3 = ff.makeTypeEnvironment();
		te3.addGivenSet("S");
		te3.addName("y", INT);
		te2.addAll(te3);
		te1.addName("y", INT);
		assertEquals(te1, te2);
	}
	
	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addAll(FreeIdentifier[])'
	 */
	public void testAddAllFreeIdent() {
		ITypeEnvironment te1 = ff.makeTypeEnvironment();
		ITypeEnvironment te2 = ff.makeTypeEnvironment();
		ITypeEnvironment empty_te = ff.makeTypeEnvironment();
		
		FreeIdentifier x_INT = ff.makeFreeIdentifier("x",null,INT);
		FreeIdentifier y_S = ff.makeFreeIdentifier("y",null,t_S);

		FreeIdentifier[] empty_fi = {};
		
		te1.addAll(empty_fi);
		assertEquals(te1, empty_te);
		
		te2.addName("x",INT);
		te1.addAll(new FreeIdentifier[] {x_INT,y_S});
		te2.addName("y",t_S);
		assertEquals(te1, te2);
		
		// Adding the same again has no effect
		te1.addAll(new FreeIdentifier[] {x_INT,y_S});
		assertEquals(te1, te2);
		
	}
	
	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addGivenSet(String)'
	 */
	public void testAddGivenSet() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		assertEquals("{S=ℙ(S)}", te.toString());

		te.addGivenSet("S");
		assertEquals("{S=ℙ(S)}", te.toString());
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String, Type)'
	 */
	public void testAddName() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		te.addName("x", INT);
		assertEquals("{x=ℤ}", te.toString());

		te.addName("x", INT);
		assertEquals("{x=ℤ}", te.toString());
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.clone()'
	 */
	public void testClone() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		assertEquals(te, te.clone());
		
		te.addGivenSet("S");
		assertEquals(te, te.clone());
		
		te.addName("x", INT);
		assertEquals(te, te.clone());
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.contains(String)'
	 */
	public void testContains() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		assertFalse(te.contains("x"));
		assertFalse(te.contains("x'"));
		assertFalse(te.contains("S"));
		
		te.addGivenSet("S");
		assertFalse(te.contains("x"));
		assertFalse(te.contains("x'"));
		assertTrue(te.contains("S"));
		
		te.addName("x", INT);
		assertTrue(te.contains("x"));
		assertFalse(te.contains("x'"));
		assertTrue(te.contains("S"));
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.containsAll(ITypeEnvironment)'
	 */
	public void testContainsAll() {
		ITypeEnvironment te1 = ff.makeTypeEnvironment();
		ITypeEnvironment empty = ff.makeTypeEnvironment();
		
		assertTrue(empty.containsAll(te1));
		assertTrue(te1.containsAll(empty));
		
		ITypeEnvironment te2 = ff.makeTypeEnvironment();
		te1.addGivenSet("S");
		te2.addGivenSet("S");
		assertFalse(empty.containsAll(te1));
		assertTrue(te1.containsAll(empty));
		assertTrue(te1.containsAll(te2));
		assertTrue(te2.containsAll(te1));
		
		te1.addName("x", INT);
		te1.addName("y", INT);
		te2.addName("y", INT);
		assertFalse(empty.containsAll(te1));
		assertTrue(te1.containsAll(empty));
		assertTrue(te1.containsAll(te2));
		assertFalse(te2.containsAll(te1));

		te2.addName("x", BOOL);
		assertFalse(empty.containsAll(te1));
		assertTrue(te1.containsAll(empty));
		assertFalse(te1.containsAll(te2));
		assertFalse(te2.containsAll(te1));
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.equals(Object)'
	 */
	public void testEquals() {
		ITypeEnvironment te1 = ff.makeTypeEnvironment();
		assertFalse(te1.equals(null));
		assertFalse(te1.equals("dummy string"));
		
		ITypeEnvironment te2 = ff.makeTypeEnvironment();
		assertTrue(te1.equals(te2));
		
		te1.addGivenSet("S");
		assertFalse(te1.equals(te2));
		
		te2.addGivenSet("S");
		assertTrue(te1.equals(te2));
		
		te1.addName("x", INT);
		assertFalse(te1.equals(te2));
		
		te2.addName("x", INT);
		assertTrue(te1.equals(te2));
	}
	
	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.getIterator()'
	 */
	public void testGetIterator() {
		ITypeEnvironment te = ff.makeTypeEnvironment();

		ITypeEnvironment.IIterator iter = te.getIterator();
		assertNoCurrentElement(iter);
		assertExhausted(iter);
		
		te.addGivenSet("S");
		iter = te.getIterator();
		assertNoCurrentElement(iter);
		assertTrue(iter.hasNext());
		iter.advance();
		assertEquals("S", iter.getName());
		assertEquals(POW(t_S), iter.getType());
		assertExhausted(iter);
		
		te.addName("x", INT);
		iter = te.getIterator();
		assertNoCurrentElement(iter);
		assertTrue(iter.hasNext());
		iter.advance();
		assertTrue(iter.hasNext());
		iter.advance();
		assertExhausted(iter);
	}

	private void assertExhausted(ITypeEnvironment.IIterator iter) {
		assertFalse(iter.hasNext());
		try {
			iter.advance();
			assertTrue("advance() should have raised an exception", false);
		} catch (NoSuchElementException e) {
			// Test passed.
		}
	}

	private void assertNoCurrentElement(ITypeEnvironment.IIterator iter) {
		try {
			iter.getName();
			assertTrue("getName() should have raised an exception", false);
		} catch (NoSuchElementException e) {
			// Test passed.
		}
		try {
			iter.getType();
			assertTrue("getType() should have raised an exception", false);
		} catch (NoSuchElementException e) {
			// Test passed.
		}
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.getNames()'
	 */
	public void testGetNames() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		Set<String> expected = new HashSet<String>();
		assertEquals(expected, te.getNames());
		
		te.addGivenSet("S");
		expected.add("S");
		assertEquals(expected, te.getNames());
		
		te.addName("x", INT);
		expected.add("x");
		assertEquals(expected, te.getNames());
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.getType(String)'
	 */
	public void testGetType() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		assertNull(te.getType("x"));
		assertNull(te.getType("x'"));
		assertNull(te.getType("S"));
		
		te.addGivenSet("S");
		assertNull(te.getType("x"));
		assertNull(te.getType("x'"));
		assertEquals(POW(t_S), te.getType("S"));
		
		te.addName("x", INT);
		assertEquals(INT, te.getType("x"));
		assertNull(te.getType("x'"));
		assertEquals(POW(t_S), te.getType("S"));
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.isEmpty()'
	 */
	public void testIsEmpty() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		assertTrue("Initial type environment should be empty", te.isEmpty());
		
		te.addGivenSet("S");
		assertFalse("Environment with one given set is not empty", te.isEmpty());
		
		te = ff.makeTypeEnvironment();
		te.addName("x", ff.makeIntegerType());
		assertFalse("Environment with one variable is not empty", te.isEmpty());
	}

	
	public void testIsGivenSet() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		te.addName("T", POW(t_T));
		te.addName("x", INT);
		te.addName("y", POW(t_S));
		te.addName("z", POW(INT));
		
		ITypeEnvironment.IIterator iter = te.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String name = iter.getName();
			final Type givenSetType = POW(ff.makeGivenType(name));
			final Type type = iter.getType();
			assertEquals(givenSetType.equals(type), iter.isGivenSet());
		}
	}

}
