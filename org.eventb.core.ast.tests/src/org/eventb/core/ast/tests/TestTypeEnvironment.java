/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - port to JUnit 4
 *     Systerel - test factory equality
 *     Systerel - test translation with factory
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.ff_extns;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mInferredTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.junit.Test;

public class TestTypeEnvironment {

	static FormulaFactory ff = FormulaFactory.getDefault();
	
	private static Type t_S = ff.makeGivenType("S"); 
	private static Type t_T = ff.makeGivenType("T"); 
	private static Type INT = ff.makeIntegerType();
	private static Type BOOL = ff.makeBooleanType();
	private static Type eBOOL = ff_extns.makeBooleanType();
	
	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addAll(ITypeEnvironment)'
	 */
	@Test
	public void testAddAllTypeEnv() {
		ITypeEnvironmentBuilder te1 = ff.makeTypeEnvironment();
		ITypeEnvironmentBuilder te2 = ff.makeTypeEnvironment();
		ITypeEnvironmentBuilder empty = ff.makeTypeEnvironment();
		
		te2.addAll(empty);
		assertEquals(te1, te2);
		
		te1.addGivenSet("S");
		te2.addAll(te1);
		assertEquals(te1, te2);
		
		ITypeEnvironmentBuilder te3 = ff.makeTypeEnvironment();
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
	 * Test method for
	 * 'org.eventb.core.ast.ITypeEnvironment.addAll(ITypeEnvironment)' with a
	 * given type environment using a different factory
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddAllTypeEnvDifferentFactory() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		ITypeEnvironmentBuilder te_extns = ff_extns.makeTypeEnvironment();
		te.addAll(te_extns);
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addAll(FreeIdentifier[])'
	 */
	@Test
	public void testAddAllFreeIdent() {
		ITypeEnvironmentBuilder te1 = ff.makeTypeEnvironment();
		ITypeEnvironmentBuilder te2 = ff.makeTypeEnvironment();
		ITypeEnvironmentBuilder empty_te = ff.makeTypeEnvironment();
		
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
	 * Test method for
	 * 'org.eventb.core.ast.ITypeEnvironment.addAll(FreeIdentifier[])' with free
	 * identifiers using a different factory
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddAllFreeIdentDifferentFactory() {
		ITypeEnvironmentBuilder te_extns = ff_extns.makeTypeEnvironment();

		FreeIdentifier x_INT = ff.makeFreeIdentifier("x", null, INT);
		FreeIdentifier y_S = ff.makeFreeIdentifier("y", null, t_S);

		te_extns.addAll(new FreeIdentifier[] { x_INT, y_S });
	}

	/*
	 * Test method for
	 * 'org.eventb.core.ast.ITypeEnvironment.add(FreeIdentifier)' with free
	 * identifier using a different factory
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddFreeIdentDifferentFactory() {
		ITypeEnvironmentBuilder te_extns = ff_extns.makeTypeEnvironment();

		FreeIdentifier y_S = ff.makeFreeIdentifier("y", null, t_S);

		te_extns.add(y_S);
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addGivenSet(String)'
	 */
	@Test
	public void testAddGivenSet() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		assertEquals("{S=ℙ(S)}", te.toString());

		te.addGivenSet("S");
		assertEquals("{S=ℙ(S)}", te.toString());
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String, Type)'
	 */
	@Test
	public void testAddName() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("x", INT);
		assertEquals("{x=ℤ}", te.toString());

		te.addName("x", INT);
		assertEquals("{x=ℤ}", te.toString());
	}
	
	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String, Type)'
	 * with a null pointer name parameter
	 */
	@Test(expected = NullPointerException.class)
	public void testAddNameNullPointerName() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName(null,  INT);
	}
	
	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String, Type)'
	 * with a null pointer name parameter
	 */
	@Test(expected = NullPointerException.class)
	public void testAddNameNullPointerType() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("x", null);
	}
	
	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String, Type)'
	 * using same name for different types
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddNameWithTwoTypes() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("x", INT);
		te.addName("x", BOOL);
	}
	
	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String,
	 * Type)' using same name for different types (using an undefined given set
	 * name)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddNameWithIncoherentTypes() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("S", BOOL);
		te.addName("x", POW(t_S));
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String,
	 * Type)' using an invalid identifier
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddNameWithInvalidIdentifier() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("id", BOOL);
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.addName(String,
	 * Type)' using a different type factory
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddNameWithDifferentFactory() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("s", eBOOL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddSelfReferringType() throws Exception {
		final ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		final Type powST = POW(ff.makeProductType(t_S, t_T));
		te.addName("S", powST);
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.clone()'
	 */
	@Test
	public void testClone() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		assertEquals(te, te.makeBuilder());
		assertEquals(te, te.makeSnapshot().makeBuilder());
		
		te.addGivenSet("S");
		assertEquals(te, te.makeBuilder());
		assertEquals(te, te.makeSnapshot().makeBuilder());
		
		te.addName("x", INT);
		assertEquals(te, te.makeBuilder());
		assertEquals(te, te.makeSnapshot().makeBuilder());
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.contains(String)'
	 * and 'org.eventb.core.ast.ITypeEnvironment.contains(FreeIdentifier)'
	 */
	@Test
	public void testContains() {
		final ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		final FreeIdentifier S = ff.makeFreeIdentifier("S", null, POW(t_S));
		final FreeIdentifier x_INT = ff.makeFreeIdentifier("x", null, INT);
		final FreeIdentifier x_S = ff.makeFreeIdentifier("x", null, t_S);

		assertFalse(te.contains("x"));
		assertFalse(te.contains("x'"));
		assertFalse(te.contains("S"));
		assertFalse(te.contains(S));
		assertFalse(te.contains(x_INT));
		assertFalse(te.contains(x_S));
		
		te.addGivenSet("S");
		assertFalse(te.contains("x"));
		assertFalse(te.contains("x'"));
		assertTrue(te.contains("S"));
		assertTrue(te.contains(S));
		assertFalse(te.contains(x_INT));
		assertFalse(te.contains(x_S));
		
		te.addName("x", INT);
		assertTrue(te.contains("x"));
		assertFalse(te.contains("x'"));
		assertTrue(te.contains("S"));
		assertTrue(te.contains(S));
		assertTrue(te.contains(x_INT));
		assertFalse(te.contains(x_S));
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.containsAll(ITypeEnvironment)'
	 */
	@Test
	public void testContainsAll() {
		ITypeEnvironmentBuilder te1 = ff.makeTypeEnvironment();
		ITypeEnvironmentBuilder empty = ff.makeTypeEnvironment();
		
		assertTrue(empty.containsAll(te1));
		assertTrue(te1.containsAll(empty));
		
		ITypeEnvironmentBuilder te2 = ff.makeTypeEnvironment();
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
	@Test
	public void testEquals() {
		ITypeEnvironmentBuilder te1 = ff.makeTypeEnvironment();
		assertFalse(te1.equals(null));
		assertFalse(te1.equals("dummy string"));
		
		ITypeEnvironmentBuilder te2 = ff.makeTypeEnvironment();
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
	@Test
	public void testGetIterator() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();

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
		assertEquals(mFreeIdentifier("S", POW(t_S)), iter.asFreeIdentifier());
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
			fail("getName() should have raised an exception");
		} catch (NoSuchElementException e) {
			// Test passed.
		}
		try {
			iter.getType();
			fail("getType() should have raised an exception");
		} catch (NoSuchElementException e) {
			// Test passed.
		}
		try {
			iter.asFreeIdentifier();
			fail("asFreeIdentifier() should have raised an exception");
		} catch (NoSuchElementException e) {
			// Test passed.
		}
	}

	/*
	 * Test method for 'org.eventb.core.ast.ITypeEnvironment.getNames()'
	 */
	@Test
	public void testGetNames() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
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
	@Test
	public void testGetType() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
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
	@Test
	public void testIsEmpty() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		assertTrue("Initial type environment should be empty", te.isEmpty());
		
		te.addGivenSet("S");
		assertFalse("Environment with one given set is not empty", te.isEmpty());
		
		te = ff.makeTypeEnvironment();
		te.addName("x", ff.makeIntegerType());
		assertFalse("Environment with one variable is not empty", te.isEmpty());
	}

	
	@Test
	public void testIsGivenSet() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
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

	/**
	 * Ensures that translation works as expected in a nominal case.
	 */
	@Test
	public void testTranslation() {
		final ITypeEnvironmentBuilder te = mTypeEnvironment("S=ℙ(S); x=ℤ", ff);
		assertTranslation(te, ff);
		assertTranslation(te, ff_extns);
		final ISealedTypeEnvironment snapshot = te.makeSnapshot();
		assertTranslation(snapshot, ff);
		assertTranslation(snapshot, ff_extns);
	}

	// Checks isTranslatable() and translate() post-conditions in nominal case.
	private void assertTranslation(ITypeEnvironment te, FormulaFactory target) {
		assertTrue(te.isTranslatable(target));
		if (target == te.getFormulaFactory()) {
			assertSame(te, te.translate(target));
		} else {
			final ITypeEnvironment actual = te.translate(target);
			assertEquals(te, actual);
			assertSame(target, actual.getFormulaFactory());
			assertSame(te.getClass(), actual.getClass());
		}
	}

	/**
	 * Ensures that translation fails as expected in erroneous cases.
	 */
	@Test
	public void testNotTranslation() {
		final ITypeEnvironment reservedName = mTypeEnvironment("prime=ℤ", ff);
		final ITypeEnvironment untranslatableType = mTypeEnvironment(
				"x=List(BOOL)", ff_extns);
		assertNotTranslation(reservedName, ff_extns);
		assertNotTranslation(untranslatableType, ff);
		assertNotTranslation(reservedName.makeSnapshot(), ff_extns);
		assertNotTranslation(untranslatableType.makeSnapshot(), ff);
	}

	// Checks isTranslatable() and translate() post-conditions in erroneous case.
	private void assertNotTranslation(ITypeEnvironment te, FormulaFactory target) {
		assertFalse(te.isTranslatable(target));
		try {
			te.translate(target);
			fail("Translation should have failed");
		} catch (IllegalArgumentException exc) {
			// pass
		}
	}

	/**
	 * Ensures that translation fails as expected for inferred type
	 * environments.
	 */
	@Test
	public void testInferredTypeEnvironmentTranslation() {
		final ITypeEnvironmentBuilder te = mTypeEnvironment();
		final IInferredTypeEnvironment inferred = mInferredTypeEnvironment(te);
		assertFalse(inferred.isTranslatable(ff));
		try {
			inferred.translate(ff);
			fail("Translation should have failed");
		} catch (UnsupportedOperationException exc) {
			// pass
		}
	}

}
