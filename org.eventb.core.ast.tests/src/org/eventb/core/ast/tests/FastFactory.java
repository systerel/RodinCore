package org.eventb.core.ast.tests;

import java.math.BigInteger;

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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Provides simplistic methods for creating new formulae without too much
 * typing.
 * <p>
 * The methods provided are essentially geared towards unit test development,
 * making it less painful to build test formulae with a keyboard.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class FastFactory {

	public static FormulaFactory ff = FormulaFactory.getDefault();

	public static AssociativeExpression mAssociativeExpression(
			int tag, Expression... children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	public static AssociativeExpression mAssociativeExpression(
			Expression... children) {
		return mAssociativeExpression(Formula.PLUS, children);
	}

	public static AssociativePredicate mAssociativePredicate(int tag,
			Predicate... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}

	public static AssociativePredicate mAssociativePredicate(
			Predicate... children) {
		return mAssociativePredicate(Formula.LAND, children);
	}

	public static AtomicExpression mAtomicExpression() {
		return ff.makeAtomicExpression(Formula.TRUE, null);
	}

	public static BinaryExpression mBinaryExpression(int tag, Expression left,
			Expression right) {
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	public static BinaryExpression mBinaryExpression(Expression left,
			Expression right) {
		return mBinaryExpression(Formula.MINUS, left, right);
	}

	public static BinaryPredicate mBinaryPredicate(int tag, Predicate left,
			Predicate right) {
		return ff.makeBinaryPredicate(tag, left, right, null);
	}

	public static BinaryPredicate mBinaryPredicate(Predicate left,
			Predicate right) {
		return mBinaryPredicate(Formula.LIMP, left, right);
	}

	public static BoundIdentDecl mBoundIdentDecl(String name) {
		return ff.makeBoundIdentDecl(name, null);
	}

	public static BoundIdentifier mBoundIdentifier(int index) {
		return ff.makeBoundIdentifier(index, null);
	}

	public static BoolExpression mBoolExpression(Predicate pred) {
		return ff.makeBoolExpression(pred, null);
	}

	public static FreeIdentifier mFreeIdentifier(String name) {
		return ff.makeFreeIdentifier(name, null);
	}

	public static IntegerLiteral mIntegerLiteral() {
		return ff.makeIntegerLiteral(BigInteger.ZERO, null);
	}

	public static LiteralPredicate mLiteralPredicate() {
		return ff.makeLiteralPredicate(Formula.BTRUE, null);
	}

	public static QuantifiedExpression mQuantifiedExpression(int tag,
			QuantifiedExpression.Form form, BoundIdentDecl[] boundIdents,
			Predicate pred, Expression expr) {
		return ff.makeQuantifiedExpression(tag, boundIdents, pred, expr, null,
				form);
	}

	public static QuantifiedExpression mQuantifiedExpression(
			BoundIdentDecl[] boundIdents, Predicate pred, Expression expr) {
		return mQuantifiedExpression(Formula.QUNION,
				QuantifiedExpression.Form.Explicit, boundIdents, pred, expr);
	}

	public static QuantifiedPredicate mQuantifiedPredicate(int tag,
			BoundIdentDecl[] boundIdents, Predicate pred) {
		return ff.makeQuantifiedPredicate(tag, boundIdents, pred, null);
	}

	public static QuantifiedPredicate mQuantifiedPredicate(
			BoundIdentDecl[] boundIdents, Predicate pred) {
		return mQuantifiedPredicate(Formula.FORALL, boundIdents, pred);
	}

	public static RelationalPredicate mRelationalPredicate(int tag,
			Expression left, Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	public static RelationalPredicate mRelationalPredicate(Expression left,
			Expression right) {
		return mRelationalPredicate(Formula.EQUAL, left, right);
	}

	public static SetExtension mSetExtension(Expression... members) {
		return ff.makeSetExtension(members, null);
	}

	public static SimplePredicate mSimplePredicate(Expression expr) {
		return ff.makeSimplePredicate(Formula.KFINITE, expr, null);
	}

	public static UnaryExpression mUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	public static UnaryExpression mUnaryExpression(Expression child) {
		return mUnaryExpression(Formula.POW, child);
	}

	public static UnaryPredicate mUnaryPredicate(int tag, Predicate child) {
		return ff.makeUnaryPredicate(tag, child, null);
	}

	public static UnaryPredicate mUnaryPredicate(Predicate child) {
		return mUnaryPredicate(Formula.NOT, child);
	}

	public static BoundIdentDecl[] mList(BoundIdentDecl... idents) {
		return idents;
	}

	public static Expression[] mList(Expression... exprs) {
		return exprs;
	}

	public static FreeIdentifier[] mList(FreeIdentifier... idents) {
		return idents;
	}

	public static Predicate[] mList(Predicate... preds) {
		return preds;
	}

	public static String[] mList(String... names) {
		return names;
	}

	public static Type[] mList(Type... types) {
		return types;
	}

	public static BinaryExpression mMaplet(Expression left, Expression right) {
		return ff.makeBinaryExpression(Formula.MAPSTO, left, right, null);
	}

	public static ITypeEnvironment mTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}
	
	public static ITypeEnvironment mTypeEnvironment(String[] names, Type[] types) {
		assert names.length == types.length;
		ITypeEnvironment result = ff.makeTypeEnvironment();
		for (int i = 0; i < names.length; i++) {
			result.addName(names[i], types[i]);
		}
		return result;
	}
	
}
