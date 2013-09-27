/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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

import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.pptrans.Translator;
import org.junit.Test;

/**
 * Ensures that identifier decomposition behaves properly.
 * 
 * @author Laurent Voisin
 * @see org.eventb.pptrans.Translator#decomposeIdentifiers(ISimpleSequent)
 */
public class IdentifierDecompositionTests extends AbstractTranslationTests {
	
	protected final ITypeEnvironmentBuilder te = mTypeEnvironment(
			"S=ℙ(S); T=ℙ(T); U=ℙ(U); V=ℙ(V)", ff);

	private static void assertDecomposed(ISimpleSequent sequent) {
		final DecompositionChecker checker = new DecompositionChecker();
		for (final ITrackedPredicate tpred : sequent.getPredicates()) {
			tpred.getPredicate().accept(checker);
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
		doDecompTest(inputString, expectedString, te);
	}

	public static void doDecompTest(String inputString, String expectedString,
			ITypeEnvironmentBuilder inputEnv) {
		final Predicate input = parse(inputString, inputEnv);
		final ISimpleSequent inputSeq = SimpleSequents.make(NONE, input, ff);
		final ITypeEnvironmentBuilder expectedEnv = inputEnv.makeBuilder();
		final Predicate expected = parse(expectedString, expectedEnv);
		final ISimpleSequent expectedSeq = SimpleSequents.make(NONE, expected,
				ff);
		final ISimpleSequent actualSeq = Translator
				.decomposeIdentifiers(inputSeq);
		assertEquals("Wrong identifier decomposition", expectedSeq, actualSeq);
		assertDecomposed(actualSeq);
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring outside of any quantified construct.
	 */
	@Test
	public final void testDecomposeFreeOutside1() {
		dotest("x ∈ S×T", "x_1↦x_2 ∈ S×T");
	}

	/**
	 * Ensures that a free identifier which hides several maplet is fully
	 * decomposed, when occurring outside of any quantified construct.
	 */
	@Test
	public final void testDecomposeFreeOutside2() {
		dotest("x ∈ S×(T×U)", "x_1↦(x_2↦x_3) ∈ S×(T×U)");
	}

	/**
	 * Ensures that two free identifiers which hide a maplet are decomposed,
	 * when occurring outside of any quantified construct.
	 */
	@Test
	public final void testDecomposeFreeOutside3() {
		dotest("x ∈ S×T ∧ y ∈ U×V", "x_1↦x_2 ∈ S×T ∧ y_1↦y_2 ∈ U×V");
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring inside a quantified predicate.
	 */
	@Test
	public final void testDecomposeFreeInQPred() {
		dotest("∀z · z ∈ BOOL ⇒ x ∈ S×T", "∀z · z ∈ BOOL ⇒ x_1↦x_2 ∈ S×T");
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring inside a quantified expression.
	 */
	@Test
	public final void testDecomposeFreeInQExpr() {
		dotest("finite({z ∣ z ∈ BOOL ∧ x ∈ S×T})",
				"finite({z ∣ z ∈ BOOL ∧ x_1↦x_2 ∈ S×T})");
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
				"∃x1,x2·x1↦x2 ∈ S×T ∧ y_1↦y_2 ∈ U×V");
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
				"∃a,x1,x2·a∈ℤ ∧ x1↦x2∈S×T ∧ X_1↦X_2∈S×T ∧ Y_1↦Y_2∈T×U" +
				" ∧ (∀y1,y2,b·y1↦y2∈T×U ∧ Y_1↦Y_2∈T×U ∧ b∈BOOL ∧ X_1↦X_2=x1↦x2" +
				" ⇒ (∃z1,z2·z1↦z2=x1↦x2 ∧ Y_1↦Y_2=y1↦y2 ∧ X_1↦X_2∈S×T))");
	}
	
	@Test
	public void testDecomposePartition() throws Exception {
		dotest("partition({x∣x ∈ S×T})", "partition({x1↦x2∣x1↦x2 ∈ S×T})");
	}
}
