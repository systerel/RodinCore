/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mId;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mPrj1;
import static org.eventb.core.ast.tests.FastFactory.mPrj2;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;

/**
 * Unit tests to check that the specialization for formulas.
 * 
 * @author Thoams Muller
 */
public class TestFormulaSpecialization extends AbstractTests {

	private static final GivenType S = ff.makeGivenType("S");
	private static final GivenType T = ff.makeGivenType("T");
	private static final Type POW_S = POW(S);
	private static final Type Z = ff.makeIntegerType();

	private ITypeEnvironment te;
	private ISpecialization spec;

	@Override
	protected void setUp() throws Exception {
		this.spec = ff.makeSpecialization();
		this.te = ff.makeTypeEnvironment();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		this.te = null;
		this.spec = null;
		super.tearDown();
	}

	/**
	 * Ensures that an exception is thrown on assignment specialization, as it
	 * is currently unsupported.
	 * 
	 * TODO : modify this test when the specialization on assignment will be
	 * implemented
	 */
	public void testAssignmentSpecialization() {
		try {
			final Assignment assign = parseAssignment("a ≔ a + 1");
			final FreeIdentifier a = mFreeIdentifier("a", ff.makeIntegerType());
			te.add(a);
			assign.typeCheck(te);
			assertTrue(assign.isTypeChecked());
			spec.put(a, mFreeIdentifier("b", a.getType()));
			assign.specialize(spec);
			fail("Should have thrown an unsupported operation error");
		} catch (UnsupportedOperationException e) {
			// pass
		}
	}

	/**
	 * Ensures that an associative expression with two children gets
	 * specialized.
	 */

	public void testAssociativeExpression() {
		final Expression exp = parseExpression("a ∪ b");
		assertFalse(exp.isTypeChecked());
		final List<FreeIdentifier> originals = getFreeIdentsOfAType(POW_S, "a",
				"b");
		te.add(originals.get(0));
		te.add(originals.get(1));
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		spec.put(S, Z);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		specialized.typeCheck(te.specialize(spec));
		final FreeIdentifier[] specFreeIdents = specialized
				.getFreeIdentifiers();
		final List<FreeIdentifier> expected = getFreeIdentsOfAType(POW(Z), "a",
				"b");
		assertTrue(expected.size() == specFreeIdents.length);
		assertTrue(expected.containsAll(Arrays.asList(specFreeIdents)));
		assertTrue(specialized.isTypeChecked());
	}

	/**
	 * Ensures that an associative expression can have two children and only one
	 * gets specialized.
	 */
	public void testAssociativeExpression2() {
		final Expression exp = parseExpression("a ∪ b");
		assertFalse(exp.isTypeChecked());
		final FreeIdentifier a = mFreeIdentifier("a", POW(S));
		final FreeIdentifier b = mFreeIdentifier("b", POW(S));
		te.add(a);
		te.add(b);
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		spec.put(S, Z);
		spec.put(a, mFreeIdentifier("c", POW(Z)));
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		specialized.typeCheck(te.specialize(spec));
		final FreeIdentifier[] specFreeIdents = specialized
				.getFreeIdentifiers();
		final List<FreeIdentifier> expected = getFreeIdentsOfAType(POW(Z), "c",
				"b");
		assertTrue(expected.size() == specFreeIdents.length);
		assertTrue(expected.containsAll(Arrays.asList(specFreeIdents)));
		assertTrue(specialized.isTypeChecked());
	}

	/**
	 * Ensures that an associative expression with three children gets
	 * specialized.
	 */
	public void testAssociativeExpression3() {
		final Expression exp = parseExpression("{a} ∪ {b} ∪ {c}");
		assertFalse(exp.isTypeChecked());
		final List<FreeIdentifier> originals = getFreeIdentsOfAType(S, "a",
				"b", "c");
		addToTypeEnvironment(originals);
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		final List<FreeIdentifier> replacements = getFreeIdentsOfAType(Z, "e",
				"f", "g");
		spec.put(S, Z);
		registerIdentReplacements(originals, replacements);

		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final List<FreeIdentifier> freeIdents = Arrays.asList(specialized
				.getFreeIdentifiers());
		final List<FreeIdentifier> expectedFreeIdents = new ArrayList<FreeIdentifier>();
		expectedFreeIdents.addAll(replacements);
		assertTrue(freeIdents.size() == expectedFreeIdents.size());
		assertTrue(freeIdents.containsAll(expectedFreeIdents));
		specialized.typeCheck(te.specialize(spec));
		assertTrue(specialized.isTypeChecked());
	}

	private List<FreeIdentifier> getFreeIdentsOfAType(Type type,
			String... names) {
		final List<FreeIdentifier> identifiers = new ArrayList<FreeIdentifier>();
		for (String name : names) {
			final FreeIdentifier ident = ff
					.makeFreeIdentifier(name, null, type);
			identifiers.add(ident);
		}
		return identifiers;
	}

	private void addToTypeEnvironment(List<FreeIdentifier> freeIdentifiers) {
		for (FreeIdentifier ident : freeIdentifiers)
			te.add(ident);
	}
	
	private void addToTypeEnvironment(FreeIdentifier... freeIdentifiers) {
		for (FreeIdentifier ident : freeIdentifiers)
			te.add(ident);
	}

	// TODO test simple predicate
	
	// TODO test unary expression
	
	// TODO test unary predicate
	

	/**
	 * Ensures that an associative predicate with two children gets specialized.
	 */
	public void testAssociativePredicate() {
		final Predicate pred = parsePredicate("s ∈ S ∧ t ∈ T");
		final FreeIdentifier s = mFreeIdentifier("s", S);
		final FreeIdentifier t = mFreeIdentifier("t", T);
		te.add(s);
		te.add(t);
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		final Type yType = ff.makeGivenType("Y");
		final Type zType = ff.makeGivenType("Z");
		spec.put(S, yType);
		spec.put(T, zType);
		final FreeIdentifier y = mFreeIdentifier("y", yType);
		spec.put(s, y);
		final FreeIdentifier z = mFreeIdentifier("z", zType);
		spec.put(t, z);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		specialized.typeCheck(te.specialize(spec));
		assertTrue(specialized.isTypeChecked());
		final Set<GivenType> specializedGTypes = specialized.getGivenTypes();
		assertFalse(specializedGTypes.contains(S));
		assertFalse(specializedGTypes.contains(T));
		final List<FreeIdentifier> freeIdents = Arrays.asList(specialized
				.getFreeIdentifiers());
		final List<Expression> expectedFreeIdents = new ArrayList<Expression>();
		expectedFreeIdents.add(y);
		expectedFreeIdents.add(z);
		expectedFreeIdents.add(yType.toExpression(ff));
		expectedFreeIdents.add(zType.toExpression(ff));
		assertTrue(freeIdents.size() == expectedFreeIdents.size());
		assertTrue(freeIdents.containsAll(expectedFreeIdents));
	}

	/**
	 * Ensures that an associative predicate with three children gets
	 * specialized.
	 */
	public void testAssociativePredicate2() {
		final Predicate pred = parsePredicate("card(t)>0 ∧ t ⊆ T ∧ t ≠ ∅");
		final FreeIdentifier t = mFreeIdentifier("t", POW(T));
		te.add(t);
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		spec.put(T, Z);
		spec.put(t, ff.makeSetExtension(
				ff.makeIntegerLiteral(BigInteger.ONE, null), null));
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		specialized.typeCheck(te.specialize(spec));
		final Set<GivenType> specializedGTypes = specialized.getGivenTypes();
		assertFalse(specializedGTypes.contains(T));
		final List<FreeIdentifier> freeIdents = Arrays.asList(specialized
				.getFreeIdentifiers());
		assertTrue(freeIdents.size() == 0);
	}

	/**
	 * Ensures that an atomic empty set expression gets specialized.
	 */
	public void testAtomicExpressionEmptySet() {
		final Expression exp = mEmptySet(POW(S));
		te.addGivenSet("S");
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		spec.put(S, T);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(POW(T), specialized.getType());
	}

	/**
	 * Ensures that an id atomic expression gets specialized.
	 */
	public void testAtomicExpressionId() {
		final Expression exp = mId(POW(PROD(S, S)));
		te.addGivenSet("S");
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		spec.put(S, T);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(POW(PROD(T, T)), specialized.getType());
	}

	/**
	 * Ensures that an prj1 atomic expression gets specialized.
	 */
	public void testAtomicExpressionPrj1() {
		final Expression exp = mPrj1(POW(PROD(PROD(S, T), S)));
		te.addGivenSet("S");
		te.addGivenSet("T");
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		spec.put(S, Z);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(POW(PROD(PROD(Z, T), Z)), specialized.getType());
	}

	/**
	 * Ensures that an prj2 atomic expression gets specialized.
	 */
	public void testAtomicExpressionPrj2() {
		final Expression exp = mPrj2(POW(PROD(PROD(S, T), T)));
		te.addGivenSet("S");
		te.addGivenSet("T");
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		spec.put(T, Z);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(POW(PROD(PROD(S, Z), Z)), specialized.getType());
	}

	/**
	 * Ensures a binary expression can be specialized on the left, and only.
	 */
	public void testBinaryExpressionLeft() {
//		assertSpecialisation(mTypeEnvironment("a", "S", "b", "T"),
//				"a ,, b",
//				mSpec("S", "U"));
		
		final Expression exp = FastFactory.mBinaryExpression(Formula.MAPSTO,
				mFreeIdentifier("a", S), mFreeIdentifier("b", T));
		te.addGivenSet("S");
		te.addGivenSet("T");
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		final GivenType U = ff.makeGivenType("U");
		spec.put(S, U);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(PROD(U, T), specialized.getType());
	}

	/**
	 * Ensures a binary expression can be specialized on the right, and only.
	 */
	public void testBinaryExpressionRight() {
		final FreeIdentifier a = mFreeIdentifier("a", S);
		final FreeIdentifier b = mFreeIdentifier("b", T);
		final Expression exp = FastFactory.mBinaryExpression(Formula.MAPSTO, a,
				b);
		addToTypeEnvironment(a, b);
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		final GivenType U = ff.makeGivenType("U");
		spec.put(T, U);
		final FreeIdentifier c = mFreeIdentifier("c", U);
		spec.put(b, c);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(PROD(S, U), specialized.getType());
		assertEquals(c, ((BinaryExpression) specialized).getRight());
	}

	/**
	 * Ensures that both left and right sides of a binary expression can be
	 * specialized.
	 */
	public void testBinaryExpressionBoth() {
		final FreeIdentifier a = mFreeIdentifier("a", S);
		final FreeIdentifier b = mFreeIdentifier("b", T);
		final Expression exp = FastFactory.mBinaryExpression(Formula.MAPSTO, a,
				b);
		te.add(a);
		te.add(b);
		exp.typeCheck(te);
		assertTrue(exp.isTypeChecked());
		final GivenType U = ff.makeGivenType("U");
		spec.put(S, U);
		spec.put(T, Z);
		final FreeIdentifier c = mFreeIdentifier("c", U);
		final FreeIdentifier d = mFreeIdentifier("d", Z);
		spec.put(a, c);
		spec.put(b, d);
		final Expression specialized = exp.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(PROD(U, Z), specialized.getType());
		assertEquals(c, ((BinaryExpression) specialized).getLeft());
		assertEquals(d, ((BinaryExpression) specialized).getRight());
	}

	/**
	 * Ensures that the left side of a binary predicate can be specialized.
	 */
	public void testBinaryPredicateSpecializationLeft() {
		final Predicate pred = parsePredicate("x ∈ S ⇒ y ∈ T");
		final FreeIdentifier x = mFreeIdentifier("x", S);
		final FreeIdentifier y = mFreeIdentifier("y", T);
		te.add(x);
		te.add(y);
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		final GivenType U = ff.makeGivenType("U");
		spec.put(S, U);
		final FreeIdentifier c = mFreeIdentifier("c", U);
		spec.put(x, c);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(
				FastFactory.mRelationalPredicate(Formula.IN, c,
						U.toExpression(ff)),
				((BinaryPredicate) specialized).getLeft());
		assertEquals(
				FastFactory.mRelationalPredicate(Formula.IN, y,
						T.toExpression(ff)),
				((BinaryPredicate) specialized).getRight());
	}

	/**
	 * Ensures that the right side of a binary predicate can be specialized.
	 */
	public void testBinaryPredicateSpecializationRight() {
		final Predicate pred = parsePredicate("x ∈ S ⇒ y ∈ T");
		final FreeIdentifier x = mFreeIdentifier("x", S);
		final FreeIdentifier y = mFreeIdentifier("y", T);
		te.add(x);
		te.add(y);
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		final GivenType U = ff.makeGivenType("U");
		spec.put(T, U);
		final FreeIdentifier c = mFreeIdentifier("c", U);
		spec.put(y, c);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(
				FastFactory.mRelationalPredicate(Formula.IN, x,
						S.toExpression(ff)),
				((BinaryPredicate) specialized).getLeft());
		assertEquals(
				FastFactory.mRelationalPredicate(Formula.IN, c,
						U.toExpression(ff)),
				((BinaryPredicate) specialized).getRight());
	}

	/**
	 * Ensures that both left and right side of a binary predicate can be
	 * specialized.
	 */
	public void testBinaryPredicateSpecializationBoth() {
		final Predicate pred = parsePredicate("x ∈ S  ⇔ y ∈ T");
		final FreeIdentifier x = mFreeIdentifier("x", S);
		final FreeIdentifier y = mFreeIdentifier("y", T);
		addToTypeEnvironment(x, y);
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		final GivenType U = ff.makeGivenType("U");
		final GivenType V = ff.makeGivenType("V");
		spec.put(S, U);
		spec.put(T, V);
		final FreeIdentifier c = mFreeIdentifier("c", U);
		final FreeIdentifier d = mFreeIdentifier("d", V);
		spec.put(x, c);
		spec.put(y, d);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTypeEnv = te.specialize(spec);
		specialized.typeCheck(specializedTypeEnv);
		assertTrue(specialized.isTypeChecked());
		assertEquals(mRelationalPredicate(IN, c, U.toExpression(ff)),
				((BinaryPredicate) specialized).getLeft());
		assertEquals(mRelationalPredicate(IN, d, V.toExpression(ff)),
				((BinaryPredicate) specialized).getRight());
	}

	/**
	 * Ensures that a boolean expression gets specialized.
	 */
	public void testBooleanExpressionSpecialization() {
		final Expression expr = parseExpression("bool(x ⊆ y)");
		te.addGivenSet("S");
		final FreeIdentifier x = mFreeIdentifier("x", POW_S);
		final FreeIdentifier y = mFreeIdentifier("y", POW_S);
		addToTypeEnvironment(x, y);
		expr.typeCheck(te);
		assertTrue(expr.isTypeChecked());
		spec.put(S, T);
		final FreeIdentifier z = mFreeIdentifier("z", POW(T));
		spec.put(x, z);
		final Expression specialized = expr.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTe = te.specialize(spec);
		specialized.typeCheck(specializedTe);
		assertTrue(specialized.isTypeChecked());
		final BoolExpression expected = mBoolExpression(mRelationalPredicate(
				SUBSETEQ, mFreeIdentifier("z", POW(T)),
				mFreeIdentifier("y", POW(T))));
		expected.typeCheck(specializedTe);
		assertTrue(expected.isTypeChecked());
		assertEquals(expected, specialized);
		assertEquals(POW(T), z.getType());
	}

	/**
	 * Ensures that the types of bound identifier declarations get specialized.
	 */
	public void testBoundIdentDeclSpecialization() {
		final BoundIdentDecl decl = FastFactory.mBoundIdentDecl("a", S);
		te.addGivenSet("S");
		decl.typeCheck(te);
		assertTrue(decl.isTypeChecked());
		spec.put(S, T);
		final BoundIdentDecl specialized = decl.specialize(spec);
		final ITypeEnvironment specializedTe = te.specialize(spec);
		specialized.typeCheck(specializedTe);
		assertTrue(specialized.isTypeChecked());
		assertEquals(T, specialized.getType());
	}

	/**
	 * Ensures that the type of a bound identifier gets specialized.
	 */
	public void testBoundIdentSpecialization() {
		final BoundIdentifier ident = FastFactory.mBoundIdentifier(0, S);
		spec.put(S, T);
		final Expression specialized = ident.specialize(spec);
		assertEquals(T, specialized.getType());
	}

	// TODO test extended predicate

	// TODO test extended expression

	/**
	 * Ensures that a free identifier get specialized.
	 */
	public void testFreeIdentifierSpecialization() {
		final FreeIdentifier ident = FastFactory.mFreeIdentifier("s", S);
		final FreeIdentifier t = mFreeIdentifier("t", T);
		te.add(ident);
		ident.typeCheck(te);
		assertTrue(ident.isTypeChecked());
		spec.put(S, T);
		spec.put(ident, t);
		final Expression specialized = ident.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTe = te.specialize(spec);
		specialized.typeCheck(specializedTe);
		assertTrue(specialized.isTypeChecked());
		assertEquals(t, specialized);
	}

	/**
	 * Ensures that an integer literal is not modified by specialization.
	 */
	public void testIntegerLiteralSpecialization() {
		final IntegerLiteral t = FastFactory.mIntegerLiteral(100);
		spec.put(S, T);
		final Expression specialized = t.specialize(spec);
		assertSame(t, specialized);
	}

	/**
	 * Ensures that an literal predicate is not modified by specialization.
	 */
	public void testLiteralPredicateSpecialization() {
		final LiteralPredicate t = FastFactory.mLiteralPredicate(Formula.BTRUE);
		spec.put(T, ff.makeBooleanType());
		final Predicate specialized = t.specialize(spec);
		assertSame(t, specialized);
	}

	/**
	 * Ensures that one expression of a multiple predicate could get
	 * specialized, and that the other are preserved.
	 */
	public void testMultiplePredicateSpecializationOne() {
		final FreeIdentifier s = mFreeIdentifier("s", POW_S);
		final FreeIdentifier t = mFreeIdentifier("t", POW_S);
		final FreeIdentifier u = mFreeIdentifier("u", POW_S);
		final MultiplePredicate pred = mMultiplePredicate(s, t, u);
		addToTypeEnvironment(s, t, u);
		final FreeIdentifier x = mFreeIdentifier("x", POW_S);
		spec.put(s, x);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTe = te.specialize(spec);
		specialized.typeCheck(specializedTe);
		assertTrue(specialized.isTypeChecked());
		assertEquals(mMultiplePredicate(x, t, u), specialized);
	}

	/**
	 * Ensures that on expression of a multiple predicate gets specialized, and
	 * that the other are preserved.
	 */
	public void testMultiplePredicateSpecializationAll() {
		final FreeIdentifier s = mFreeIdentifier("s", POW_S);
		final FreeIdentifier t = mFreeIdentifier("t", POW_S);
		final FreeIdentifier u = mFreeIdentifier("u", POW_S);
		final MultiplePredicate pred = mMultiplePredicate(s, t, u);
		addToTypeEnvironment(s, t, u);
		final FreeIdentifier x = mFreeIdentifier("x", POW(T));
		final FreeIdentifier y = mFreeIdentifier("y", POW(T));
		final FreeIdentifier z = mFreeIdentifier("z", POW(T));
		spec.put(S, T);
		spec.put(s, x);
		spec.put(t, y);
		spec.put(u, z);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTe = te.specialize(spec);
		specialized.typeCheck(specializedTe);
		assertTrue(specialized.isTypeChecked());
		assertEquals(mMultiplePredicate(x, y, z), specialized);
	}
	
	
	/**
	 * Ensures that given type S is specialized when used implicitly in a
	 * quantified expression and that its replacement type is successfully
	 * recursively appearing. The formula bares its typing environment.
	 */
	public void testTEWithASpecifiedImplicitGivenType() {
		final BoundIdentDecl xdecl = ff.makeBoundIdentDecl("x", null, S);
		final BoundIdentifier xbound = ff.makeBoundIdentifier(0, null, S);
		final QuantifiedExpression qexpr = ff.makeQuantifiedExpression(
				Formula.CSET,
				new BoundIdentDecl[] { xdecl },
				ff.makeRelationalPredicate(Formula.IN, xbound,
						S.toExpression(ff), null), xbound, null,
				QuantifiedExpression.Form.Implicit);
		spec.put(S, T);
		final QuantifiedExpression specialized = (QuantifiedExpression) qexpr
				.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		// Type check with an empty type environment
		specialized.typeCheck(ff.makeTypeEnvironment());
		assertTrue(specialized.isTypeChecked());
		assertEquals(T,
				specialized.getExpression().getBoundIdentifiers()[0].getType());
		assertEquals(T, specialized.getBoundIdentDecls()[0].getType());
		assertEquals(T, specialized.getExpression().getType());
		assertEquals(POW(T), specialized.getType());
	}

	/**
	 * Ensures that one quantified expression gets specilized
	 */
	public void testQuantifiedExpression() {
		final Expression expr = parseExpression("{x ∣ x ∈ S}");
		te.addGivenSet("S");
		expr.typeCheck(te);
		spec.put(S, T);
		final Expression specialized = expr.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment newTe = te.specialize(spec);
		final ITypeCheckResult result = specialized.typeCheck(newTe);
		assertFalse(result.hasProblem());
		assertTrue(result.getInferredEnvironment().isEmpty());

		final QuantifiedExpression expected = (QuantifiedExpression) parseExpression("{x ∣ x ∈ T}");
		// TODO check with Laurent if this is normal.
		// newTe.addGivenSet("T");
		expected.typeCheck(newTe);
		assertTrue(expected.isTypeChecked());
		assertEquals(expected, specialized);
	}

	/**
	 * Ensures that a quantified predicated remains unchanged if bound
	 * identifier declaration have the same name as the specialized free
	 * identifiers.
	 */
	public void testQuantifiedPredicateSpecialization() {
		final QuantifiedPredicate pred = (QuantifiedPredicate) parsePredicate("∀x,y·x ∈ ℕ ∧ y ∈ ℕ ⇒ x + y ∈ ℕ");
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		final FreeIdentifier x = mFreeIdentifier("x", Z);
		final FreeIdentifier t = mFreeIdentifier("t", Z);
		spec.put(x, t);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		assertEquals(pred, specialized);
	}

	/**
	 * Ensures that a quantified predicated remains unchanged if bound
	 * identifier declaration have the same name as the specialized free
	 * identifiers.
	 */
	public void testQuantifiedPredicateTypeSpecialization() {
		final QuantifiedPredicate pred = (QuantifiedPredicate) parsePredicate("∀x,y·x ∈ ℙ(T) ∧ y ∈ ℙ(T) ⇒ x ∪ y ∈ ℙ(T)");
		te.addGivenSet("T");
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		final FreeIdentifier x = mFreeIdentifier("x", POW(T));
		final FreeIdentifier t = mFreeIdentifier("t", POW(Z));
		spec.put(T, Z);
		spec.put(x, t);
		final Predicate specialized = pred.specialize(spec);
		final ITypeEnvironment specializedTe = te.specialize(spec);
		specialized.typeCheck(specializedTe);
		assertTrue(specialized.isTypeChecked());
		final QuantifiedPredicate expected = (QuantifiedPredicate) parsePredicate("∀x,y·x ∈ ℙ(ℤ) ∧ y ∈ ℙ(ℤ) ⇒ x ∪ y ∈ ℙ(ℤ)");
		expected.typeCheck(specializedTe);
		assertTrue(expected.isTypeChecked());
		assertEquals(expected, specialized);
	}

	/**
	 * Ensures that the left part of a relational predicate gets specialized.
	 */
	public void testRelationalPredicateSpecialization() {
		final Predicate relPred = parsePredicate("s ∈ S");
		te.addGivenSet("S");
		te.add(mFreeIdentifier("s", S));
		relPred.typeCheck(te);
		assertTrue(relPred.isTypeChecked());
		spec.put(S, T);
		final Predicate specPred = relPred.specialize(spec);
		assertTrue(specPred.isTypeChecked());
		specPred.typeCheck(te.specialize(spec));
		assertTrue(specPred.isTypeChecked());
	}

	/**
	 * Ensures that a set in extension expression gets recursively specialized.
	 */
	public void testSetExtensionSpecialization() {
		final FreeIdentifier s = mFreeIdentifier("s", S);
		final FreeIdentifier t = mFreeIdentifier("t", S);
		final FreeIdentifier u = mFreeIdentifier("u", S);
		final Expression expr = ff.makeSetExtension(
				new Expression[] { s, t, u }, null);
		te.addGivenSet("S");
		expr.typeCheck(te);
		assertTrue(expr.isTypeChecked());
		assertEquals(POW(S), expr.getType());
		spec.put(S, T);
		final FreeIdentifier x = mFreeIdentifier("x", T);
		spec.put(s, x);
		final FreeIdentifier y = mFreeIdentifier("y", T);
		spec.put(t, y);
		final SetExtension specialized = (SetExtension) expr.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment specializedTe = te.specialize(spec);
		specialized.typeCheck(specializedTe);
		assertTrue(specialized.isTypeChecked());
		final Expression[] members = specialized.getMembers();
		final Expression ut = mFreeIdentifier("u", T);
		assertTrue(members.length == 3);
		assertEquals(members[0], x);
		assertEquals(members[1], y);
		assertEquals(members[2], ut);
	}
	
	public void testSimplePredicate() {
		final SimplePredicate pred = (SimplePredicate) parsePredicate("finite(s)");
		final FreeIdentifier s = mFreeIdentifier("s", POW(S));
		te.add(s);
		pred.typeCheck(te);
		assertTrue(pred.isTypeChecked());
		spec.put(S, T);
		final Predicate specialized = pred.specialize(spec);
		assertTrue(specialized.isTypeChecked());
		specialized.typeCheck(te.specialize(spec));
	}
	
	private void registerIdentReplacements(List<FreeIdentifier> toReplace,
			List<FreeIdentifier> replacements) {
		assert (toReplace.size() == replacements.size());
		for (int i = 0; i < toReplace.size(); i++) {
			spec.put(toReplace.get(i), replacements.get(i));
		}
	}

	private static Type PROD(Type source, Type target) {
		return ff.makeProductType(source, target);
	}

}
