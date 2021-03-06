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
package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.AbstractPPTest.mTypeEnvironment;
import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.formula.AbstractFormula;
import org.eventb.internal.pp.loader.formula.ArithmeticFormula;
import org.eventb.internal.pp.loader.formula.EqualityFormula;
import org.eventb.internal.pp.loader.formula.PredicateFormula;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.junit.Test;


/**
 * This class tests that the literals produced by the sequent loader are
 * the ones expected. The tested input is the output of the PP translator.
 * The expected output is the corresponding object in the PP data structure.
 *
 * @author François Terrier
 *
 */
public class TestExpectedLiterals {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	private static ITypeEnvironmentBuilder env = mTypeEnvironment(
			"a=A; c=BOOL; N=ℙ(ℤ); S=ℙ(S); T=ℤ↔ℤ", ff);
	
	String[] testPredicates = new String[]{
			"x ∈ N",
			"x ↦ y ∈ T",
//			"x + 1 ∈ N",
//			"x + 1 ↦ y ∈ T",
	};
	
	String[] testConstants = new String[] {
			"⊤",
			"⊥"
	};
	
	String[] testArithmetic = new String[]{
			"x > y",
			"x < y",
			"x ≤ y",
			"x ≥ y",
//			"x = 1",
//			"x + y = 1",
//			"x ∗ y = 1",
//			"x − y = 1",
//			"x mod y = 1",
//			"x ÷ y = 1",
	};
	
	String[] testEquality = new String[]{
			"a = b",
//			"n = 1"
	};
	
	
	
    @Test
	public void testArithmetic() {
		doTest(testArithmetic, ArithmeticFormula.class);
	}
	
    @Test
	public void testEquality() {
		doTest(testEquality, EqualityFormula.class);
	}
	
    @Test
	public void testPredicates() {
		doTest(testPredicates, PredicateFormula.class);
	}
	
	public void doTest(String[] tests, Class<?> expectedClass) {
		for (String string : tests) {
			final AbstractContext context = new AbstractContext();
			final Predicate predicate = Util.parsePredicate(string, env);
			context.load(predicate, false);
			final AbstractFormula<?> obj = context.getLastResult().getSignature().getFormula();
			assertEquals(expectedClass, obj.getClass());
		}
	}
	
}
