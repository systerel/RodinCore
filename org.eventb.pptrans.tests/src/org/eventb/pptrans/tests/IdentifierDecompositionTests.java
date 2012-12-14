/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added a test for partition (math V2)
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.pptrans.Translator;
import org.junit.Test;

/**
 * Ensures that identifier decomposition behaves properly.
 * 
 * @author Laurent Voisin
 * @see org.eventb.pptrans.Translator#decomposeIdentifiers(Predicate, FormulaFactory)
 */
@SuppressWarnings({ "deprecation", "javadoc" })
public class IdentifierDecompositionTests extends AbstractTranslationTests {
	
	protected final ITypeEnvironmentBuilder te;
	{
		te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		te.addGivenSet("T");
		te.addGivenSet("U");
		te.addGivenSet("V");
	}

	private static void assertDecomposed(Predicate pred) {
		if (needsFreeIdentDecomposition(pred)) {
			assertEquals("Free identifiers haven't been decomposed",
					pred.getTag(), Formula.FORALL);
			final QuantifiedPredicate qPred = (QuantifiedPredicate) pred;
			final Predicate subPred = qPred.getPredicate();
			assertEquals("Invalid free identifier decomposition",
					subPred.getTag(), Formula.LIMP);
			final BinaryPredicate binPred = (BinaryPredicate) subPred;
			final Predicate lhs = binPred.getLeft();
			final Predicate rhs = binPred.getRight();
			assertFreeIdentsDecomposition(
					qPred.getBoundIdentDecls(), 
					lhs);
			rhs.accept(new DecompositionChecker());
		} else {
			pred.accept(new DecompositionChecker());
		}
	}
	
	private static boolean needsFreeIdentDecomposition(Predicate pred) {
		final FreeIdentifier[] freeIdents = pred.getFreeIdentifiers();
		for (FreeIdentifier ident : freeIdents) {
			if (ident.getType() instanceof ProductType) {
				return true;
			}
		}
		return false;
	}
	
	private static void assertFreeIdentsDecomposition(BoundIdentDecl[] bids,
			Predicate pred) {

		// Ensure declarations are not composite
		for (BoundIdentDecl bid : bids) {
			assertNotComposite(bid);
		}
		
		// Then, ensures the left-hand side is a conjunction of equalities
		final int length = bids.length;
		final boolean[] bidUsed = new boolean[length];
		if (pred.getTag() == Formula.LAND) {
			AssociativePredicate assocPred = (AssociativePredicate) pred;
			for (Predicate child : assocPred.getChildren()) {
				assertFreeIdentDecomposition(bidUsed, child);
			}
		} else {
			assertFreeIdentDecomposition(bidUsed, pred);
		}
		
		final int end = length - 1;
		for (int i = 0; i < length; i++) {
			assertTrue(
					"Unused bound identifier declaration" + bids[end - i],
					bidUsed[i]
			);
		}
	}

	private static void assertFreeIdentDecomposition(boolean[] bidUsed,
			Predicate pred) {
		
		assertEquals("Invalid free identifier decomposition" + pred,
				pred.getTag(), Formula.EQUAL);
		final RelationalPredicate relPred = (RelationalPredicate) pred;
		final Expression lhs = relPred.getLeft();
		final Expression rhs = relPred.getRight();
		
		// Ensure left-hand side is a free identifier of a composite type
		assertEquals(lhs.getTag(), Formula.FREE_IDENT);
		assertTrue(lhs.getType() instanceof ProductType);
		
		// Ensure right-hand side is a maplet of unused bound identifiers
		assertUnusedBoundMaplet(bidUsed, rhs);
	}

	private static void assertUnusedBoundMaplet(boolean[] bidUsed,
			Expression expr) {
		
		final int tag = expr.getTag();
		if (tag == Formula.BOUND_IDENT) {
			final BoundIdentifier ident = (BoundIdentifier) expr;
			assertNotComposite(ident);
			final int index = ident.getBoundIndex();
			assertFalse("Bound identifier " + index + "is used twice",
					bidUsed[index]);
			bidUsed[index] = true;
		} else {
			assertEquals("Invalid maplet decomposition" + expr,
					tag, Formula.MAPSTO);
			final BinaryExpression binExpr = (BinaryExpression) expr;
			assertUnusedBoundMaplet(bidUsed, binExpr.getLeft());
			assertUnusedBoundMaplet(bidUsed, binExpr.getRight());
		}
	}

	static void assertNotComposite(BoundIdentDecl ident) {
		assertFalse(
				"Bound Identifier declaration has a composite type: " + ident,
				ident.getType() instanceof ProductType
		);
	}
	
	static void assertNotComposite(Identifier ident) {
		assertFalse(
				"Identifier has a composite type: " + ident,
				ident.getType() instanceof ProductType
		);
	}
	
	static class DecompositionChecker extends DefaultVisitor {
		
		@Override
		public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
			assertNotComposite(ident);
			return true;
		}

		@Override
		public boolean visitBOUND_IDENT(BoundIdentifier ident) {
			assertNotComposite(ident);
			return true;
		}

		@Override
		public boolean visitFREE_IDENT(FreeIdentifier ident) {
			assertNotComposite(ident);
			return true;
		}
		
	}
	
	private void dotest(String inputString, String expectedString) {
		final Predicate input = parse(inputString, te);
		final Predicate expected = parse(expectedString, te);
		final Predicate actual = Translator.decomposeIdentifiers(input, ff);
		assertTypeChecked(actual);
		assertEquals("Wrong identifier decomposition", expected, actual);
		assertDecomposed(actual);
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring outside of any quantified construct.
	 */
	@Test
	public final void testDecomposeFreeOutside1() {
		dotest("x ∈ S×T", "∀x1,x2 · x = x1↦x2 ⇒ x1↦x2 ∈ S×T");
	}

	/**
	 * Ensures that a free identifier which hides several maplet is fully
	 * decomposed, when occurring outside of any quantified construct.
	 */
	@Test
	public final void testDecomposeFreeOutside2() {
		dotest("x ∈ S×(T×U)",
				"∀x1,x2,x3 · x = x1↦(x2↦x3) ⇒ x1↦(x2↦x3) ∈ S×(T×U)");
	}

	/**
	 * Ensures that two free identifiers which hide a maplet are decomposed,
	 * when occurring outside of any quantified construct.
	 */
	@Test
	public final void testDecomposeFreeOutside3() {
		dotest("x ∈ S×T ∧ y ∈ U×V",
				"∀x1,x2,y1,y2 · x=x1↦x2 ∧ y=y1↦y2 ⇒ "
				+ "x1↦x2 ∈ S×T ∧ y1↦y2 ∈ U×V");
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring inside a quantified predicate.
	 */
	@Test
	public final void testDecomposeFreeInQPred() {
		dotest("∀z · z ∈ BOOL ⇒ x ∈ S×T",
				"∀x1,x2 · x = x1↦x2 ⇒ (∀z · z ∈ BOOL ⇒ x1↦x2 ∈ S×T)");
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring inside a quantified expression.
	 */
	@Test
	public final void testDecomposeFreeInQExpr() {
		dotest("finite({z ∣ z ∈ BOOL ∧ x ∈ S×T})",
				"∀x1,x2 · x = x1↦x2 ⇒ finite({z ∣ z ∈ BOOL ∧ x1↦x2 ∈ S×T})");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring outside of any other quantified construct.
	 */
	@Test
	public final void testDecomposeBoundOutside1() {
		dotest("∃x · x ∈ S×T", "∃x1,x2 · x1↦x2 ∈ S×T");
	}

	/**
	 * Ensures that two bound identifiers which hide a maplet are decomposed, when
	 * occurring outside of any other quantified construct.
	 */
	@Test
	public final void testDecomposeBoundOutside2() {
		dotest("∃x,y · x ∈ S×T ∧ y ∈ U×V", 
				"∃x1,x2,y1,y2 · x1↦x2 ∈ S×T ∧ y1↦y2 ∈ U×V");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring outside of any other quantified construct and as first
	 * declaration in its own quantifier.
	 */
	@Test
	public final void testDecomposeBoundOutsideFirst() {
		dotest("∃x,y,z · x ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL",
				"∃x1,x2,y,z · x1↦x2 ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring outside of any other quantified construct and as last
	 * declaration in its own quantifier.
	 */
	@Test
	public final void testDecomposeBoundOutsideLast() {
		dotest("∃y,z,x · x ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL",
				"∃y,z,x1,x2 · x1↦x2 ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring inside another quantified construct.
	 */
	@Test
	public final void testDecomposeBoundInside1() {
		dotest("∃a·a ∈ ℤ ∧ (∃x·x ∈ S×T ∧ 0 ≤ a) ∧ 1 ≤ a",
				"∃a·a ∈ ℤ ∧ (∃x1,x2·x1↦x2 ∈ S×T ∧ 0 ≤ a) ∧ 1 ≤ a");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring inside two other nested quantified constructs.
	 */
	@Test
	public final void testDecomposeBoundInside2() {
		dotest("∃a·a ∈ ℤ ∧ (∃b·b ∈ ℤ ∧ (∃x·x ∈ S×T ∧ a ≤ b) ∧ b ≤ a) ∧ 1 ≤ a",
				"∃a·a ∈ ℤ ∧ (∃b·b ∈ ℤ ∧ (∃x1,x2·x1↦x2 ∈ S×T ∧ a ≤ b) ∧ b ≤ a) ∧ 1 ≤ a");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring inside another nested quantified construct and with mixing
	 * of bound variables from both quantifiers.
	 */
	@Test
	public final void testDecomposeBoundInside3() {
		dotest("∃a,x,b·a∈S ∧ b∈T ∧ x=a↦b"
				+ " ∧ (∃c,y,d·a↦x↦b↦c↦y↦d∈S×(S×T)×T×U×(U×V)×V)",
				"∃a,x1,x2,b·a∈S ∧ b∈T ∧ x1↦x2=a↦b"
				+ " ∧ (∃c,y1,y2,d·a↦(x1↦x2)↦b↦c↦(y1↦y2)↦d∈S×(S×T)×T×U×(U×V)×V)");
	}

	/**
	 * Ensures that a free and a bound identifier which hide a maplet are both
	 * decomposed.
	 */
	@Test
	public final void testDecomposeFreeBound() {
		dotest("∃x·x ∈ S×T ∧ y ∈ U×V",
				"∀y1,y2·y = y1↦y2 ⇒ (∃x1,x2·x1↦x2 ∈ S×T ∧ y1↦y2 ∈ U×V)");
	}

	/**
	 * Ensures that a free and bound identifiers which hide a maplet are both
	 * decomposed, in a quite complex predicate.
	 */
	@Test
	public final void testDecomposeComplex() {
		dotest("∃a,x·a∈ℤ ∧ x∈S×T ∧ X∈S×T ∧ Y∈T×U" +
				" ∧ (∀y,b·y∈T×U ∧ Y∈T×U ∧ b∈BOOL ∧ X=x" +
				" ⇒ (∃z·z=x ∧ Y=y ∧ X∈S×T))",
				"∀X1,X2,Y1,Y2·X=X1↦X2 ∧ Y=Y1↦Y2 ⇒ " +
				"(∃a,x1,x2·a∈ℤ ∧ x1↦x2∈S×T ∧ X1↦X2∈S×T ∧ Y1↦Y2∈T×U" +
				" ∧ (∀y1,y2,b·y1↦y2∈T×U ∧ Y1↦Y2∈T×U ∧ b∈BOOL ∧ X1↦X2=x1↦x2" +
				" ⇒ (∃z1,z2·z1↦z2=x1↦x2 ∧ Y1↦Y2=y1↦y2 ∧ X1↦X2∈S×T)))");
	}
	
	@Test
	public void testDecomposePartition() throws Exception {
		dotest("partition({x∣x ∈ S×T})", "partition({x1↦x2∣x1↦x2 ∈ S×T})");
	}
}
