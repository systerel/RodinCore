package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.*;
import static org.eventb.core.ast.tests.FastFactory.*;
import static org.eventb.core.ast.QuantifiedExpression.Form.*;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaFilter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Position;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.QuantifiedExpression.Form;

public class TestSubFormulas extends TestCase {

	private static class FixedFilter implements IFormulaFilter {
		
		Formula searched;
		
		public FixedFilter(Formula searched) {
			this.searched = searched;
		}

		public boolean retainAssociativeExpression(AssociativeExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainAssociativePredicate(AssociativePredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainAtomicExpression(AtomicExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainBinaryExpression(BinaryExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainBinaryPredicate(BinaryPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainBoolExpression(BoolExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainBoundIdentDecl(BoundIdentDecl decl) {
			return searched.equals(decl);
		}

		public boolean retainBoundIdentifier(BoundIdentifier identifier) {
			return searched.equals(identifier);
		}

		public boolean retainFreeIdentifier(FreeIdentifier identifier) {
			return searched.equals(identifier);
		}

		public boolean retainIntegerLiteral(IntegerLiteral literal) {
			return searched.equals(literal);
		}

		public boolean retainLiteralPredicate(LiteralPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainQuantifiedExpression(QuantifiedExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainQuantifiedPredicate(QuantifiedPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainRelationalPredicate(RelationalPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainSetExtension(SetExtension expression) {
			return searched.equals(expression);
		}

		public boolean retainSimplePredicate(SimplePredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainUnaryExpression(UnaryExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainUnaryPredicate(UnaryPredicate predicate) {
			return searched.equals(predicate);
		}
		
	}

	private Type INT = ff.makeIntegerType();
	
	private Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private Predicate btrue = mLiteralPredicate(BTRUE);
	
	private BoundIdentDecl bd_x = mBoundIdentDecl("x", INT);
	private BoundIdentDecl bd_y = mBoundIdentDecl("y", INT);
	private BoundIdentDecl bd_z = mBoundIdentDecl("z", INT);
	
	private FreeIdentifier id_x = mFreeIdentifier("x", INT);
	private FreeIdentifier id_y = mFreeIdentifier("y", INT);
	private FreeIdentifier id_S = mFreeIdentifier("S", POW(INT));
	private FreeIdentifier id_T = mFreeIdentifier("T", POW(INT));
	
	private Expression b0 = mBoundIdentifier(0, INT);
	private Expression b1 = mBoundIdentifier(1, INT);

	private Expression m0x = mMaplet(b0, id_x);
	private Expression m01x = mMaplet(mMaplet(b0, b1), id_x);
	private Expression m0y = mMaplet(b0, id_y);
	
	private RelationalPredicate equals = mRelationalPredicate(EQUAL, id_x, id_x);

	private FixedFilter bdFilter = new FixedFilter(bd_x);
	private FixedFilter idFilter = new FixedFilter(id_x);
	private FixedFilter equalsFilter = new FixedFilter(equals);

	private <T extends Formula<T>> void checkPositions(FixedFilter filter,
			Formula<T> formula, String... positions) {
		
		assertTrue(formula.isTypeChecked());
		final List<Position> actualPos = formula.getPositions(filter);
		final int length = positions.length;
		assertEquals("wrong number of positions retrieved",
				length, actualPos.size());
		for (int i = 0; i < length; ++ i) {
			assertEquals("Unexpected position",
					positions[i], actualPos.get(i).toString());
			assertEquals("Unexpected sub-formula",
					filter.searched, formula.getSubFormula(actualPos.get(i)));
		}
	}
	
	private void checkBdFilterQExpr(int tag, Form form) {
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_y), btrue, id_S));
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_x), btrue, id_S),
				"0");
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_x, bd_y), btrue, id_S),
				"0");
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_x, bd_y, bd_z), btrue, id_S),
				"0");
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_y, bd_x), btrue, id_S),
				"1");
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_z, bd_y, bd_x), btrue, id_S),
				"2");
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_z, bd_x, bd_x), btrue, id_S),
				"1", "2");
	}

	/**
	 * Ensures that the position of a bound identifier declaration can be
	 * retrieved or not retrieved from all places where a declaration can occur.
	 */
	public void testBdFilter() throws Exception {
		checkPositions(bdFilter, bd_y);
		checkPositions(bdFilter, bd_x, "");
		
		checkBdFilterQExpr(QUNION, Implicit);
		checkBdFilterQExpr(QUNION, Explicit);
		checkBdFilterQExpr(CSET, Implicit);
		checkBdFilterQExpr(CSET, Explicit);
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_y), btrue, m0x));
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0x),
				"0");
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), btrue, m01x),
				"0");
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_y, bd_x), btrue, m01x),
				"1");
		
		checkPositions(bdFilter, mQuantifiedPredicate(mList(bd_x), btrue), "0");
		checkPositions(bdFilter, mQuantifiedPredicate(mList(bd_y), btrue));
		checkPositions(bdFilter, mQuantifiedPredicate(mList(bd_x, bd_y), btrue), "0");
		checkPositions(bdFilter, mQuantifiedPredicate(mList(bd_x, bd_y, bd_z), btrue), "0");
		checkPositions(bdFilter, mQuantifiedPredicate(mList(bd_y, bd_x), btrue), "1");
		checkPositions(bdFilter, mQuantifiedPredicate(mList(bd_z, bd_y, bd_x), btrue), "2");
		checkPositions(bdFilter, mQuantifiedPredicate(mList(bd_x, bd_y, bd_x), btrue), "0", "2");
	}
	
	/**
	 * Ensures that the position of an expression can be retrieved or not
	 * retrieved from all places where an expression can occur.
	 */
	public void testIdFilter() throws Exception {
		checkPositions(idFilter, mAssociativeExpression(PLUS, id_y, id_y));
		checkPositions(idFilter, mAssociativeExpression(PLUS, id_x, id_y), "0");
		checkPositions(idFilter, mAssociativeExpression(PLUS, id_y, id_x), "1");
		checkPositions(idFilter, mAssociativeExpression(PLUS, id_x, id_y, id_x), "0", "2");
		
		checkPositions(idFilter, mAtomicExpression());
		
		checkPositions(idFilter, mBinaryExpression(MINUS, id_x, id_x), "0", "1");
		checkPositions(idFilter, mBinaryExpression(MINUS, id_x, id_y), "0");
		checkPositions(idFilter, mBinaryExpression(MINUS, id_y, id_x), "1");
		checkPositions(idFilter, mBinaryExpression(MINUS, id_y, id_y));
		
		checkPositions(idFilter, b0);

		checkPositions(idFilter, id_y);
		checkPositions(idFilter, id_x, "");
		
		checkPositions(idFilter, mIntegerLiteral());
		
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_y));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_x),
				"2");
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_y));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_x),
				"2");
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0y));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0x),
				"2.1");
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), btrue, m01x),
				"3.1");
		
		checkPositions(idFilter, mRelationalPredicate(EQUAL, id_x, id_x), "0", "1");
		checkPositions(idFilter, mRelationalPredicate(EQUAL, id_x, id_y), "0");
		checkPositions(idFilter, mRelationalPredicate(EQUAL, id_y, id_x), "1");
		checkPositions(idFilter, mRelationalPredicate(EQUAL, id_y, id_y));

		checkPositions(idFilter, mSetExtension(id_x, id_y), "0");
		checkPositions(idFilter, mSetExtension(id_y, id_x), "1");
		checkPositions(idFilter, mSetExtension(id_x, id_y, id_x), "0", "2");
		
		checkPositions(idFilter, mSimplePredicate(id_S));
		checkPositions(new FixedFilter(id_S), mSimplePredicate(id_S), "0");

		checkPositions(idFilter, mUnaryExpression(UNMINUS, id_x), "0");
	}
	
	/**
	 * Ensures that the position of a predicate can be retrieved from all
	 * contexts.
	 */
	public void testEqualsFilter() throws Exception {
		checkPositions(equalsFilter,
				mAssociativePredicate(LAND, equals, btrue),
				"0");
		checkPositions(equalsFilter,
				mAssociativePredicate(LAND, btrue, equals),
				"1");
		checkPositions(equalsFilter, 
				mAssociativePredicate(LAND, equals, btrue, equals),
				"0", "2");
		
		checkPositions(equalsFilter, mBinaryPredicate(LIMP, btrue, btrue));
		checkPositions(equalsFilter, mBinaryPredicate(LIMP, equals, btrue),
				"0");
		checkPositions(equalsFilter, mBinaryPredicate(LIMP, btrue, equals),
				"1");
		checkPositions(equalsFilter, mBinaryPredicate(LIMP, equals, equals),
				"0", "1");
		
		checkPositions(equalsFilter, mBoolExpression(btrue));
		checkPositions(equalsFilter, mBoolExpression(equals), "0");
		
		checkPositions(equalsFilter, mLiteralPredicate());

		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), equals, id_x),
				"1");
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x, bd_y), equals, id_x),
				"2");
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), equals, id_x),
				"1");
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), equals, id_x),
				"2");
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), equals, m0x),
				"1");
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), equals, m01x),
				"2");
		
		checkPositions(equalsFilter,
				mQuantifiedPredicate(FORALL, mList(bd_x), btrue));
		checkPositions(equalsFilter,
				mQuantifiedPredicate(FORALL, mList(bd_x), equals), "1");
		checkPositions(equalsFilter,
				mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), equals), "2");
		
		checkPositions(equalsFilter, mUnaryPredicate(NOT, btrue));
		checkPositions(equalsFilter, mUnaryPredicate(NOT, equals), "0");
	}
	
	private <T extends Formula<T>> void checkRootPosition(Formula<T> f1,
			Formula<T> f2) {
		assertEquals(f1.getClass(), f2.getClass());
		assertFalse(f1.equals(f2));
		checkPositions(new FixedFilter(f1), f2);
		checkPositions(new FixedFilter(f1), f1, "");
	}
	
	/**
	 * Ensures that filtering is implemented for all kinds of formulas.
	 */
	public void testAllClasses() throws Exception {
		checkRootPosition(
				mAssociativeExpression(PLUS, id_x, id_x),
				mAssociativeExpression(PLUS, id_x, id_y)
		);
		checkRootPosition(
				mAssociativePredicate(LAND, btrue, equals),
				mAssociativePredicate(LAND, btrue, btrue)
		);
		checkRootPosition(
				mBinaryExpression(MINUS, id_x, id_x),
				mBinaryExpression(MINUS, id_x, id_y)
		);
		checkRootPosition(
				mBinaryPredicate(LIMP, btrue, equals),
				mBinaryPredicate(LIMP, btrue, btrue)
		);
		checkRootPosition(
				mBoolExpression(equals),
				mBoolExpression(btrue)
		);
		checkRootPosition(
				mBoundIdentDecl("x", INT),
				mBoundIdentDecl("y", INT)
		);
		checkRootPosition(
				mBoundIdentifier(0, INT),
				mBoundIdentifier(1, INT)
		);
		checkRootPosition(
				mFreeIdentifier("x", INT),
				mFreeIdentifier("y", INT)
		);
		checkRootPosition(
				mIntegerLiteral(0),
				mIntegerLiteral(1)
		);
		checkRootPosition(
				mLiteralPredicate(BTRUE),
				mLiteralPredicate(BFALSE)
		);
		checkRootPosition(
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_x),
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_y)
		);
		checkRootPosition(
				mQuantifiedPredicate(FORALL, mList(bd_x), equals),
				mQuantifiedPredicate(FORALL, mList(bd_x), btrue)
		);
		checkRootPosition(
				mRelationalPredicate(EQUAL, id_x, id_x),
				mRelationalPredicate(EQUAL, id_x, id_y)
		);
		checkRootPosition(
				mSetExtension(id_x),
				mSetExtension(id_y)
		);
		checkRootPosition(
				mSimplePredicate(id_S),
				mSimplePredicate(id_T)
		);
		checkRootPosition(
				mUnaryExpression(UNMINUS, id_x),
				mUnaryExpression(UNMINUS, id_y)
		);
		checkRootPosition(
				mUnaryPredicate(NOT, equals),
				mUnaryPredicate(NOT, btrue)
		);
	}
	
	/**
	 * Ensures that a sub-expression that occurs deeply in a formula can be
	 * retrieved.
	 */
	public void testDeep() {
		checkPositions(idFilter,
				mAssociativePredicate(
						mRelationalPredicate(EQUAL, id_x, id_y),
						mRelationalPredicate(EQUAL, id_y,
								mBinaryExpression(MINUS, id_x, id_y))
				),
				"0.0", "1.1.0"
		);
		
	}
}
