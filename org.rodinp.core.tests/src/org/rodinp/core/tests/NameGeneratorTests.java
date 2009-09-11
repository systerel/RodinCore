/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.rodinp.internal.core.NameGenerator.isValid;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.rodinp.internal.core.NameGenerator;

/**
 * Unit tests for the {@link NameGenerator} class.
 * 
 * @author Laurent Voisin
 */
public class NameGeneratorTests extends TestCase {
	
	private static class VerifyingGenerator extends NameGenerator {

		private final Set<String> usedNames = new HashSet<String>();
		
		@Override
		public void addUsedName(String s) {
			usedNames.add(s);
			super.addUsedName(s);
		}

		@Override
		public String advance() {
			final String result = super.advance();
			assertTrue(isValid(result));
			assertFalse(usedNames.contains(result));
			usedNames.add(result);
			return result;
		}
		
	}

	private VerifyingGenerator g;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		g = new VerifyingGenerator();
	}

	/**
	 * Ensures that the {@link NameGenerator#isValid(String) isValid} method
	 * works appropriately.
	 */
	public void testIsValid() {
		assertTrue(isValid(""));

		assertFalse(isValid("&")); // below lower bound
		assertTrue(isValid("'"));
		assertTrue(isValid(";"));
		assertFalse(isValid("<")); // invalid inside range
		assertTrue(isValid("="));
		assertTrue(isValid("~"));
		assertFalse(isValid("\u007f")); // above upper bound

		assertFalse(isValid("abc<def"));
	}

	/**
	 * Ensures that the first values generated are "'", "(" and ")". 
	 */
	public void testInitial() {
		assertNextName("'");
		assertNextName("(");
		assertNextName(")");
	}

	/**
	 * Ensures that used names are taken into account. 
	 */
	public void testUsedNames() {
		g.addUsedName("foo");
		assertNextName("fop");
		
		g.addUsedName("bar");
		assertNextName("foq");
		
		g.addUsedName("foq2");
		assertNextName("foq3");
		
		g.addUsedName("zzz\"");	// ignored
		assertNextName("foq4");
	}

	/**
	 * Ensures that carries are propagated when increasing. 
	 */
	public void testPropagation_1() {
		g.addUsedName("~");
		assertNextName("''");
	}

	/**
	 * Ensures that carries are propagated when increasing. 
	 */
	public void testPropagation_2() {
		g.addUsedName("a~");
		assertNextName("b'");
	}

	/**
	 * Ensures that carries are propagated when increasing. 
	 */
	public void testPropagation_3() {
		g.addUsedName("a~~~");
		assertNextName("b'''");
	}

	/**
	 * Ensures that carries are propagated when increasing. 
	 */
	public void testPropagation_4() {
		g.addUsedName("foo~");
		assertNextName("fop'");
	}

	/**
	 * Ensures that carries are propagated when increasing. 
	 */
	public void testBug2856893() {
		g.addUsedName("~");
		g.addUsedName("''");
		assertNextName("'(");
	}

	private void assertNextName(String expected) {
		assertEquals(expected, g.advance());
	}
	
}
