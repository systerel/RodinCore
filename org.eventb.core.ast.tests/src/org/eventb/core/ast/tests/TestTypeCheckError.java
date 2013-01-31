/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.junit.Test;

/**
 * Unit test of errors produced by the mathematical formula Type-Checker.
 * 
 * @author Laurent Voisin
 */
public class TestTypeCheckError extends AbstractTests {
	
	private void doTestPredicate(String input, ITypeEnvironment te, ProblemKind... problems) {
		final Predicate pred = parsePredicate(input, te.getFormulaFactory());
		doTest(pred, te, problems);
	}
	
	private void doTestAssignment(String input, ITypeEnvironment te, ProblemKind... problems) {
		final Assignment assign = parseAssignment(input);
		doTest(assign, te, problems);
	}
	
	private void doTest(Formula<?> formula, ITypeEnvironment te, ProblemKind... problems) {
		final ITypeCheckResult tcResult = formula.typeCheck(te);
		TypeCheckedChecker.check(formula);
		assertFailure("Type checker succeeded unexpectedly", tcResult);
		assertFalse("Predicate shouldn't be typechecked", formula.isTypeChecked());
		List<ASTProblem> actualProblems = tcResult.getProblems();
		assertEquals("Unexpected list of problems", problems.length, actualProblems.size());
		int idx = 0;
		for (ASTProblem actualProblem: actualProblems) {
			assertEquals("Unexpected problem", problems[idx], actualProblem.getMessage());
			++ idx;
		}	
	}

	/**
	 * Ensures that assignments are translated correctly
	 */
	@Test 
	public void testAssignment() {
		doTestAssignment("x≔x{a ↦ b}",
				FastFactory.mTypeEnvironment(),
				ProblemKind.TypeUnknown,
				ProblemKind.TypeUnknown,
				ProblemKind.TypeUnknown
		);
		doTestAssignment("x(a)≔b",
				FastFactory.mTypeEnvironment(),
				ProblemKind.TypeUnknown,
				ProblemKind.TypeUnknown,
				ProblemKind.TypeUnknown
		);
	}
	
	/**
	 * Ensures that a TypesDoNotMatch is produced when there is a type conflict.
	 */
	@Test 
	public void testTypesDoNotMatch() {
		doTestPredicate("1 = TRUE", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.TypesDoNotMatch);
	}
	
	/**
	 * Ensures that a Circularity is produced when there is a mutual
	 * incompatible dependency between types.
	 */
	@Test 
	public void testCircularity() {
		doTestPredicate("x∈y ∧ y∈x", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.Circularity);
	}
	
	/**
	 * Ensures that a TypeUnknown is produced when a type can't be inferred
	 */
	@Test 
	public void testTypeUnknown() {
		doTestPredicate("finite(∅)", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.TypeUnknown);
	}	
	
	/**
	 * Ensures that a MinusAppliedToSet is produced for both children of an
	 * arithmetic subtraction.
	 */
	@Test 
	public void testMinusAppliedToSet() {
		doTestPredicate("x = a − ∅", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MinusAppliedToSet);
		doTestPredicate("x = ∅ − b", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MinusAppliedToSet);
	}
	
	@Test 
	public void testMulAppliedToSet() {
		doTestPredicate("x = a ∗ ∅", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTestPredicate("x = ∅ ∗ b", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTestPredicate("x = a ∗ b ∗ ∅", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTestPredicate("x = a ∗ ∅ ∗ c", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTestPredicate("x = ∅ ∗ b ∗ c", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
	}
	
	/**
	 * Ensures that no assertion is raised when type-checking a formula with a
	 * shared bound identifier declaration which doesn't typecheck.
	 */
	@Test 
	public void testSharedBID() {
		BoundIdentDecl decl = ff.makeBoundIdentDecl("x", new SourceLocation(0, 1));
		BoundIdentDecl[] decls = new BoundIdentDecl[] {decl};
		Predicate btrue = ff.makeLiteralPredicate(Predicate.BTRUE, null);
		Predicate p = ff.makeQuantifiedPredicate(Predicate.FORALL, decls, btrue, null);
		Predicate q = ff.makeBinaryPredicate(Predicate.LIMP, p, p, null);
		doTest(q, ff.makeTypeEnvironment(), ProblemKind.TypeUnknown);
	}
	
	@Test 
	public void testParamTypes() throws Exception {
		doTestPredicate("x ∈ A ∧ x ∈ List(A)", 
				LIST_FAC.makeTypeEnvironment(), 
				ProblemKind.Circularity);
		
	}
}
