/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.prover.classicBtests;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.externalReasoners.classicB.ClassicB;

public class TestOldProver extends TestCase {
	
	private static FormulaFactory factory = FormulaFactory.getDefault();
	
	public static String[] mList(String... names) {
		return names;
	}

	public static Type[] mList(Type... types) {
		return types;
	}

	private static IntegerType INTEGER = factory.makeIntegerType();
	private static BooleanType BOOL = factory.makeBooleanType();

	private static Type POW(Type base) {
		return factory.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return factory.makeProductType(left, right);
	}
	
	public static ITypeEnvironment mTypeEnvironment(String[] names, Type[] types) {
		assert names.length == types.length;
		ITypeEnvironment result = factory.makeTypeEnvironment();
		for (int i = 0; i < names.length; i++) {
			result.addName(names[i], types[i]);
		}
		return result;
	}

	private static ITypeEnvironment defaultTEnv = mTypeEnvironment(
			mList(
					"x",
					"y",
					"z",
					"A",
					"B",
					"f",
					"g",
					"h",
					"U"
			),
			mList(
					INTEGER,
					INTEGER,
					INTEGER,
					POW(INTEGER),
					POW(INTEGER),
					POW(CPROD(INTEGER,INTEGER)),
					POW(CPROD(INTEGER,INTEGER)),
					POW(CPROD(INTEGER,INTEGER)),
					POW(BOOL)
			)
	);
	
	private static ITypeEnvironment smallTEnv = mTypeEnvironment(
			mList(
					"x",
					"y",
					"z",
					"A",
					"B",
					"U"
			),
			mList(
					INTEGER,
					INTEGER,
					INTEGER,
					POW(INTEGER),
					POW(INTEGER),
					POW(BOOL)
			)
	);
	
	static public Predicate mPredicate(String string) {
		IParseResult presult = factory.parsePredicate(string);
		assertTrue("parsed", presult.isSuccess());
		Predicate predicate = presult.getParsedPredicate();
		ITypeCheckResult tresult = predicate.typeCheck(defaultTEnv);
		assertTrue("type-checked", tresult.isSuccess());
		return predicate;
	}
	
	static public Set<Predicate> mHypothesis(String...strings) {
		HashSet<Predicate> predicates = new HashSet<Predicate>(strings.length * 4 / 3 + 1);
		for(String string : strings) {
			Predicate predicate = mPredicate(string);
			predicates.add(predicate);
		}
		return predicates;
	}
	
	abstract class TestItem {
		public Set<Predicate> hypothesis;
		public Predicate goal;
		public abstract void test() throws Exception;
	};
	
	TestItem[] items = new TestItem[] {
			new TestPK(mHypothesis("x=y", "y=z"), mPredicate("x=z")),
			new TestPK(mHypothesis("(∀x·x∈ℕ)", "y=z"), mPredicate("(∃C·C=A ∧ y<z)")),
			new TestPK(mHypothesis("(∀v,w·v=x∧w=f(x)+(λx·x=w∣x∗y)(x))", "A⊆B ∧ B=g[A]", "x∈A∩B"), 
					mPredicate("(∀a,b,c·a+(b−c)=x)")),
					
			// a lemma on equality
			new TestML(mHypothesis("x=y", "y=z"), mPredicate("x=z"), true),
			
			// this is not true!
			new TestML(mHypothesis("x=y", "y=z"), mPredicate("x∈A"), false),
			
			// a simpe lemma with bound variables
			new TestML(mHypothesis("(∀n·n∈ℕ ⇒ n∈A)"), mPredicate("(∃n·n∈ℕ ∧ n∈A)"), true),
			
			// test added type information
			new TestML(mHypothesis("(∀n·n∈A)"), mPredicate("(∃n·n∈A)"), true),
			new TestML(mHypothesis(), mPredicate("(∀m,n·m∈A∧n∈U⇒m∈ℤ∧n∈BOOL)"), true),
			
			// test free identifier type information
			new TestML(mHypothesis("x∈A", "x∈B"), mPredicate("x∈A∩B"), true),
			new TestML(mHypothesis("x∈{y+z}"), mPredicate("y∈{x−z}"), true),
			new TestML(mHypothesis("x∈{y+z}"), mPredicate("y∈{x−z}"), true),
			new TestML(mHypothesis("y∈0‥x"), mPredicate("x∈ℤ"), true),
			
			// some tests for PP
			new TestPP(mHypothesis("x∈A", "x∈B"), mPredicate("x∈A∩B"), true),
			new TestPP(mHypothesis("x∈A", "x∈B"), mPredicate("y∈A∩B"), false)
	};
	
	class TestPK extends TestItem {
		
		public TestPK(Set<Predicate> hypothesis, Predicate goal) {
			this.hypothesis = hypothesis;
			this.goal = goal;
		}
		
		@Override
		public void test() throws Exception {
			StringBuffer buffer = ClassicB.translateSequent(defaultTEnv, hypothesis, goal);
			boolean result = ClassicB.callPKforPP(buffer);
			assertTrue("true", result);
			result = ClassicB.callPKforML(buffer);
			assertTrue("true", result);
		}
		
	}
	
	class TestML extends TestItem {
		
		public boolean isTrue;
		
		public TestML(Set<Predicate> hypothesis, Predicate goal, boolean isTrue) {
			this.hypothesis = hypothesis;
			this.goal = goal;
			this.isTrue = isTrue;
		}
		
		@Override
		public void test() throws Exception {
			StringBuffer buffer = ClassicB.translateSequent(smallTEnv, hypothesis, goal);
			boolean result = ClassicB.proveWithML(buffer);
			if(isTrue)
				assertTrue("true", result);
			else
				assertFalse("false", result);
		}
		
	}
	
	class TestPP extends TestItem {
		
		public boolean isTrue;
		
		public TestPP(Set<Predicate> hypothesis, Predicate goal, boolean isTrue) {
			this.hypothesis = hypothesis;
			this.goal = goal;
			this.isTrue = isTrue;
		}
		
		@Override
		public void test() throws Exception {
			StringBuffer buffer = ClassicB.translateSequent(smallTEnv, hypothesis, goal);
			boolean result = ClassicB.proveWithPP(buffer);
			if(isTrue)
				assertTrue("true", result);
			else
				assertFalse("false", result);
		}
		
	}
	
	public void testAll() throws Exception {
    	for(TestItem item : items)
    		item.test();
    }
}
