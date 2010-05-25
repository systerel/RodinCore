/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - fixed source location test for locations outside the root
 *******************************************************************************/ 
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.INTEGER;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mPredicateVariable;

import java.util.HashMap;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

public class TestSourceLocation extends AbstractTests {

	public final void testContains() {
		SourceLocation s11 = new SourceLocation(1, 1);
		SourceLocation s12 = new SourceLocation(1, 2);
		SourceLocation s13 = new SourceLocation(1, 3);
		SourceLocation s22 = new SourceLocation(2, 2);
		SourceLocation s23 = new SourceLocation(2, 3);
		
		assertTrue(s11.contains(s11));
		assertFalse(s11.contains(s12));
		assertFalse(s11.contains(s13));
		assertFalse(s11.contains(s22));
		assertFalse(s11.contains(s23));
		
		assertTrue(s12.contains(s11));
		assertTrue(s12.contains(s12));
		assertFalse(s12.contains(s13));
		assertTrue(s12.contains(s22));
		assertFalse(s12.contains(s23));
		
		assertTrue(s13.contains(s11));
		assertTrue(s13.contains(s12));
		assertTrue(s13.contains(s13));
		assertTrue(s13.contains(s22));
		assertTrue(s13.contains(s23));
		
		assertFalse(s22.contains(s11));
		assertFalse(s22.contains(s12));
		assertFalse(s22.contains(s13));
	}

	public final void testEqualsObject() {
		SourceLocation s11 = new SourceLocation(1, 1);
		SourceLocation s11b = new SourceLocation(1, 1);
		SourceLocation s12 = new SourceLocation(1, 2);
		SourceLocation s01 = new SourceLocation(0, 1);
		
		assertFalse(s11.equals(null));
		assertTrue(s11.equals(s11));
		assertTrue(s11.equals(s11b));
		assertFalse(s11.equals(s12));
		assertFalse(s11.equals(s01));
	}

	private void addAllPairs(HashMap<SourceLocation, Formula<?>> pairs,
			final int start, final int end, Formula<?> sub) {

		for (int i = start; i <= end; ++ i) {
			for (int j = i; j <= end; ++j) {
				pairs.put(new SourceLocation(i, j), sub);
			}
		}
	}

	private void testAllLocations(Formula<?> formula, int max,
			HashMap<SourceLocation, Formula<?>> children) {

		// All locations inside the formula
		for (int i = 0; i < max; ++i) {
			for (int j = i; j < max; ++j) {
				final SourceLocation sloc = new SourceLocation(i, j);
				final IPosition pos = formula.getPosition(sloc);
				if (!formula.contains(sloc)) {
					assertNull(pos);
					break;
				}
				assertNotNull(pos);
				final Formula<?> actual = formula.getSubFormula(pos);
				assertTrue(actual.getSourceLocation().contains(sloc));
				Formula<?> expected = children.get(sloc);
				if (expected == null) expected = formula;
				assertEquals("Wrong sub-formula",
						expected, actual);
			}
		}
		// Some locations outside the formula
		for (int i = 0; i <= max; ++i) {
			assertNull(formula.getPosition(new SourceLocation(i, max)));
		}
	}

	private void assertPositions(Formula<?> formula, int max, Object... args) {
		HashMap<SourceLocation, Formula<?>> children =
			new HashMap<SourceLocation, Formula<?>>();
		int idx = 0;
		while (idx < args.length) {
			final int start = (Integer) args[idx++];
			final int end = (Integer) args[idx++];
			final Formula<?> sub = (Formula<?>) args[idx++];
			addAllPairs(children, start, end, sub);
		}

		testAllLocations(formula, max, children);
	}

	private void assertPositionsE(String input, Object... args) {
		Formula<?> formula = parseExpression(input);
		assertPositions(formula, input.length(), args);
	}

	private void assertPositionsP(String input, Object... args) {
		Formula<?> formula = parsePredicate(input);
		assertPositions(formula, input.length(), args);
	}

	private final BoundIdentDecl bd_x = mBoundIdentDecl("x");
	private final BoundIdentDecl bd_y = mBoundIdentDecl("y");

	private final Expression id_x = mFreeIdentifier("x");
	private final Expression id_y = mFreeIdentifier("y");
	private final Expression id_z = mFreeIdentifier("z");
	private final Expression b0 = mBoundIdentifier(0);
	private final Expression b1 = mBoundIdentifier(1);
	private final Expression union10 = mAssociativeExpression(BUNION, b1, b0);
	private final Expression map10 = mBinaryExpression(MAPSTO, b1, b0);
	
	private final Predicate btrue = mLiteralPredicate(BTRUE);
	private final Predicate bfalse = mLiteralPredicate(BFALSE);
	private final Predicate pv_P = mPredicateVariable("$P");
	
	/**
	 * Tests for {@link Formula#getPosition(SourceLocation)}.
	 */
	public void testGetPosition() {
		// AssociativeExpression
		assertPositionsE("x+y", 0, 0, id_x, 2, 2, id_y);
		assertPositionsE("x+y+z", 0, 0, id_x, 2, 2, id_y, 4, 4, id_z);
		
		// AssociativePredicate
		assertPositionsP("⊤∧⊥", 0, 0, btrue, 2, 2, bfalse);
		assertPositionsP("⊤∧⊥∧⊤", 0, 0, btrue, 2, 2, bfalse, 4, 4, btrue);
		
		// AtomicExpression
		assertPositionsE("ℤ", 0, 0, mAtomicExpression(INTEGER));
		assertPositionsE("id", 0, 1, mAtomicExpression(KID_GEN));
		
		// BinaryExpression
		assertPositionsE("x−y", 0, 0, id_x, 2, 2, id_y);

		// BinaryPredicate
		assertPositionsP("⊤⇒⊥", 0, 0, btrue, 2, 2, bfalse);
		
		// BoolExpression
		assertPositionsE("bool(⊤)", 5, 5, btrue);
		
		// BoundIdentDecl
		assertPositions(ff.makeBoundIdentDecl("x", new SourceLocation(0,0)), 1,
				0, 0, bd_x);

		// BoundIdentifier
		assertPositions(ff.makeBoundIdentifier(0, new SourceLocation(0,0)), 1,
				0, 0, b0);
		assertPositions(ff.makeBoundIdentifier(0, new SourceLocation(0,3)), 4,
				0, 3, b0);

		// FreeIdentifier
		assertPositionsE("x", 0, 0, id_x);
		assertPositionsE("foo");
		assertPositionsE("(a)");
		assertPositionsE(" a ");

		// IntegerLiteral
		assertPositionsE("1", 0, 0, mIntegerLiteral(1));
		assertPositionsE("1024", 0, 3, mIntegerLiteral(1024));

		// LiteralPredicate
		assertPositionsP("⊤", 0, 0, btrue);

		// MultiplePredicate
		assertPositionsP("partition(x)", 10, 10, id_x);
		assertPositionsP("partition(x,y)", 10, 10, id_x, 12, 12, id_y);

		// PredicateVariable
		assertPositionsP("$P⇒⊥", 0, 1, pv_P, 3, 3, bfalse);

		// QuantifiedExpression
		assertPositionsE("⋃x·⊤∣x", 1,1, bd_x, 3,3, btrue, 5,5, b0);
		assertPositionsE("⋃x,y·⊤∣y", 1,1, bd_x, 3,3, bd_y, 5,5, btrue, 7,7, b0);

		assertPositionsE("⋃x∣⊤", 1,1, b0, 3,3, btrue);
		assertPositionsE("⋃x∪y∣⊤", 1,3, union10, 1,1, b1, 3,3, b0, 5,5, btrue);

		assertPositionsE("{x·⊤∣x}", 1,1, bd_x, 3,3, btrue, 5,5, b0);
		assertPositionsE("{x∣⊤}", 1,1, b0, 3,3, btrue);
		assertPositionsE("λx·⊤∣x", 1,1, b0, 3,3, btrue, 5,5, b0);
		assertPositionsE("λx↦y·⊤∣x∪y",
				1,3, map10, 1,1, b1, 3,3, b0,
				5,5, btrue,
				7,9, union10, 7,7, b1, 9,9, b0);
		
		// QuantifiedPredicate
		assertPositionsP("∀x·⊤", 1,1, bd_x, 3,3, btrue);
		assertPositionsP("∀x,y·⊤", 1,1, bd_x, 3,3, bd_y, 5,5, btrue);

		// RelationalPredicate
		assertPositionsP("x=y", 0,0, id_x, 2,2, id_y);

		// SetExtension
		assertPositionsE("{}");
		assertPositionsE("{x}", 1,1, id_x);
		assertPositionsE("{x,y}", 1,1, id_x, 3,3, id_y);

		// SimplePredicate
		assertPositionsP("finite(x)", 7,7, id_x);

		// UnaryExpression
		assertPositionsE("ℙ(x)", 2,2, id_x);

		// UnaryPredicate
		assertPositionsP("¬⊤", 1,1, btrue);
	}

}
