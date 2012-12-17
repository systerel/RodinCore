package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.mArray;
import static org.eventb.internal.pp.core.elements.terms.Util.mBoundIdentDecl;
import static org.eventb.internal.pp.core.elements.terms.Util.mBoundIdentifier;
import static org.eventb.internal.pp.core.elements.terms.Util.mConstant;
import static org.eventb.internal.pp.core.elements.terms.Util.mDivide;
import static org.eventb.internal.pp.core.elements.terms.Util.mExpn;
import static org.eventb.internal.pp.core.elements.terms.Util.mInteger;
import static org.eventb.internal.pp.core.elements.terms.Util.mMinus;
import static org.eventb.internal.pp.core.elements.terms.Util.mMod;
import static org.eventb.internal.pp.core.elements.terms.Util.mPlus;
import static org.eventb.internal.pp.core.elements.terms.Util.mTimes;
import static org.eventb.internal.pp.core.elements.terms.Util.mUnaryMinus;
import static org.eventb.internal.pp.core.elements.terms.Util.mVariable;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.TermBuilder;
import org.junit.Assert;
import org.junit.Test;

public class TestTermBuilder extends AbstractPPTest {

	private static final ITypeEnvironmentBuilder typenv = mTypeEnvironment(
			"a=ℤ", ff);

	private static BoundIdentDecl dS = mBoundIdentDecl("s", ty_S);
	private static BoundIdentDecl dT = mBoundIdentDecl("t", ty_T);
	private static BoundIdentDecl dU = mBoundIdentDecl("u", ty_U);
	private static BoundIdentDecl dV = mBoundIdentDecl("v", ty_V);

	private static final TermSignature a = mConstant("a", Sort.NATURAL);
	private static final TermSignature b = mConstant("b");
	private static final TermSignature c = mConstant("c");
	private static final TermSignature one = mInteger(1);

	private static void assertEquals(TermSignature expected,
			TermSignature actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
		Assert.assertEquals(expected.toString(), actual.toString());
	}

	public static void doTest(Expression expr, TermSignature expected,
			BoundIdentDecl... decls) {
		final TermBuilder builder = new TermBuilder(new AbstractContext());
		builder.pushDecls(decls);
		final TermSignature actual = builder.buildTerm(expr);
		builder.popDecls(decls);
		assertEquals(expected, actual);
	}

	public static void doTest(String image, TermSignature expected,
			BoundIdentDecl... decls) {
		final Expression expr = Util.parseExpression(image);
		expr.typeCheck(typenv);
		doTest(expr, expected, decls);
	}

	/**
	 * Ensures that simple terms are build correctly.
	 */
    @Test
	public void testTerms() {
		doTest("a", a);
		doTest("−a", mUnaryMinus(a));
		doTest("1", one);
		doTest("a + 1", mPlus(a, one));
		doTest("a + b + 1", mPlus(a, b, one));
		doTest("a ∗ b ∗ 1", mTimes(a, b, one));
		doTest("a − b", mMinus(a, b));
		doTest("(a − b) − c", mMinus(mMinus(a, b), c));
		doTest("a ÷ b", mDivide(a, b));
		doTest("(a ÷ b) ÷ c", mDivide(mDivide(a, b), c));
		doTest("a ^ b", mExpn(a, b));
		doTest("(a ^ b) ^ c", mExpn(mExpn(a, b), c));
		doTest("a mod b", mMod(a, b));
		doTest("(a mod b) mod c", mMod(mMod(a, b), c));
		doTest("a + 1", mPlus(a, one));
		doTest("(a ÷ b) ∗ c", mTimes(mDivide(a, b), c));
		doTest("(a − b) + c", mPlus(mMinus(a, b), c));
		doTest("(a ∗ b) ÷ c", mDivide(mTimes(a, b), c));
		doTest("(a + b) − c", mMinus(mPlus(a, b), c));

		doTest(mBoundIdentifier(0, ty_S), mVariable(0, 0, S), dS);

		doTest(mBoundIdentifier(0, ty_T), mVariable(1, 1, T), dS, dT);
		doTest(mBoundIdentifier(1, ty_S), mVariable(0, 0, S), dS, dT);

		doTest(mBoundIdentifier(0, ty_U), mVariable(2, 2, U), dS, dT, dU);
		doTest(mBoundIdentifier(1, ty_T), mVariable(1, 1, T), dS, dT, dU);
		doTest(mBoundIdentifier(2, ty_S), mVariable(0, 0, S), dS, dT, dU);
	}

	private void testBoundIdentifier(TermBuilder builder, int idx, Type type,
			TermSignature expected) {
		final BoundIdentifier bi = mBoundIdentifier(idx, type);
		final TermSignature actual = builder.buildTerm(bi);
		assertEquals(expected, actual);
	}

	/**
	 * Ensures that when the top quantifier is changed, variables associated to
	 * bound identifiers are computed correctly.
	 */
    @Test
	public void testChangeTopQuantifier() {
		final TermBuilder builder = new TermBuilder(new AbstractContext());
		final BoundIdentDecl[] decls_1 = mArray(dS);
		final BoundIdentDecl[] decls_2 = mArray(dT);

		builder.pushDecls(decls_1);
		testBoundIdentifier(builder, 0, ty_S, mVariable(0, 0, S));
		builder.popDecls(decls_1);

		builder.pushDecls(decls_1);
		testBoundIdentifier(builder, 0, ty_S, mVariable(1, 0, S));
		builder.popDecls(decls_1);

		builder.pushDecls(decls_2);
		testBoundIdentifier(builder, 0, ty_T, mVariable(2, 0, T));
		builder.popDecls(decls_2);
	}

	/**
	 * Ensures that when a nested quantifier contains several declarations,
	 * variables associated to bound identifiers are computed correctly.
	 */
    @Test
	public void testLongNestedQuantifier() {
		final TermBuilder builder = new TermBuilder(new AbstractContext());
		final BoundIdentDecl[] decls_1 = mArray(dS, dT);
		final BoundIdentDecl[] decls_2 = mArray(dU, dV);

		builder.pushDecls(decls_1);
		testBoundIdentifier(builder, 0, ty_T, mVariable(1, 1, T));
		testBoundIdentifier(builder, 1, ty_S, mVariable(0, 0, S));
		builder.pushDecls(decls_2);
		testBoundIdentifier(builder, 0, ty_V, mVariable(3, 3, V));
		testBoundIdentifier(builder, 1, ty_U, mVariable(2, 2, U));
		testBoundIdentifier(builder, 2, ty_T, mVariable(1, 1, T));
		testBoundIdentifier(builder, 3, ty_S, mVariable(0, 0, S));
		builder.popDecls(decls_2);
		testBoundIdentifier(builder, 0, ty_T, mVariable(1, 1, T));
		testBoundIdentifier(builder, 1, ty_S, mVariable(0, 0, S));
		builder.popDecls(decls_1);
	}

	/**
	 * Ensures that when a nested quantifier is changed, variables associated to
	 * bound identifiers are computed correctly.
	 */
    @Test
	public void testChangeNestedQuantifier() {
		final TermBuilder builder = new TermBuilder(new AbstractContext());
		final BoundIdentDecl[] decls_1 = mArray(dS);
		final BoundIdentDecl[] decls_2 = mArray(dT);
		final BoundIdentDecl[] decls_3 = mArray(dU);

		builder.pushDecls(decls_1);
		testBoundIdentifier(builder, 0, ty_S, mVariable(0, 0, S));
		builder.pushDecls(decls_2);
		testBoundIdentifier(builder, 0, ty_T, mVariable(1, 1, T));
		testBoundIdentifier(builder, 1, ty_S, mVariable(0, 0, S));
		builder.popDecls(decls_2);
		builder.pushDecls(decls_3);
		testBoundIdentifier(builder, 0, ty_U, mVariable(2, 1, U));
		testBoundIdentifier(builder, 1, ty_S, mVariable(0, 0, S));
		builder.popDecls(decls_3);
		testBoundIdentifier(builder, 0, ty_S, mVariable(0, 0, S));
		builder.popDecls(decls_1);
	}

}
