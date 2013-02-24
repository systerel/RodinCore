/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover.tests;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.internal.ui.utils.PredicateHeightComputer.getHeight;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.junit.Test;

/**
 * Ensures that predicate heights are correctly computed by the prover UI. There
 * is one test method per Predicate sub-class.
 * 
 * @author Laurent Voisin
 */
public class TestPredicateHeight {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static final Type Z = ff.makeIntegerType();
	private static final Type POW_Z = ff.makePowerSetType(Z);

	private static final BoundIdentDecl[] bids = new BoundIdentDecl[] { ff
			.makeBoundIdentDecl("x", null, Z) };

	private static final Predicate L0 = ff.makeLiteralPredicate(BTRUE, null);
	private static final Predicate L1 = ff.makeUnaryPredicate(NOT, L0, null);
	private static final Predicate L2 = ff.makeUnaryPredicate(NOT, L1, null);

	private static final Expression EMPTY = ff.makeEmptySet(POW_Z, null);
	private static final Expression ZERO = ff.makeIntegerLiteral(
			BigInteger.ZERO, null);

	private static void assertHeight(int expected, Predicate p) {
		assertEquals(expected, getHeight(p));
	}

	private static Predicate mAnd(Predicate... preds) {
		return ff.makeAssociativePredicate(LAND, preds, null);
	}

	@Test
	public void testAssociativePredicate() throws Exception {
		assertHeight(1, mAnd(L0, L0));
		assertHeight(2, mAnd(L0, L1));
		assertHeight(2, mAnd(L1, L0));
		assertHeight(2, mAnd(L1, L1));

		assertHeight(1, mAnd(L0, L0, L0));
		assertHeight(2, mAnd(L0, L0, L1));
		assertHeight(3, mAnd(L0, L0, L2));
		assertHeight(2, mAnd(L0, L1, L0));
		assertHeight(2, mAnd(L0, L1, L1));
		assertHeight(3, mAnd(L0, L1, L2));
		assertHeight(3, mAnd(L0, L2, L0));
		assertHeight(3, mAnd(L0, L2, L1));
		assertHeight(3, mAnd(L0, L2, L2));

		assertHeight(2, mAnd(L1, L0, L0));
		assertHeight(2, mAnd(L1, L0, L1));
		assertHeight(3, mAnd(L1, L0, L2));
		assertHeight(2, mAnd(L1, L1, L0));
		assertHeight(2, mAnd(L1, L1, L1));
		assertHeight(3, mAnd(L1, L1, L2));
		assertHeight(3, mAnd(L1, L2, L0));
		assertHeight(3, mAnd(L1, L2, L1));
		assertHeight(3, mAnd(L1, L2, L2));

		assertHeight(3, mAnd(L2, L0, L0));
		assertHeight(3, mAnd(L2, L0, L1));
		assertHeight(3, mAnd(L2, L0, L2));
		assertHeight(3, mAnd(L2, L1, L0));
		assertHeight(3, mAnd(L2, L1, L1));
		assertHeight(3, mAnd(L2, L1, L2));
		assertHeight(3, mAnd(L2, L2, L0));
		assertHeight(3, mAnd(L2, L2, L1));
		assertHeight(3, mAnd(L2, L2, L2));
	}

	@Test
	public void testBinaryPredicate() throws Exception {
		assertHeight(1, ff.makeBinaryPredicate(LIMP, L0, L0, null));
		assertHeight(2, ff.makeBinaryPredicate(LIMP, L0, L1, null));
		assertHeight(2, ff.makeBinaryPredicate(LIMP, L1, L0, null));
		assertHeight(2, ff.makeBinaryPredicate(LIMP, L1, L1, null));
	}

	@Test
	public void testLiteralPredicate() throws Exception {
		assertHeight(0, L0);
	}

	@Test
	public void testMultiplePredicate() throws Exception {
		assertHeight(0, ff.makeMultiplePredicate(KPARTITION, asList(EMPTY),
				null));
	}

	@Test
	public void testPredicateVariable() throws Exception {
		assertHeight(0, ff.makePredicateVariable("$P", null));
	}

	@Test
	public void testQuantifiedPredicate() throws Exception {
		assertHeight(0, ff.makeQuantifiedPredicate(FORALL, bids, L0, null));
		assertHeight(1, ff.makeQuantifiedPredicate(FORALL, bids, L1, null));
	}

	@Test
	public void testRelationalPredicate() throws Exception {
		assertHeight(0, ff.makeRelationalPredicate(EQUAL, ZERO, ZERO, null));
	}

	@Test
	public void testSimplePredicate() throws Exception {
		assertHeight(0, ff.makeSimplePredicate(KFINITE, EMPTY, null));
	}

	@Test
	public void testUnaryPredicate() throws Exception {
		assertHeight(1, L1);
		assertHeight(2, L2);
	}
}
