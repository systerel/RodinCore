package org.eventb.core.ast.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;

/**
 * Unit test of errors produced by the mathematical formula Type-Checker.
 * 
 * @author Laurent Voisin
 */
public class TestTypeCheckError extends TestCase {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	private void doTest(String input, ITypeEnvironment te, ProblemKind... problems) {
		final IParseResult parseResult = ff.parsePredicate(input);
		assertTrue("Parser failed on: " + input, parseResult.isSuccess());
		final Predicate pred = parseResult.getParsedPredicate();
		
		final ITypeCheckResult tcResult = pred.typeCheck(te);
		assertFalse("Type checker succeeded unexpectedly", tcResult.isSuccess());
		assertFalse("Predicate shouldn't be typechecked", pred.isTypeChecked());
		List<ASTProblem> actualProblems = tcResult.getProblems();
		assertEquals("Unexpected list of problems", problems.length, actualProblems.size());
		int idx = 0;
		for (ASTProblem actualProblem: actualProblems) {
			assertEquals("Unexpected problem", problems[idx], actualProblem.getMessage());
			++ idx;
		}
	}

	/**
	 * Ensures that a TypesDoNotMatch is produced when there is a type conflict.
	 */
	public void testTypesDoNotMatch() {
		doTest("1 = TRUE", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.TypesDoNotMatch);
	}
	
	/**
	 * Ensures that a Circularity is produced when there is a mutual
	 * incompatible dependency between types.
	 */
	public void testCircularity() {
		doTest("x∈y ∧ y∈x", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.Circularity);
	}
	
	/**
	 * Ensures that a TypeUnknown is produced when a type can't be inferred
	 */
	public void testTypeUnknown() {
		doTest("finite(∅)", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.TypeUnknown);
	}	
	
	/**
	 * Ensures that a MinusAppliedToSet is produced for both children of an
	 * arithmetic subtraction.
	 */
	public void testMinusAppliedToSet() {
		doTest("x = a − ∅", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MinusAppliedToSet);
		doTest("x = ∅ − b", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MinusAppliedToSet);
	}
	
	public void testMulAppliedToSet() {
		doTest("x = a ∗ ∅", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTest("x = ∅ ∗ b", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTest("x = a ∗ b ∗ ∅", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTest("x = a ∗ ∅ ∗ c", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
		doTest("x = ∅ ∗ b ∗ c", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MulAppliedToSet);
	}
}
