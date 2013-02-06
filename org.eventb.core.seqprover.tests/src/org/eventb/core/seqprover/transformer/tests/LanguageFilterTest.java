/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.BOUND_IDENT_DECL;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KBOOL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KSUCC;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.PREDICATE_VARIABLE;
import static org.eventb.core.ast.Formula.SETEXT;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests.DT_FAC;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.transformer.SimpleSequents.filterLanguage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.internal.core.seqprover.transformer.LanguageFilter;
import org.junit.Test;

/**
 * Acceptance tests for class {@link LanguageFilter}.
 * 
 * Tests are only performed on goal, making the assumption that the
 * implementation does not process it specially.
 * 
 * @author Laurent Voisin
 */
public class LanguageFilterTest extends AbstractTransformerTests {

	private final ITypeEnvironmentBuilder typenv = DT_FAC.makeTypeEnvironment();

	private static void assertTagFiltered(String predImage, int tag) {
		assertNotFiltered(predImage);
		assertFiltered(predImage, tag);
	}

	private static void assertTagFiltered(Predicate pred, int tag) {
		assertNotFiltered(pred);
		assertFiltered(pred, tag);
	}

	private static void assertFiltered(ITypeEnvironmentBuilder typenv,
			String predImage, int... tags) {
		assertFiltered(true, typenv, predImage, tags);
	}

	private static void assertFiltered(String predImage, int... tags) {
		assertFiltered(true, predImage, tags);
	}

	private static void assertFiltered(Predicate pred, int... tags) {
		assertFiltered(true, makeSequent(pred), tags);
	}

	private static void assertNotFiltered(ITypeEnvironmentBuilder typenv,
			String predImage, int... tags) {
		assertFiltered(false, typenv, predImage, tags);
	}

	private static void assertNotFiltered(String predImage, int... tags) {
		assertFiltered(false, predImage, tags);
	}

	private static void assertNotFiltered(Predicate pred, int... tags) {
		assertFiltered(false, makeSequent(pred), tags);
	}

	private static void assertFiltered(boolean expected, String predImage,
			int[] tags) {
		final ITypeEnvironmentBuilder typenv = DT_FAC.makeTypeEnvironment();
		assertFiltered(expected, typenv, predImage, tags);
	}

	private static void assertFiltered(boolean expected,
			ITypeEnvironmentBuilder typenv, String predImage, int[] tags) {
		final ISimpleSequent sequent = makeSequent(typenv, predImage);
		assertFiltered(expected, sequent, tags);
	}

	public static void assertFiltered(boolean expected,
			final ISimpleSequent sequent, int[] tags) {
		final ISimpleSequent actual = filterLanguage(sequent, tags);
		if (expected) {
			assertEmpty(actual);
		} else {
			assertSame(ff, actual.getFormulaFactory());
			if (ff == sequent.getFormulaFactory()) {
				assertSame(sequent, actual);
			} else {
				assertEquals(sequent, actual);
			}
		}
	}

	private static void assertEmpty(ISimpleSequent sequent) {
		if (0 != sequent.getPredicates().length) {
			fail("Expected an empty sequent, but got " + sequent);
		}
	}

	private static String equal(String arg) {
		return arg + " = " + arg;
	}

	private static Predicate equal(Expression arg) {
		return DT_FAC.makeRelationalPredicate(EQUAL, arg, arg, null);
	}

	// Twisted way to build an empty set extension with a given type
	public static Expression setext(ITypeEnvironmentBuilder typenv, String type) {
		final Predicate pred = genPred(typenv, "{ } = (∅⦂" + type + ")");
		final Expression setext = ((RelationalPredicate) pred).getLeft();
		assertEquals(Formula.SETEXT, setext.getTag());
		assertNotNull(setext.getType());
		return setext;
	}

	/**
	 * Ensures that a regular predicate is retained.
	 */
	@Test
	public void regular() {
		assertNotFiltered("0 = 1");
	}

	/**
	 * Ensures that a free identifier is filtered out or retained depending on
	 * whether its type contains a parametric type.
	 */
	@Test
	public void freeIdentParametric() {
		typenv.addGivenSet("S");

		// Integer
		assertNotFiltered("a = 1");

		// Boolean
		assertNotFiltered("a = TRUE");

		// Given set
		assertNotFiltered(typenv, "a ∈ S");

		// Parametric
		genPred(typenv, "b ∈ SD");
		assertFiltered(typenv, "b = b");

		// Powerset
		assertNotFiltered(typenv, "A ∈ ℙ(S)");
		genPred(typenv, "B ∈ ℙ(SD)");
		assertFiltered(typenv, "B = B");

		// Cartesian product
		assertNotFiltered(typenv, "m ∈ S × S");
		genPred(typenv, "p ∈ S × SD");
		assertFiltered(typenv, "p = p");
		genPred(typenv, "q ∈ SD × S");
		assertFiltered(typenv, "q = q");
	}

	/**
	 * Ensures that a bound identifier declaration is filtered out or retained
	 * depending on whether its type contains a parametric type.
	 */
	@Test
	public void boundIdentDeclParametric() {
		typenv.addGivenSet("S");

		// Integer
		assertNotFiltered("∃a·a = 1");

		// Boolean
		assertNotFiltered("∃a·a = TRUE");

		// Given set
		assertNotFiltered(typenv, "∃a·a ∈ S");

		// Parametric
		assertFiltered(typenv, "∃b⦂SD·b = b");

		// Powerset
		assertNotFiltered(typenv, "∃A⦂ℙ(S)·A ∈ ℙ(S)");
		assertFiltered(typenv, "∃B⦂ℙ(SD)·B = B");

		// Cartesian product
		assertNotFiltered(typenv, "∃m⦂S×S·m ∈ S × S");
		assertFiltered(typenv, "∃p⦂SD×S·p = p");
		assertFiltered(typenv, "∃p⦂S×SD·p = p");
	}

	/**
	 * Ensures that a generic operator is filtered out or retained depending on
	 * whether its type contains a parametric type.
	 */
	@Test
	public void genericParametric() {
		// Empty set
		assertNotFiltered(typenv, equal("(∅⦂ℙ(S))"));
		assertFiltered(typenv, equal("(∅⦂ℙ(SD))"));
		assertFiltered(typenv, equal("(∅⦂ℙ(S×SD))"));

		// Identity
		assertNotFiltered(typenv, equal("(id⦂ℙ(S×S))"));
		assertFiltered(typenv, equal("(id⦂ℙ(SD×SD))"));

		// Projections
		assertNotFiltered(typenv, equal("(prj1⦂ℙ(S×S×S))"));
		assertNotFiltered(typenv, equal("(prj2⦂ℙ(S×S×S))"));
		assertFiltered(typenv, equal("(prj1⦂ℙ(SD×SD×SD))"));
		assertFiltered(typenv, equal("(prj2⦂ℙ(SD×SD×SD))"));
		assertFiltered(typenv, equal("(prj1⦂ℙ(S×SD×S))"));
		assertFiltered(typenv, equal("(prj2⦂ℙ(S×SD×SD))"));
		assertFiltered(typenv, equal("(prj1⦂ℙ(SD×S×SD))"));
		assertFiltered(typenv, equal("(prj2⦂ℙ(SD×S×S))"));
	}

	/**
	 * Ensures that an empty set extension is filtered out or retained depending
	 * on whether its type contains a parametric type.
	 */
	@Test
	public void emptySetExtensionParametric() {
		assertNotFiltered(equal(setext(typenv, "ℙ(S)")));
		assertFiltered(equal(setext(typenv, "ℙ(SD)")));
		assertFiltered(equal(setext(typenv, "ℙ(S×SD)")));
		assertFiltered(equal(setext(typenv, "ℙ(SD×S)")));
	}

	/**
	 * Ensures that an extended expression is filtered out.
	 */
	@Test
	public void extendedExpression() {
		assertFiltered(typenv, "cons0 = cons0");
	}

	/**
	 * Ensures that an extended predicate is filtered out.
	 */
	@Test
	public void extendedPredicate() {
		assertFiltered(typenv, "prime(2)");
	}

	/**
	 * Ensures that an operator of the given tag(s) is filtered out.
	 */
	@Test
	public void byTag() {
		// AssociativeExpression
		assertTagFiltered("a = 1 + 1", PLUS);
		// AssociativePredicate
		assertTagFiltered("a = 1 ∧ a = 2", LAND);
		// AtomicExpression
		assertTagFiltered("a = succ", KSUCC);
		// BinaryExpression
		assertTagFiltered("a = a − 1", MINUS);
		// BinaryPredicate
		assertTagFiltered("a = 1 ⇒ a = 2", LIMP);
		// BoolExpression
		assertTagFiltered("bool(a = 1) = TRUE", KBOOL);
		// BoundIdentDecl
		assertTagFiltered("∃a·a = 1", BOUND_IDENT_DECL);
		// BoundIdentifier
		assertTagFiltered("∃a·a = 1", BOUND_IDENT);
		// FreeIdentifier
		assertTagFiltered("a = 1", FREE_IDENT);
		// IntegerLiteral
		assertTagFiltered("1 = 1", INTLIT);
		// LiteralPredicate
		assertTagFiltered("⊥", BFALSE);
		// MultiplePredicate
		assertTagFiltered("partition(a, {1})", KPARTITION);
		// PredicateVariable
		assertTagFiltered(DT_FAC.makePredicateVariable("$P", null),
				PREDICATE_VARIABLE);
		// QuantifiedExpression
		assertTagFiltered("{x∣x∈ℤ} = ∅", CSET);
		// QuantifiedPredicate
		assertTagFiltered("∃a·a = 1", EXISTS);
		// RelationalPredicate
		assertTagFiltered("a = 1", EQUAL);
		// SetExtension
		assertTagFiltered("a = {1}", SETEXT);
		// SimplePredicate
		assertTagFiltered("finite(∅⦂ℙ(ℤ))", KFINITE);
		// UnaryExpression
		assertTagFiltered("a = −b", UNMINUS);
		// UnaryPredicate
		assertTagFiltered("¬ a = 1", NOT);
	}

}
