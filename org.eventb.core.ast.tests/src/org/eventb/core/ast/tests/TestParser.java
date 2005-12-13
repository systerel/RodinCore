/*
 * Created on 07-jul-2005
 *
 */
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
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
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * @author franz
 *
 */
public class TestParser extends TestCase {
	
	private static FormulaFactory ff = new FormulaFactory();

	private static FreeIdentifier id_x = mFreeIdentifier("x");
	private static FreeIdentifier id_y = mFreeIdentifier("y");
	private static FreeIdentifier id_z = mFreeIdentifier("z");
	private static FreeIdentifier id_t = mFreeIdentifier("t");
	private static FreeIdentifier id_a = mFreeIdentifier("a");
	private static FreeIdentifier id_b = mFreeIdentifier("b");
	private static FreeIdentifier id_S = mFreeIdentifier("S");
	private static FreeIdentifier id_T = mFreeIdentifier("T");
	private static FreeIdentifier id_f = mFreeIdentifier("f");
	private static FreeIdentifier id_filter = mFreeIdentifier("filter");
	
	private static BoundIdentDecl bd_x = mBoundIdentDecl("x");
	private static BoundIdentDecl bd_y = mBoundIdentDecl("y");
	private static BoundIdentDecl bd_z = mBoundIdentDecl("z");
	private static BoundIdentDecl bd_s = mBoundIdentDecl("s");
	private static BoundIdentDecl bd_t = mBoundIdentDecl("t");
	private static BoundIdentDecl bd_f = mBoundIdentDecl("f");
	private static BoundIdentDecl bd_a = mBoundIdentDecl("a");

	private static BoundIdentifier b0 = mBoundIdentifier(0);
	private static BoundIdentifier b1 = mBoundIdentifier(1);
	private static BoundIdentifier b2 = mBoundIdentifier(2);
	private static BoundIdentifier b3 = mBoundIdentifier(3);
	
	private static LiteralPredicate bfalse = mLiteralPredicate(Formula.BFALSE);
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/*
	 * LPAR RPAR LBRACKET RBRACKET LBRACE RBRACE EXPN NOT CPROD LAMBDA UPTO
	 * NATURAL NATURAL1 POW POW1 INTEGER TFUN REL TSUR TINJ MAPSTO LIMP LEQV
	 * PFUN FORALL EXISTS EMPTYSET IN NOTIN SETMINUS MUL BCOMP PPROD LAND LOR
	 * BINTER BUNION EQUAL NOTEQUAL LT LE GT GE SUBSET NOTSUBSET SUBSETEQ
	 * NOTSUBSETEQ DPROD BTRUE BFALSE QINTER QUNION QDOT RANRES DOMRES PSUR PINJ
	 * TBIJ DOMSUB RANSUB TREL SREL STREL OVR FCOMP COMMA PLUS MINUS DIV MID
	 * CONVERSE BOOL TRUE FALSE KPRED KSUCC MOD KBOOL KCARD KUNION KINTER KDOM
	 * KRAN KID KFINITE KPRJ1 KPRJ2 KMIN KMAX DOT FREE_IDENT INTLIT
	 */
	Object[] testPairs = new Object[]{
			// AtomicPredicate
			"\u22a5", 
			bfalse, 
			"\u22a4", 
			mLiteralPredicate(Formula.BTRUE), 
			"finite(x)", 
			mSimplePredicate(Formula.KFINITE, id_x), 
			"x=x", 
			mRelationalPredicate(Formula.EQUAL, id_x, id_x), 
			"x\u2260x", 
			mRelationalPredicate(Formula.NOTEQUAL, id_x, id_x), 
			"x<x", 
			mRelationalPredicate(Formula.LT, id_x, id_x), 
			"x<=x", 
			mRelationalPredicate(Formula.LE, id_x, id_x), 
			"x>x", 
			mRelationalPredicate(Formula.GT, id_x, id_x), 
			"x>=x", 
			mRelationalPredicate(Formula.GE, id_x, id_x), 
			"x\u2208S", 
			mRelationalPredicate(Formula.IN, id_x, id_S), 
			"x\u2209S", 
			mRelationalPredicate(Formula.NOTIN, id_x, id_S), 
			"x\u2282S", 
			mRelationalPredicate(Formula.SUBSET, id_x, id_S), 
			"x\u2284S", 
			mRelationalPredicate(Formula.NOTSUBSET, id_x, id_S), 
			"x\u2286S", 
			mRelationalPredicate(Formula.SUBSETEQ, id_x, id_S), 
			"x\u2288S", 
			mRelationalPredicate(Formula.NOTSUBSETEQ, id_x, id_S), 
			"(\u22a5)", 
			bfalse, 
			
			// LiteralPredicate
			"\u00ac\u22a5", 
			mUnaryPredicate(Formula.NOT, bfalse), 
			"\u00ac\u00ac\u22a5", 
			mUnaryPredicate(Formula.NOT, 
					mUnaryPredicate(Formula.NOT, bfalse)
			), 
			
			// SimplePredicate
			"\u22a5\u2227\u22a5", 
			mAssociativePredicate(Formula.LAND, bfalse, bfalse), 
			"\u22a5\u2228\u22a5", 
			mAssociativePredicate(Formula.LOR, bfalse, bfalse), 
			"\u22a5\u2227\u22a5\u2227\u22a5", 
			mAssociativePredicate(Formula.LAND, bfalse, bfalse, bfalse), 
			"\u22a5\u2228\u22a5\u2228\u22a5", 
			mAssociativePredicate(Formula.LOR, bfalse, bfalse, bfalse), 
			
			// UnquantifiedPredicate
			"\u22a5\u21d2\u22a5", 
			mBinaryPredicate(Formula.LIMP, bfalse, bfalse), 
			"\u22a5\u21d4\u22a5", 
			mBinaryPredicate(Formula.LEQV, bfalse, bfalse), 
			
			// Quantifier + IdentList + Predicate
			"\u2200x\u22c5\u22a5", 
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x), bfalse), 
			"\u2203x\u22c5\u22a5", 
			mQuantifiedPredicate(Formula.EXISTS, mList(bd_x), bfalse), 
			"\u2200x, y, z\u22c5\u22a5", 
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y, bd_z), bfalse), 
			"\u2203x, y, z\u22c5\u22a5", 
			mQuantifiedPredicate(Formula.EXISTS, mList(bd_x, bd_y, bd_z), bfalse), 
			"\u2200x, y\u22c5\u2200s, t\u22c5\u22a5", 
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y), 
					mQuantifiedPredicate(Formula.FORALL, mList(bd_s, bd_t), bfalse)
			), 
			"\u2203x, y\u22c5\u2203s, t\u22c5\u22a5", 
			mQuantifiedPredicate(Formula.EXISTS, mList(bd_x, bd_y), 
					mQuantifiedPredicate(Formula.EXISTS, mList(bd_s, bd_t), bfalse)
			), 
			"\u2200x, y\u22c5\u2203s, t\u22c5\u22a5", 
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y), 
					mQuantifiedPredicate(Formula.EXISTS, mList(bd_s, bd_t), bfalse)
			), 
			"\u2200 x,y \u22c5\u2200 s,t \u22c5 x\u2208s \u2227 y\u2208t",
			mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y), 
					mQuantifiedPredicate(Formula.FORALL, mList(bd_s, bd_t),
							mAssociativePredicate(Formula.LAND,
									mRelationalPredicate(Formula.IN, b3, b1),
									mRelationalPredicate(Formula.IN, b2, b0)
							)
					)
			), 
			
			// SimpleExpression
			"bool(\u22a5)=y", 
			mRelationalPredicate(Formula.EQUAL, mBoolExpression(bfalse), id_y), 
			"card(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KCARD, id_x), id_y), 
			"\u2119(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.POW, id_x), id_y), 
			"\u21191(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.POW1, id_x), id_y), 
			"union(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KUNION, id_x), id_y), 
			"inter(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KINTER, id_x), id_y), 
			"dom(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KDOM, id_x), id_y), 
			"ran(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KRAN, id_x), id_y), 
			"prj1(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KPRJ1, id_x), id_y), 
			"prj2(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KPRJ2, id_x), id_y), 
			"id(x)=y", 
			mRelationalPredicate(Formula.EQUAL, mUnaryExpression(Formula.KID, id_x), id_y), 
			"(x)=x", 
			mRelationalPredicate(Formula.EQUAL, id_x, id_x), 
			"{x, y\u22c5\u22a5\u2223z}=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y), bfalse, id_z, 
							QuantifiedExpression.Form.Explicit
					), id_a
			), 
			"{x\u22c5\u22a5\u2223z}=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x), bfalse, id_z, 
							QuantifiedExpression.Form.Explicit
					), id_a
			), 
			"{x, y\u22c5\u22a5\u2223y}=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					), id_a
			), 
			"{x\u22c5\u22a5\u2223x}=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					), id_a
			), 
			"{x\u2223\u22a5}=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x), bfalse, 
							b0, QuantifiedExpression.Form.Implicit
					), id_a
			), 
			"{x+y\u2223\u22a5}=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y), bfalse, 
							mAssociativeExpression(Formula.PLUS, 
									b1, 
									b0
							), QuantifiedExpression.Form.Implicit
					), id_a
			), 
			"{}=a", 
			mRelationalPredicate(Formula.EQUAL, mSetExtension(), id_a), 
			"{x}=a", 
			mRelationalPredicate(Formula.EQUAL, mSetExtension(id_x), id_a), 
			"{x, y}=a", 
			mRelationalPredicate(Formula.EQUAL, mSetExtension(id_x, id_y), id_a), 
			"{x, y, z}=a", 
			mRelationalPredicate(Formula.EQUAL, mSetExtension(id_x, id_y, id_z), id_a), 
			"x\u2208\u2124", 
			mRelationalPredicate(Formula.IN, id_x, mAtomicExpression(Formula.INTEGER)), 
			"x\u2208\u2115", 
			mRelationalPredicate(Formula.IN, id_x, mAtomicExpression(Formula.NATURAL)), 
			"x\u2208\u21151", 
			mRelationalPredicate(Formula.IN, id_x, mAtomicExpression(Formula.NATURAL1)), 
			"x\u2208BOOL", 
			mRelationalPredicate(Formula.IN, id_x, mAtomicExpression(Formula.BOOL)), 
			"x=TRUE", 
			mRelationalPredicate(Formula.EQUAL, id_x, mAtomicExpression(Formula.TRUE)), 
			"x=FALSE", 
			mRelationalPredicate(Formula.EQUAL, id_x, mAtomicExpression(Formula.FALSE)), 
			"x=2", 
			mRelationalPredicate(Formula.EQUAL, id_x, mIntegerLiteral(2)), 
			
			// Primary
			"x~=y", 
			mRelationalPredicate(Formula.EQUAL, 
					mUnaryExpression(Formula.CONVERSE, id_x), id_y
			), 
			"x~~=y", 
			mRelationalPredicate(Formula.EQUAL, 
					mUnaryExpression(Formula.CONVERSE, 
							mUnaryExpression(Formula.CONVERSE, id_x)
					), id_y
			), 
			
			// Image
			"f(x)=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.FUNIMAGE, id_f, id_x), id_a
			), 
			"f[x]=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.RELIMAGE, id_f, id_x), id_a
			), 
			"f[x](y)=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.FUNIMAGE, 
							mBinaryExpression(Formula.RELIMAGE, id_f, id_x), id_y
					), id_a
			), 
			"f(x)[y]=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.RELIMAGE, 
							mBinaryExpression(Formula.FUNIMAGE, id_f, id_x), id_y
					), id_a
			), 
			"f(x)(y)=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.FUNIMAGE, 
							mBinaryExpression(Formula.FUNIMAGE, id_f, id_x), id_y
					), id_a
			), 
			"f[x][y]=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.RELIMAGE, 
							mBinaryExpression(Formula.RELIMAGE, id_f, id_x), id_y
					), id_a
			), 
			
			// Factor
			"x^y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.EXPN, id_x, id_y), id_a
			), 
			
			// Term
			"x*x=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.MUL, id_x, id_x), id_a
			), 
			"x*x*x=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.MUL, id_x, id_x, id_x), id_a
			), 
			"x\u00f7x=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.DIV, id_x, id_x), id_a
			), 
			"x mod x=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.MOD, id_x, id_x), id_a
			), 
			
			// ArithmeticExpr
			"x+y=a", 
			mRelationalPredicate(Formula.EQUAL, mAssociativeExpression(Formula.PLUS, id_x, id_y), id_a), 
			"x+y+z=a", 
			mRelationalPredicate(Formula.EQUAL, mAssociativeExpression(Formula.PLUS, id_x, id_y, id_z), id_a), 
			"-x+y+z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.PLUS, 
							mUnaryExpression(Formula.UNMINUS, id_x), 
							id_y, 
							id_z
					), id_a
			), 
			"x-y=a", 
			mRelationalPredicate(Formula.EQUAL, mBinaryExpression(Formula.MINUS, id_x, id_y), id_a), 
			"x-y-z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.MINUS, 
							mBinaryExpression(Formula.MINUS, id_x, id_y), 
							id_z
					), id_a
			), 
			"-x-y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.MINUS, 
							mUnaryExpression(Formula.UNMINUS, id_x), 
							id_y
					), id_a
			), 
			"x-y+z-t=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.MINUS, 
							mAssociativeExpression(Formula.PLUS, 
									mBinaryExpression(Formula.MINUS, id_x, id_y), 
									id_z
							), id_t
					), id_a
			), 
			"-x-y+z-t=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.MINUS, 
							mAssociativeExpression(Formula.PLUS, 
									mBinaryExpression(Formula.MINUS, 
											mUnaryExpression(Formula.UNMINUS, id_x), 
											id_y
									), id_z
							), id_t
					), id_a
			), 
			"x+y-z+t=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.PLUS, 
							mBinaryExpression(Formula.MINUS, 
									mAssociativeExpression(Formula.PLUS, id_x, id_y), 
									id_z
							), id_t
					), id_a
			), 
			"-x+y-z+t=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.PLUS, 
							mBinaryExpression(Formula.MINUS, 
									mAssociativeExpression(Formula.PLUS, 
											mUnaryExpression(Formula.UNMINUS, id_x), 
											id_y
									), id_z
							), id_t
					), id_a
			), 
			
			// IntervalExpr
			"x..y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.UPTO, id_x, id_y), id_a
			), 
			
			// RelationExpr
			"x\u2297y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.DPROD, id_x, id_y), id_a
			), 
			"x;y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.FCOMP, id_x, id_y), id_a
			), 
			"x;y;z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.FCOMP, id_x, id_y, id_z), id_a
			), 
			"x\u25b7y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.RANRES, id_x, id_y), id_a
			), 
			"x\u2a65y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.RANSUB, id_x, id_y), id_a
			), 
			"x\u2229y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.BINTER, id_x, id_y), id_a
			), 
			"x\u2229y\u2229z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.BINTER, id_x, id_y, id_z), id_a
			), 
			"x\\y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.SETMINUS, id_x, id_y), id_a
			), 
			"x;y\u2a65z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.RANSUB, 
							mAssociativeExpression(Formula.FCOMP, id_x, id_y), 
							id_z
					), id_a
			), 
			"x\u2229y\u2a65z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.RANSUB, 
							mAssociativeExpression(Formula.BINTER, id_x, id_y), 
							id_z
					), id_a
			), 
			"x\u2229y\\z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.SETMINUS, 
							mAssociativeExpression(Formula.BINTER, id_x, id_y), 
							id_z
					), id_a
			), 
			
			// SetExpr
			"x\u222ay=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.BUNION, id_x, id_y), id_a
			), 
			"x\u222ay\u222az=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.BUNION, id_x, id_y, id_z), id_a
			), 
			"x\u00d7y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.CPROD, id_x, id_y), id_a
			), 
			"x\u00d7y\u00d7z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.CPROD, 
							mBinaryExpression(Formula.CPROD, id_x, id_y), id_z
					), id_a
			), 
			"x\ue103y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.OVR, id_x, id_y), id_a
			), 
			"x\ue103y\ue103z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.OVR, id_x, id_y, id_z), id_a
			), 
			"x\u2218y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.BCOMP, id_x, id_y), id_a
			), 
			"x\u2218y\u2218z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mAssociativeExpression(Formula.BCOMP, id_x, id_y, id_z), id_a
			), 
			"x\u2225y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.PPROD, id_x, id_y), id_a
			), 
			"x\u25c1y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.DOMRES, id_x, id_y), id_a
			), 
			"x\u2a64y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.DOMSUB, id_x, id_y), id_a
			), 
			
			// RelationalSetExpr
			"x\ue100y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TREL, id_x, id_y), id_a
			), 					
			"x\ue100y\ue100z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TREL, 
							mBinaryExpression(Formula.TREL, id_x, id_y), id_z
					), id_a
			), 					
			"x\ue101y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.SREL, id_x, id_y), id_a
			), 					
			"x\ue101y\ue101z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.SREL, 
							mBinaryExpression(Formula.SREL, id_x, id_y), id_z
					), id_a
			), 					
			"x\ue102y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.STREL, id_x, id_y), id_a
			), 					
			"x\ue102y\ue102z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.STREL, 
							mBinaryExpression(Formula.STREL, id_x, id_y), id_z
					), id_a
			), 					
			"x\u2900y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.PSUR, id_x, id_y), id_a
			), 					
			"x\u2900y\u2900z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.PSUR, 
							mBinaryExpression(Formula.PSUR, id_x, id_y), id_z
					), id_a
			), 					
			"x\u2914y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.PINJ, id_x, id_y), id_a
			), 					
			"x\u2914y\u2914z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.PINJ, 
							mBinaryExpression(Formula.PINJ, id_x, id_y), id_z
					), id_a
			), 					
			"x\u2916y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TBIJ, id_x, id_y), id_a
			), 					
			"x\u2916y\u2916z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TBIJ, 
							mBinaryExpression(Formula.TBIJ, id_x, id_y), id_z
					), id_a
			), 					
			"x\u2192y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TFUN, id_x, id_y), id_a
			), 					
			"x\u2192y\u2192z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TFUN, 
							mBinaryExpression(Formula.TFUN, id_x, id_y), id_z
					), id_a
			), 					
			"x\u2194y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.REL, id_x, id_y), id_a
			), 					
			"x\u2194y\u2194z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.REL, 
							mBinaryExpression(Formula.REL, id_x, id_y), id_z
					), id_a
			), 					
			"x\u21a0y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TSUR, id_x, id_y), id_a
			), 					
			"x\u21a0y\u21a0z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TSUR, 
							mBinaryExpression(Formula.TSUR, id_x, id_y), id_z
					), id_a
			), 					
			"x\u21a3y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TINJ, id_x, id_y), id_a
			), 					
			"x\u21a3y\u21a3z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.TINJ, 
							mBinaryExpression(Formula.TINJ, id_x, id_y), id_z
					), id_a
			), 					
			"x\u21f8y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.PFUN, id_x, id_y), id_a
			), 					
			"x\u21f8y\u21f8z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.PFUN, 
							mBinaryExpression(Formula.PFUN, id_x, id_y), id_z
					), id_a
			), 					
			
			// PairExpr
			"x\u21a6y=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.MAPSTO, id_x, id_y), id_a
			), 					
			"x\u21a6y\u21a6z=a", 
			mRelationalPredicate(Formula.EQUAL, 
					mBinaryExpression(Formula.MAPSTO, 
							mBinaryExpression(Formula.MAPSTO, id_x, id_y), id_z
					), id_a
			), 					
			
			// QuantifiedExpr & IdentPattern
			// UnBound
			"finite(\u03bb x\u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET,  mList(bd_x), bfalse, 
							mBinaryExpression(Formula.MAPSTO, b0, id_z), 
							QuantifiedExpression.Form.Lambda
					)
			), 
			"finite(\u03bb x\u21a6y\u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, b1, b0), 
									id_z
							), QuantifiedExpression.Form.Lambda
					)
			), 
			"finite(\u03bb x\u21a6y\u21a6s\u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y, bd_s), 
							bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, 
											mBinaryExpression(Formula.MAPSTO, b2, 	b1), 
											b0
									), id_z
							), QuantifiedExpression.Form.Lambda
					)
			), 
			"finite(\u03bb x\u21a6(y\u21a6s)\u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y, bd_s), 
							bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, 
											b2, 
											mBinaryExpression(Formula.MAPSTO, b1, b0)
									), id_z
							), QuantifiedExpression.Form.Lambda
					)
			), 
			
			// Bound
			"finite(\u03bb x\u22c5\u22a5\u2223x)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x), bfalse, 
							mBinaryExpression(Formula.MAPSTO, b0, b0), 
							QuantifiedExpression.Form.Lambda
					)
			), 
			"finite(\u03bb x\u21a6y\u22c5\u22a5\u2223y)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, b1, b0),
									b0
							), QuantifiedExpression.Form.Lambda
					)
			), 
			"finite(\u03bb x\u21a6y\u21a6s\u22c5\u22a5\u2223s)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y, bd_s), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, 
											mBinaryExpression(Formula.MAPSTO, b2, b1), 
											b0
									), 
									b0
							), QuantifiedExpression.Form.Lambda
					)
			), 
			"finite(\u03bb x\u21a6(y\u21a6s)\u22c5\u22a5\u2223s)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.CSET, mList(bd_x, bd_y, bd_s), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, b2, 
											mBinaryExpression(Formula.MAPSTO, b1, b0)
									), b0
							), QuantifiedExpression.Form.Lambda
					)
			), 
			
			// UnBound
			"finite(\u22c3x\u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x), bfalse, 
							id_z, QuantifiedExpression.Form.Explicit
					)
			), 
			"finite(\u22c3 x, y \u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x, bd_y), bfalse, 
							id_z, QuantifiedExpression.Form.Explicit
					)
			), 
			"finite(\u22c3 x, y, s \u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x, bd_y, bd_s), bfalse, 
							id_z, QuantifiedExpression.Form.Explicit
					)
			), 
			
			// Bound
			"finite(\u22c3x\u22c5\u22a5\u2223x)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					)
			),
			"finite(\u22c3 x, y \u22c5\u22a5\u2223y)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x, bd_y), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					)
			),
			"finite(\u22c3 x, y, s \u22c5\u22a5\u2223s)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x, bd_y, bd_s), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					)
			),
			
			// UnBound
			"finite(\u22c3x\u2223\u22a5)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x), bfalse, 
							b0, QuantifiedExpression.Form.Implicit
					)
			),
			"finite(\u22c3 x-y \u2223\u22a5)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QUNION, mList(bd_x, bd_y), bfalse, 
							mBinaryExpression(Formula.MINUS, b1, b0), 
							QuantifiedExpression.Form.Implicit
					)
			),
			
			// UnBound
			"finite(\u22c2x\u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_x), bfalse, 
							id_z, QuantifiedExpression.Form.Explicit
					)
			),
			"finite(\u22c2 x, y \u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_x, bd_y), bfalse, 
							id_z, QuantifiedExpression.Form.Explicit
					)
			),
			"finite(\u22c2 x, y, s \u22c5\u22a5\u2223z)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_x, bd_y, bd_s), bfalse, 
							id_z, QuantifiedExpression.Form.Explicit
					)
			),
			
			// Bound
			"finite(\u22c2 x \u22c5\u22a5\u2223x)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_x), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					)
			),
			"finite(\u22c2 x, y \u22c5\u22a5\u2223y)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_x, bd_y), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					)
			),
			"finite(\u22c2 x, y, s \u22c5\u22a5\u2223s)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_x, bd_y, bd_s), bfalse, 
							b0, QuantifiedExpression.Form.Explicit
					)
			),
			
			// UnBound
			"finite(\u22c2x\u2223\u22a5)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_x), bfalse, 
							b0, QuantifiedExpression.Form.Implicit
					)
			),
			"finite(\u22c2y-x\u2223\u22a5)", 
			mSimplePredicate(Formula.KFINITE, 
					mQuantifiedExpression(Formula.QINTER, mList(bd_y, bd_x), bfalse, 
							mBinaryExpression(Formula.MINUS, b1, b0), 
							QuantifiedExpression.Form.Implicit
					)
			),
			
			
			// Special cases
			" filter =  { f ∣ ( ∀ a ⋅ ⊤ ) } ∧  a = b ", 
			mAssociativePredicate(Formula.LAND, 
					mRelationalPredicate(
							Formula.EQUAL, 
							id_filter, 
							mQuantifiedExpression(
									Formula.CSET, 
									mList(bd_f), 
									mQuantifiedPredicate(
											Formula.FORALL, 
											mList(bd_a), 					
											mLiteralPredicate(Formula.BTRUE)
									),
									b0, 
									QuantifiedExpression.Form.Implicit)
					),
					mRelationalPredicate(Formula.EQUAL, id_a, id_b)
			),
			
			// with ident bound twice
			"∀x⋅x ∈ S ∧ (∀x⋅x ∈ T)",
			mQuantifiedPredicate(Formula.FORALL,
				mList(bd_x),
				mAssociativePredicate(Formula.LAND,
					mRelationalPredicate(Formula.IN, b0, id_S),
					mQuantifiedPredicate(Formula.FORALL,
						mList(bd_x),
						mRelationalPredicate(Formula.IN, b0, id_T)
					)
				)
			),
			// with two idents bound twice
			"∀x,y⋅x ∈ S ∧ y ∈ T ∧ (∀y,x⋅x ∈ T ∧ y ∈ S)",
			mQuantifiedPredicate(Formula.FORALL,
				mList(bd_x, bd_y),
				mAssociativePredicate(Formula.LAND,
					mRelationalPredicate(Formula.IN, b1, id_S),
					mRelationalPredicate(Formula.IN, b0, id_T),
					mQuantifiedPredicate(Formula.FORALL,
						mList(bd_y, bd_x),
						mAssociativePredicate(Formula.LAND,
								mRelationalPredicate(Formula.IN, b0, id_T),
								mRelationalPredicate(Formula.IN, b1, id_S)
						)
					)
				)
			),
			// with two idents bound twice
			"∀x,y,z⋅ finite(x ∪ y ∪ z ∪ {y | y ⊆ x ∪ z})",
			mQuantifiedPredicate(Formula.FORALL,
				mList(bd_x, bd_y, bd_z),
				mSimplePredicate(Formula.KFINITE,
					mAssociativeExpression(Formula.BUNION,
						b2, b1, b0,
						mQuantifiedExpression(Formula.CSET,
							mList(bd_y),
							mRelationalPredicate(Formula.SUBSETEQ,
								b0,
								mAssociativeExpression(Formula.BUNION, b3, b1)
							),
							b0,
							QuantifiedExpression.Form.Implicit
						)
					)
				)
			),
			// Test that line terminator and strange spaces are ignored
			"\t\n\r\f ⊤ \u00A0\u2007\u202F",
			mLiteralPredicate(Formula.BTRUE),
	};
	Object[] testUnpermitted = new Object[]{
			"x/x/x", 
			"x mod x mod x", 
			"x domsub y + z", 
			"x setminus y inter z", 
			"x\u2225y\u2225z"
			
	};

	private static AssociativeExpression mAssociativeExpression(int tag, 
			Expression... children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	private static AssociativePredicate mAssociativePredicate(int tag, 
			Predicate... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}

	private static AtomicExpression mAtomicExpression(int tag) {
		return ff.makeAtomicExpression(tag, null);
	}

	private static BinaryExpression mBinaryExpression(int tag, Expression left, 
			Expression right) {
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	private static BinaryPredicate mBinaryPredicate(int tag, Predicate left, 
			Predicate right) {
		return ff.makeBinaryPredicate(tag, left, right, null);
	}

	private static IntegerLiteral mIntegerLiteral(int value) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(value), null);
	}

	private static LiteralPredicate mLiteralPredicate(int tag) {
		return ff.makeLiteralPredicate(tag, null);
	}

	private static QuantifiedExpression mQuantifiedExpression(int tag, 
			BoundIdentDecl[] boundIdents, Predicate pred, Expression expr, QuantifiedExpression.Form form) {
		return ff.makeQuantifiedExpression(tag, boundIdents, pred, 
				expr, null, form);
	}

	private static QuantifiedPredicate mQuantifiedPredicate(int tag, 
			BoundIdentDecl[] boundIdents, Predicate pred) {
		return ff.makeQuantifiedPredicate(tag, boundIdents, pred, 
				null);
	}

	private static RelationalPredicate mRelationalPredicate(int tag, Expression left, 
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	private static SimplePredicate mSimplePredicate(int tag, Expression expr) {
		return ff.makeSimplePredicate(tag, expr, null);
	}

	private static UnaryExpression mUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	private static UnaryPredicate mUnaryPredicate(int tag, Predicate child) {
		return ff.makeUnaryPredicate(tag, child, null);
	}

	
	
	
	/**
	 * Main test routine.
	 */
	public void testParser() {
		for (int i = 0; i < testPairs.length; i += 2) {
			final String input = (String) testPairs[i];
			final Predicate expectedTree = (Predicate) testPairs[i + 1];

			IParseResult result = ff.parsePredicate(input);
			assertTrue("\nParser unexpectedly failed on: " + input
					+ "\nwith error message: " + result.getProblems(), 
					result.isSuccess());
			assertEquals("\nTest failed on: " + input + "\nTree expected: "
					+ expectedTree.getSyntaxTree() + "\nTree received: "
					+ result.getParsedPredicate().getSyntaxTree()
					+ "\nParser result: " + result, 
					expectedTree, result.getParsedPredicate());
		}
	}
}
