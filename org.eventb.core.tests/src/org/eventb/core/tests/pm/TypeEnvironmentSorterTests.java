/**
 * 
 */
package org.eventb.core.tests.pm;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter, "S");
		assertVars(sorter);
	}
	
	@Test
	public void testSeveralSets() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		te.addGivenSet("A");
		te.addGivenSet("T");
		te.addGivenSet("B");
		te.addGivenSet("U");
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter, "A", "B", "S", "T", "U");
		assertVars(sorter);
	}
	
	@Test
	public void testOneVar() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("x", INT);
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertSets(sorter);
		assertVars(sorter, "x", INT);
	}
	
	@Test
	public void testSeveralVars() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addName("x", INT);
		te.addName("a", POW(INT));
		te.addName("y", BOOL);
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(te);
		assertVars(sorter, //
				"a", POW(INT),//
				"x", INT,//
				"y", BOOL //
		);
	}
	
	@Test
	public void testMixed() {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		te.addName("x", INT);
		te.addGivenSet("T");
		te.addName("a", POW(INT));
		te.addName("b", t_S);
		te.addName("z", POW(t_S));
		te.addGivenSet("U");
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
