package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.*;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;

/**
 * Unit test for formula normalization.
 * 
 * @author franz
 */
public class TestFlattener extends TestCase {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	private static FreeIdentifier id_x = ff.makeFreeIdentifier("x",null);
	
	private static BoundIdentDecl bd_a = ff.makeBoundIdentDecl("a",null);
	private static BoundIdentDecl bd_b = ff.makeBoundIdentDecl("b",null);
	private static BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x",null);
	private static BoundIdentDecl bd_y = ff.makeBoundIdentDecl("y",null);
	private static BoundIdentDecl bd_z = ff.makeBoundIdentDecl("z",null);
	private static BoundIdentDecl bd_s = ff.makeBoundIdentDecl("s",null);
	private static BoundIdentDecl bd_t = ff.makeBoundIdentDecl("t",null);

	private static LiteralPredicate btrue = ff.makeLiteralPredicate(Formula.BTRUE, null);

	
	private Expression[] unnormalizedExpressions = new Expression[] {
			mAssociativeExpression(Formula.PLUS,
					mAssociativeExpression(Formula.PLUS, id_x, id_x),
					mAssociativeExpression(Formula.MUL, id_x, id_x)
			),
			mAssociativeExpression(Formula.PLUS,
					mAssociativeExpression(Formula.PLUS,
							mAssociativeExpression(Formula.PLUS, id_x, id_x),
							id_x,
							id_x
					),
					mAssociativeExpression(Formula.MUL,
							id_x,
							id_x)
			),
			mAssociativeExpression(Formula.PLUS,
					mAssociativeExpression(Formula.PLUS, id_x, id_x),
					mAssociativeExpression(Formula.MUL, id_x, id_x),
					mAssociativeExpression(Formula.PLUS, id_x, id_x)
			),
	
			mUnaryExpression(Formula.UNMINUS, mIntegerLiteral(5)),
			mUnaryExpression(Formula.UNMINUS,
					mUnaryExpression(Formula.UNMINUS, mIntegerLiteral(5))
			),
			
			mSetExtension(),
	};
	
	private Expression[] normalizedExpressions = new Expression[] {
			mAssociativeExpression(Formula.PLUS,
					id_x,
					id_x,
					mAssociativeExpression(Formula.MUL, id_x, id_x)
			),
			mAssociativeExpression(Formula.PLUS,
					id_x,
					id_x,
					id_x,
					id_x,
					mAssociativeExpression(Formula.MUL, id_x,id_x)
			),
			mAssociativeExpression(Formula.PLUS, 
					id_x,
					id_x,
					mAssociativeExpression(Formula.MUL, id_x, id_x),
					id_x,
					id_x),
	
			mIntegerLiteral(-5),
			mIntegerLiteral(5),
			
			mAtomicExpression(Formula.EMPTYSET),
	};
	
	private Predicate[] unnormalizedPredicates = new Predicate[] {
			mAssociativePredicate(Formula.LOR,
					mAssociativePredicate(Formula.LOR,  btrue, btrue),
					mAssociativePredicate(Formula.LAND, btrue, btrue)
			),
			mAssociativePredicate(Formula.LOR,
					mAssociativePredicate(Formula.LOR,
							mAssociativePredicate(Formula.LOR, btrue, btrue),
							btrue,
							btrue
					),
					mAssociativePredicate(Formula.LAND, btrue, btrue)
			),
			mAssociativePredicate(Formula.LOR,
					mAssociativePredicate(Formula.LOR,  btrue, btrue),
					mAssociativePredicate(Formula.LAND, btrue, btrue),
					mAssociativePredicate(Formula.LOR,  btrue, btrue)
			),
	
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_y), btrue)
			),
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_s, bd_t), btrue)
			),

			mQuantifiedPredicate(Formula.FORALL, mList(bd_x),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_y),
							mQuantifiedPredicate(Formula.FORALL, mList(bd_z), btrue)
					)
			),
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_s, bd_t),
							mQuantifiedPredicate(Formula.FORALL, mList(bd_a, bd_b), btrue)
					)
			),
			
	};
	
	private Predicate[] normalizedPredicates = new Predicate[] {
			mAssociativePredicate(Formula.LOR,
					btrue,
					btrue,
					mAssociativePredicate(Formula.LAND, btrue, btrue)
			),
			mAssociativePredicate(Formula.LOR,
					btrue,
					btrue,
					btrue,
					btrue,
					mAssociativePredicate(Formula.LAND, btrue,btrue)
			),
			mAssociativePredicate(Formula.LOR,
					btrue,
					btrue,
					mAssociativePredicate(Formula.LAND, btrue, btrue),
					btrue,
					btrue
			),
	
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y), btrue),
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y, bd_s, bd_t), btrue),
			
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y, bd_z), btrue),
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y, bd_s, bd_t, bd_a, bd_b), btrue),

	};
	
	private Expression[] constructExpressions(Predicate[] preds, Expression exprs[]) {
		Expression[] expressions = new Expression[7 * preds.length];
		int l = 0;
		
		for (int i = 0; i < preds.length; i++) {
			expressions[l++] = mBinaryExpression(exprs[i%exprs.length], exprs[i%exprs.length]);
			expressions[l++] = mAtomicExpression(Formula.TRUE);
			expressions[l++] = mBoolExpression(preds[i]);
			expressions[l++] = mQuantifiedExpression(mList(bd_x), preds[i], exprs[i%exprs.length]);
			expressions[l++] = mSetExtension(exprs[i%exprs.length]);
			expressions[l++] = mUnaryExpression(Formula.POW, exprs[i%exprs.length]);
			expressions[l++] = id_x;
		}
		return expressions;
	}
	
	private Predicate[] constructPredicates(Predicate[] preds, Expression exprs[]) {
		Predicate[] predicates = new Predicate[preds.length * 5];
		int l = 0;
		for (int i = 0; i < preds.length; i++) {
			predicates[l++] = mBinaryPredicate(preds[i], preds[i]);
			predicates[l++] = mLiteralPredicate();
			predicates[l++] = mSimplePredicate(exprs[i%exprs.length]);
			predicates[l++] = mRelationalPredicate(exprs[i%exprs.length], exprs[i%exprs.length]);
			predicates[l++] = mUnaryPredicate(preds[i]);
		}
		return predicates;
	}

	private static AssociativeExpression mAssociativeExpression(
			int tag, Expression... children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	private AssociativePredicate mAssociativePredicate(int tag, Predicate... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}

	private static AtomicExpression mAtomicExpression(int tag) {
		return ff.makeAtomicExpression(tag, null);
	}

	private static IntegerLiteral mIntegerLiteral(int i) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(i), null);
	}

	private Predicate mQuantifiedPredicate(int tag, BoundIdentDecl[] idents, Predicate pred) {
		return ff.makeQuantifiedPredicate(tag, idents, pred, null);
	}

	private static UnaryExpression mUnaryExpression(int tag, Expression expr) {
		return ff.makeUnaryExpression(tag, expr, null);
	}
	
	private void routineTest(Formula[] expressions, Formula[] norms) {
		for (int i = 0; i < expressions.length; i++) {
			assertEquals("\nTest failed:\nString to normalize: "
					+ expressions[i] + "\nTree: "
					+ expressions[i].getSyntaxTree() + "\nExpected result: "
					+ norms[i] + "\nTree: " + norms[i].getSyntaxTree()
					+ "\nObtained tree:\n"
					+ expressions[i].flatten(ff).getSyntaxTree(),
					expressions[i].flatten(ff), norms[i]);
		}
	}
	
	/**
	 * Main test routine. 
	 */
	public void testNormalizer() {
		routineTest(unnormalizedExpressions,normalizedExpressions);
		routineTest(unnormalizedPredicates,normalizedPredicates);
		routineTest(constructExpressions(unnormalizedPredicates,unnormalizedExpressions),constructExpressions(normalizedPredicates,normalizedExpressions));
		routineTest(constructPredicates(unnormalizedPredicates, unnormalizedExpressions),constructPredicates(normalizedPredicates,normalizedExpressions));
	}
	
	/**
	 * Ensures that flattening an already flattened formula doesn't create a new
	 * formula.
	 */
	public void testNormalizerNop() {
		assertNop(normalizedExpressions);
		assertNop(normalizedPredicates);
		assertNop(constructExpressions(normalizedPredicates,normalizedExpressions));
		assertNop(constructPredicates(normalizedPredicates,normalizedExpressions));
	}

	private void assertNop(Formula[] formulas) {
		for (Formula formula : formulas) {
			final Formula flattened = formula.flatten(ff);
			assertEquals("Flattener not involutive for formula: " + formula, formula, flattened);
			assertSame("Flattener created a copy of formula: " + formula, formula, flattened);
		}
	}
	
}
