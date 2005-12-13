package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BCOMP;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.FCOMP;
import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_BINARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_BINARY_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_RELATIONAL_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_UNARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_UNARY_PREDICATE;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.OVR;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mMaplet;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;
import static org.eventb.core.ast.tests.ITestHelper.ASSOCIATIVE_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.ASSOCIATIVE_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.BINARY_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.BINARY_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.QUANTIFIED_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.QUANTIFIED_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.RELATIONAL_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.UNARY_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.UNARY_PREDICATE_LENGTH;
import junit.framework.TestCase;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;



/**
 * This class tests the unparser, which is the toString method. It is supposed
 * to return the formula with as less parentheses as possible. However, it is
 * not possible to do it without writing by hand all possible combinations of
 * formulae. This test takes an abstract syntax tree, unparses it, parses it
 * again and verifies that the tree obtained is the same as the one given. This
 * test tests all possible combinations (with regards to the operators) of
 * Expressions, and all combinations of Predicates.
 * 
 * @author fterrier
 */
public class TestUnparse extends TestCase {

	private static FormulaFactory ff = new FormulaFactory();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	private static FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	private static FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
	private static FreeIdentifier id_z = ff.makeFreeIdentifier("z", null);
	private static FreeIdentifier id_t = ff.makeFreeIdentifier("t", null);
	private static FreeIdentifier id_f = ff.makeFreeIdentifier("f", null);
	private static FreeIdentifier id_A = ff.makeFreeIdentifier("A", null);
	private static FreeIdentifier id_g = ff.makeFreeIdentifier("g", null);
	
	private static BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
	private static BoundIdentDecl bd_y = ff.makeBoundIdentDecl("y", null);
	private static BoundIdentDecl bd_z = ff.makeBoundIdentDecl("z", null);

	private static BoundIdentifier b0 = ff.makeBoundIdentifier(0, null);
	private static BoundIdentifier b1 = ff.makeBoundIdentifier(1, null);

	private static LiteralPredicate btrue = ff.makeLiteralPredicate(BTRUE, null);

	private static IntegerLiteral two = ff.makeIntegerLiteral(Common.TWO, null);
	
	private static class TestPair {
		String image;
		Predicate formula;
		TestPair(String image, Predicate formula) {
			this.image = image;
			this.formula = formula;
		}
	}
	
	/*
	 * this verifies that each child of an associative expression is treated the
	 * same way by the parser/unparser
	 */
	private TestPair[] associativeExpressionTestPairs = new TestPair[] {
			// {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
			new TestPair(
					"finite(x\u2217y\u222ax\u2217y\u222ax\u2217y)",
					buildExpression(MUL, BUNION)
			), new TestPair(
					"finite((x\u222ay)\u2217(x\u222ay)\u2217(x\u222ay))",
					buildExpression(BUNION, MUL)
			), new TestPair(
					"finite(x+y\u222ax+y\u222ax+y)",
					buildExpression(PLUS, BUNION)
			), new TestPair(
					"finite((x\u222ay)+(x\u222ay)+(x\u222ay))",
					buildExpression(BUNION, PLUS)
			), new TestPair(
					"finite((x\ue103y)\u222a(x\ue103y)\u222a(x\ue103y))",
					buildExpression(OVR, BUNION)
			), new TestPair(
					"finite((x\u222ay)\ue103(x\u222ay)\ue103(x\u222ay))",
					buildExpression(BUNION, OVR)
			), new TestPair(
					"finite((x;y)\u222a(x;y)\u222a(x;y))",
					buildExpression(FCOMP, BUNION)
			), new TestPair(
					"finite((x\u222ay);(x\u222ay);(x\u222ay))",
					buildExpression(BUNION, FCOMP)
			), new TestPair(
					"finite((x\u2218y)\u222a(x\u2218y)\u222a(x\u2218y))",
					buildExpression(BCOMP, BUNION)
			), new TestPair(
					"finite((x\u222ay)\u2218(x\u222ay)\u2218(x\u222ay))",
					buildExpression(BUNION, BCOMP)
			), new TestPair(
					"finite((x\u2229y)\u222a(x\u2229y)\u222a(x\u2229y))",
					buildExpression(BINTER, BUNION)
			), new TestPair(
					"finite((x\u222ay)\u2229(x\u222ay)\u2229(x\u222ay))",
					buildExpression(BUNION, BINTER)
			), new TestPair(
					"finite((x\u2218y)\u2229(x\u2218y)\u2229(x\u2218y))",
					buildExpression(BCOMP, BINTER)
			), new TestPair(
					"finite((x\u2229y)\u2218(x\u2229y)\u2218(x\u2229y))",
					buildExpression(BINTER, BCOMP)
			), new TestPair(
					"finite((x;y)\u2229(x;y)\u2229(x;y))",
					buildExpression(FCOMP, BINTER)
			), new TestPair(
					"finite((x\u2229y);(x\u2229y);(x\u2229y))",
					buildExpression(BINTER, FCOMP)
			), new TestPair(
					"finite((x\ue103y)\u2229(x\ue103y)\u2229(x\ue103y))",
					buildExpression(OVR, BINTER)
			), new TestPair(
					"finite((x\u2229y)\ue103(x\u2229y)\ue103(x\u2229y))",
					buildExpression(BINTER, OVR)
			), new TestPair(
					"finite(x+y\u2229x+y\u2229x+y)",
					buildExpression(PLUS, BINTER)
			), new TestPair(
					"finite((x\u2229y)+(x\u2229y)+(x\u2229y))",
					buildExpression(BINTER, PLUS)
			), new TestPair(
					"finite(x\u2217y\u2229x\u2217y\u2229x\u2217y)",
					buildExpression(MUL, BINTER)
			), new TestPair(
					"finite((x\u2229y)\u2217(x\u2229y)\u2217(x\u2229y))",
					buildExpression(BINTER, MUL)
			), new TestPair(
					"finite((x;y)\u2218(x;y)\u2218(x;y))",
					buildExpression(FCOMP, BCOMP)
			), new TestPair(
					"finite((x\u2218y);(x\u2218y);(x\u2218y))",
					buildExpression(BCOMP, FCOMP)
			), new TestPair(
					"finite((x\ue103y)\u2218(x\ue103y)\u2218(x\ue103y))",
					buildExpression(OVR, BCOMP)
			), new TestPair(
					"finite((x\u2218y)\ue103(x\u2218y)\ue103(x\u2218y))",
					buildExpression(BCOMP, OVR)
			), new TestPair(
					"finite(x+y\u2218x+y\u2218x+y)",
					buildExpression(PLUS, BCOMP)
			), new TestPair(
					"finite((x\u2218y)+(x\u2218y)+(x\u2218y))",
					buildExpression(BCOMP, PLUS)
			), new TestPair(
					"finite(x\u2217y\u2218x\u2217y\u2218x\u2217y)",
					buildExpression(MUL, BCOMP)
			), new TestPair(
					"finite((x\u2218y)\u2217(x\u2218y)\u2217(x\u2218y))",
					buildExpression(BCOMP, MUL)
			), new TestPair(
					"finite((x\ue103y);(x\ue103y);(x\ue103y))",
					buildExpression(OVR, FCOMP)
			), new TestPair(
					"finite((x;y)\ue103(x;y)\ue103(x;y))",
					buildExpression(FCOMP, OVR)
			), new TestPair(
					"finite(x+y;x+y;x+y)",
					buildExpression(PLUS, FCOMP)
			), new TestPair(
					"finite((x;y)+(x;y)+(x;y))",
					buildExpression(FCOMP, PLUS)
			), new TestPair(
					"finite(x\u2217y;x\u2217y;x\u2217y)",
					buildExpression(MUL, FCOMP)
			), new TestPair(
					"finite((x;y)\u2217(x;y)\u2217(x;y))",
					buildExpression(FCOMP, MUL)
			), new TestPair(
					"finite(x+y\ue103x+y\ue103x+y)",
					buildExpression(PLUS, OVR)
			), new TestPair(
					"finite((x\ue103y)+(x\ue103y)+(x\ue103y))",
					buildExpression(OVR, PLUS)
			), new TestPair(
					"finite(x\u2217y\ue103x\u2217y\ue103x\u2217y)",
					buildExpression(MUL, OVR)
			), new TestPair(
					"finite((x\ue103y)\u2217(x\ue103y)\u2217(x\ue103y))",
					buildExpression(OVR, MUL)
			), new TestPair(
					"finite(x\u2217y+x\u2217y+x\u2217y)",
					buildExpression(MUL, PLUS)
			), new TestPair(
					"finite((x+y)\u2217(x+y)\u2217(x+y))",
					buildExpression(PLUS, MUL)
			),
	};
	
	private TestPair[] associativePredicateTestPairs = new TestPair[] {
//		"((\u22a4\u21d4\u22a4)\u21d4(\u22a4\u21d4\u22a4))\u21d4(\u22a4\u21d4\u22a4)",
//		ff.makeAssociativePredicate(new Predicate[]{ff.makeAssociativePredicate(new Predicate[]{ff.makeAssociativePredicate(new Predicate[]{btrue,btrue},LEQV,null),ff.makeAssociativePredicate(new Predicate[]{btrue,btrue},LEQV,null)},LEQV,null), ff.makeAssociativePredicate(new Predicate[]{btrue,btrue},LEQV,null)},LEQV,null),
//		"(\u22a4\u21d4\u22a4)\u21d4((\u22a4\u21d4\u22a4)\u21d4(\u22a4\u21d4\u22a4))",
//		ff.makeAssociativePredicate(new Predicate[]{ff.makeAssociativePredicate(new Predicate[]{btrue,btrue},LEQV,null), ff.makeAssociativePredicate(new Predicate[]{ff.makeAssociativePredicate(new Predicate[]{btrue,btrue},LEQV,null),ff.makeAssociativePredicate(new Predicate[]{btrue,btrue},LEQV,null)},LEQV,null)},LEQV,null),
//		// not correct
//		"(\u22a4\u21d4\u22a4)\u2227(\u22a4\u21d4\u22a4)\u2227(\u22a4\u21d4\u22a4)",
//		buildPredicates(LEQV, LAND),
//		"\u22a4\u2227\u22a4\u21d4\u22a4\u2227\u22a4\u21d4\u22a4\u2227\u22a4",
//		buildPredicates(LAND, LEQV),
//		"(\u22a4\u21d4\u22a4)\u2228(\u22a4\u21d4\u22a4)\u2228(\u22a4\u21d4\u22a4)",
//		buildPredicates(LEQV, LOR),
//		"\u22a4\u2228\u22a4\u21d4\u22a4\u2228\u22a4\u21d4\u22a4\u2228\u22a4",
//		buildPredicates(LOR, LEQV),
			new TestPair(
					"(\u22a4\u2227\u22a4)\u2227(\u22a4\u2227\u22a4)\u2227(\u22a4\u2227\u22a4)",
					buildPredicate(LAND, LAND)
			), new TestPair(
					"(\u22a4\u2227\u22a4)\u2228(\u22a4\u2227\u22a4)\u2228(\u22a4\u2227\u22a4)",
					buildPredicate(LAND, LOR)
			), new TestPair(
					"(\u22a4\u2228\u22a4)\u2227(\u22a4\u2228\u22a4)\u2227(\u22a4\u2228\u22a4)",
					buildPredicate(LOR, LAND)
			), new TestPair(
					"(\u22a4\u2228\u22a4)\u2228(\u22a4\u2228\u22a4)\u2228(\u22a4\u2228\u22a4)",
					buildPredicate(LOR, LOR)
			),
	};
			
	// test empty setext
	private TestPair[] uncommonFormulaeTestPairs = new TestPair[] {
			new TestPair(
					"finite({})",
					mSimplePredicate(mSetExtension())
					), new TestPair(
					"finite(A ◁ f;g)",
					mSimplePredicate(mAssociativeExpression(FCOMP,
							mBinaryExpression(DOMRES, id_A, id_f),
							id_g 
					))
// TODO restore test of optimality of toString()
//			), new TestPair(
//					"finite(f;g ▷ A)",
//					mSimplePredicate(mAssociativeExpression(FCOMP,
//							id_f, 
//							mBinaryExpression(RANRES, id_g, id_A)
//					))
			),
	};
	
	private Predicate buildPredicate(int firstTag, int secondTag) {
		return mAssociativePredicate(secondTag,
				mAssociativePredicate(firstTag, btrue, btrue),
				mAssociativePredicate(firstTag, btrue, btrue),
				mAssociativePredicate(firstTag, btrue, btrue)
		);
	}
	
	private Predicate buildExpression(int firstTag, int secondTag) {
		return mSimplePredicate(
				mAssociativeExpression(secondTag,
						mAssociativeExpression(firstTag, id_x, id_y),
						mAssociativeExpression(firstTag, id_x, id_y),
						mAssociativeExpression(firstTag, id_x, id_y)
				));
	}
	
	//--------------------------------------------------------------
	//
	//  SYSTEMATIC TESTS
	//
	//--------------------------------------------------------------

	private Expression[] constructBinaryBinaryTrees () {
//		 {MAPSTO,REL,TREL,SREL,STREL,PFUN,TFUN,PINJ,TINJ,PSUR,TSUR,TBIJ,SETMINUS,CPROD,DPROD,PPROD,DOMRES,DOMSUB,RANRES,RANSUB,UPTO,MINUS,DIV,MOD,EXPN}
		int length = BINARY_EXPRESSION_LENGTH;
		Expression[]  formulae = new Expression[(length)*(length)*2];
		int idx = 0;
		for (int i=0;i<length;i++) {
			for (int j=0;j<length;j++) {
				formulae[idx ++] = mBinaryExpression(
						j + FIRST_BINARY_EXPRESSION,
						mBinaryExpression(i + FIRST_BINARY_EXPRESSION, id_x, id_y),
						id_z);
				formulae[idx ++] = mBinaryExpression(
						j + FIRST_BINARY_EXPRESSION, 
						id_x, 
						mBinaryExpression(i + FIRST_BINARY_EXPRESSION, id_y, id_z));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Formula[] constructAssociativeAssociativeTrees() {
		// {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
		int length = ASSOCIATIVE_EXPRESSION_LENGTH;
		Expression[]  formulae = new Expression[3 * length * length];
		int idx = 0;
		for (int i=0;i<length;i++) {
			for (int j=0;j<length;j++) {
				formulae[idx ++] = mAssociativeExpression(
						j + FIRST_ASSOCIATIVE_EXPRESSION,
						mAssociativeExpression(i + FIRST_ASSOCIATIVE_EXPRESSION, id_y, id_z),
						id_x);
				formulae[idx ++] = mAssociativeExpression(
						j + FIRST_ASSOCIATIVE_EXPRESSION,
						id_x,
						mAssociativeExpression(i + FIRST_ASSOCIATIVE_EXPRESSION, id_y, id_z),
						id_t);
				formulae[idx ++] = mAssociativeExpression(
						j + FIRST_ASSOCIATIVE_EXPRESSION,
						id_x,
						mAssociativeExpression(i + FIRST_ASSOCIATIVE_EXPRESSION, id_y, id_z));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructAssociativeBinaryTrees() {
		Expression[]  formulae = new Expression[5 * ASSOCIATIVE_EXPRESSION_LENGTH * BINARY_EXPRESSION_LENGTH];
		int idx = 0;
		for (int i=0;i<ASSOCIATIVE_EXPRESSION_LENGTH;i++) {
			for (int j=0;j<BINARY_EXPRESSION_LENGTH;j++) {
				formulae[idx ++] = mAssociativeExpression(
						i + FIRST_ASSOCIATIVE_EXPRESSION,
						id_x,
						mBinaryExpression(j + FIRST_BINARY_EXPRESSION, id_y, id_z));
				formulae[idx ++] = mAssociativeExpression(
						i + FIRST_ASSOCIATIVE_EXPRESSION,
						id_x,
						mBinaryExpression(j + FIRST_BINARY_EXPRESSION, id_y, id_z),
						id_t);
				formulae[idx ++] = mAssociativeExpression(
						i + FIRST_ASSOCIATIVE_EXPRESSION,
						mBinaryExpression(j + FIRST_BINARY_EXPRESSION, id_x, id_y),
						id_z);
				formulae[idx ++] = mBinaryExpression(
						j + FIRST_BINARY_EXPRESSION,
						mAssociativeExpression(i + FIRST_ASSOCIATIVE_EXPRESSION, id_x, id_y),
						id_z);
				formulae[idx ++] = mBinaryExpression(
						j + FIRST_BINARY_EXPRESSION,
						id_x,
						mAssociativeExpression(i + FIRST_ASSOCIATIVE_EXPRESSION, id_y, id_z));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructBinaryUnaryTrees() {
//		 {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID, KMIN, KMAX, CONVERSE}
		Expression[]  formulae = new Expression[(UNARY_EXPRESSION_LENGTH)*(BINARY_EXPRESSION_LENGTH)*3];
		int idx = 0;
		for (int i=0;i<UNARY_EXPRESSION_LENGTH;i++) {
			for (int j=0;j<BINARY_EXPRESSION_LENGTH;j++) {
				formulae[idx ++] = mUnaryExpression(
						i + FIRST_UNARY_EXPRESSION, 
						mBinaryExpression(j + FIRST_BINARY_EXPRESSION, id_x, id_y));
				formulae[idx ++] = mBinaryExpression(
						j + FIRST_BINARY_EXPRESSION,
						mUnaryExpression(i + FIRST_UNARY_EXPRESSION, id_x),
						id_y);
				formulae[idx ++] = mBinaryExpression(
						j + FIRST_BINARY_EXPRESSION,
						id_x,
						mUnaryExpression(i + FIRST_UNARY_EXPRESSION, id_y));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructAssociativeUnaryTrees() {
//		 {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID, KMIN, KMAX, CONVERSE}
		Expression[]  formulae = new Expression[4 * UNARY_EXPRESSION_LENGTH * ASSOCIATIVE_EXPRESSION_LENGTH];
		int idx = 0;
		for (int i=0;i<UNARY_EXPRESSION_LENGTH;i++) {
			for (int j=0;j<ASSOCIATIVE_EXPRESSION_LENGTH;j++) {
				formulae[idx ++] = mUnaryExpression(
						i + FIRST_UNARY_EXPRESSION,
						mAssociativeExpression(j + FIRST_ASSOCIATIVE_EXPRESSION, id_x, id_y));
				formulae[idx ++] = mAssociativeExpression(
						j + FIRST_ASSOCIATIVE_EXPRESSION,
						id_x,
						mUnaryExpression(i + FIRST_UNARY_EXPRESSION, id_y));
				formulae[idx ++] = mAssociativeExpression(
						j + FIRST_ASSOCIATIVE_EXPRESSION,
						id_x,
						mUnaryExpression(i + FIRST_UNARY_EXPRESSION, id_y), 
						id_z);
				formulae[idx ++] = mAssociativeExpression(
						j + FIRST_ASSOCIATIVE_EXPRESSION, 
						mUnaryExpression(i + FIRST_UNARY_EXPRESSION, id_x), 
						id_y);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructUnaryUnaryTrees() {
		Expression[]  formulae = new Expression[(UNARY_EXPRESSION_LENGTH)*(UNARY_EXPRESSION_LENGTH)*2];
		int idx = 0;
		for (int i=0;i<UNARY_EXPRESSION_LENGTH;i++) {
			for (int j=0;j<UNARY_EXPRESSION_LENGTH;j++) {
				formulae[idx ++] = mUnaryExpression(
						i + FIRST_UNARY_EXPRESSION, 
						mUnaryExpression(j + FIRST_UNARY_EXPRESSION, id_x));
				formulae[idx ++] = mUnaryExpression(
						j + FIRST_UNARY_EXPRESSION,
						mUnaryExpression(i + FIRST_UNARY_EXPRESSION, id_x));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	// test that they are all handled the same way
	private Expression[] constructQuantifiedQuantifiedTrees() {
		return new Expression[] {
			makeQuantifiedExpression(CSET, CSET, Implicit, Lambda),
			makeQuantifiedExpression(CSET, CSET, Explicit, Explicit),
			makeQuantifiedExpression(CSET, CSET, Explicit, Lambda),
			makeQuantifiedExpression(CSET, CSET, Explicit, Implicit),
			makeQuantifiedExpression(CSET, CSET, Implicit, Explicit),

			makeQuantifiedExpression(CSET, QUNION, Explicit, Explicit),
			makeQuantifiedExpression(CSET, QUNION, Implicit, Explicit),
			makeQuantifiedExpression(CSET, QUNION, Explicit, Implicit),
			
			makeQuantifiedExpression(QUNION, CSET, Explicit, Explicit),
			makeQuantifiedExpression(QUNION, CSET, Explicit, Implicit),
			makeQuantifiedExpression(QUNION, CSET, Explicit, Lambda),
			makeQuantifiedExpression(QUNION, CSET, Implicit, Explicit),
			makeQuantifiedExpression(QUNION, CSET, Implicit, Lambda),
			
			makeQuantifiedExpression(CSET, QINTER, Explicit, Explicit),
			makeQuantifiedExpression(CSET, QINTER, Implicit, Explicit),
			makeQuantifiedExpression(CSET, QINTER, Explicit, Implicit),
			
			makeQuantifiedExpression(QINTER, CSET, Explicit, Explicit),
			makeQuantifiedExpression(QINTER, CSET, Explicit, Implicit),
			makeQuantifiedExpression(QINTER, CSET, Explicit, Lambda),
			makeQuantifiedExpression(QINTER, CSET, Implicit, Explicit),
			makeQuantifiedExpression(QINTER, CSET, Implicit, Lambda),
			
			makeQuantifiedExpression(QINTER, QUNION, Explicit, Explicit),
			makeQuantifiedExpression(QUNION, QINTER, Explicit, Explicit),
			makeQuantifiedExpression(QINTER, QINTER, Explicit, Explicit),
			makeQuantifiedExpression(QUNION, QUNION, Explicit, Explicit),
			makeQuantifiedExpression(QINTER, QUNION, Implicit, Explicit),
			makeQuantifiedExpression(QUNION, QINTER, Implicit, Explicit),
			makeQuantifiedExpression(QINTER, QINTER, Explicit, Implicit),
			makeQuantifiedExpression(QUNION, QUNION, Explicit, Implicit),
		};
	}
	
	private Expression makeQuantifiedExpression(int firstTag, int secondTag, QuantifiedExpression.Form firstForm, QuantifiedExpression.Form secondForm) {
		return mQuantifiedExpression(
				firstTag,
				firstForm,
				mList(bd_z),
				btrue,
				mQuantifiedExpression(
						secondTag,
						secondForm,
						(secondForm == Implicit ? mList(bd_y,bd_z) : mList(bd_y)),
						btrue,
						mBinaryExpression(MAPSTO,
								secondForm == Lambda ? b0 : b1,
								secondForm == Implicit || firstForm == Implicit
								? (secondForm == Lambda ? b1 : b0)
								: id_x)
				));
	}
	
	private Expression mQExpr(int qtag, QuantifiedExpression.Form form, BoundIdentDecl bid,
			Expression expr) {
		if (form == Lambda) {
			return mQuantifiedExpression(qtag, form, mList(bid), btrue, mMaplet(b0, expr));
		}
		return mQuantifiedExpression(qtag, form, mList(bid), btrue, mMaplet(b0, expr));
	}
	
	private Expression[] constructQuantifiedBinaryTrees() {
		final int length = 3 * BINARY_EXPRESSION_LENGTH * (2 * QUANTIFIED_EXPRESSION_LENGTH + 1);
		Expression[]  formulae = new Expression[length];
		int idx = 0;
		for (int i=0;i<QUANTIFIED_EXPRESSION_LENGTH;i++) {
			for (int j = 0; j < BINARY_EXPRESSION_LENGTH; j++) {
				for (QuantifiedExpression.Form form: QuantifiedExpression.Form.values()) {
					if (form == Lambda && FIRST_QUANTIFIED_EXPRESSION + i != CSET)
						continue;
					formulae[idx ++] = mQExpr(i + FIRST_QUANTIFIED_EXPRESSION, form, 
							bd_x,
							mBinaryExpression(j + FIRST_BINARY_EXPRESSION, id_y, id_z));
					formulae[idx ++] = mBinaryExpression(j + FIRST_BINARY_EXPRESSION,
							id_x,
							mQExpr(i + FIRST_QUANTIFIED_EXPRESSION, form, bd_y, id_z));
					formulae[idx ++] = mBinaryExpression(j + FIRST_BINARY_EXPRESSION,
							mQExpr(i + FIRST_QUANTIFIED_EXPRESSION, form, bd_x, id_y),
							id_z);
				}
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Expression[] constructQuantifiedAssociativeTree() {
		final int length = 4 * ASSOCIATIVE_EXPRESSION_LENGTH * (2 * QUANTIFIED_EXPRESSION_LENGTH + 1);
		Expression[]  formulae = new Expression[length];
		int idx = 0;
		for (int i=0;i<QUANTIFIED_EXPRESSION_LENGTH;i++) {
			for (int j = 0; j < ASSOCIATIVE_EXPRESSION_LENGTH; j++) {
				for (QuantifiedExpression.Form form: QuantifiedExpression.Form.values()) {
					if (form == Lambda && FIRST_QUANTIFIED_EXPRESSION + i != CSET)
						continue;
					formulae[idx ++] = mQExpr(i+FIRST_QUANTIFIED_EXPRESSION, form, bd_x,
							mAssociativeExpression(j + FIRST_ASSOCIATIVE_EXPRESSION, id_y, id_z));
					formulae[idx ++] = mAssociativeExpression(j + FIRST_ASSOCIATIVE_EXPRESSION,
							id_x,
							mQExpr(i + FIRST_QUANTIFIED_EXPRESSION, form, bd_y, id_z));
					formulae[idx ++] = mAssociativeExpression(j + FIRST_ASSOCIATIVE_EXPRESSION,
							id_x,
							mQExpr(i + FIRST_QUANTIFIED_EXPRESSION, form, bd_y, id_z),
							id_t);
					formulae[idx ++] = mAssociativeExpression(j + FIRST_ASSOCIATIVE_EXPRESSION, 
							mQExpr(i + FIRST_QUANTIFIED_EXPRESSION, form, bd_x, id_y),
							id_z);
				}
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructQuantifiedUnaryTree() {
		final int length = 2 * UNARY_EXPRESSION_LENGTH * (2 * QUANTIFIED_EXPRESSION_LENGTH + 1);
		Expression[]  formulae = new Expression[length];
		int idx = 0;
		for (int i = 0; i < QUANTIFIED_EXPRESSION_LENGTH; i++) {
			for (int j = 0; j < UNARY_EXPRESSION_LENGTH; j++) {
				for (QuantifiedExpression.Form form: QuantifiedExpression.Form.values()) {
					if (form == Lambda && FIRST_QUANTIFIED_EXPRESSION + i != CSET)
						continue;
					formulae[idx ++] = mQExpr(FIRST_QUANTIFIED_EXPRESSION + i, form, bd_x, 
							mUnaryExpression(FIRST_UNARY_EXPRESSION + j, b0));
					formulae[idx ++] = mUnaryExpression(FIRST_UNARY_EXPRESSION + j,
							mQExpr(FIRST_QUANTIFIED_EXPRESSION + i, form, bd_x, b0));
				}
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructAssociativeAssociativePredicateTree() {
		final int length = ASSOCIATIVE_PREDICATE_LENGTH;
		Predicate[]  formulae = new Predicate[length * length * 3];
		int idx = 0;
		for (int i=0;i<length;i++) {
			for (int j=0;j<length;j++) {
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE, btrue, btrue),
						btrue);
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE, btrue, btrue),
						btrue);
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE, btrue, btrue));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructBinaryBinaryPredicateTrees () {
		int length = BINARY_PREDICATE_LENGTH;
		Predicate[]  formulae = new Predicate[(length)*(length)*2];
		int idx = 0;
		for (int i=0;i<length;i++) {
			for (int j=0;j<length;j++) {
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						mBinaryPredicate(i+FIRST_BINARY_PREDICATE, btrue, btrue),
						btrue);
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						btrue,
						mBinaryPredicate(i+FIRST_BINARY_PREDICATE, btrue, btrue));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructAssociativeBinaryPredicateTrees() {
		Predicate[]  formulae = new Predicate[(ASSOCIATIVE_PREDICATE_LENGTH)*(BINARY_PREDICATE_LENGTH)*5];
		int idx = 0;
		for (int i=0;i<ASSOCIATIVE_PREDICATE_LENGTH;i++) {
			for (int j=0;j<BINARY_PREDICATE_LENGTH;j++) {
				formulae[idx ++] = mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mBinaryPredicate(j+FIRST_BINARY_PREDICATE, btrue, btrue));
				formulae[idx ++] = mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mBinaryPredicate(j+FIRST_BINARY_PREDICATE, btrue, btrue),
						btrue);
				formulae[idx ++] = mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE,
						mBinaryPredicate(j+FIRST_BINARY_PREDICATE, btrue, btrue),
						btrue);
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE, btrue,btrue),
						btrue);
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						btrue,
						mAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE, btrue,btrue));
				
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructUnaryUnaryPredicateTrees() {
		Predicate[]  formulae = new Predicate[(UNARY_PREDICATE_LENGTH)*(UNARY_PREDICATE_LENGTH)];
		int idx = 0;
		for (int i=0;i<UNARY_PREDICATE_LENGTH;i++) {
			for (int j=0;j<UNARY_PREDICATE_LENGTH;j++) {
				formulae[idx ++] = mUnaryPredicate(i+FIRST_UNARY_PREDICATE,
						mUnaryPredicate(j+FIRST_UNARY_PREDICATE, btrue));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructAssociativeUnaryPredicateTrees() {
		Predicate[]  formulae = new Predicate[(UNARY_PREDICATE_LENGTH)*(ASSOCIATIVE_PREDICATE_LENGTH)*4];
		int idx = 0;
		for (int i=0;i<UNARY_PREDICATE_LENGTH;i++) {
			for (int j=0;j<ASSOCIATIVE_PREDICATE_LENGTH;j++) {
				formulae[idx ++] = mUnaryPredicate(i+FIRST_UNARY_PREDICATE,
						mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE, btrue,btrue));
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mUnaryPredicate(i+FIRST_UNARY_PREDICATE, btrue));
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mUnaryPredicate(i+FIRST_UNARY_PREDICATE, btrue),
						btrue);
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						mUnaryPredicate(i+FIRST_UNARY_PREDICATE, btrue),
						btrue);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructBinaryUnaryPredicateTrees() {
		Predicate[]  formulae = new Predicate[(UNARY_PREDICATE_LENGTH)*(BINARY_PREDICATE_LENGTH)*3];
		int idx = 0;
		for (int i=0;i<UNARY_PREDICATE_LENGTH;i++) {
			for (int j=0;j<BINARY_PREDICATE_LENGTH;j++) {
				formulae[idx ++] = mUnaryPredicate(i+FIRST_UNARY_PREDICATE,
						mBinaryPredicate(j+FIRST_BINARY_PREDICATE, btrue, btrue));
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						mUnaryPredicate(i+FIRST_UNARY_PREDICATE, btrue),
						btrue);
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						btrue,
						mUnaryPredicate(i+FIRST_UNARY_PREDICATE, btrue));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructQuantifiedQuantifiedPredicateTrees() {
		int length = QUANTIFIED_PREDICATE_LENGTH;
		Predicate[]  formulae = new Predicate[(length)*(length)];
		int idx = 0;
		for (int i=0;i<length;i++) {
			for (int j=0;j<length;j++) {
				formulae[idx ++] = mQuantifiedPredicate(j+FIRST_QUANTIFIED_PREDICATE,
						mList(bd_x),
						mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE, mList(bd_y), btrue));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructQuantifiedBinaryPredicateTrees() {
		Predicate[] formulae = new Predicate[QUANTIFIED_PREDICATE_LENGTH*BINARY_PREDICATE_LENGTH*3];
		int idx = 0;
		for (int i=0;i<QUANTIFIED_PREDICATE_LENGTH;i++) {
			for (int j=0;j<BINARY_PREDICATE_LENGTH;j++) {
				formulae[idx ++] = mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE,
						mList(bd_x),
						mBinaryPredicate(j+FIRST_BINARY_PREDICATE, btrue, btrue));
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE, mList(bd_x), btrue),
						btrue);
				formulae[idx ++] = mBinaryPredicate(j+FIRST_BINARY_PREDICATE,
						btrue,
						mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE, mList(bd_x), btrue));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructQuantifiedAssociativePredicateTrees() {
		Predicate[] formulae = new Predicate[QUANTIFIED_PREDICATE_LENGTH*ASSOCIATIVE_PREDICATE_LENGTH*4];
		int idx = 0;
		for (int i=0;i<QUANTIFIED_PREDICATE_LENGTH;i++) {
			for (int j=0;j<ASSOCIATIVE_PREDICATE_LENGTH;j++) {
				formulae[idx ++] = mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE,
						mList(bd_x),
						mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE, btrue, btrue));
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE, mList(bd_x), btrue));
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						btrue,
						mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE, mList(bd_x), btrue),
						btrue);
				formulae[idx ++] = mAssociativePredicate(j+FIRST_ASSOCIATIVE_PREDICATE,
						mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE, mList(bd_x), btrue),
						btrue);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructQuantifiedUnaryPredicateTrees() {
		Predicate[] formulae = new Predicate[QUANTIFIED_PREDICATE_LENGTH*UNARY_PREDICATE_LENGTH*2];
		int idx = 0;
		for (int i=0;i<QUANTIFIED_PREDICATE_LENGTH;i++) {
			for (int j=0;j<UNARY_PREDICATE_LENGTH;j++) {
				formulae[idx ++] = mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE,
						mList(bd_y),
						mUnaryPredicate(j+FIRST_UNARY_PREDICATE, btrue));
				formulae[idx ++] = mUnaryPredicate(j+FIRST_UNARY_PREDICATE,
						mQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE, mList(bd_y), btrue));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructRelop() {
		Expression[] exprs = Common.constructExpressions();
		Predicate[] formulae = new Predicate[2 * exprs.length * RELATIONAL_PREDICATE_LENGTH];
		int idx = 0;
		for (Expression expr: exprs) {
			for (int k = 0; k < RELATIONAL_PREDICATE_LENGTH; k++) {
				formulae[idx ++] = mRelationalPredicate(k+FIRST_RELATIONAL_PREDICATE, expr, id_t); 
				formulae[idx ++] = mRelationalPredicate(k+FIRST_RELATIONAL_PREDICATE, id_t, expr); 
			}
		}
		assert idx == formulae.length;
		return formulae; 
	}
	
	private Expression[] constructQuantifierWithPredicate() {
		Predicate[] preds = Common.constructPredicates();
		Expression[] formulae = new Expression[2 * preds.length * QUANTIFIED_EXPRESSION_LENGTH];
		int idx = 0;
		for (Predicate pred: preds) {
			for (int k = 0; k < QUANTIFIED_EXPRESSION_LENGTH; k++) {
				formulae[idx ++] = mQuantifiedExpression(k+FIRST_QUANTIFIED_EXPRESSION, Explicit,
						mList(bd_x), pred, two); 
				formulae[idx ++] = mQuantifiedExpression(k+FIRST_QUANTIFIED_EXPRESSION, Implicit,
						mList(bd_x), pred, b0); 
			}
		}
		assert idx == formulae.length;
		return formulae; 
	}
	
	// this test is for making sure that an associative expression treats all
	// its children the same way
	private void routineTestStringFormula(TestPair[] pairs) {
		IParseResult result = null;
		for (TestPair pair: pairs) {
			String formula = pair.formula.toString();
			String formulaParenthesized = pair.formula.toStringFullyParenthesized();
			assertEquals("\nTest failed on original String: "+pair.image+"\nUnparser produced: "+formula+"\n",
					pair.image, formula);
			
			result = ff.parsePredicate(formulaParenthesized);
			assertTrue("Parse failed on formula: " + formulaParenthesized, result.isSuccess());
			assertEquals(pair.formula, result.getParsedPredicate());
			
			result = ff.parsePredicate(formula);
			assertTrue("Parse failed on formula: " + formula, result.isSuccess());
			assertEquals(pair.formula, result.getParsedPredicate());
		}
	}
	
	/**
	 * Test of hand-written formulae
	 */
	public void testStringFormula() {
		routineTestStringFormula(associativeExpressionTestPairs);
		routineTestStringFormula(associativePredicateTestPairs);
		routineTestStringFormula(uncommonFormulaeTestPairs);
	}
	
	/**
	 * Test of automatically generated formulae
	 */
	public void testUnparse() {
		
		routineTest(constructAssociativeAssociativeTrees(), true);
		
		routineTest(constructBinaryBinaryTrees(), true);
		
		routineTest(constructUnaryUnaryTrees(), true);
		
		routineTest(constructQuantifiedQuantifiedTrees(), true);
		
		routineTest(constructAssociativeBinaryTrees(), true);
		
		routineTest(constructAssociativeUnaryTrees(), true);
	
		routineTest(constructBinaryUnaryTrees(), true);
		
		routineTest(constructQuantifiedBinaryTrees(), true);
		
		routineTest(constructQuantifiedAssociativeTree(), true);
		
		routineTest(constructQuantifiedUnaryTree(), true);
		
		routineTest(constructAssociativeAssociativePredicateTree(),false);
		
		routineTest(constructBinaryBinaryPredicateTrees(),false);
		
		routineTest(constructUnaryUnaryPredicateTrees(),false);
		
		routineTest(constructAssociativeBinaryPredicateTrees(),false);
		
		routineTest(constructAssociativeUnaryPredicateTrees(),false);
		
		routineTest(constructBinaryUnaryPredicateTrees(),false);
		
		routineTest(constructQuantifiedQuantifiedPredicateTrees(),false);
		
		routineTest(constructQuantifiedBinaryPredicateTrees(),false);
		
		routineTest(constructQuantifiedAssociativePredicateTrees(),false);
		
		routineTest(constructQuantifiedUnaryPredicateTrees(),false);
		
		routineTest(constructRelop(),false);
		
		routineTest(constructQuantifierWithPredicate(),true);
	}
	
	
	private void routineTest (Formula[] formulae, boolean isExpr) {
		IParseResult result = null;
		String formula = null;
		String formulaParenthesized = null;
		Predicate temp = null;
		for (int i = 0; i < formulae.length; i++) {
			if (isExpr) {
				Expression expr = (Expression) formulae[i];
				temp = ff.makeSimplePredicate(KFINITE, expr, null);
			}
			else {
				temp = (Predicate) formulae[i];
			}
			formula = temp.toString();
			formulaParenthesized = temp.toStringFullyParenthesized();
			result = ff.parsePredicate(formula);
			assertTrue("Parser failed on formula: "+ formula + "\nInput Tree was:\n"+ temp.getSyntaxTree() + "\n",
					result.isSuccess());
			assertEquals(
					"\nTest failed on original tree:\n"
					+ temp.getSyntaxTree()
					+ "Fully parenthesized: "
					+ temp.toStringFullyParenthesized()
					+ "\n" + "\nTest returned string: " + formula
					+ "\nParser produced tree:\n"
					+ result.getParsedPredicate().getSyntaxTree()
					+ "Fully parenthesized: "
					+ result.getParsedPredicate().toStringFullyParenthesized()
					+ "\n\n" + "Parser problems: "
					+ result.toString() + "\n",
					result.getParsedPredicate(), temp);
			assertEquals(result.getParsedPredicate().toString(), formula);
			
			// toStringFullyParenthesized
			result = ff.parsePredicate(formulaParenthesized);
			assertTrue("Parser failed on formula: "+ formulaParenthesized + "\nInput Tree was:\n"+ temp.getSyntaxTree() + "\n",
					result.isSuccess());
			assertEquals(
					"\nTest parenthesized failed on original tree:\n"
					+ temp.getSyntaxTree()
					+ "\n" + "\nTest returned string: " + formulaParenthesized
					+ "\nParser produced tree:\n"
					+ result.getParsedPredicate().getSyntaxTree()
					+ "\n\n" + "Parser problems: "
					+ result.toString() + "\n",
					result.getParsedPredicate(), temp);
		}
	}
}
