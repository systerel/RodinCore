/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.pm.TypeEnvironmentSorter;
import org.eventb.internal.core.pm.TypeEnvironmentSorter.Entry;
import org.junit.Test;

/**
 * Unit tests for class {@link TypeEnvironmentSorter}
 * 
 * @author Laurent Voisin
 */
public class TypeEnvironmentSorterTests {

	private static final FormulaFactory ff = FormulaFactory.getDefault();
	
	private static Type t_S = ff.makeGivenType("S"); 
	private static Type INT = ff.makeIntegerType();
	private static Type BOOL = ff.makeBooleanType();

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static void assertSets(TypeEnvironmentSorter sorter, String... expects) {
		final int length = expects.length;
		assertEquals(length, sorter.givenSets.length);
		for (int i = 0; i < length; ++ i) {
			assertEquals(expects[i], sorter.givenSets[i]);
		}
	}
	
	private static void assertVars(TypeEnvironmentSorter sorter, Object... expects) {
		final int length = expects.length;
		assertTrue("Needs an even number of args", (length & 1) == 0);
		assertEquals(expects.length / 2, sorter.variables.length);
		int j = 0;
		for (int i = 0; i < length; i += 2) {
			final String name = (String) expects[i];
			final Type type = (Type) expects[i+1];
			final Entry expected = new Entry(name, type);
			assertEquals(expected, sorter.variables[j++]);
		}
	}
	
	@Test
	public void testEmpty() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter);
		assertVars(sorter);
	}
	
	@Test
	public void testOneSet() {
		ITypeEnvironmentBuilder te = mTypeEnvironment("S=ℙ(S)", ff);
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter, "S");
		assertVars(sorter);
	}
	
	@Test
	public void testSeveralSets() {
		ITypeEnvironmentBuilder te = mTypeEnvironment(
				"S=ℙ(S); A=ℙ(A); T=ℙ(T); B=ℙ(B); U=ℙ(U)", ff);
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter, "A", "B", "S", "T", "U");
		assertVars(sorter);
	}
	
	@Test
	public void testOneVar() {
		ITypeEnvironmentBuilder te = mTypeEnvironment("x=ℤ", ff);
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter);
		assertVars(sorter, "x", INT);
	}
	
	@Test
	public void testSeveralVars() {
		ITypeEnvironmentBuilder te = mTypeEnvironment("x=ℤ; a=ℙ(ℤ); y=BOOL", ff);
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertVars(sorter, //
				"a", POW(INT),//
				"x", INT,//
				"y", BOOL //
		);
	}
	
	@Test
	public void testMixed() {
		ITypeEnvironmentBuilder te = mTypeEnvironment(
				"S=ℙ(S); x=ℤ; T=ℙ(T); a=ℙ(ℤ); b=S; z=ℙ(S); U=ℙ(U)", ff);
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter, "S", "T", "U");
		assertVars(sorter,//
				"a", POW(INT),//
				"b", t_S,//
				"x", INT,//
				"z", POW(t_S) //
		);
	}
	
}
