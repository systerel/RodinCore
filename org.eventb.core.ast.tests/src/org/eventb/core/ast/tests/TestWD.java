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
	
	private ITypeEnvironment defaultTEnv = mTypeEnvironment(
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
		TestPredicate(String in, String exp, ITypeEnvironment te) {
			input = in;
			expected = exp;
			env = te;
		}
		
		@Override
		public void test() {
			IParseResult resIn = ff.parsePredicate(input);
			assertTrue(resIn.isSuccess());
			Predicate inP = resIn.getParsedPredicate();
			ITypeCheckResult result = inP.typeCheck(env);
			assertTrue(input, result.isSuccess());
			
			ITypeEnvironment newEnv = env.clone();
			newEnv.addAll(result.getInferredEnvironment());
			
			Predicate inWD = inP.getWDPredicate(ff);
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
	
	private class TestAssignment extends TestFormula {
		String input;
		String expected;
		ITypeEnvironment env;
		TestAssignment(String in, String exp, ITypeEnvironment te) {
			input = in;
			expected = exp;
			env = te;
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
			new TestPredicate("x≠y∧y=1", "⊤", defaultTEnv),
			new TestPredicate("x+y+x+1=0⇒y<x", "⊤", defaultTEnv),
			new TestPredicate("x+1=0∨x<y", "⊤", defaultTEnv),
			new TestPredicate("(∃x·0<x⇒(∀y·y+x=0))", "⊤", defaultTEnv),
			new TestPredicate("(B×Y)(x)∈Y", "x∈dom(B × Y)∧(B × Y)~;({x}◁(B × Y))⊆id(ran(B × Y))", defaultTEnv),
			new TestPredicate("x=f(f(y))", "((y∈dom(f)∧f~;({y}◁f)⊆id(ran(f)))∧f(y)∈dom(f))∧f~;({f(y)}◁f)⊆id(ran(f))", defaultTEnv),
			new TestPredicate("(x÷y=y)⇔(y mod x=0)", "y≠0∧x≠0", defaultTEnv),
			new TestPredicate("∀z·x^z>y", "∀z⋅0≤x∧0≤z", defaultTEnv),
			new TestPredicate("card(A)>x", "finite(A)", defaultTEnv),
			new TestPredicate("inter({A,B})⊆A∩B", "{A,B}≠∅", defaultTEnv),
			new TestPredicate("(λm↦n·m>n|y)(1↦x)=y", "1 ↦ x∈dom(λm ↦ n⋅m>n ∣ y)∧(λm ↦ n⋅m>n ∣ y)~;({1 ↦ x}◁(λm ↦ n⋅m>n ∣ y))⊆id(ran(λm ↦ n⋅m>n ∣ y))", defaultTEnv),
			// TODO put back tests for WD
			//new TestPredicate("{m,n·m=f(n)|m↦n}[A]⊂B", "∀n⋅n∈dom(f)∧f~;({n}◁f)⊆id(ran(f))", env),
			//new TestPredicate("{f(n)↦m|x=n∧y+x=m}=A×B", "∀f,n,m⋅x=n∧y+x=m⇒n∈dom(f)∧f~;({n}◁f)⊆id(ran(f))", env),
			new TestPredicate("{1,2,x,x+y,4,6}=B", "⊤", defaultTEnv),
			new TestPredicate("(⋂m,n·m∈A∧n∈B|{m÷n})=B", "(∀m,n⋅(m∈A∧n∈B)⇒n≠0)∧(∃m,n⋅(m∈A∧n∈B))", defaultTEnv),
			new TestPredicate("(⋂{m+n}|m+n∈A)=B", "∃m,n⋅m+n∈A", defaultTEnv),
			new TestPredicate("bool(⊤)=bool(⊥)", "⊤", defaultTEnv),
			new TestPredicate("x+y+(x mod y)*x+1=0⇒y<x", "y≠0", defaultTEnv),
			new TestAssignment("x≔y", "⊤", defaultTEnv),
			new TestAssignment("x :| x'>x", "⊤", defaultTEnv),
			new TestAssignment("x :∈ {x,y}", "⊤", defaultTEnv),
			new TestAssignment("x :∈ {x÷y,y}", "y≠0", defaultTEnv),
			new TestAssignment("f(x)≔f(x)", "x∈dom(f)∧f~;({x} ◁ f)⊆id(ran(f))", defaultTEnv),
			new TestAssignment("x:|x'=min(A∪{x'})", "∀x'⋅A∪{x'}≠∅", defaultTEnv)
	};
	
	public void testWD() {
		
		for(int i=0; i<formulas.length; i++) {
			
			formulas[i].test();
			
//			IParseResult resIn = ff.parsePredicate(formulas[i].input);
//			assertTrue(resIn.isSuccess());
//			Predicate inP = resIn.getParsedPredicate();
//			ITypeCheckResult result = inP.typeCheck(formulas[i].env);
//			assertTrue(formulas[i].input, result.isSuccess());
//			
//			ITypeEnvironment newEnv = formulas[i].env.clone();
//			newEnv.addAll(result.getInferredEnvironment());
//			
//			Predicate inWD = inP.getWDPredicate(ff);
//			ITypeCheckResult intResult = inWD.typeCheck(newEnv);
//			assertTrue(formulas[i].input + "\n" + inWD.toString() + "\n" + inWD.getSyntaxTree() + "\n", intResult.isSuccess());
//			
//			IParseResult resExp = ff.parsePredicate(formulas[i].expected);
//			assertTrue(formulas[i].input, resExp.isSuccess());
//			Predicate exP = resExp.getParsedPredicate().flatten(ff);
//			ITypeCheckResult newResult = exP.typeCheck(newEnv);
//			assertTrue(newResult.isSuccess());
//			
//			assertEquals(formulas[i].input, exP, inWD);
		}

		return;
	}
	
}
