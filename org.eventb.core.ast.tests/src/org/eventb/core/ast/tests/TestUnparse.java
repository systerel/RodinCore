package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BCOMP;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CPROD;
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
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LEQV;
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

	public static final FormulaFactory ff = FormulaFactory.getDefault();
	
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
	
	private static abstract class TestPair {
		String image;
		TestPair(String image) {
			this.image = image;
		}
		abstract Formula getFormula();
		void parseAndCheck(String input) {
			// This check ensures that there is no unnecessary external parenthesis in
			// the given string
			if (input.charAt(0) != '(')
				return;
			final int length = input.length();
			int count = 1;
			for (int i = 1; i < length; i++) {
				switch (input.charAt(i)) {
				case '(':
					++ count;
					break;
				case ')':
					-- count;
					if (count == 0) {
						assertFalse("'" + input + "' contains unnecessary external parentheses",
								i == length - 1);
						return;
					}
					break;
				}
			}
			assertFalse("'" + input + "' contains unbalanced parentheses", true);
		}
	}
	
	private static class ExprTestPair extends TestPair {
		Expression formula;
		ExprTestPair(String image, Expression formula) {
			super(image);
			this.formula = formula;
		}
		@Override 
		Expression getFormula() {
			return formula;
		}
		@Override 
		void parseAndCheck(String input) {
			super.parseAndCheck(input);
			IParseResult result = ff.parseExpression(input);
			assertTrue("Parse failed", result.isSuccess());
			assertEquals("Unexpected parser result", formula, result.getParsedExpression());
		}
	}
	
	private static class PredTestPair extends TestPair {
		Predicate formula;
		PredTestPair(String image, Predicate formula) {
			super(image);
			this.formula = formula;
		}
		@Override 
		Predicate getFormula() {
			return formula;
		}
		@Override 
		void parseAndCheck(String input) {
			super.parseAndCheck(input);
			IParseResult result = ff.parsePredicate(input);
			assertTrue("Parse failed", result.isSuccess());
			assertEquals("Unexpected parser result", formula, result.getParsedPredicate());
		}
	}

	/*
	 * this verifies that each child of an associative expression is treated the
	 * same way by the parser/unparser
	 */
	private ExprTestPair[] associativeExpressionTestPairs = new ExprTestPair[] {
			// {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
			new ExprTestPair(
					"x\u2217y\u222ax\u2217y\u222ax\u2217y",
					buildExpression(MUL, BUNION)
			), new ExprTestPair(
					"(x\u222ay)\u2217(x\u222ay)\u2217(x\u222ay)",
					buildExpression(BUNION, MUL)
			), new ExprTestPair(
					"x+y\u222ax+y\u222ax+y",
					buildExpression(PLUS, BUNION)
			), new ExprTestPair(
					"(x\u222ay)+(x\u222ay)+(x\u222ay)",
					buildExpression(BUNION, PLUS)
			), new ExprTestPair(
					"(x\ue103y)\u222a(x\ue103y)\u222a(x\ue103y)",
					buildExpression(OVR, BUNION)
			), new ExprTestPair(
					"(x\u222ay)\ue103(x\u222ay)\ue103(x\u222ay)",
					buildExpression(BUNION, OVR)
			), new ExprTestPair(
					"(x;y)\u222a(x;y)\u222a(x;y)",
					buildExpression(FCOMP, BUNION)
			), new ExprTestPair(
					"(x\u222ay);(x\u222ay);(x\u222ay)",
					buildExpression(BUNION, FCOMP)
			), new ExprTestPair(
					"(x\u2218y)\u222a(x\u2218y)\u222a(x\u2218y)",
					buildExpression(BCOMP, BUNION)
			), new ExprTestPair(
					"(x\u222ay)\u2218(x\u222ay)\u2218(x\u222ay)",
					buildExpression(BUNION, BCOMP)
			), new ExprTestPair(
					"(x\u2229y)\u222a(x\u2229y)\u222a(x\u2229y)",
					buildExpression(BINTER, BUNION)
			), new ExprTestPair(
					"(x\u222ay)\u2229(x\u222ay)\u2229(x\u222ay)",
					buildExpression(BUNION, BINTER)
			), new ExprTestPair(
					"(x\u2218y)\u2229(x\u2218y)\u2229(x\u2218y)",
					buildExpression(BCOMP, BINTER)
			), new ExprTestPair(
					"(x\u2229y)\u2218(x\u2229y)\u2218(x\u2229y)",
					buildExpression(BINTER, BCOMP)
			), new ExprTestPair(
					"(x;y)\u2229(x;y)\u2229(x;y)",
					buildExpression(FCOMP, BINTER)
			), new ExprTestPair(
					"(x\u2229y);(x\u2229y);(x\u2229y)",
					buildExpression(BINTER, FCOMP)
			), new ExprTestPair(
					"(x\ue103y)\u2229(x\ue103y)\u2229(x\ue103y)",
					buildExpression(OVR, BINTER)
			), new ExprTestPair(
					"(x\u2229y)\ue103(x\u2229y)\ue103(x\u2229y)",
					buildExpression(BINTER, OVR)
			), new ExprTestPair(
					"x+y\u2229x+y\u2229x+y",
					buildExpression(PLUS, BINTER)
			), new ExprTestPair(
					"(x\u2229y)+(x\u2229y)+(x\u2229y)",
					buildExpression(BINTER, PLUS)
			), new ExprTestPair(
					"x\u2217y\u2229x\u2217y\u2229x\u2217y",
					buildExpression(MUL, BINTER)
			), new ExprTestPair(
					"(x\u2229y)\u2217(x\u2229y)\u2217(x\u2229y)",
					buildExpression(BINTER, MUL)
			), new ExprTestPair(
					"(x;y)\u2218(x;y)\u2218(x;y)",
					buildExpression(FCOMP, BCOMP)
			), new ExprTestPair(
					"(x\u2218y);(x\u2218y);(x\u2218y)",
					buildExpression(BCOMP, FCOMP)
			), new ExprTestPair(
					"(x\ue103y)\u2218(x\ue103y)\u2218(x\ue103y)",
					buildExpression(OVR, BCOMP)
			), new ExprTestPair(
					"(x\u2218y)\ue103(x\u2218y)\ue103(x\u2218y)",
					buildExpression(BCOMP, OVR)
			), new ExprTestPair(
					"x+y\u2218x+y\u2218x+y",
					buildExpression(PLUS, BCOMP)
			), new ExprTestPair(
					"(x\u2218y)+(x\u2218y)+(x\u2218y)",
					buildExpression(BCOMP, PLUS)
			), new ExprTestPair(
					"x\u2217y\u2218x\u2217y\u2218x\u2217y",
					buildExpression(MUL, BCOMP)
			), new ExprTestPair(
					"(x\u2218y)\u2217(x\u2218y)\u2217(x\u2218y)",
					buildExpression(BCOMP, MUL)
			), new ExprTestPair(
					"(x\ue103y);(x\ue103y);(x\ue103y)",
					buildExpression(OVR, FCOMP)
			), new ExprTestPair(
					"(x;y)\ue103(x;y)\ue103(x;y)",
					buildExpression(FCOMP, OVR)
			), new ExprTestPair(
					"x+y;x+y;x+y",
					buildExpression(PLUS, FCOMP)
			), new ExprTestPair(
					"(x;y)+(x;y)+(x;y)",
					buildExpression(FCOMP, PLUS)
			), new ExprTestPair(
					"x\u2217y;x\u2217y;x\u2217y",
					buildExpression(MUL, FCOMP)
			), new ExprTestPair(
					"(x;y)\u2217(x;y)\u2217(x;y)",
					buildExpression(FCOMP, MUL)
			), new ExprTestPair(
					"x+y\ue103x+y\ue103x+y",
					buildExpression(PLUS, OVR)
			), new ExprTestPair(
					"(x\ue103y)+(x\ue103y)+(x\ue103y)",
					buildExpression(OVR, PLUS)
			), new ExprTestPair(
					"x\u2217y\ue103x\u2217y\ue103x\u2217y",
					buildExpression(MUL, OVR)
			), new ExprTestPair(
					"(x\ue103y)\u2217(x\ue103y)\u2217(x\ue103y)",
					buildExpression(OVR, MUL)
			), new ExprTestPair(
					"x\u2217y+x\u2217y+x\u2217y",
					buildExpression(MUL, PLUS)
			), new ExprTestPair(
					"(x+y)\u2217(x+y)\u2217(x+y)",
					buildExpression(PLUS, MUL)
			),
	};

	private Predicate btrueEquivBtrue = mBinaryPredicate(LEQV, btrue, btrue);
	private Predicate btrueAndBtrue = mAssociativePredicate(LAND, btrue, btrue);
	private Predicate btrueOrBtrue = mAssociativePredicate(LOR, btrue, btrue);
	
	private PredTestPair[] associativePredicateTestPairs = new PredTestPair[] {
			new PredTestPair(
					"((\u22a4\u21d4\u22a4)\u21d4(\u22a4\u21d4\u22a4))\u21d4(\u22a4\u21d4\u22a4)",
					mBinaryPredicate(LEQV,
							mBinaryPredicate(LEQV, btrueEquivBtrue, btrueEquivBtrue),
							btrueEquivBtrue)
			), new PredTestPair(
					"(\u22a4\u21d4\u22a4)\u21d4((\u22a4\u21d4\u22a4)\u21d4(\u22a4\u21d4\u22a4))",
					mBinaryPredicate(LEQV,
							btrueEquivBtrue,
							mBinaryPredicate(LEQV, btrueEquivBtrue, btrueEquivBtrue))
			), new PredTestPair(
					"(\u22a4\u21d4\u22a4)\u2227(\u22a4\u21d4\u22a4)\u2227(\u22a4\u21d4\u22a4)",
					mAssociativePredicate(LAND,
							btrueEquivBtrue,
							btrueEquivBtrue, 
							btrueEquivBtrue)
			), new PredTestPair(
					"(\u22a4\u2227\u22a4\u21d4\u22a4\u2227\u22a4)\u21d4\u22a4\u2227\u22a4",
					mBinaryPredicate(LEQV,
							mBinaryPredicate(LEQV, btrueAndBtrue, btrueAndBtrue),
							btrueAndBtrue)
			), new PredTestPair(
					"\u22a4\u2227\u22a4\u21d4(\u22a4\u2227\u22a4\u21d4\u22a4\u2227\u22a4)",
					mBinaryPredicate(LEQV,
							btrueAndBtrue,
							mBinaryPredicate(LEQV, btrueAndBtrue, btrueAndBtrue))

			), new PredTestPair(
					"(\u22a4\u21d4\u22a4)\u2228(\u22a4\u21d4\u22a4)\u2228(\u22a4\u21d4\u22a4)",
					mAssociativePredicate(LOR,
							btrueEquivBtrue,
							btrueEquivBtrue, 
							btrueEquivBtrue)
			), new PredTestPair(
					"(\u22a4\u2228\u22a4\u21d4\u22a4\u2228\u22a4)\u21d4\u22a4\u2228\u22a4",
					mBinaryPredicate(LEQV,
							mBinaryPredicate(LEQV, btrueOrBtrue, btrueOrBtrue),
							btrueOrBtrue)
			), new PredTestPair(
					"\u22a4\u2228\u22a4\u21d4(\u22a4\u2228\u22a4\u21d4\u22a4\u2228\u22a4)",
					mBinaryPredicate(LEQV,
							btrueOrBtrue,
							mBinaryPredicate(LEQV, btrueOrBtrue, btrueOrBtrue))
			), new PredTestPair(
					"(\u22a4\u2227\u22a4)\u2227(\u22a4\u2227\u22a4)\u2227(\u22a4\u2227\u22a4)",
					buildPredicate(LAND, LAND)
			), new PredTestPair(
					"(\u22a4\u2227\u22a4)\u2228(\u22a4\u2227\u22a4)\u2228(\u22a4\u2227\u22a4)",
					buildPredicate(LAND, LOR)
			), new PredTestPair(
					"(\u22a4\u2228\u22a4)\u2227(\u22a4\u2228\u22a4)\u2227(\u22a4\u2228\u22a4)",
					buildPredicate(LOR, LAND)
			), new PredTestPair(
					"(\u22a4\u2228\u22a4)\u2228(\u22a4\u2228\u22a4)\u2228(\u22a4\u2228\u22a4)",
					buildPredicate(LOR, LOR)
			),
	};
			
	// test empty setext
	private ExprTestPair[] uncommonFormulaeTestPairs = new ExprTestPair[] {
			new ExprTestPair(
					"{}",
					mSetExtension()
			), new ExprTestPair(
					"A ◁ f;g",
					mAssociativeExpression(FCOMP,
							mBinaryExpression(DOMRES, id_A, id_f),
							id_g 
					)
			), new ExprTestPair(
					"x × y",
					mBinaryExpression(CPROD, id_x, id_y)
// TODO restore test of optimality of toString()
//			), new ExprTestPair(
//					"f;g ▷ A",
//					mAssociativeExpression(FCOMP,
//							id_f, 
//							mBinaryExpression(RANRES, id_g, id_A)
//					)
			),
	};
	
	private Predicate buildPredicate(int firstTag, int secondTag) {
		return mAssociativePredicate(secondTag,
				mAssociativePredicate(firstTag, btrue, btrue),
				mAssociativePredicate(firstTag, btrue, btrue),
				mAssociativePredicate(firstTag, btrue, btrue)
		);
	}
	
	private Expression buildExpression(int firstTag, int secondTag) {
		return mAssociativeExpression(secondTag,
				mAssociativeExpression(firstTag, id_x, id_y),
				mAssociativeExpression(firstTag, id_x, id_y),
				mAssociativeExpression(firstTag, id_x, id_y)
		);
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
	
	// this test ensures that an associative expression treats all
	// its children the same way
	private void routineTestStringFormula(TestPair[] pairs) {
		for (TestPair pair: pairs) {
			String formula = pair.getFormula().toString();
			String formulaParenthesized = pair.getFormula().toStringFullyParenthesized();
			assertEquals("\nTest failed on original String: "+pair.image+"\nUnparser produced: "+formula+"\n",
					pair.image, formula);
			
			pair.parseAndCheck(formula);
			pair.parseAndCheck(formulaParenthesized);
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
		routineTest(constructAssociativeAssociativeTrees());
		routineTest(constructBinaryBinaryTrees());
		routineTest(constructUnaryUnaryTrees());
		routineTest(constructQuantifiedQuantifiedTrees());
		routineTest(constructAssociativeBinaryTrees());
		routineTest(constructAssociativeUnaryTrees());
		routineTest(constructBinaryUnaryTrees());
		routineTest(constructQuantifiedBinaryTrees());
		routineTest(constructQuantifiedAssociativeTree());
		routineTest(constructQuantifiedUnaryTree());
		routineTest(constructAssociativeAssociativePredicateTree());
		routineTest(constructBinaryBinaryPredicateTrees());
		routineTest(constructUnaryUnaryPredicateTrees());
		routineTest(constructAssociativeBinaryPredicateTrees());
		routineTest(constructAssociativeUnaryPredicateTrees());
		routineTest(constructBinaryUnaryPredicateTrees());
		routineTest(constructQuantifiedQuantifiedPredicateTrees());
		routineTest(constructQuantifiedBinaryPredicateTrees());
		routineTest(constructQuantifiedAssociativePredicateTrees());
		routineTest(constructQuantifiedUnaryPredicateTrees());
		routineTest(constructRelop());
		routineTest(constructQuantifierWithPredicate());
	}
	
	
	private void routineTest (Formula[] formulae) {
		for (int i = 0; i < formulae.length; i++) {
			TestPair pair;
			if (formulae[i] instanceof Expression) {
				pair = new ExprTestPair(null, (Expression) formulae[i]);
			} else {
				pair = new PredTestPair(null, (Predicate) formulae[i]);
			}
			
			String formula = pair.getFormula().toString();
			String formulaParenthesized = pair.getFormula().toStringFullyParenthesized();
			
			pair.parseAndCheck(formula);
			pair.parseAndCheck(formulaParenthesized);
		}
	}
}
