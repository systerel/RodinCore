package org.eventb.pp.core.provers;

import static org.eventb.pp.Util.mList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.provers.equality.IEquivalenceManager;
import org.eventb.internal.pp.core.provers.equality.INode;
import org.eventb.internal.pp.core.provers.equality.IOrigin;
import org.eventb.internal.pp.core.provers.equality.TMPEquivalenceClassManager;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

public class TMPTestEqualityManager extends AbstractPPTest {

	private static INode a;
	private static INode b;
	private static INode c;
	private static INode d;

	private static IOrigin ab; 
	private static IOrigin bc; 
	private static IOrigin abc; 
	private static IOrigin cd;
	private static IOrigin bcd; 
	private static IOrigin ac; 
	private static IOrigin ad; 
	private static IOrigin abcd; 
	private static IOrigin bd; 
	private static IOrigin adc; 
	private static IOrigin bad; 
	
	private static IOrigin nab; 
	private static IOrigin nbc; 
	private static IOrigin ncd;
	private static IOrigin nac; 
	private static IOrigin nad; 
	private static IOrigin nbd; 
		
	
	private static <T> Set<T> mSet(T... elements) {
		return new HashSet<T>(Arrays.asList(elements));
	}
	
	// deductions
	public void testEmptyManager() {
		IEquivalenceManager manager = new TMPEquivalenceClassManager();

		assertFalse(manager.contradictsInequality(a, b));
		assertFalse(manager.contradictsEquality(a, b));
	}
	
	public void testSimpleEquality() {
		TMPEquivalenceClassManager manager = new TMPEquivalenceClassManager();
		
		manager.addEquality(a, b);
		assertTrue(manager.contradictsInequality(a, b));
		assertFalse(manager.contradictsEquality(a, b));
		manager.addEquality(a, c);
		assertTrue(manager.contradictsInequality(a, b));
		assertTrue(manager.contradictsInequality(a, c));
		assertTrue(manager.contradictsInequality(b, c));
		assertTrue(manager.contradictsInequality(b, a));
		assertTrue(manager.contradictsInequality(c, a));
		assertTrue(manager.contradictsInequality(c, b));
		
		assertTrue(manager.getEqualityClasses().size()==1);
		assertTrue(manager.getInequalityClasses().size()==0);
	}
	
	public void testSimpleInequality() {
		TMPEquivalenceClassManager manager = new TMPEquivalenceClassManager();
		
		manager.addInequality(a, b);
		assertTrue(manager.contradictsEquality(a, b));
		assertFalse(manager.contradictsInequality(a, b));
		assertTrue(manager.getEqualityClasses().size() == 0);
		assertTrue(manager.getInequalityClasses().size() == 2);
		
		manager.addInequality(a, c);
		assertTrue(manager.contradictsEquality(a, b));
		assertTrue(manager.contradictsEquality(a, c));
		assertFalse(manager.contradictsEquality(b, c));
		assertTrue(manager.contradictsEquality(b, a));
		assertTrue(manager.contradictsEquality(c, a));
		assertFalse(manager.contradictsEquality(c, b));
		
		assertTrue(manager.getEqualityClasses().size() == 0);
		assertTrue(manager.getInequalityClasses().size() == 3);
	}
	
	public void testTwoDifferentEqualities() {
		TMPEquivalenceClassManager manager = new TMPEquivalenceClassManager();
		
		manager.addEquality(a, b);
		manager.addEquality(c, d);
		assertTrue(manager.contradictsInequality(a, b));
		assertTrue(manager.contradictsInequality(b, a));
		assertTrue(manager.contradictsInequality(c, d));
		assertTrue(manager.contradictsInequality(d, c));
		
		assertFalse(manager.contradictsInequality(a, c));
		assertFalse(manager.contradictsInequality(a, d));
		assertFalse(manager.contradictsInequality(b, c));
		assertFalse(manager.contradictsInequality(b, d));
		
		assertTrue(manager.getInequalityClasses().size()==0);
		assertTrue(manager.getEqualityClasses().size()==2);
	}
	
	public void testTwoDifferentInequalities() {
		TMPEquivalenceClassManager manager = new TMPEquivalenceClassManager();
		
		manager.addInequality(a, b);
		manager.addInequality(c, d);
		assertTrue(manager.contradictsEquality(a, b));
		assertTrue(manager.contradictsEquality(c, d));
		assertTrue(manager.contradictsEquality(b, a));
		assertTrue(manager.contradictsEquality(d, c));
		
		assertFalse(manager.contradictsEquality(a, c));
		assertFalse(manager.contradictsEquality(a, d));
		assertFalse(manager.contradictsEquality(b, c));
		assertFalse(manager.contradictsEquality(b, d));
		
		assertTrue(manager.getInequalityClasses().size() == 4);
		assertTrue(manager.getEqualityClasses().size() == 0);
	}
	
	public void testEqualitiesThenInequalities() {
		TMPEquivalenceClassManager manager = new TMPEquivalenceClassManager();
		
		manager.addEquality(a, b);
		manager.addEquality(c, d);
		manager.addInequality(a, c);
		assertTrue(manager.contradictsEquality(a, d));
		assertTrue(manager.contradictsEquality(a, c));
		assertTrue(manager.contradictsEquality(b, c));
		assertTrue(manager.contradictsEquality(d, a));
		assertTrue(manager.contradictsEquality(c, a));
		assertTrue(manager.contradictsEquality(c, b));
		
		assertTrue(manager.contradictsEquality(b, d));
		assertTrue(manager.contradictsEquality(d, b));
		
		assertFalse(manager.contradictsEquality(a, b));
		assertFalse(manager.contradictsEquality(c, d));
		assertFalse(manager.contradictsInequality(a, c));
		
		assertTrue(manager.getInequalityClasses().size()==4);
		assertTrue(manager.getEqualityClasses().size()==2);
	}

	public void testInequalitiesThenEqualities() {
		TMPEquivalenceClassManager manager = new TMPEquivalenceClassManager();
		
		manager.addInequality(a, b);
		manager.addInequality(c, d);
		manager.addEquality(a, c);
		
		assertTrue(manager.contradictsEquality(a, d));
		assertTrue(manager.contradictsEquality(c, b));
		assertTrue(manager.contradictsEquality(a, b));
		assertTrue(manager.contradictsEquality(c, d));

		assertTrue(manager.contradictsEquality(d, a));
		assertTrue(manager.contradictsEquality(b, c));
		assertTrue(manager.contradictsEquality(b, a));
		assertTrue(manager.contradictsEquality(d, c));
		
		assertTrue(manager.contradictsEquality(a, b));
		assertTrue(manager.contradictsEquality(c, d));
		
		assertFalse(manager.contradictsEquality(b, d));
		assertFalse(manager.contradictsEquality(d, b));
		
		assertTrue(manager.getInequalityClasses().size()==4);
		assertTrue(manager.getEqualityClasses().size()==1);
	}
	
	// origin
	public void testEqualityOrigin() {
		IEquivalenceManager manager = new TMPEquivalenceClassManager();
		
		manager.addEquality(a, b);
		manager.addEquality(b, c);
		
		assertEquals(mSet(ab),manager.getOrigins(a, b));
		assertEquals(mSet(bc),manager.getOrigins(b, c));
		assertEquals(mSet(abc),manager.getOrigins(a, c));
		
		manager.addEquality(c, d);
		assertEquals(mSet(ab),manager.getOrigins(a, b));
		assertEquals(mSet(bc),manager.getOrigins(b, c));
		assertEquals(mSet(abc),manager.getOrigins(a, c));

		assertEquals(mSet(abcd),manager.getOrigins(a, d));
		assertEquals(mSet(bcd),manager.getOrigins(b, d));

		manager.addEquality(a, d);
		assertEquals(mSet(ab),manager.getOrigins(a, b));
		assertEquals(mSet(bc),manager.getOrigins(b, c));
		assertEquals(mSet(abc,adc),manager.getOrigins(a, c));

		assertEquals(mSet(abcd,ad),manager.getOrigins(a, d));
		assertEquals(mSet(bad),manager.getOrigins(b, d));
		
	}
	
	// origin with different levels
	
	
	// backtracking
	
	
}
