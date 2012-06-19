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

import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.tests.FastFactory.addToTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mSpec;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.TestGenParser.DIRECT_PRODUCT;

import java.util.Map;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.internal.core.ast.Specialization;

/**
 * Unit tests to check that the specialization for formulas.
 * 
 * @author Thomas Muller
 */
public class TestFormulaSpecialization extends AbstractTests {

	private static final GivenType S = ff.makeGivenType("S");
	private static final GivenType T = ff.makeGivenType("T");

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
		assertExpressionSpecialization(
				mTypeEnvironment("a", "ℙ(S)", "b", "ℙ(S)"), //
				"a ∪ b", //
				mSpec(te, mList("S", "ℤ")));
	}

	/**
	 * Ensures that an associative expression can have two children and only one
	 * gets specialized.
	 */
	public void testAssociativeExpression2() {
		te = mTypeEnvironment("a", "ℙ(S)", "b", "ℙ(S)");
		assertExpressionSpecialization(te, //
				"a ∪ b", //
				mSpec(te,//
						mList("S", "ℤ"), //
						mList("a", "ℙ(S)", "c", "ℙ(ℤ)")));
	}

	/**
	 * Ensures that an associative expression with three children gets
	 * specialized.
	 */
	public void testAssociativeExpression3() {
		te = mTypeEnvironment("a", "S", "b", "S", "c", "S");
		assertExpressionSpecialization(te, //
				"{a} ∪ {b} ∪ {c}", //
				mSpec(te,//
						mList("S", "ℤ"), //
						mList("a", "S", "e", "ℤ", //
								"b", "S", "f", "ℤ",//
								"c", "S", "g", "ℤ")));
	}
	
	/**
	 * Ensures that an unary expression with one child gets specialized.
	 */
	public void testUnaryExpression() {
		te = mTypeEnvironment("j", "S", "h", "S", "i", "S");
		assertExpressionSpecialization(te, //
				"card({j, h, i})", //
				mSpec(te,//
						mList("S", "ℤ"), //
						mList("j", "S", "e", "ℤ", //
							  "h", "S", "f", "ℤ",//
							  "i", "S", "g", "ℤ")));
	}
	

	/**
	 * Ensures that an unary predicate with two children gets specialized.
	 */
	public void testUnaryPredicate() {
		te = mTypeEnvironment("s", "S");
		assertPredicateSpecialization(te, //
				"¬(s ∈ S)", //
				mSpec(te,//
						mList("S", "Y"), //
						mList("s", "S", "y", "Y")));
	}
	
	/**
	 * Ensures that an associative predicate with two children gets specialized.
	 */
	public void testAssociativePredicate() {
		te = mTypeEnvironment("s", "S", "t", "T");
		assertPredicateSpecialization(te, //
				"s ∈ S ∧ t ∈ T", //
				mSpec(te,//
						mList("S", "Y", //
								"T", "Z"), //
						mList("s", "S", "y", "Y", //
								"t", "T", "z", "Z")));
	}

	/**
	 * Ensures that an associative predicate with three children gets
	 * specialized.
	 */
	public void testAssociativePredicate2() {
		te = mTypeEnvironment("T", "ℙ(T)");
		assertPredicateSpecialization(te, //
				"card(t)>0 ∧ t ⊆ T ∧ t ≠ ∅", //
				mSpec(te,//
						mList("T", "ℤ"), //
						mList("t", "ℙ(T)", "{1}", "ℙ(ℤ)")));
	}

	/**
	 * Ensures that an atomic empty set expression gets specialized.
	 */
	public void testAtomicExpressionEmptySet() {
		assertExpressionSpecialization(ff.makeTypeEnvironment(), //
				"∅⦂ℙ(S)", //
				mSpec(te, mList("S", "T")));
	}

	/**
	 * Ensures that an id atomic expression gets specialized.
	 */
	public void testAtomicExpressionId() {
		te = mTypeEnvironment("S", "ℙ(S)");
		assertExpressionSpecialization(te, //
				"id(ℙ(S×S))", //
				mSpec(te, mList("S", "T")));
	}

	/**
	 * Ensures that an prj1 atomic expression gets specialized.
	 */
	public void testAtomicExpressionPrj1() {
		te = mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)");
		assertExpressionSpecialization(te, //
				"(S×T×S)◁ prj1", //
				mSpec(te, mList("S", "ℤ")));
	}

	/**
	 * Ensures that an prj2 atomic expression gets specialized.
	 */
	public void testAtomicExpressionPrj2() {
		te = mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)");
		assertExpressionSpecialization(te, //
				"(S×T×S)◁ prj2", //
				mSpec(te, mList("S", "ℤ")));
	}

	/**
	 * Ensures a binary expression can be specialized on the left, and only.
	 */
	public void testBinaryExpressionLeft() {
		te = mTypeEnvironment("a", "S", "b", "T");
		assertExpressionSpecialization(te, //
				"a ↦ b", //
				mSpec(te, mList("S", "U")));
	}

	/**
	 * Ensures a binary expression can be specialized on the right, and only.
	 */
	public void testBinaryExpressionRight() {
		te = mTypeEnvironment("a", "S", "b", "T");
		assertExpressionSpecialization(te, //
				"a ↦ b", //
				mSpec(te, mList("T", "U"), mList("b", "T", "c", "U")));
	}

	/**
	 * Ensures that both left and right sides of a binary expression can be
	 * specialized.
	 */
	public void testBinaryExpressionBoth() {
		te = mTypeEnvironment("a", "S", "b", "T");
		assertExpressionSpecialization(te, //
				"a ↦ b", //
				mSpec(te, mList("S", "U", "T", "V"),//
						mList("a", "S", "c", "U",//
								"b", "T", "d", "V")));
	}

	/**
	 * Ensures that the left side of a binary predicate can be specialized.
	 */
	public void testBinaryPredicateSpecializationLeft() {
		te = mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "x", "S", "y", "T");
		assertPredicateSpecialization(te, //
				"x ∈ S ⇒ y ∈ T", //
				mSpec(te, mList("S", "U"), mList("x", "S", "c", "U")));
	}

	/**
	 * Ensures that the right side of a binary predicate can be specialized.
	 */
	public void testBinaryPredicateSpecializationRight() {
		te = mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "x", "S", "y", "T");
		assertPredicateSpecialization(te, //
				"x ∈ S ⇒ y ∈ T", //
				mSpec(te, mList("T", "U"), mList("y", "T", "c", "U")));
	}

	/**
	 * Ensures that both left and right side of a binary predicate can be
	 * specialized.
	 */
	public void testBinaryPredicateSpecializationBoth() {
		te = mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "x", "S", "y", "T");
		assertPredicateSpecialization(te, //
				"x ∈ S  ⇔ y ∈ T", //
				mSpec(te, mList("S", "U", "T", "V"), //
						mList("x", "S", "c", "U", //
								"y", "T", "d", "V")));
	}

	/**
	 * Ensures that a boolean expression gets specialized.
	 */
	public void testBooleanExpressionSpecialization() {
		te = mTypeEnvironment("S", "ℙ(S)", "x", "ℙ(S)", "y", "ℙ(S)");
		assertExpressionSpecialization(te, //
				"bool(x ⊆ y)", //
				mSpec(te, mList("S", "T"), //
						mList("x", "ℙ(S)", "z", "ℙ(T)")));
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
		final ITypeCheckResult tcResult = specialized.typeCheck(specializedTe);
		assertTrue(tcResult.isSuccess());
		assertTrue(tcResult.getInferredEnvironment().isEmpty());
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

	/**
	 * Tests that an extended predicate gets specialized.
	 */
	public void testExtendedPredicateSpecialisation() {
		final IPredicateExtension alphaExt = ExtensionHelper
				.getAlphaExtension();
		final FormulaFactory extFac = FormulaFactory.getInstance(alphaExt);
		te = extFac.makeTypeEnvironment();
		FastFactory.addToTypeEnvironment(te, "S", "ℙ(S)", "s", "S");
		assertPredicateSpecialization(extFac, te, "α(s∈S, s)",
				mSpec(te, mList("S", "T"), mList("s", "S", "t", "T")));
	}

	/**
	 * Tests that an extended expression gets specialized.
	 */
	public void testExtendedExpressionSpecialisation() {
		final FormulaFactory extFac = FormulaFactory
				.getInstance(DIRECT_PRODUCT);
		te = extFac.makeTypeEnvironment();
		addToTypeEnvironment(te, "S", "ℙ(S)", "T", "ℙ(T)", "V", "ℙ(V)", "A",
				"ℙ(S×T)", "B", "ℙ(S×V)");
		assertExpressionSpecialization(extFac, te, "A§B",
				mSpec(te, mList("S", "X"), new String[0]));
	}

	/**
	 * Ensures that a free identifier get specialized.
	 */
	public void testFreeIdentifierSpecialization() {
		te = mTypeEnvironment("S", "ℙ(S)", "s", "S");
		assertExpressionSpecialization(te, //
				"s", //
				mSpec(te, mList("S", "T")));
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
		te = mTypeEnvironment("S", "ℙ(S)", "h", "ℙ(S)", "s", "ℙ(S)", "t",
				"ℙ(S)", "u", "ℙ(S)");
		assertPredicateSpecialization(te, //
				"partition(h, s, t, u)", //
				mSpec(te, //
						new String[0], //
						mList("s", "ℙ(S)", "x", "ℙ(S)")));
	}

	/**
	 * Ensures that on expression of a multiple predicate gets specialized, and
	 * that the other are preserved.
	 */
	public void testMultiplePredicateSpecializationAll() {
		te = mTypeEnvironment("S", "ℙ(S)", "h", "ℙ(S)", //
				"s", "ℙ(S)", "t", "ℙ(S)", "u", "ℙ(S)");
		assertPredicateSpecialization(te, //
				"partition(h, s, t, u)", //
				mSpec(te, //
						mList("S", "T"), //
						mList("s", "ℙ(S)", "x", "ℙ(T)", //
								"s", "ℙ(S)", "y", "ℙ(T)", //
								"s", "ℙ(S)", "z", "ℙ(T)" //
						)));
	}

	/**
	 * Ensures that given type S is specialized when used implicitly in a
	 * quantified expression and that its replacement type is successfully
	 * recursively appearing. The formula bares its typing environment.
	 */
	public void testQuantifiedExpressionImplicitGivenType() {
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
		te = mTypeEnvironment("S", "ℙ(S)");
		assertExpressionSpecialization(te, //
				"{x ∣ x ∈ S}", //
				mSpec(te, //
						mList("S", "T"), //
						new String[0]));
	}

	/**
	 * Ensures that a quantified predicated remains unchanged if bound
	 * identifier declaration have the same name as the specialized free
	 * identifiers.
	 */
	public void testQuantifiedPredicateSpecialization() {
		assertPredicateSpecialization(te, //
				"∀x,y·x ∈ ℕ ∧ y ∈ ℕ ⇒ x + y ∈ ℕ", //
				mSpec(te, //
						new String[0], //
						mList("x", "ℤ", "t", "ℤ")));
	}

	/**
	 * Ensures that a quantified predicated remains unchanged if bound
	 * identifier declaration have the same name as the specialized free
	 * identifiers.
	 */
	public void testQuantifiedPredicateTypeSpecialization() {
		te = mTypeEnvironment("T", " ℙ(T)");
		assertPredicateSpecialization(te, //
				"∀x,y·x ∈ ℙ(T) ∧ y ∈ ℙ(T) ⇒ x ∪ y ∈ ℙ(T)", //
				mSpec(te, //
						mList("T", "ℤ"), //
						mList("x", "ℙ(T)", "t", "ℙ(ℤ)")));
	}

	/**
	 * Ensures that the left part of a relational predicate gets specialized.
	 */
	public void testRelationalPredicateSpecialization() {
		te = mTypeEnvironment("S", " ℙ(S)", "s", "S");
		assertPredicateSpecialization(te, //
				"s ∈ S", //
				mSpec(te, //
						mList("S", "T"), //
						new String[0]));
	}

	/**
	 * Ensures that a set in extension expression gets recursively specialized.
	 */
	public void testSetExtensionSpecialization() {
		te = mTypeEnvironment("S", "ℙ(S)", "s", "S", "t", "S", "u", "S");
		assertExpressionSpecialization(te, "{ s, t, u }", //
				mSpec(te, //
						mList("S", "T"), //
						mList("s", "S", "x", "T", //
								"t", "S", "y", "T")));
	}

	public void testSimplePredicate() {
		te = mTypeEnvironment("S", "ℙ(S)", "s", "ℙ(S)");
		assertPredicateSpecialization(te, //
				"finite(s)", //
				mSpec(te, //
						mList("S", "T"), //
						new String[0]));
	}
	
	public static void assertPredicateSpecialization(FormulaFactory fac,
			ITypeEnvironment typeEnv, String predStr,
			ISpecialization specialization) {
		final IParseResult parsed = fac.parsePredicate(predStr,
				LATEST, null);
		assertFalse(parsed.hasProblem());
		assertTrue(parsed.getParsedPredicate() != null);
		final Predicate pred = parsed.getParsedPredicate();
		assertFormulaSpecialization(fac, typeEnv, predStr, pred, specialization);
	}
	
	public static void assertPredicateSpecialization(ITypeEnvironment typeEnv,
			String predStr, ISpecialization specialization) {
		assertPredicateSpecialization(ff, typeEnv, predStr, specialization);
	}
	
	public static void assertExpressionSpecialization(FormulaFactory fac,
			ITypeEnvironment typeEnv, String exprStr,
			ISpecialization specialization) {
		final IParseResult parsed = fac.parseExpression(exprStr, LATEST, null);
		final Expression expr = parsed.getParsedExpression();
		assertFormulaSpecialization(fac, typeEnv, exprStr, expr, specialization);
	}

	public static void assertExpressionSpecialization(ITypeEnvironment typeEnv,
			String exprStr, ISpecialization specialization) {
		assertExpressionSpecialization(ff, typeEnv, exprStr, specialization);
	}
	
	private static void assertFormulaSpecialization(FormulaFactory fac, ITypeEnvironment typeEnv,
			String formulaStr, Formula<?> formula,
			ISpecialization specialization) {
		final ITypeCheckResult typeCheckResult = formula.typeCheck(typeEnv);
		assertTrue(formula.isTypeChecked());
		assertFalse(typeCheckResult.hasProblem());
		final Formula<?> specialized = formula.specialize(specialization);
		assertTrue(specialized.isTypeChecked());
		final ITypeEnvironment newTypeEnv = typeEnv.specialize(specialization);
		final ITypeCheckResult result = specialized.typeCheck(newTypeEnv);
		assertFalse(result.hasProblem());
		assertTrue(result.getInferredEnvironment().isEmpty());
		assertValid(fac, formulaStr, specialized, specialization, newTypeEnv);
	}

	private static void assertValid(FormulaFactory fac, String formulaStr, Formula<?> specialized,
			ISpecialization specialization, ITypeEnvironment specTypeEnv) {
		final String expectedImage = getSpecializedImage(fac,formulaStr,
				specialization);
		assertSpecializedImage(fac, expectedImage, specialized, specTypeEnv);
	}

	private static String getSpecializedImage(FormulaFactory fac, String formulaStr,
			ISpecialization specialization) {
		String expectedImage = formulaStr;
		final Map<GivenType, Type> typeSubst = ((Specialization) specialization)
				.getTypeSubstitutions();
		for (GivenType type : typeSubst.keySet()) {
			expectedImage = expectedImage.replaceAll(type.getName(), typeSubst
					.get(type).toExpression(fac).toString());
		}
		final Map<FreeIdentifier, Expression> idSubst = ((Specialization) specialization)
				.getIndentifierSubstitutions();
		for (FreeIdentifier id : idSubst.keySet()) {
			expectedImage = expectedImage.replaceAll(id.getName(),
					idSubst.get(id).toString());
		}
		return expectedImage;
	}

	private static void assertSpecializedImage(FormulaFactory fac,
			String expectedImg, Formula<?> specialized,
			ITypeEnvironment specTypeEnv) {
		Formula<?> expected = null;
		if (specialized instanceof Expression)
			expected = fac.parseExpression(expectedImg, LATEST, null)
					.getParsedExpression();
		if (specialized instanceof Predicate)
			expected = fac.parsePredicate(expectedImg, LATEST, null)
					.getParsedPredicate();
		assertTrue(expected != null);
		final ITypeCheckResult typeCheck = expected.typeCheck(specTypeEnv);
		assertTrue(typeCheck.isSuccess());
		assertFalse(typeCheck.hasProblem());
		assertEquals(expected, specialized);
	}

}
