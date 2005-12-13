/**
 * 
 */
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import junit.framework.TestCase;

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

	private static FormulaFactory ff = FormulaFactory.getDefault(); 
	
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
	
	private class TestFormula {
		String input;
		String expected;
		ITypeEnvironment env;
		TestFormula(String in, String exp, ITypeEnvironment te) {
			input = in;
			expected = exp;
			env = te;
		}
	}
	
	private TestFormula[] formulas = new TestFormula[] {
			new TestFormula("x≠y∧y=1", "⊤", defaultTEnv),
			new TestFormula("x+y+x+1=0⇒y<x", "⊤", defaultTEnv),
			new TestFormula("x+1=0∨x<y", "⊤", defaultTEnv),
			new TestFormula("(∃x·0<x⇒(∀y·y+x=0))", "⊤", defaultTEnv),
			new TestFormula("(B×Y)(x)∈Y", "x∈dom(B × Y)∧(B × Y)~;({x}◁(B × Y))⊆id(ran(B × Y))", defaultTEnv),
			new TestFormula("x=f(f(y))", "((y∈dom(f)∧f~;({y}◁f)⊆id(ran(f)))∧f(y)∈dom(f))∧f~;({f(y)}◁f)⊆id(ran(f))", defaultTEnv),
			new TestFormula("(x÷y=y)⇔(y mod x=0)", "y≠0∧x≠0", defaultTEnv),
			new TestFormula("∀z·x^z>y", "∀z⋅0≤x∧0≤z", defaultTEnv),
			new TestFormula("card(A)>x", "finite(A)", defaultTEnv),
			new TestFormula("inter({A,B})⊆A∩B", "{A,B}≠∅", defaultTEnv),
			new TestFormula("(λm↦n·m>n|y)(1↦x)=y", "1 ↦ x∈dom(λm ↦ n⋅m>n ∣ y)∧(λm ↦ n⋅m>n ∣ y)~;({1 ↦ x}◁(λm ↦ n⋅m>n ∣ y))⊆id(ran(λm ↦ n⋅m>n ∣ y))", defaultTEnv),
			// TODO put back tests for WD
			//new TestFormula("{m,n·m=f(n)|m↦n}[A]⊂B", "∀n⋅n∈dom(f)∧f~;({n}◁f)⊆id(ran(f))", env),
			//new TestFormula("{f(n)↦m|x=n∧y+x=m}=A×B", "∀f,n,m⋅x=n∧y+x=m⇒n∈dom(f)∧f~;({n}◁f)⊆id(ran(f))", env),
			new TestFormula("{1,2,x,x+y,4,6}=B", "⊤", defaultTEnv),
			new TestFormula("(⋂m,n·m∈A∧n∈B|{m÷n})=B", "(∀m,n⋅(m∈A∧n∈B)⇒n≠0)∧(∃m,n⋅(m∈A∧n∈B))", defaultTEnv),
			new TestFormula("(⋂{m+n}|m+n∈A)=B", "∃m,n⋅m+n∈A", defaultTEnv),
			new TestFormula("bool(⊤)=bool(⊥)", "⊤", defaultTEnv),
			new TestFormula("x+y+(x mod y)*x+1=0⇒y<x", "y≠0", defaultTEnv)
	};
	
	public void testWD() {
		
		for(int i=0; i<formulas.length; i++) {
			IParseResult resIn = ff.parsePredicate(formulas[i].input);
			assertTrue(resIn.isSuccess());
			Predicate inP = resIn.getParsedPredicate();
			ITypeCheckResult result = inP.typeCheck(formulas[i].env);
			assertTrue(formulas[i].input, result.isSuccess());
			
			ITypeEnvironment newEnv = formulas[i].env.clone();
			newEnv.addAll(result.getInferredEnvironment());
			
			Predicate inWD = inP.getWDPredicate(ff);
			ITypeCheckResult intResult = inWD.typeCheck(newEnv);
			assertTrue(formulas[i].input + "\n" + inWD.toString() + "\n" + inWD.getSyntaxTree() + "\n", intResult.isSuccess());
			
			IParseResult resExp = ff.parsePredicate(formulas[i].expected);
			assertTrue(formulas[i].input, resExp.isSuccess());
			Predicate exP = resExp.getParsedPredicate().flatten(ff);
			ITypeCheckResult newResult = exP.typeCheck(newEnv);
			assertTrue(newResult.isSuccess());
			
			assertEquals(formulas[i].input, exP, inWD);
		}

		return;
	}
	
}
