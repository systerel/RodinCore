package org.eventb.core.ast.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;

/**
 * Unit test of errors produced by the mathematical formula Type-Checker.
 * 
 * @author Laurent Voisin
 */
public class TestTypeCheckError extends TestCase {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	private void doTestPredicate(String input, ITypeEnvironment te, ProblemKind... problems) {
		final IParseResult parseResult = ff.parsePredicate(input);
		assertTrue("Parser failed on: " + input, parseResult.isSuccess());
		final Predicate pred = parseResult.getParsedPredicate();
		
		doTest(pred, te, problems);
	}
	
	private void doTestAssignment(String input, ITypeEnvironment te, ProblemKind... problems) {
		final IParseResult parseResult = ff.parseAssignment(input);
		assertTrue("Parser failed on: " + input, parseResult.isSuccess());
		final Assignment pred = parseResult.getParsedAssignment();
		
		doTest(pred, te, problems);
	}
	
	private void doTest(Formula<?> formula, ITypeEnvironment te, ProblemKind... problems) {
		final ITypeCheckResult tcResult = formula.typeCheck(te);
		assertFalse("Type checker succeeded unexpectedly", tcResult.isSuccess());
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
	public void testTypesDoNotMatch() {
		doTestPredicate("1 = TRUE", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.TypesDoNotMatch);
	}
	
	/**
	 * Ensures that a Circularity is produced when there is a mutual
	 * incompatible dependency between types.
	 */
	public void testCircularity() {
		doTestPredicate("x∈y ∧ y∈x", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.Circularity);
	}
	
	/**
	 * Ensures that a TypeUnknown is produced when a type can't be inferred
	 */
	public void testTypeUnknown() {
		doTestPredicate("finite(∅)", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.TypeUnknown);
	}	
	
	/**
	 * Ensures that a MinusAppliedToSet is produced for both children of an
	 * arithmetic subtraction.
	 */
	public void testMinusAppliedToSet() {
		doTestPredicate("x = a − ∅", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MinusAppliedToSet);
		doTestPredicate("x = ∅ − b", 
				FastFactory.mTypeEnvironment(), 
				ProblemKind.MinusAppliedToSet);
	}
	
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
	public void testSharedBID() {
		BoundIdentDecl decl = ff.makeBoundIdentDecl("x", new SourceLocation(0, 1));
		BoundIdentDecl[] decls = new BoundIdentDecl[] {decl};
		Predicate btrue = ff.makeLiteralPredicate(Predicate.BTRUE, null);
		Predicate p = ff.makeQuantifiedPredicate(Predicate.FORALL, decls, btrue, null);
		Predicate q = ff.makeBinaryPredicate(Predicate.LIMP, p, p, null);
		doTest(q, ff.makeTypeEnvironment(), ProblemKind.TypeUnknown);
	}
	
}
