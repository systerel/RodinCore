package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.ff;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.*;
import static org.eventb.core.ast.Formula.*;

import java.util.HashMap;

import junit.framework.TestCase;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Position;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

public class TestSourceLocation extends TestCase {

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

	/*
	 * Parse the given formula.
	 */
	private Formula parseFormula(String input) {
		IParseResult result = ff.parsePredicate(input);
		if (result.isSuccess()) {
			return result.getParsedPredicate();
		}
		result = ff.parseExpression(input);
		if (result.isSuccess()) {
			return result.getParsedExpression();
		}
		fail("Not a predicate, nor an expression.");
		return null;
	}
	
	private void addAllPairs(HashMap<SourceLocation, Formula> pairs,
			final int start, final int end, Formula sub) {

		for (int i = start; i <= end; ++ i) {
			for (int j = i; j <= end; ++j) {
				pairs.put(new SourceLocation(i, j), sub);
			}
		}
	}

	private void testAllLocations(Formula formula, int max,
			HashMap<SourceLocation, Formula> children) {

		// All locations inside the formula
		for (int i = 0; i < max; ++i) {
			for (int j = i; j < max; ++j) {
				final SourceLocation sloc = new SourceLocation(i, j);
				final Position pos = formula.getPosition(sloc);
				assertNotNull(pos);
				final Formula actual = formula.getSubFormula(pos);
				assertTrue(actual.getSourceLocation().contains(sloc));
				Formula expected = children.get(sloc);
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

	private void assertPositions(Formula formula, int max, Object... args) {
		HashMap<SourceLocation, Formula> children =
			new HashMap<SourceLocation, Formula>();
		int idx = 0;
		while (idx < args.length) {
			final int start = (Integer) args[idx++];
			final int end = (Integer) args[idx++];
			final Formula sub = (Formula) args[idx++];
			addAllPairs(children, start, end, sub);
		}

		testAllLocations(formula, max, children);
	}

	private void assertPositions(String input, Object... args) {
		Formula formula = parseFormula(input);
		HashMap<SourceLocation, Formula> children =
			new HashMap<SourceLocation, Formula>();
		int idx = 0;
		while (idx < args.length) {
			final int start = (Integer) args[idx++];
			final int end = (Integer) args[idx++];
			final Formula sub = (Formula) args[idx++];
			addAllPairs(children, start, end, sub);
		}

		testAllLocations(formula, input.length(), children);
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
	
	/**
	 * Tests for {@link Formula#getPosition(SourceLocation)}.
	 */
	public void testGetPosition() {
		// AssociativeExpression
		assertPositions("x+y", 0, 0, id_x, 2, 2, id_y);
		assertPositions("x+y+z", 0, 0, id_x, 2, 2, id_y, 4, 4, id_z);
		
		// AssociativePredicate
		assertPositions("⊤∧⊥", 0, 0, btrue, 2, 2, bfalse);
		assertPositions("⊤∧⊥∧⊤", 0, 0, btrue, 2, 2, bfalse, 4, 4, btrue);
		
		// AtomicExpression
		assertPositions("ℤ", 0, 0, mAtomicExpression(INTEGER));
		
		// BinaryExpression
		assertPositions("x−y", 0, 0, id_x, 2, 2, id_y);

		// BinaryPredicate
		assertPositions("⊤⇒⊥", 0, 0, btrue, 2, 2, bfalse);
		
		// BoolExpression
		assertPositions("bool(⊤)", 5, 5, btrue);
		
		// BoundIdentDecl
		assertPositions(ff.makeBoundIdentDecl("x", new SourceLocation(0,0)), 1,
				0, 0, bd_x);

		// BoundIdentifier
		assertPositions(ff.makeBoundIdentifier(0, new SourceLocation(0,0)), 1,
				0, 0, b0);
		assertPositions(ff.makeBoundIdentifier(0, new SourceLocation(0,3)), 4,
				0, 3, b0);

		// FreeIdentifier
		assertPositions("x", 0, 0, id_x);
		assertPositions("foo");

		// IntegerLiteral
		assertPositions("1", 0, 0, mIntegerLiteral(1));
		assertPositions("1024", 0, 3, mIntegerLiteral(1024));

		// LiteralPredicate
		assertPositions("⊤", 0, 0, btrue);

		// QuantifiedExpression
		assertPositions("⋃x·⊤∣x", 1,1, bd_x, 3,3, btrue, 5,5, b0);
		assertPositions("⋃x,y·⊤∣y", 1,1, bd_x, 3,3, bd_y, 5,5, btrue, 7,7, b0);

		assertPositions("⋃x∣⊤", 1,1, b0, 3,3, btrue);
		assertPositions("⋃x∪y∣⊤", 1,3, union10, 1,1, b1, 3,3, b0, 5,5, btrue);

		assertPositions("{x·⊤∣x}", 1,1, bd_x, 3,3, btrue, 5,5, b0);
		assertPositions("{x∣⊤}", 1,1, b0, 3,3, btrue);
		assertPositions("λx·⊤∣x", 1,1, b0, 3,3, btrue, 5,5, b0);
		assertPositions("λx↦y·⊤∣x∪y",
				1,3, map10, 1,1, b1, 3,3, b0,
				5,5, btrue,
				7,9, union10, 7,7, b1, 9,9, b0);
		
		// QuantifiedPredicate
		assertPositions("∀x·⊤", 1,1, bd_x, 3,3, btrue);
		assertPositions("∀x,y·⊤", 1,1, bd_x, 3,3, bd_y, 5,5, btrue);

		// RelationalPredicate
		assertPositions("x=y", 0,0, id_x, 2,2, id_y);

		// SetExtension
		assertPositions("{}");
		assertPositions("{x}", 1,1, id_x);
		assertPositions("{x,y}", 1,1, id_x, 3,3, id_y);

		// SimplePredicate
		assertPositions("finite(x)", 7,7, id_x);

		// UnaryExpression
		assertPositions("ℙ(x)", 2,2, id_x);

		// UnaryPredicate
		assertPositions("¬⊤", 1,1, btrue);
	}

}
