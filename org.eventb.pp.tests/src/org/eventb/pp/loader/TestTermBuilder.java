package org.eventb.pp.loader;

import static org.eventb.pp.Util.mConstant;
import static org.eventb.pp.Util.mDivide;
import static org.eventb.pp.Util.mExpn;
import static org.eventb.pp.Util.mInteger;
import static org.eventb.pp.Util.mMinus;
import static org.eventb.pp.Util.mMod;
import static org.eventb.pp.Util.mPlus;
import static org.eventb.pp.Util.mTimes;
import static org.eventb.pp.Util.mUnaryMinus;

import java.util.Stack;

import junit.framework.TestCase;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.NormalizedFormula;
import org.eventb.internal.pp.loader.predicate.TermBuilder;
import org.eventb.pp.Util;


public class TestTermBuilder extends TestCase {

	private static class TestPair {
		// for the readability of the test, use of string
		Expression expression;
		TermSignature expected;
		
		TestPair (String expression, TermSignature expected) {
			this.expression = Util.parseExpression(expression);
			this.expected = expected;
		}
		
		TestPair (Expression expression, TermSignature expected) {
			this.expression = expression;
			this.expected = expected;
		}
		
	}
	
//	private static BoundIdentifier b0 = mBoundIdentifier(0);
	
	private static TermSignature a = mConstant("a");
	private static TermSignature b = mConstant("b");
	private static TermSignature c = mConstant("c");
	private static TermSignature one = mInteger(1);
	
//	private static TermSignature var0 = mVariable(0,0);
	
	TestPair[] tests = new TestPair[]{
			new TestPair("a",
					a
			),
			new TestPair("−a",
					mUnaryMinus(a)
			),
			new TestPair("1",
					one
			),
			new TestPair("a + 1",
					mPlus(a, one)
			),
			new TestPair("a + b + 1",
					mPlus(a, b, one)
			),
			new TestPair("a ∗ b ∗ 1",
					mTimes(a, b, one)
			),
			new TestPair("a − b",
					mMinus(a, b)
			),
			new TestPair("(a − b) − c",
					mMinus(mMinus(a, b), c)
			),
			new TestPair("a ÷ b",
					mDivide(a, b)
			),
			new TestPair("(a ÷ b) ÷ c",
					mDivide(mDivide(a, b),c)
			),
			new TestPair("a ^ b",
					mExpn(a, b)
			),
			new TestPair("(a ^ b) ^ c",
					mExpn(mExpn(a, b), c)
			),
			new TestPair("a mod b",
					mMod(a, b)
			),
			new TestPair("(a mod b) mod c",
					mMod(mMod(a, b), c)
			),
			new TestPair("a + 1",
					mPlus(a, one)
			),
			new TestPair("(a ÷ b) ∗ c",
					mTimes(mDivide(a, b),c)
			),
			new TestPair("(a − b) + c",
					mPlus(mMinus(a, b), c)
			),
			new TestPair("(a ∗ b) ÷ c",
					mDivide(mTimes(a, b),c)
			),
			new TestPair("(a + b) − c",
					mMinus(mPlus(a, b), c)
			),
//			new TestPair(b0,
//					var0
//			),
			
//			"x ∈ S",
//			"x ↦ y ∈ S",
//			"x + 1 ∈ S",
//			"x + 1 ↦ y ∈ S"
	};
	
	
	public void doTest(Expression input, TermSignature expected) {
		// we assume that the string is a relational predicate and more
		// precisely Formula.IN
		Stack<NormalizedFormula> result = new Stack<NormalizedFormula>();
		result.push(new NormalizedFormula(null,0,0,0,new BoundIdentDecl[0],null,false));
		TermBuilder builder = new TermBuilder(result);
		TermSignature actual = builder.buildTerm(input);
		
		assertEquals(expected, actual);
		assertEquals(expected.hashCode(), actual.hashCode());
	}

	public void testTerms() {
		for (TestPair test : tests) {
			doTest(test.expression, test.expected);
		}
	}
	
}
