/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.core.ast.FreshNameSolver;
import org.junit.Test;

/**
 * Unit tests for class {@link FreshNameSolver}.
 * 
 * @author Laurent Voisin
 */
public class TestFreshNameSolver extends AbstractTests {

	/**
	 * Ensures that solve returns a fresh name, even in case of conflict.
	 */
	@Test
	public void testSolve() {
		assertFreshName("foo", "foo", ff);
		assertFreshName("foo", "foo0", ff, "foo");
		assertFreshName("foo", "foo1", ff, "foo", "foo0");
		assertFreshName("foo", "foo", ff, "foo0");
	}

	/**
	 * Ensures that solve returns a fresh name, even in case of conflict with
	 * other names or reserved names (<code>prj1</code> and <code>prj2</code>)
	 */
	@Test
	public void testSolvePrj() {
		assertFreshName("prj", "prj", ff);
		assertFreshName("prj", "prj0", ff, "prj");
		assertFreshName("prj", "prj3", ff, "prj", "prj0");
	}

	/**
	 * Ensures that solve returns a fresh name, even in case of conflict with
	 * other names or reserved names contributed by a datatype.
	 */
	@Test
	public void testSolveDatatype() {
		assertFreshName("List", "List0", LIST_FAC);
		assertFreshName("nil", "nil0", LIST_FAC);
		assertFreshName("cons", "cons0", LIST_FAC);
		assertFreshName("head", "head0", LIST_FAC);
		assertFreshName("tail", "tail0", LIST_FAC);
	}

	/**
	 * Ensures that the computed fresh name is not returned twice when added.
	 */
	@Test
	public void testSolveAndAdd() {
		final Set<String> usedSet = new HashSet<String>();
		final FreshNameSolver solver = new FreshNameSolver(usedSet, ff);
		final String actual = solver.solveAndAdd("foo");
		assertEquals("foo", actual);
		final String actual2 = solver.solveAndAdd("foo");
		assertEquals("foo0", actual2);
	}

	private void assertFreshName(String base, String expected,
			FormulaFactory factory, String... usedNames) {
		final Set<String> usedSet = new HashSet<String>(asList(usedNames));
		final FreshNameSolver solver = new FreshNameSolver(usedSet, factory);
		final String actual = solver.solve(base);
		assertEquals(expected, actual);
		assertFalse(usedSet.contains(actual));
	}

}
