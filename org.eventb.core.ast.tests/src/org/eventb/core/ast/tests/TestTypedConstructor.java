package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.*;

import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.Type;

public class TestTypedConstructor extends TestCase {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	// Types used in these tests
	private static IntegerType INT = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();

	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");
	private static GivenType ty_V = ff.makeGivenType("V");

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	private FreeIdentifier mTypedIdent(String name, Type type) {
		FreeIdentifier ident = mFreeIdentifier(name);
		env.addName(name, type);
		ITypeCheckResult result = ident.typeCheck(env);
		assertTrue("Ident typechecked failed", result.isSuccess());
		return ident;
	}
	
	private BoundIdentDecl mTypedBoundIdentDecl(String name, Expression typeExpr) {
		BoundIdentDecl ident = mBoundIdentDecl(name);
		
		// Run the type-checker to decorate the identifier with its type.
		Predicate pred = mQuantifiedPredicate(Formula.EXISTS, mList(ident),
				mRelationalPredicate(Formula.IN, mBoundIdentifier(0), typeExpr));
		ITypeCheckResult result = pred.typeCheck(env);
		assertTrue("Bound Ident typechecked failed", result.isSuccess());
		
		return ident;
	}
	
	private void typeCheckForBoundIdent(BoundIdentDecl[] decls, Expression maplet) {
		Predicate typingPred = mQuantifiedPredicate(decls, 
				mRelationalPredicate(Formula.IN,
						maplet,
						mAtomicExpression(Formula.EMPTYSET)
				)
		);
		ITypeCheckResult tcResult = typingPred.typeCheck(env);
		assertTrue("Bound ident type-check failed", tcResult.isSuccess());
	}
	
	ITypeEnvironment env;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		env = mTypeEnvironment();
	}
	
	/**
	 * Main test routine for expressions.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 */
	public void testExpressionTypeSynthesis () {
		
		FreeIdentifier fST = mTypedIdent("fST", REL(ty_S, ty_T));
		FreeIdentifier fSU = mTypedIdent("fSU", REL(ty_S, ty_U));
		FreeIdentifier fTU = mTypedIdent("fTU", REL(ty_T, ty_U));
		FreeIdentifier fUV = mTypedIdent("fUV", REL(ty_U, ty_V));
		FreeIdentifier gST = mTypedIdent("gST", REL(ty_S, ty_T));
		FreeIdentifier hST = mTypedIdent("hST", REL(ty_S, ty_T));
		FreeIdentifier SS = mTypedIdent("SS", POW(POW(ty_S)));
		FreeIdentifier id_x = mTypedIdent("x", INT);
		FreeIdentifier id_y = mTypedIdent("y", INT);
		FreeIdentifier id_z = mTypedIdent("z", INT);
		FreeIdentifier id_s = mTypedIdent("s", ty_S);
		FreeIdentifier id_t = mTypedIdent("t", ty_T);
		FreeIdentifier id_v = mTypedIdent("v", ty_V);
		FreeIdentifier id_A = mTypedIdent("A", POW(ty_S));
		FreeIdentifier id_B = mTypedIdent("B", POW(ty_S));
		FreeIdentifier id_C = mTypedIdent("C", POW(ty_S));
		FreeIdentifier id_S = mTypedIdent("S", POW(ty_S));
		FreeIdentifier id_T = mTypedIdent("T", POW(ty_T));
		FreeIdentifier id_U = mTypedIdent("U", POW(ty_U));

		BoundIdentDecl bd_x = mTypedBoundIdentDecl("x", id_S);
		BoundIdentDecl bd_y = mTypedBoundIdentDecl("y", id_T);
		BoundIdentDecl bd_z = mTypedBoundIdentDecl("z", id_U);
		
		BoundIdentifier b0S = mBoundIdentifier(0);
		BoundIdentifier b0T = mBoundIdentifier(0);
		BoundIdentifier b0U = mBoundIdentifier(0);
		BoundIdentifier b1S = mBoundIdentifier(1);
		BoundIdentifier b1T = mBoundIdentifier(1);
		BoundIdentifier b2S = mBoundIdentifier(2);

		typeCheckForBoundIdent(mList(bd_x), b0S);
		typeCheckForBoundIdent(mList(bd_x, bd_y), mMaplet(b1S, b0T));
		typeCheckForBoundIdent(mList(bd_x, bd_y, bd_z), mMaplet(mMaplet(b2S, b1T), b0U));
		
		//--------------------
		//  Binary expressions
		//--------------------
		doTest(mBinaryExpression(Formula.FUNIMAGE, fST, id_s), ty_T);

		doTest(mBinaryExpression(Formula.RELIMAGE, fST, id_A), POW(ty_T));
		
		doTest(mBinaryExpression(Formula.MAPSTO, id_s, id_t), CPROD(ty_S, ty_T));

		doTest(mBinaryExpression(Formula.REL,   id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TREL,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.SREL,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.STREL, id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PFUN,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TFUN,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PINJ,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TINJ,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PSUR,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TSUR,  id_S, id_T), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TBIJ,  id_S, id_T), POW(REL(ty_S, ty_T)));

		doTest(mAssociativeExpression(Formula.BUNION, id_A, id_B),       POW(ty_S));
		doTest(mAssociativeExpression(Formula.BUNION, id_A, id_B, id_C), POW(ty_S));
		doTest(mAssociativeExpression(Formula.BINTER, id_A, id_B),       POW(ty_S));
		doTest(mAssociativeExpression(Formula.BINTER, id_A, id_B, id_C), POW(ty_S));
		doTest(mBinaryExpression(Formula.SETMINUS, id_A, id_B), POW(ty_S));
		
		doTest(mBinaryExpression(Formula.CPROD, id_S, id_T), REL(ty_S, ty_T));
		
		doTest(mBinaryExpression(Formula.DPROD, fST, fSU), REL(ty_S, CPROD(ty_T, ty_U)));
		
		doTest(mBinaryExpression(Formula.PPROD, fST, fUV), 
				REL(CPROD(ty_S, ty_U), CPROD(ty_T, ty_V)));
		
		doTest(mAssociativeExpression(Formula.BCOMP, fTU, fST),      REL(ty_S, ty_U));
		doTest(mAssociativeExpression(Formula.BCOMP, fUV, fTU, fST), REL(ty_S, ty_V));
		
		doTest(mAssociativeExpression(Formula.FCOMP, fST, fTU),      REL(ty_S, ty_U));
		doTest(mAssociativeExpression(Formula.FCOMP, fST, fTU, fUV), REL(ty_S, ty_V));
		
		doTest(mAssociativeExpression(Formula.OVR, fST, gST),      REL(ty_S, ty_T));
		doTest(mAssociativeExpression(Formula.OVR, fST, gST, hST), REL(ty_S, ty_T));
		
		doTest(mBinaryExpression(Formula.DOMRES, id_S, fST), REL(ty_S, ty_T));
		doTest(mBinaryExpression(Formula.DOMSUB, id_S, fST), REL(ty_S, ty_T));

		doTest(mBinaryExpression(Formula.RANRES, fST, id_T), REL(ty_S, ty_T));
		doTest(mBinaryExpression(Formula.RANSUB, fST, id_T), REL(ty_S, ty_T));
		
		doTest(mBinaryExpression(Formula.UPTO, id_x, id_y), POW(INT));
		
		doTest(mAssociativeExpression(Formula.PLUS,  id_x, id_y      ), INT);
		doTest(mAssociativeExpression(Formula.PLUS,  id_x, id_y, id_z), INT);
		doTest(mBinaryExpression     (Formula.MINUS, id_x, id_y      ), INT);
		doTest(mAssociativeExpression(Formula.MUL,   id_x, id_y      ), INT);
		doTest(mAssociativeExpression(Formula.MUL,   id_x, id_y, id_z), INT);
		doTest(mBinaryExpression     (Formula.DIV,   id_x, id_y      ), INT);
		doTest(mBinaryExpression     (Formula.MOD,   id_x, id_y      ), INT);
		doTest(mBinaryExpression     (Formula.EXPN,  id_x, id_y      ), INT);

		
		//-------------------
		//  Unary expressions
		//-------------------
		doTest(mUnaryExpression(Formula.UNMINUS, id_x), INT);

		doTest(mUnaryExpression(Formula.CONVERSE, fST), REL(ty_T, ty_S));
		
		// doTest(mUnaryExpression(Formula.CARD, id_A), INT);

		doTest(mUnaryExpression(Formula.POW,  id_A), POW(POW(ty_S)));
		doTest(mUnaryExpression(Formula.POW1, id_A), POW(POW(ty_S)));
		
		doTest(mUnaryExpression(Formula.KUNION, SS), POW(ty_S));
		doTest(mUnaryExpression(Formula.KINTER, SS), POW(ty_S));
		
		doTest(mUnaryExpression(Formula.KDOM, fST), POW(ty_S));
		
		doTest(mUnaryExpression(Formula.KRAN, fST), POW(ty_T));
		
		doTest(mUnaryExpression(Formula.KPRJ1, fST), REL(CPROD(ty_S, ty_T), ty_S));

		doTest(mUnaryExpression(Formula.KPRJ2, fST), REL(CPROD(ty_S, ty_T), ty_T));
		
		doTest(mUnaryExpression(Formula.KID, id_A), REL(ty_S, ty_S));

		
		//--------------------
		//  Lambda expressions
		//--------------------
		doTest(mQuantifiedExpression(Formula.CSET, QuantifiedExpression.Form.Lambda,
						mList(bd_x),
						mLiteralPredicate(),
						mMaplet(b0S, id_v)),
				REL(ty_S, ty_V));
		doTest(mQuantifiedExpression(Formula.CSET, QuantifiedExpression.Form.Lambda,
						mList(bd_x, bd_y),
						mLiteralPredicate(),
						mMaplet(mMaplet(b1S, b0T), id_v)),
				REL(CPROD(ty_S, ty_T), ty_V));
		doTest(mQuantifiedExpression(Formula.CSET, QuantifiedExpression.Form.Lambda,
						mList(bd_x, bd_y, bd_z),
						mLiteralPredicate(),
						mMaplet(mMaplet(mMaplet(b2S, b1T), b0U), id_v)),
				REL(CPROD(CPROD(ty_S, ty_T), ty_U), ty_V));
		doTest(mQuantifiedExpression(Formula.CSET, QuantifiedExpression.Form.Lambda,
						mList(bd_x, bd_y, bd_z),
						mLiteralPredicate(),
						mMaplet(mMaplet(b2S, mMaplet(b1T, b0U)), id_v)),
				REL(CPROD(ty_S, CPROD(ty_T, ty_U)), ty_V));
		
		//------------------------
		//  Quantified expressions
		//------------------------
		doTest(mQuantifiedExpression(Formula.QUNION, QuantifiedExpression.Form.Explicit,
						mList(bd_x),
						mLiteralPredicate(),
						mSetExtension(b0S)),
				POW(ty_S));
		doTest(mQuantifiedExpression(Formula.QINTER, QuantifiedExpression.Form.Explicit,
						mList(bd_x),
						mLiteralPredicate(),
						mSetExtension(b0S)),
				POW(ty_S));
		doTest(mQuantifiedExpression(Formula.CSET, QuantifiedExpression.Form.Explicit,
						mList(bd_x),
						mLiteralPredicate(),
						b0S),
				POW(ty_S));
	
		//-------------------
		//  "bool" expression
		//-------------------
		doTest(mBoolExpression(mLiteralPredicate()), BOOL);

		//---------------
		//  Set extension
		//---------------
		doTest(mSetExtension(id_s), ty_S);
		doTest(mSetExtension(id_x, id_y), INT);
		doTest(mSetExtension(id_x, id_y, id_z), INT);

		//-------------
		//  Identifiers
		//-------------
		// No test for identifiers, type is not synthesizable
		
		//--------------------
		//  Atomic expressions
		//--------------------
		doTest(mAtomicExpression(Formula.INTEGER), POW(INT));
		doTest(mAtomicExpression(Formula.NATURAL), POW(INT));
		doTest(mAtomicExpression(Formula.NATURAL1), POW(INT));
		
		doTest(mAtomicExpression(Formula.BOOL), POW(BOOL));
		
		doTest(mAtomicExpression(Formula.TRUE), BOOL);
		doTest(mAtomicExpression(Formula.FALSE), BOOL);
		
		// No test for Formula.EMPTYSET, type is not synthesizable

		//-----------------
		//  Integer Literal
		//-----------------
		doTest(mIntegerLiteral(), INT);
		
	}
	
	private void doTest(Expression expr, Type expected) {
		assertTrue("Result is not typed", expr.isTypeChecked());
		assertEquals("Bad type", expected, expr.getType());
		ITypeCheckResult result = expr.typeCheck(env);
		assertTrue("Expression didn't typecheck", result.isSuccess());
	}

	/**
	 * Main test routine for predicates.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 */
	public void testPredicateTypeSynthesis () {
		
		LiteralPredicate btrue = mLiteralPredicate(Formula.BTRUE);
		LiteralPredicate bfalse = mLiteralPredicate(Formula.BFALSE);
		
		FreeIdentifier id_x = mTypedIdent("x", INT);
		FreeIdentifier id_y = mTypedIdent("y", INT);
		FreeIdentifier id_s = mTypedIdent("s", ty_S);
		FreeIdentifier id_A = mTypedIdent("A", POW(ty_S));
		FreeIdentifier id_B = mTypedIdent("B", POW(ty_S));
		FreeIdentifier id_S = mTypedIdent("S", POW(ty_S));
		FreeIdentifier id_T = mTypedIdent("T", POW(ty_T));
		FreeIdentifier id_U = mTypedIdent("U", POW(ty_U));

		BoundIdentDecl bd_x = mTypedBoundIdentDecl("x", id_S);
		BoundIdentDecl bd_y = mTypedBoundIdentDecl("y", id_T);
		BoundIdentDecl bd_z = mTypedBoundIdentDecl("z", id_U);
		
		BoundIdentifier b0S = mBoundIdentifier(0);
		BoundIdentifier b0T = mBoundIdentifier(0);
		BoundIdentifier b0U = mBoundIdentifier(0);
		BoundIdentifier b1S = mBoundIdentifier(1);
		BoundIdentifier b1T = mBoundIdentifier(1);
		BoundIdentifier b2S = mBoundIdentifier(2);

		typeCheckForBoundIdent(mList(bd_x), b0S);
		typeCheckForBoundIdent(mList(bd_x, bd_y), mMaplet(b1S, b0T));
		typeCheckForBoundIdent(mList(bd_x, bd_y, bd_z), mMaplet(mMaplet(b2S, b1T), b0U));
		
		//--------------------
		//  Binary predicates
		//--------------------
		doTest(mBinaryPredicate(Formula.LIMP, btrue, bfalse));
		doTest(mBinaryPredicate(Formula.LEQV, btrue, bfalse));
		
		doTest(mAssociativePredicate(Formula.LAND, btrue, bfalse));
		doTest(mAssociativePredicate(Formula.LAND, btrue, bfalse, btrue));
		doTest(mAssociativePredicate(Formula.LOR, btrue, bfalse));
		doTest(mAssociativePredicate(Formula.LOR, btrue, bfalse, btrue));

		//-----------------
		//  Unary predicate
		//-----------------
		doTest(mUnaryPredicate(Formula.NOT, bfalse));
		
		//-----------------------
		//  Quantified predicates
		//-----------------------
		doTest(mQuantifiedPredicate(Formula.FORALL, mList(bd_x),
				mRelationalPredicate(Formula.EQUAL, b0S, b0S)));
		doTest(mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y),
				mRelationalPredicate(Formula.EQUAL, mMaplet(b1S, b0T), mMaplet(b1S, b0T))));
		doTest(mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y, bd_z),
				mRelationalPredicate(Formula.EQUAL, 
						mMaplet(b2S, mMaplet(b1T, b0U)), mMaplet(b2S, mMaplet(b1T, b0U)))));
		
		doTest(mQuantifiedPredicate(Formula.EXISTS, mList(bd_x),
				mRelationalPredicate(Formula.EQUAL, b0S, b0S)));
		doTest(mQuantifiedPredicate(Formula.EXISTS, mList(bd_x, bd_y),
				mRelationalPredicate(Formula.EQUAL, mMaplet(b1S, b0T), mMaplet(b1S, b0T))));
		doTest(mQuantifiedPredicate(Formula.EXISTS, mList(bd_x, bd_y, bd_z),
				mRelationalPredicate(Formula.EQUAL, 
						mMaplet(b2S, mMaplet(b1T, b0U)), mMaplet(b2S, mMaplet(b1T, b0U)))));
		
		//--------------------
		//  Literal predicates
		//--------------------
		doTest(mLiteralPredicate(Formula.BTRUE));
		doTest(mLiteralPredicate(Formula.BFALSE));
		
		//--------------------
		//  Simple predicates
		//--------------------
		doTest(mSimplePredicate(id_A));
		
		//-----------------------
		//  Relational predicates
		//-----------------------
		doTest(mRelationalPredicate(Formula.EQUAL, id_s, id_s));
		doTest(mRelationalPredicate(Formula.NOTEQUAL, id_s, id_s));

		doTest(mRelationalPredicate(Formula.LT, id_x, id_y));
		doTest(mRelationalPredicate(Formula.LE, id_x, id_y));
		doTest(mRelationalPredicate(Formula.GT, id_x, id_y));
		doTest(mRelationalPredicate(Formula.GE, id_x, id_y));

		doTest(mRelationalPredicate(Formula.IN, id_x, id_A));
		doTest(mRelationalPredicate(Formula.NOTIN, id_x, id_A));

		doTest(mRelationalPredicate(Formula.SUBSET, id_A, id_B));
		doTest(mRelationalPredicate(Formula.NOTSUBSET, id_A, id_B));
		doTest(mRelationalPredicate(Formula.SUBSETEQ, id_A, id_B));
		doTest(mRelationalPredicate(Formula.NOTSUBSETEQ, id_A, id_B));
	}
	
	private void doTest(Predicate pred) {
		assertTrue("Result is not typed", pred.isTypeChecked());
		ITypeCheckResult result = pred.typeCheck(env);
		assertTrue("Predicate didn't typecheck", result.isSuccess());
	}

	// TODO ajouter fonction de test sur un peu tout ce qui échoue systématiquement
	// à synthétiser un type.  Attention à MAPSTO.  Penser au cas où un des enfants
	// n'a pas été typechecké.
	
}
