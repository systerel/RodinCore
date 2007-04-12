package org.eventb.pp.loader;

import static org.eventb.pp.Util.INTEGER;
import static org.eventb.pp.Util.mSort;

import java.util.Stack;

import junit.framework.TestCase;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
import org.eventb.internal.pp.loader.predicate.NormalizedFormula;
import org.eventb.internal.pp.loader.predicate.TermBuilder;
import org.eventb.pp.Util;

/**
 * This class tests that the sorts created by the term builder are
 * indeed the expected sorts.
 * Are tested here :
 * <ul>
 * 	<li>arithmetic sort for all arithmetic type</li>
 * 	<li>carrier set sorts, same sort for same carrier set, different sort for
 * 	    different carrier sets</li>
 * 	<li>boolean sorts...</li>
 * </ul>
 *
 * @author François Terrier
 *
 */
public class TestSorts extends TestCase {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	static Type S = ff.makeGivenType("S");
	static Type T = ff.makeGivenType("T");
	
	static FreeIdentifier a = ff.makeFreeIdentifier("a", null);
	static FreeIdentifier b = ff.makeFreeIdentifier("b", null);
	static FreeIdentifier k = ff.makeFreeIdentifier("k", null);
	
	static BoundIdentifier b0 = ff.makeBoundIdentifier(0, null, S);
	
	static {
		ITypeEnvironment env;

		env = ff.makeTypeEnvironment();
		env.addName("a", S);
		a.typeCheck(env);
		
		env = ff.makeTypeEnvironment();
		env.addName("b", T);
		b.typeCheck(env);
		
		env = ff.makeTypeEnvironment();
		env.addName("k", ff.makeIntegerType());
		k.typeCheck(env);
	}
	
	private static class TestPair {
		// for the readability of the test, use of string
		Expression expression;
		Sort expected;
		
		TestPair (Expression expression, Sort expected) {
			this.expression = expression;
			this.expected = expected;
		}
		
		TestPair (String expression, Sort expected) {
			this.expression = (Expression)Util.parseExpression(expression);
			this.expression.typeCheck(ff.makeTypeEnvironment());
			this.expected = expected;
		}
		
	}
	
	TestPair[] tests = new TestPair[]{
		new TestPair(a,
			mSort(S)
		),
		new TestPair(
			b,
			mSort(T)
		),
		new TestPair(
			k,
			INTEGER()
		),
		new TestPair(
			"k + 1",
			INTEGER()
		),
		new TestPair(
			"k ∗ 1",
			INTEGER()
		),
		new TestPair(
			"k ÷ 1",
			INTEGER()
		),
		new TestPair(
			"k mod 1",
			INTEGER()
		),
		new TestPair(
			"k ^ 1",
			INTEGER()
		),
		new TestPair(
			"k − 1",
			INTEGER()
		),
//		new TestPair(
//			b0,
//			mSort(S)
//		),
	};
	
	public void doTest(Expression expression, Sort expected) {
		Stack<INormalizedFormula> result = new Stack<INormalizedFormula>();
		result.push(new NormalizedFormula(null,0,0,0,new BoundIdentDecl[0],null,false));
		TermBuilder builder = new TermBuilder(result);
		TermSignature term = builder.buildTerm(expression);
		
		assertEquals(expression.toString(), expected, term.getSort());
	}
	
	public void testSorts() {
		for (TestPair test : tests) {
			doTest(test.expression, test.expected);
		}
	}
}
