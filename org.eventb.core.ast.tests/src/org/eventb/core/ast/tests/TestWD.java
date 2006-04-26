/**
 * 
 */
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import junit.framework.TestCase;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * @author halstefa
 *
 */
public class TestWD extends TestCase {

	public static FormulaFactory ff = FormulaFactory.getDefault(); 
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	private static IntegerType INTEGER = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	ITypeEnvironment defaultTEnv = mTypeEnvironment(
			mList(
					"x",
					"y",
					"A",
					"B",
					"f",
					"Y"
			),
			mList(
					INTEGER,
					INTEGER,
					POW(INTEGER),
					POW(INTEGER),
					POW(CPROD(INTEGER,INTEGER)),
					POW(BOOL)
			)
	);
	
	private abstract class TestFormula {
		// base class of various tests
		
		public abstract void test();
	}
	
	private class TestPredicate extends TestFormula {
		String input;
		String expected;
		ITypeEnvironment env;
		TestPredicate(String in, String exp) {
			input = in;
			expected = exp;
			env = defaultTEnv;
		}
		
		@Override
		public void test() {
			IParseResult resIn = ff.parsePredicate(input);
			assertTrue("Couldn't parse " + input, resIn.isSuccess());
			Predicate inP = resIn.getParsedPredicate();
			ITypeCheckResult result = inP.typeCheck(env);
			assertTrue("Couldn't typecheck " + input, inP.isTypeChecked());
			
			ITypeEnvironment newEnv = env.clone();
			newEnv.addAll(result.getInferredEnvironment());
			
			Predicate inWD = inP.getWDPredicate(ff);
			assertTrue(input + "\n"
					+ inWD.toString() + "\n"
					+ inWD.getSyntaxTree() + "\n",
					inWD.isTypeChecked());
			
			IParseResult resExp = ff.parsePredicate(expected);
			assertTrue(input, resExp.isSuccess());
			Predicate exP = resExp.getParsedPredicate().flatten(ff);
			exP.typeCheck(newEnv);
			assertTrue("Couldn't typeCheck " + exP, exP.isTypeChecked());
			
			assertEquals(input, exP, inWD);
		}
	}
	
	private class TestAssignment extends TestFormula {
		String input;
		String expected;
		ITypeEnvironment env;
		private TestAssignment(String in, String exp) {
			input = in;
			expected = exp;
			env = defaultTEnv;
		}
		
		@Override
		public void test() {
			IParseResult resIn = ff.parseAssignment(input);
			assertTrue(resIn.isSuccess());
			Assignment inA = resIn.getParsedAssignment();
			ITypeCheckResult result = inA.typeCheck(env);
			assertTrue(input, result.isSuccess());
			
			ITypeEnvironment newEnv = env.clone();
			newEnv.addAll(result.getInferredEnvironment());
			
			Predicate inWD = inA.getWDPredicate(ff);
			ITypeCheckResult intResult = inWD.typeCheck(newEnv);
			assertTrue(input + "\n" + inWD.toString() + "\n" + inWD.getSyntaxTree() + "\n", intResult.isSuccess());
			
			IParseResult resExp = ff.parsePredicate(expected);
			assertTrue(input, resExp.isSuccess());
			Predicate exP = resExp.getParsedPredicate().flatten(ff);
			ITypeCheckResult newResult = exP.typeCheck(newEnv);
			assertTrue(newResult.isSuccess());
			
			assertEquals(input, exP, inWD);
		}
	}
	
	private TestFormula[] formulas = new TestFormula[] {
			new TestPredicate(
					"x≠y ∧ y=1",
					"⊤"
			), new TestPredicate(
					"x+y+x+1=0 ⇒ y<x",
					"⊤"
			), new TestPredicate(
					"x+1=0 ∨ x<y",
					"⊤"
			), new TestPredicate(
					"(∃x \u00b7 0<x ⇒ (∀y \u00b7 y+x=0))",
					"⊤"
			), new TestPredicate(
					"(B×Y)(x) ∈ Y",
					"x∈dom(B × Y) ∧ (B × Y)\u223c;({x}◁(B × Y)) ⊆ id(BOOL)"
			), new TestPredicate(
					"x=f(f(y))",
					"((y∈dom(f) ∧ f\u223c;({y}◁f)⊆id(ℤ))" +
					"∧ f(y)∈dom(f)) ∧ f\u223c;({f(y)}◁f)⊆id(ℤ)"
			), new TestPredicate(
					"(x÷y=y) ⇔ (y mod x=0)",
					"y≠0 ∧ x≠0"
			), new TestPredicate(
					"∀z \u00b7 x^z>y",
					"∀z \u00b7 0≤x ∧ 0≤z"
			), new TestPredicate(
					"card(A)>x",
					"finite(A)"
			), new TestPredicate(
					"inter({A,B}) ⊆ A∩B",
					"{A,B}≠∅"
			), new TestPredicate(
					"(λ m↦n \u00b7 m>n \u2223 y)(1↦x) = y",
					"1 ↦ x∈dom(λm ↦ n\u00b7m>n ∣ y) " +
					"∧ (λm ↦ n\u00b7m>n ∣ y)\u223c;({1 ↦ x}◁(λm ↦ n\u00b7m>n ∣ y))⊆id(ℤ)"
			), new TestPredicate(
					"{m,n \u00b7 m=f(n) \u2223 m↦n}[A] ⊂ B",
					"∀n \u00b7 n∈dom(f) ∧ f\u223c;({n}◁f) ⊆ id(ℤ)"
			), new TestPredicate(
					"{f(n)↦m \u2223 x=n ∧ y+x=m ∧ f ∈ ℤ→A} = A×B",
					"∀f,n,m \u00b7 x=n ∧ y+x=m ∧ f ∈ ℤ→A ⇒ n∈dom(f) ∧ f\u223c;({n}◁f)⊆id(ℤ)"
			), new TestPredicate(
					"{1, 2, x, x+y, 4, 6} = B",
					"⊤"
			), new TestPredicate(
					"(⋂ m,n \u00b7 m∈A ∧ n∈B \u2223 {m÷n}) = B",
					"(∀m,n \u00b7 (m∈A ∧ n∈B) ⇒ n≠0) ∧ (∃m,n \u00b7 (m∈A ∧ n∈B))"
			), new TestPredicate(
					"(⋂{m+n} \u2223 m+n∈A)=B",
					"∃m,n\u00b7m+n∈A"
			), new TestPredicate(
					"bool(⊤)=bool(⊥)",
					"⊤"
			), new TestPredicate(
					"x+y+(x mod y)\u2217x+1=0 ⇒ y<x",
					"y≠0"
			), new TestAssignment(
					"x≔y",
					"⊤"
			), new TestAssignment(
					"x :\u2223 x'>x",
					"⊤"
			), new TestAssignment(
					"x :∈ {x,y}",
					"⊤"
			), new TestAssignment(
					"x :∈ {x÷y, y}",
					"y≠0"
			), new TestAssignment(
					"f(x)≔f(x)",
					"x∈dom(f)∧f\u223c;({x} ◁ f)⊆id(ℤ)"
			), new TestAssignment(
					"x :\u2223 x'=min(A∪{x'})",
					"∀x' \u00b7 A∪{x'}≠∅"
			)
	};
	
	public void testWD() {
		for (int i=0; i<formulas.length; i++) {
			formulas[i].test();
		}
	}
	
}
