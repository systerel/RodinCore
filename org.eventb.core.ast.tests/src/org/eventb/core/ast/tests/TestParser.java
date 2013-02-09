/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - added tests for lambda with duplicate idents in pattern
 *     Systerel - added tests for illegal quantified expressions
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesMemberOf;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mPredicateVariable;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.junit.Test;

/**
 * @author franz
 *
 */
public class TestParser extends AbstractTests {
	
	private static FreeIdentifier id_x = mFreeIdentifier("x");
	private static FreeIdentifier id_y = mFreeIdentifier("y");
	private static FreeIdentifier id_z = mFreeIdentifier("z");
	private static FreeIdentifier id_t = mFreeIdentifier("t");
	private static FreeIdentifier id_u = mFreeIdentifier("u");
	private static FreeIdentifier id_v = mFreeIdentifier("v");
	private static FreeIdentifier id_a = mFreeIdentifier("a");
	private static FreeIdentifier id_b = mFreeIdentifier("b");
	private static FreeIdentifier id_S = mFreeIdentifier("S");
	private static FreeIdentifier id_T = mFreeIdentifier("T");
	private static FreeIdentifier id_f = mFreeIdentifier("f");
	private static FreeIdentifier id_filter = mFreeIdentifier("filter");

	private static FreeIdentifier id_x_V1 = ffV1.makeFreeIdentifier("x", null);
	private static FreeIdentifier id_y_V1 = ffV1.makeFreeIdentifier("y", null);
	private static FreeIdentifier id_z_V1 = ffV1.makeFreeIdentifier("z", null);
	private static FreeIdentifier id_t_V1 = ffV1.makeFreeIdentifier("t", null);
	private static FreeIdentifier id_u_V1 = ffV1.makeFreeIdentifier("u", null);
	private static FreeIdentifier id_v_V1 = ffV1.makeFreeIdentifier("v", null);
	private static FreeIdentifier id_a_V1 = ffV1.makeFreeIdentifier("a", null);
	private static FreeIdentifier id_b_V1 = ffV1.makeFreeIdentifier("b", null);
	private static FreeIdentifier id_S_V1 = ffV1.makeFreeIdentifier("S", null);
	private static FreeIdentifier id_T_V1 = ffV1.makeFreeIdentifier("T", null);
	private static FreeIdentifier id_f_V1 = ffV1.makeFreeIdentifier("f", null);
	private static FreeIdentifier id_filter_V1 = ffV1.makeFreeIdentifier(
			"filter", null);
	private static FreeIdentifier id_partition_V1 = ffV1.makeFreeIdentifier(
			"partition", null);
	
	private static BoundIdentDecl bd_x = mBoundIdentDecl("x");
	private static BoundIdentDecl bd_y = mBoundIdentDecl("y");
	private static BoundIdentDecl bd_z = mBoundIdentDecl("z");
	private static BoundIdentDecl bd_s = mBoundIdentDecl("s");
	private static BoundIdentDecl bd_t = mBoundIdentDecl("t");
	private static BoundIdentDecl bd_f = mBoundIdentDecl("f");
	private static BoundIdentDecl bd_a = mBoundIdentDecl("a");
	private static BoundIdentDecl bd_xp = mBoundIdentDecl("x'");
	private static BoundIdentDecl bd_yp = mBoundIdentDecl("y'");
	private static BoundIdentDecl bd_zp = mBoundIdentDecl("z'");

	private static BoundIdentDecl bd_x_V1 = ffV1.makeBoundIdentDecl("x", null);
	private static BoundIdentDecl bd_y_V1 = ffV1.makeBoundIdentDecl("y", null);
	private static BoundIdentDecl bd_z_V1 = ffV1.makeBoundIdentDecl("z", null);
	private static BoundIdentDecl bd_s_V1 = ffV1.makeBoundIdentDecl("s", null);
	private static BoundIdentDecl bd_t_V1 = ffV1.makeBoundIdentDecl("t", null);
	private static BoundIdentDecl bd_f_V1 = ffV1.makeBoundIdentDecl("f", null);
	private static BoundIdentDecl bd_a_V1 = ffV1.makeBoundIdentDecl("a", null);
	private static BoundIdentDecl bd_xp_V1 = ffV1.makeBoundIdentDecl("x'", null);
	private static BoundIdentDecl bd_yp_V1 = ffV1.makeBoundIdentDecl("y'", null);
	private static BoundIdentDecl bd_zp_V1 = ffV1.makeBoundIdentDecl("z'", null);
	private static BoundIdentDecl bd_partition_V1 = ffV1.makeBoundIdentDecl(
			"partition", null);

	private static BoundIdentifier b0 = mBoundIdentifier(0);
	private static BoundIdentifier b1 = mBoundIdentifier(1);
	private static BoundIdentifier b2 = mBoundIdentifier(2);
	private static BoundIdentifier b3 = mBoundIdentifier(3);

	private static BoundIdentifier b0_V1 = ffV1.makeBoundIdentifier(0, null);
	private static BoundIdentifier b1_V1 = ffV1.makeBoundIdentifier(1, null);
	private static BoundIdentifier b2_V1 = ffV1.makeBoundIdentifier(2, null);
	private static BoundIdentifier b3_V1 = ffV1.makeBoundIdentifier(3, null);

	
	private static LiteralPredicate bfalse = mLiteralPredicate(Formula.BFALSE);

	private static LiteralPredicate bfalse_V1 = ffV1.makeLiteralPredicate(Formula.BFALSE, null);
	
	static SourceLocationChecker slChecker = new SourceLocationChecker();
	
	private static abstract class TestPair {

		private static final LanguageVersion[] VERSIONS
				= LanguageVersion.values();

		private final String image;
		private final Formula<?>[] expects;

		/**
		 * Constructs a test case for some formula to parse
		 *
		 * @param image
		 *            the string image of the formula
		 * @param expects
		 *            the expected formula trees, one for each language version.
		 *            Provide <code>null</code> if the formula should not parse
		 */
		TestPair(String image, Formula<?>[] expects) {
			this.image = image;
			this.expects = expects;
			assertEquals(VERSIONS.length, expects.length);
		}
		final void verify() {
			for (int i = 0; i < VERSIONS.length; ++i) {
				final LanguageVersion version = VERSIONS[i];
				final Formula<?> expected = expects[i];
				if (expected == null) {
					verifyFailure(version);
				} else {
					verify(version, expected);
				}
			}
		}
		@SuppressWarnings("deprecation")
		final void verifyFailure(LanguageVersion version) {
			final IParseResult result = parseResult(image, version);
			assertTrue(result.hasProblem());
			assertFalse(result.isSuccess());
		}
		final void verify(LanguageVersion version, Formula<?> exp) {
			final Formula<?> parsedFormula = parseAndCheck(image, version, exp);
			
			// Verify that source locations are properly nested
			parsedFormula.accept(slChecker);

			// also check that the source location reported corresponds to the
			// whole substring.
			final SourceLocation loc = parsedFormula.getSourceLocation();
			final String subImage = image.substring(loc.getStart(), loc.getEnd() + 1);
			parseAndCheck(subImage, version, exp);
		}
		final Formula<?> parseAndCheck(String stringToParse,
				LanguageVersion version, Formula<?> expected) {
			Formula<?> actual = parse(stringToParse, version);
			assertEquals("Unexpected parser result", expected, actual);
			return actual;
		}
		abstract Formula<?> parse(String input, LanguageVersion version);
		abstract IParseResult parseResult(String input, LanguageVersion version);
	}
	
	private static class ExprTestPair extends TestPair {
		ExprTestPair(String image, Expression... expects) {
			super(image, expects);
		}
		@Override 
		Formula<?> parse(String input, LanguageVersion version) {
			return parseExpression(input, version);
		}
		@Override 
		IParseResult parseResult(String input, LanguageVersion version) {
			return ff.parseExpression(input, version, null);
		}
	}
	
	private static class PredTestPair extends TestPair {
		PredTestPair(String image, Predicate... expects) {
			super(image, expects);
		}
		@Override 
		Formula<?> parse(String input, LanguageVersion version) {
			return parsePredicate(input, version);
		}
		@Override 
		IParseResult parseResult(String input, LanguageVersion version) {
			return ff.parsePredicate(input, version, null);
		}
	}
	
	private static class AssignmentTestPair extends TestPair {
		AssignmentTestPair(String image, Assignment... expects) {
			super(image, expects);
		}
		@Override 
		Formula<?> parse(String input, LanguageVersion version) {
			return parseAssignment(input, version);
		}
		@Override 
		IParseResult parseResult(String input, LanguageVersion version) {
			return ff.parseAssignment(input, version, null);
		}
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
	TestPair[] preds = new TestPair[]{
			// AtomicPredicate
			new PredTestPair(
					"\u22a5", 
					ffV1.makeLiteralPredicate(Formula.BFALSE, null),
					bfalse
			), new PredTestPair(
					"\u22a4", 
					ffV1.makeLiteralPredicate(Formula.BTRUE, null),
					mLiteralPredicate(Formula.BTRUE)
			), new PredTestPair(
					"finite(x)", 
					ffV1.makeSimplePredicate(Formula.KFINITE, id_x_V1, null),
					mSimplePredicate(id_x) 
			), new PredTestPair(
					"x=x", 
					ffV1.makeRelationalPredicate(Formula.EQUAL, id_x_V1, id_x_V1, null),
					mRelationalPredicate(Formula.EQUAL, id_x, id_x) 
			), new PredTestPair(
					"x\u2260x", 
					ffV1.makeRelationalPredicate(Formula.NOTEQUAL, id_x_V1, id_x_V1, null),
					mRelationalPredicate(Formula.NOTEQUAL, id_x, id_x) 
			), new PredTestPair(
					"x<x", 
					ffV1.makeRelationalPredicate(Formula.LT, id_x_V1, id_x_V1, null),
					mRelationalPredicate(Formula.LT, id_x, id_x)
			), new PredTestPair(
					"x≤x", 
					ffV1.makeRelationalPredicate(Formula.LE, id_x_V1, id_x_V1, null),
					mRelationalPredicate(Formula.LE, id_x, id_x) 
			), new PredTestPair(
					"x>x", 
					ffV1.makeRelationalPredicate(Formula.GT, id_x_V1, id_x_V1, null),
					mRelationalPredicate(Formula.GT, id_x, id_x) 
			), new PredTestPair(
					"x≥x", 
					ffV1.makeRelationalPredicate(Formula.GE, id_x_V1, id_x_V1, null),
					mRelationalPredicate(Formula.GE, id_x, id_x) 
			), new PredTestPair(
					"x\u2208S", 
					ffV1.makeRelationalPredicate(Formula.IN, id_x_V1, id_S_V1, null),
					mRelationalPredicate(Formula.IN, id_x, id_S)
			), new PredTestPair(
					"x\u2209S", 
					ffV1.makeRelationalPredicate(Formula.NOTIN, id_x_V1, id_S_V1, null),
					mRelationalPredicate(Formula.NOTIN, id_x, id_S) 
			), new PredTestPair(
					"x\u2282S", 
					ffV1.makeRelationalPredicate(Formula.SUBSET, id_x_V1, id_S_V1, null),
					mRelationalPredicate(Formula.SUBSET, id_x, id_S) 
			), new PredTestPair(
					"x\u2284S", 
					ffV1.makeRelationalPredicate(Formula.NOTSUBSET, id_x_V1, id_S_V1, null),
					mRelationalPredicate(Formula.NOTSUBSET, id_x, id_S) 
			), new PredTestPair(
					"x\u2286S", 
					ffV1.makeRelationalPredicate(Formula.SUBSETEQ, id_x_V1, id_S_V1, null),
					mRelationalPredicate(Formula.SUBSETEQ, id_x, id_S)
			), new PredTestPair(
					"x\u2288S", 
					ffV1.makeRelationalPredicate(Formula.NOTSUBSETEQ, id_x_V1, id_S_V1, null),
					mRelationalPredicate(Formula.NOTSUBSETEQ, id_x, id_S) 
			), new PredTestPair(
					"(\u22a5)", 
					ffV1.makeLiteralPredicate(Formula.BFALSE, null),
					bfalse
			),
			
			// LiteralPredicate
			new PredTestPair(
					"\u00ac\u22a5", 
					ffV1.makeUnaryPredicate(Formula.NOT, bfalse_V1, null),
					mUnaryPredicate(Formula.NOT, bfalse) 
			), new PredTestPair(
					"\u00ac\u00ac\u22a5", 
					ffV1.makeUnaryPredicate(Formula.NOT,
							ffV1.makeUnaryPredicate(Formula.NOT, bfalse_V1, null), null
					),
					mUnaryPredicate(Formula.NOT, 
							mUnaryPredicate(Formula.NOT, bfalse)
					)
			),
			
			// PredicateVariable
			new PredTestPair(
					"$P",
					ffV1.makePredicateVariable("$P", null),
					mPredicateVariable("$P")),
			new PredTestPair(
					"$P\u2227$Q",
					ffV1.makeAssociativePredicate(Formula.LAND,
							mList(ffV1.makePredicateVariable("$P", null),
							ffV1.makePredicateVariable("$Q", null)), null),
					mAssociativePredicate(Formula.LAND,
							mPredicateVariable("$P"),
							mPredicateVariable("$Q"))),

			// SimplePredicate
			new PredTestPair(
					"\u22a5\u2227\u22a5", 
					ffV1.makeAssociativePredicate(Formula.LAND, mList(bfalse_V1, bfalse_V1), null),
					mAssociativePredicate(Formula.LAND, bfalse, bfalse) 
			), new PredTestPair(
					"\u22a5\u2228\u22a5", 
					ffV1.makeAssociativePredicate(Formula.LOR, mList(bfalse_V1, bfalse_V1), null),
					mAssociativePredicate(Formula.LOR, bfalse, bfalse)
			), new PredTestPair(
					"\u22a5\u2227\u22a5\u2227\u22a5", 
					ffV1.makeAssociativePredicate(Formula.LAND, mList(bfalse_V1, bfalse_V1, bfalse_V1), null),
					mAssociativePredicate(Formula.LAND, bfalse, bfalse, bfalse) 
			), new PredTestPair(
					"\u22a5\u2228\u22a5\u2228\u22a5", 
					ffV1.makeAssociativePredicate(Formula.LOR, mList(bfalse_V1, bfalse_V1, bfalse_V1), null),
					mAssociativePredicate(Formula.LOR, bfalse, bfalse, bfalse) 
			),
			
			// MultiplePredicate
			new PredTestPair(
					"partition(x)", 
					null,
					mMultiplePredicate(Formula.KPARTITION, id_x)
			), new ExprTestPair(
					"partition(x)", 
					ffV1.makeBinaryExpression(Formula.FUNIMAGE, id_partition_V1, id_x_V1, null),
					null
			), new PredTestPair(
					"partition(x, y)", 
					null,
					mMultiplePredicate(Formula.KPARTITION, id_x, id_y)
			), new PredTestPair(
					"partition(x, y, z)",
					null,
					mMultiplePredicate(Formula.KPARTITION, id_x, id_y, id_z)
			), new PredTestPair(
					"\u2200partition\u00b7partition(x)=y",
					ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_partition_V1),
							ffV1.makeRelationalPredicate(Formula.EQUAL,
									ffV1.makeBinaryExpression(Formula.FUNIMAGE, b0_V1, id_x_V1, null),
									id_y_V1, null), null
					),
					null
			),

			// UnquantifiedPredicate
			new PredTestPair(
					"\u22a5\u21d2\u22a5", 
					ffV1.makeBinaryPredicate(Formula.LIMP, bfalse_V1, bfalse_V1, null),
					mBinaryPredicate(Formula.LIMP, bfalse, bfalse) 
			), new PredTestPair(
					"\u22a5\u21d4\u22a5", 
					ffV1.makeBinaryPredicate(Formula.LEQV, bfalse_V1, bfalse_V1, null),
					mBinaryPredicate(Formula.LEQV, bfalse, bfalse) 
			),
			
			// Quantifier + IdentList + Predicate
			new PredTestPair(
					"\u2200x\u00b7\u22a5", 
					ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_x_V1), bfalse_V1, null),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_x), bfalse)
			), new PredTestPair(
					"\u2203x\u00b7\u22a5", 
					ffV1.makeQuantifiedPredicate(Formula.EXISTS, mList(bd_x_V1), bfalse_V1, null),
					mQuantifiedPredicate(Formula.EXISTS, mList(bd_x), bfalse)
			), new PredTestPair(
					"\u2200x, y, z\u00b7\u22a5", 
					ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_x_V1, bd_y_V1, bd_z_V1), bfalse_V1, null),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y, bd_z), bfalse)
			), new PredTestPair(
					"\u2203x, y, z\u00b7\u22a5", 
					ffV1.makeQuantifiedPredicate(Formula.EXISTS, mList(bd_x_V1, bd_y_V1, bd_z_V1), bfalse_V1, null),
					mQuantifiedPredicate(Formula.EXISTS, mList(bd_x, bd_y, bd_z), bfalse)
			), new PredTestPair(
					"\u2200x, y\u00b7\u2200s, t\u00b7\u22a5", 
					ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_x_V1, bd_y_V1),
							ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_s_V1, bd_t_V1), bfalse_V1, null), null
					),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y), 
							mQuantifiedPredicate(Formula.FORALL, mList(bd_s, bd_t), bfalse)
					)
			), new PredTestPair(
					"\u2203x, y\u00b7\u2203s, t\u00b7\u22a5", 
					ffV1.makeQuantifiedPredicate(Formula.EXISTS, mList(bd_x_V1, bd_y_V1),
							ffV1.makeQuantifiedPredicate(Formula.EXISTS, mList(bd_s_V1, bd_t_V1), bfalse_V1, null), null
					),
					mQuantifiedPredicate(Formula.EXISTS, mList(bd_x, bd_y), 
							mQuantifiedPredicate(Formula.EXISTS, mList(bd_s, bd_t), bfalse)
					)
			), new PredTestPair(
					"\u2200x, y\u00b7\u2203s, t\u00b7\u22a5", 
					ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_x_V1, bd_y_V1),
							ffV1.makeQuantifiedPredicate(Formula.EXISTS, mList(bd_s_V1, bd_t_V1), bfalse_V1, null), null
					),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y), 
							mQuantifiedPredicate(Formula.EXISTS, mList(bd_s, bd_t), bfalse)
					)
			), new PredTestPair(
					"\u2200 x,y \u00b7\u2200 s,t \u00b7 x\u2208s \u2227 y\u2208t",
					ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_x_V1, bd_y_V1),
							ffV1.makeQuantifiedPredicate(Formula.FORALL, mList(bd_s_V1, bd_t_V1),
									ffV1.makeAssociativePredicate(Formula.LAND,
											mList(ffV1.makeRelationalPredicate(Formula.IN, b3_V1, b1_V1, null),
											ffV1.makeRelationalPredicate(Formula.IN, b2_V1, b0_V1, null)), null
									), null
							), null
					),
					mQuantifiedPredicate(Formula.FORALL, mList(bd_x, bd_y), 
							mQuantifiedPredicate(Formula.FORALL, mList(bd_s, bd_t),
									mAssociativePredicate(Formula.LAND,
											mRelationalPredicate(Formula.IN, b3, b1),
											mRelationalPredicate(Formula.IN, b2, b0)
									)
							)
					)
			),
			
			// Special cases
			new PredTestPair(
					"filter =  { f ∣ ( ∀ a · ⊤ ) } ∧  a = b", 
					ffV1.makeAssociativePredicate(Formula.LAND,
							mList(ffV1.makeRelationalPredicate(
									Formula.EQUAL,
									id_filter_V1,
									ffV1.makeQuantifiedExpression(
											Formula.CSET,
											mList(bd_f_V1),
											ffV1.makeQuantifiedPredicate(
													Formula.FORALL,
													mList(bd_a_V1),
													ffV1.makeLiteralPredicate(Formula.BTRUE, null), null
											),
											b0_V1, null, Implicit), null
							),
							ffV1.makeRelationalPredicate(Formula.EQUAL, id_a_V1, id_b_V1, null)), null
					),
					mAssociativePredicate(Formula.LAND, 
							mRelationalPredicate(
									Formula.EQUAL, 
									id_filter, 
									mQuantifiedExpression(
											Formula.CSET, Implicit, 
											mList(bd_f), 
											mQuantifiedPredicate(
													Formula.FORALL, 
													mList(bd_a), 					
													mLiteralPredicate(Formula.BTRUE)
											),
											b0)
							),
							mRelationalPredicate(Formula.EQUAL, id_a, id_b)
					)
			),
					
			// with ident bound twice
			new PredTestPair(
					"∀x·x ∈ S ∧ (∀x·x ∈ T)",
					ffV1.makeQuantifiedPredicate(Formula.FORALL,
							mList(bd_x_V1),
							ffV1.makeAssociativePredicate(Formula.LAND,
									mList(ffV1.makeRelationalPredicate(Formula.IN, b0_V1, id_S_V1, null),
									ffV1.makeQuantifiedPredicate(Formula.FORALL,
											mList(bd_x_V1),
											ffV1.makeRelationalPredicate(Formula.IN, b0_V1, id_T_V1, null), null
									)), null
							), null
					),
					mQuantifiedPredicate(Formula.FORALL,
							mList(bd_x),
							mAssociativePredicate(Formula.LAND,
									mRelationalPredicate(Formula.IN, b0, id_S),
									mQuantifiedPredicate(Formula.FORALL,
											mList(bd_x),
											mRelationalPredicate(Formula.IN, b0, id_T)
									)
							)
					)
			),
			
			// with two idents bound twice
			new PredTestPair(
					"∀x,y\u00b7x ∈ S ∧ y ∈ T ∧ (∀y,x\u00b7x ∈ T ∧ y ∈ S)",
					ffV1.makeQuantifiedPredicate(Formula.FORALL,
							mList(bd_x_V1, bd_y_V1),
							ffV1.makeAssociativePredicate(Formula.LAND,
									mList(ffV1.makeRelationalPredicate(Formula.IN, b1_V1, id_S_V1, null),
									ffV1.makeRelationalPredicate(Formula.IN, b0_V1, id_T_V1, null),
									ffV1.makeQuantifiedPredicate(Formula.FORALL,
											mList(bd_y_V1, bd_x_V1),
											ffV1.makeAssociativePredicate(Formula.LAND,
													mList(ffV1.makeRelationalPredicate(Formula.IN, b0_V1, id_T_V1, null),
													ffV1.makeRelationalPredicate(Formula.IN, b1_V1, id_S_V1, null)), null
											), null
									)), null
							), null
					),
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
					)
			),
			
			// with two idents bound twice
			new PredTestPair(
					"∀x,y,z \u00b7 finite(x ∪ y ∪ z ∪ {y \u2223 y ⊆ x ∪ z})",
					ffV1.makeQuantifiedPredicate(Formula.FORALL,
							mList(bd_x_V1, bd_y_V1, bd_z_V1),
							ffV1.makeSimplePredicate(
									Formula.KFINITE, ffV1.makeAssociativeExpression(Formula.BUNION,
											mList(b2_V1, b1_V1, b0_V1,
											ffV1.makeQuantifiedExpression(Formula.CSET,
													mList(bd_y_V1),
													ffV1.makeRelationalPredicate(Formula.SUBSETEQ,
															b0_V1,
															ffV1.makeAssociativeExpression(Formula.BUNION, mList(b3_V1, b1_V1), null), null
													),
													b0_V1,  null, Implicit
											)), null
									), null
							), null
					),
					mQuantifiedPredicate(Formula.FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mAssociativeExpression(Formula.BUNION,
											b2, b1, b0,
											mQuantifiedExpression(Formula.CSET, Implicit,
													mList(bd_y),
													mRelationalPredicate(Formula.SUBSETEQ,
															b0,
															mAssociativeExpression(Formula.BUNION, b3, b1)
													),
													b0
											)
									)
							)
					)
			),
			
			// Test that line terminator and strange spaces are ignored
			new PredTestPair(
					"\t\n\r\f ⊤ \u00A0\u2007\u202F",
					ffV1.makeLiteralPredicate(Formula.BTRUE, null),
					mLiteralPredicate(Formula.BTRUE)
			),
	};

	@SuppressWarnings("deprecation")
	ExprTestPair[] exprs = new ExprTestPair[] {
			// SimpleExpression
			new ExprTestPair(
					"bool(\u22a5)", 
					ffV1.makeBoolExpression(bfalse_V1, null),
					mBoolExpression(bfalse)
			), new ExprTestPair(
					"bool($P)",
					ffV1.makeBoolExpression(ffV1.makePredicateVariable("$P", null), null),
					mBoolExpression(mPredicateVariable("$P"))
			), new ExprTestPair(
					"card(x)", 
					ffV1.makeUnaryExpression(Formula.KCARD, id_x_V1, null),
					mUnaryExpression(Formula.KCARD, id_x) 
			), new ExprTestPair(
					"\u2119(x)", 
					ffV1.makeUnaryExpression(Formula.POW, id_x_V1, null),
					mUnaryExpression(Formula.POW, id_x) 
			), new ExprTestPair(
					"\u21191(x)", 
					ffV1.makeUnaryExpression(Formula.POW1, id_x_V1, null),
					mUnaryExpression(Formula.POW1, id_x) 
			), new ExprTestPair(
					"union(x)", 
					ffV1.makeUnaryExpression(Formula.KUNION, id_x_V1, null),
					mUnaryExpression(Formula.KUNION, id_x) 
			), new ExprTestPair(
					"inter(x)", 
					ffV1.makeUnaryExpression(Formula.KINTER, id_x_V1, null),
					mUnaryExpression(Formula.KINTER, id_x) 
			), new ExprTestPair(
					"dom(x)", 
					ffV1.makeUnaryExpression(Formula.KDOM, id_x_V1, null),
					mUnaryExpression(Formula.KDOM, id_x) 
			), new ExprTestPair(
					"ran(x)", 
					ffV1.makeUnaryExpression(Formula.KRAN, id_x_V1, null),
					mUnaryExpression(Formula.KRAN, id_x) 
			), new ExprTestPair(
					"prj1(x)", 
					ffV1.makeUnaryExpression(Formula.KPRJ1, id_x_V1, null),
					mBinaryExpression(Formula.FUNIMAGE,
							mAtomicExpression(Formula.KPRJ1_GEN), id_x)
			), new ExprTestPair(
					"prj2(x)", 
					ffV1.makeUnaryExpression(Formula.KPRJ2, id_x_V1, null),
					mBinaryExpression(Formula.FUNIMAGE,
							mAtomicExpression(Formula.KPRJ2_GEN), id_x)
			), new ExprTestPair(
					"id(x)", 
					ffV1.makeUnaryExpression(Formula.KID, id_x_V1, null),
					mBinaryExpression(Formula.FUNIMAGE,
							mAtomicExpression(Formula.KID_GEN), id_x)
			), new ExprTestPair(
					"(x)", 
					id_x_V1,
					id_x 
			), new ExprTestPair(
					"{x, y\u00b7\u22a5\u2223z}", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.CSET, Explicit,
							mList(bd_x, bd_y), bfalse, id_z
					) 
			), new ExprTestPair(
					"{x\u00b7\u22a5\u2223z}", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.CSET, Explicit, 
							mList(bd_x), bfalse, id_z
					) 
			), new ExprTestPair(
					"{x, y\u00b7\u22a5\u2223y}", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1), bfalse_V1, b0_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.CSET, Explicit, 
							mList(bd_x, bd_y), bfalse, b0
					) 
			), new ExprTestPair(
					"{x\u00b7\u22a5\u2223x}", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1), bfalse_V1, b0_V1, null, Implicit
					),
					mQuantifiedExpression(Formula.CSET, Implicit, 
							mList(bd_x), bfalse, b0
					) 
			), new ExprTestPair(
					"{x\u2223\u22a5}", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1), bfalse_V1, b0_V1, null, Implicit
					),
					mQuantifiedExpression(Formula.CSET, Implicit, 
							mList(bd_x), bfalse, b0
					) 
			), new ExprTestPair(
					"{x+y\u2223\u22a5}", 
					ffV1.makeQuantifiedExpression(Formula.CSET, mList(bd_x_V1, bd_y_V1), bfalse_V1,
							ffV1.makeAssociativeExpression(Formula.PLUS,
									mList(b1_V1,
									b0_V1), null
							), null, Implicit
					),
					mQuantifiedExpression(Formula.CSET, Implicit, mList(bd_x, bd_y), bfalse, 
							mAssociativeExpression(Formula.PLUS, 
									b1, 
									b0
							)
					) 
			), new ExprTestPair(
					"{}", 
					ffV1.makeSetExtension(new Expression[0], null),
					mSetExtension()
			), new ExprTestPair(
					"{x}", 
					ffV1.makeSetExtension(id_x_V1, null),
					mSetExtension(id_x)
			), new ExprTestPair(
					"{x, y}", 
					ffV1.makeSetExtension(mList(id_x_V1, id_y_V1), null),
					mSetExtension(id_x, id_y)
			), new ExprTestPair(
					"{x, y, z}", 
					ffV1.makeSetExtension(mList(id_x_V1, id_y_V1, id_z_V1), null),
					mSetExtension(id_x, id_y, id_z)
			), new ExprTestPair(
					"\u2124", 
					ffV1.makeAtomicExpression(Formula.INTEGER, null),
					mAtomicExpression(Formula.INTEGER) 
			), new ExprTestPair(
					"\u2115", 
					ffV1.makeAtomicExpression(Formula.NATURAL, null),
					mAtomicExpression(Formula.NATURAL) 
			), new ExprTestPair(
					"\u21151", 
					ffV1.makeAtomicExpression(Formula.NATURAL1, null),
					mAtomicExpression(Formula.NATURAL1) 
			), new ExprTestPair(
					"BOOL", 
					ffV1.makeAtomicExpression(Formula.BOOL, null),
					mAtomicExpression(Formula.BOOL) 
			), new ExprTestPair(
					"TRUE", 
					ffV1.makeAtomicExpression(Formula.TRUE, null),
					mAtomicExpression(Formula.TRUE) 
			), new ExprTestPair(
					"FALSE", 
					ffV1.makeAtomicExpression(Formula.FALSE, null),
					mAtomicExpression(Formula.FALSE) 
			), new ExprTestPair(
					"pred", 
					ffV1.makeAtomicExpression(Formula.KPRED, null),
					mAtomicExpression(Formula.KPRED) 
			), new ExprTestPair(
					"succ", 
					ffV1.makeAtomicExpression(Formula.KSUCC, null),
					mAtomicExpression(Formula.KSUCC) 
			), new ExprTestPair(
					"prj1",
					null,
					mAtomicExpression(Formula.KPRJ1_GEN)
			), new ExprTestPair(
					"prj2",
					null,
					mAtomicExpression(Formula.KPRJ2_GEN)
			), new ExprTestPair(
					"id",
					null,
					mAtomicExpression(Formula.KID_GEN)
			), new ExprTestPair(
					"2", 
					ffV1.makeIntegerLiteral(BigInteger.valueOf(2), null),
					mIntegerLiteral(2) 
			), new ExprTestPair(
					"3000000000",
					ffV1.makeIntegerLiteral(BigInteger.valueOf(3000000000L), null),
					mIntegerLiteral(3000000000L)
			), new ExprTestPair(
					"−3000000000",
					ffV1.makeIntegerLiteral(BigInteger.valueOf(-3000000000L), null),
					mIntegerLiteral(-3000000000L)
			), new ExprTestPair(
					"50000000000000000000",
					ffV1.makeIntegerLiteral(//
							new BigInteger("50000000000000000000"), null),
					ff.makeIntegerLiteral(//
							new BigInteger("50000000000000000000"), null)
			), new ExprTestPair(
					"−50000000000000000000",
					ffV1.makeIntegerLiteral(//
							new BigInteger("-50000000000000000000"), null),
					ff.makeIntegerLiteral(//
							new BigInteger("-50000000000000000000"), null)
			), new ExprTestPair(
					"−1", 
					ffV1.makeIntegerLiteral(BigInteger.valueOf(-1), null),
					mIntegerLiteral(-1) 
			),
			
			// Primary
			new ExprTestPair(
					"x\u223c", 
					ffV1.makeUnaryExpression(Formula.CONVERSE, id_x_V1, null),
					mUnaryExpression(Formula.CONVERSE, id_x)
			), new ExprTestPair(
					"x\u223c\u223c", 
					ffV1.makeUnaryExpression(Formula.CONVERSE,
							ffV1.makeUnaryExpression(Formula.CONVERSE, id_x_V1, null), null
					),
					mUnaryExpression(Formula.CONVERSE, 
							mUnaryExpression(Formula.CONVERSE, id_x)
					)
			),
			
			// Image
			new ExprTestPair(
					"f(x)", 
					ffV1.makeBinaryExpression(Formula.FUNIMAGE, id_f_V1, id_x_V1, null),
					mBinaryExpression(Formula.FUNIMAGE, id_f, id_x)
			), new ExprTestPair(
					"f[x]", 
					ffV1.makeBinaryExpression(Formula.RELIMAGE, id_f_V1, id_x_V1, null),
					mBinaryExpression(Formula.RELIMAGE, id_f, id_x)
			), new ExprTestPair(
					"f[x](y)", 
					ffV1.makeBinaryExpression(Formula.FUNIMAGE,
							ffV1.makeBinaryExpression(Formula.RELIMAGE, id_f_V1, id_x_V1, null),
							id_y_V1, null
					),
					mBinaryExpression(Formula.FUNIMAGE, 
							mBinaryExpression(Formula.RELIMAGE, id_f, id_x),
							id_y
					)
			), new ExprTestPair(
					"f(x)[y]", 
					ffV1.makeBinaryExpression(Formula.RELIMAGE,
							ffV1.makeBinaryExpression(Formula.FUNIMAGE, id_f_V1, id_x_V1, null),
							id_y_V1, null
					),
					mBinaryExpression(Formula.RELIMAGE, 
							mBinaryExpression(Formula.FUNIMAGE, id_f, id_x), 
							id_y
					) 
			), new ExprTestPair(
					"f(x)(y)", 
					ffV1.makeBinaryExpression(Formula.FUNIMAGE,
							ffV1.makeBinaryExpression(Formula.FUNIMAGE, id_f_V1, id_x_V1, null),
							id_y_V1, null
					),
					mBinaryExpression(Formula.FUNIMAGE, 
							mBinaryExpression(Formula.FUNIMAGE, id_f, id_x), 
							id_y
					)
			), new ExprTestPair(
					"f[x][y]", 
					ffV1.makeBinaryExpression(Formula.RELIMAGE,
							ffV1.makeBinaryExpression(Formula.RELIMAGE, id_f_V1, id_x_V1, null),
							id_y_V1, null
					),
					mBinaryExpression(Formula.RELIMAGE, 
							mBinaryExpression(Formula.RELIMAGE, id_f, id_x), 
							id_y
					)
			),
			
			// Factor
			new ExprTestPair(
					"x^y", 
					ffV1.makeBinaryExpression(Formula.EXPN, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.EXPN, id_x, id_y)
			), 
			
			// Term
			new ExprTestPair(
					"x\u2217y", 
					ffV1.makeAssociativeExpression(Formula.MUL, mList(id_x_V1, id_y_V1), null),
					mAssociativeExpression(Formula.MUL, id_x, id_y)
			), new ExprTestPair(
					"x\u2217y\u2217z", 
					ffV1.makeAssociativeExpression(Formula.MUL, mList(id_x_V1, id_y_V1, id_z_V1), null),
					mAssociativeExpression(Formula.MUL, id_x, id_y, id_z)
			), new ExprTestPair(
					"x\u00f7y", 
					ffV1.makeBinaryExpression(Formula.DIV, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.DIV, id_x, id_y)
			), new ExprTestPair(
					"x mod y", 
					ffV1.makeBinaryExpression(Formula.MOD, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.MOD, id_x, id_y)
			), 
			
			// ArithmeticExpr
			new ExprTestPair(
					"x+y", 
					ffV1.makeAssociativeExpression(Formula.PLUS, mList(id_x_V1, id_y_V1), null),
					mAssociativeExpression(Formula.PLUS, id_x, id_y) 
			), new ExprTestPair(
					"x+y+z", 
					ffV1.makeAssociativeExpression(Formula.PLUS, mList(id_x_V1, id_y_V1, id_z_V1), null),
					mAssociativeExpression(Formula.PLUS, id_x, id_y, id_z) 
			), new ExprTestPair(
					"−x+y+z", 
					ffV1.makeAssociativeExpression(Formula.PLUS,
							mList(ffV1.makeUnaryExpression(Formula.UNMINUS, id_x_V1, null),
							id_y_V1,
							id_z_V1), null
					),
					mAssociativeExpression(Formula.PLUS, 
							mUnaryExpression(Formula.UNMINUS, id_x), 
							id_y, 
							id_z
					) 
			), new ExprTestPair(
					"x−y", 
					ffV1.makeBinaryExpression(Formula.MINUS, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.MINUS, id_x, id_y) 
			), new ExprTestPair(
					"x−y−z", 
					ffV1.makeBinaryExpression(Formula.MINUS,
							ffV1.makeBinaryExpression(Formula.MINUS, id_x_V1, id_y_V1, null),
							id_z_V1, null
					),
					mBinaryExpression(Formula.MINUS, 
							mBinaryExpression(Formula.MINUS, id_x, id_y), 
							id_z
					) 
			), new ExprTestPair(
					"−x−y", 
					ffV1.makeBinaryExpression(Formula.MINUS,
							ffV1.makeUnaryExpression(Formula.UNMINUS, id_x_V1, null),
							id_y_V1, null
					),
					mBinaryExpression(Formula.MINUS, 
							mUnaryExpression(Formula.UNMINUS, id_x), 
							id_y
					) 
			), new ExprTestPair(
					"x−y+z−t", 
					ffV1.makeBinaryExpression(Formula.MINUS,
							ffV1.makeAssociativeExpression(Formula.PLUS,
									mList(ffV1.makeBinaryExpression(Formula.MINUS, id_x_V1, id_y_V1, null),
									id_z_V1), null
							), id_t_V1, null
					),
					mBinaryExpression(Formula.MINUS, 
							mAssociativeExpression(Formula.PLUS, 
									mBinaryExpression(Formula.MINUS, id_x, id_y), 
									id_z
							), id_t
					) 
			), new ExprTestPair(
					"−x−y+z−t", 
					ffV1.makeBinaryExpression(Formula.MINUS,
							ffV1.makeAssociativeExpression(Formula.PLUS,
									mList(ffV1.makeBinaryExpression(Formula.MINUS,
											ffV1.makeUnaryExpression(Formula.UNMINUS, id_x_V1, null),
											id_y_V1, null
									), id_z_V1), null
							), id_t_V1, null
					),
					mBinaryExpression(Formula.MINUS, 
							mAssociativeExpression(Formula.PLUS, 
									mBinaryExpression(Formula.MINUS, 
											mUnaryExpression(Formula.UNMINUS, id_x), 
											id_y
									), id_z
							), id_t
					) 
			), new ExprTestPair(
					"x+y−z+t", 
					ffV1.makeAssociativeExpression(Formula.PLUS,
							mList(ffV1.makeBinaryExpression(Formula.MINUS,
									ffV1.makeAssociativeExpression(Formula.PLUS, mList(id_x_V1, id_y_V1), null),
									id_z_V1, null
							), id_t_V1), null
					),
					mAssociativeExpression(Formula.PLUS, 
							mBinaryExpression(Formula.MINUS, 
									mAssociativeExpression(Formula.PLUS, id_x, id_y), 
									id_z
							), id_t
					)
			), new ExprTestPair(
					"−x+y−z+t", 
					ffV1.makeAssociativeExpression(Formula.PLUS,
							mList(ffV1.makeBinaryExpression(Formula.MINUS,
									ffV1.makeAssociativeExpression(Formula.PLUS,
											mList(ffV1.makeUnaryExpression(Formula.UNMINUS, id_x_V1, null),
											id_y_V1), null
									), id_z_V1, null
							), id_t_V1), null
					),
					mAssociativeExpression(Formula.PLUS, 
							mBinaryExpression(Formula.MINUS, 
									mAssociativeExpression(Formula.PLUS, 
											mUnaryExpression(Formula.UNMINUS, id_x), 
											id_y
									), id_z
							), id_t
					) 
			), new ExprTestPair(
					"− 3", 
					ffV1.makeUnaryExpression(Formula.UNMINUS, ffV1.makeIntegerLiteral(BigInteger.valueOf(3), null), null),
					mUnaryExpression(Formula.UNMINUS, mIntegerLiteral(3))
			), new ExprTestPair(
					"−(4)", 
					ffV1.makeUnaryExpression(Formula.UNMINUS, ffV1.makeIntegerLiteral(BigInteger.valueOf(4), null), null),
					mUnaryExpression(Formula.UNMINUS, mIntegerLiteral(4))
			), new ExprTestPair(
					"−x", 
					ffV1.makeUnaryExpression(Formula.UNMINUS, id_x_V1, null),
					mUnaryExpression(Formula.UNMINUS, id_x)
			), new ExprTestPair(
					"−(x+y)", 
					ffV1.makeUnaryExpression(Formula.UNMINUS,
							ffV1.makeAssociativeExpression(Formula.PLUS, mList(id_x_V1, id_y_V1), null), null
					),
					mUnaryExpression(Formula.UNMINUS,
							mAssociativeExpression(Formula.PLUS, id_x, id_y) 
					)
			),
			
			// IntervalExpr
			new ExprTestPair(
					"x\u2025y", 
					ffV1.makeBinaryExpression(Formula.UPTO, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.UPTO, id_x, id_y)
			), 
			
			// RelationExpr
			new ExprTestPair(
					"x\u2297y", 
					ffV1.makeBinaryExpression(Formula.DPROD, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.DPROD, id_x, id_y) 
			), new ExprTestPair(
					"x;y", 
					ffV1.makeAssociativeExpression(Formula.FCOMP, mList(id_x_V1, id_y_V1), null),
					mAssociativeExpression(Formula.FCOMP, id_x, id_y) 
			), new ExprTestPair(
					"x;y;z", 
					ffV1.makeAssociativeExpression(Formula.FCOMP, mList(id_x_V1, id_y_V1, id_z_V1), null),
					mAssociativeExpression(Formula.FCOMP, id_x, id_y, id_z) 
			), new ExprTestPair(
					"x\u25b7y", 
					ffV1.makeBinaryExpression(Formula.RANRES, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.RANRES, id_x, id_y) 
			), new ExprTestPair(
					"x\u2a65y", 
					ffV1.makeBinaryExpression(Formula.RANSUB, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.RANSUB, id_x, id_y) 
			), new ExprTestPair(
					"x\u2229y", 
					ffV1.makeAssociativeExpression(Formula.BINTER, mList(id_x_V1, id_y_V1), null),
					mAssociativeExpression(Formula.BINTER, id_x, id_y) 
			), new ExprTestPair(
					"x\u2229y\u2229z", 
					ffV1.makeAssociativeExpression(Formula.BINTER, mList(id_x_V1, id_y_V1, id_z_V1), null),
					mAssociativeExpression(Formula.BINTER, id_x, id_y, id_z) 
			), new ExprTestPair(
					"x\u2216y", 
					ffV1.makeBinaryExpression(Formula.SETMINUS, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.SETMINUS, id_x, id_y) 
			), new ExprTestPair(
					"x;y\u2a65z", 
					ffV1.makeBinaryExpression(Formula.RANSUB,
							ffV1.makeAssociativeExpression(Formula.FCOMP, mList(id_x_V1, id_y_V1), null),
							id_z_V1, null
					),
					mBinaryExpression(Formula.RANSUB, 
							mAssociativeExpression(Formula.FCOMP, id_x, id_y), 
							id_z
					) 
			), new ExprTestPair(
					"x\u2229y\u2a65z", 
					ffV1.makeBinaryExpression(Formula.RANSUB,
							ffV1.makeAssociativeExpression(Formula.BINTER, mList(id_x_V1, id_y_V1), null),
							id_z_V1, null
					),
					mBinaryExpression(Formula.RANSUB, 
							mAssociativeExpression(Formula.BINTER, id_x, id_y), 
							id_z
					) 
			), new ExprTestPair(
					"x\u2229y\u2216z", 
					ffV1.makeBinaryExpression(Formula.SETMINUS,
							ffV1.makeAssociativeExpression(Formula.BINTER, mList(id_x_V1, id_y_V1), null),
							id_z_V1, null
					),
					mBinaryExpression(Formula.SETMINUS, 
							mAssociativeExpression(Formula.BINTER, id_x, id_y), 
							id_z
					) 
			),
			
			// SetExpr
			new ExprTestPair(
					"x\u222ay", 
					ffV1.makeAssociativeExpression(Formula.BUNION, mList(id_x_V1, id_y_V1), null),
					mAssociativeExpression(Formula.BUNION, id_x, id_y) 
			), new ExprTestPair(
					"x\u222ay\u222az", 
					ffV1.makeAssociativeExpression(Formula.BUNION, mList(id_x_V1, id_y_V1, id_z_V1), null),
					mAssociativeExpression(Formula.BUNION, id_x, id_y, id_z) 
			), new ExprTestPair(
					"x\u00d7y", 
					ffV1.makeBinaryExpression(Formula.CPROD, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.CPROD, id_x, id_y) 
			), new ExprTestPair(
					"x\u00d7y\u00d7z", 
					ffV1.makeBinaryExpression(Formula.CPROD,
							ffV1.makeBinaryExpression(Formula.CPROD, id_x_V1, id_y_V1, null), id_z_V1, null
					),
					mBinaryExpression(Formula.CPROD, 
							mBinaryExpression(Formula.CPROD, id_x, id_y), id_z
					) 
			), new ExprTestPair(
					"x\ue103y", 
					ffV1.makeAssociativeExpression(Formula.OVR, mList(id_x_V1, id_y_V1), null),
					mAssociativeExpression(Formula.OVR, id_x, id_y) 
			), new ExprTestPair(
					"x\ue103y\ue103z", 
					ffV1.makeAssociativeExpression(Formula.OVR, mList(id_x_V1, id_y_V1, id_z_V1), null),
					mAssociativeExpression(Formula.OVR, id_x, id_y, id_z) 
			), new ExprTestPair(
					"x\u2218y", 
					ffV1.makeAssociativeExpression(Formula.BCOMP, mList(id_x_V1, id_y_V1), null),
					mAssociativeExpression(Formula.BCOMP, id_x, id_y) 
			), new ExprTestPair(
					"x\u2218y\u2218z", 
					ffV1.makeAssociativeExpression(Formula.BCOMP, mList(id_x_V1, id_y_V1, id_z_V1), null),
					mAssociativeExpression(Formula.BCOMP, id_x, id_y, id_z) 
			), new ExprTestPair(
					"x\u2225y", 
					ffV1.makeBinaryExpression(Formula.PPROD, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.PPROD, id_x, id_y) 
			), new ExprTestPair(
					"x\u25c1y", 
					ffV1.makeBinaryExpression(Formula.DOMRES, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.DOMRES, id_x, id_y) 
			), new ExprTestPair(
					"x\u2a64y", 
					ffV1.makeBinaryExpression(Formula.DOMSUB, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.DOMSUB, id_x, id_y)
			),
			
			// RelationalSetExpr
			new ExprTestPair(
					"x\ue100y", 
					ffV1.makeBinaryExpression(Formula.TREL, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.TREL, id_x, id_y) 					
			), new ExprTestPair(
					"x\ue100y\ue100z", 
					ffV1.makeBinaryExpression(Formula.TREL,
							ffV1.makeBinaryExpression(Formula.TREL, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\ue101y", 
					ffV1.makeBinaryExpression(Formula.SREL, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.SREL, id_x, id_y) 					
			), new ExprTestPair(
					"x\ue101y\ue101z", 
					ffV1.makeBinaryExpression(Formula.SREL,
							ffV1.makeBinaryExpression(Formula.SREL, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\ue102y", 
					ffV1.makeBinaryExpression(Formula.STREL, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.STREL, id_x, id_y) 					
			), new ExprTestPair(
					"x\ue102y\ue102z", 
					ffV1.makeBinaryExpression(Formula.STREL,
							ffV1.makeBinaryExpression(Formula.STREL, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u2900y", 
					ffV1.makeBinaryExpression(Formula.PSUR, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.PSUR, id_x, id_y) 					
			), new ExprTestPair(
					"x\u2900y\u2900z", 
					ffV1.makeBinaryExpression(Formula.PSUR,
							ffV1.makeBinaryExpression(Formula.PSUR, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u2914y", 
					ffV1.makeBinaryExpression(Formula.PINJ, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.PINJ, id_x, id_y) 					
			), new ExprTestPair(
					"x\u2914y\u2914z", 
					ffV1.makeBinaryExpression(Formula.PINJ,
							ffV1.makeBinaryExpression(Formula.PINJ, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u2916y", 
					ffV1.makeBinaryExpression(Formula.TBIJ, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.TBIJ, id_x, id_y) 					
			), new ExprTestPair(
					"x\u2916y\u2916z", 
					ffV1.makeBinaryExpression(Formula.TBIJ,
							ffV1.makeBinaryExpression(Formula.TBIJ, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u2192y", 
					ffV1.makeBinaryExpression(Formula.TFUN, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.TFUN, id_x, id_y) 					
			), new ExprTestPair(
					"x\u2192y\u2192z", 
					ffV1.makeBinaryExpression(Formula.TFUN,
							ffV1.makeBinaryExpression(Formula.TFUN, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u2194y", 
					ffV1.makeBinaryExpression(Formula.REL, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.REL, id_x, id_y) 					
			), new ExprTestPair(
					"x\u2194y\u2194z", 
					ffV1.makeBinaryExpression(Formula.REL,
							ffV1.makeBinaryExpression(Formula.REL, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u21a0y", 
					ffV1.makeBinaryExpression(Formula.TSUR, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.TSUR, id_x, id_y) 					
			), new ExprTestPair(
					"x\u21a0y\u21a0z", 
					ffV1.makeBinaryExpression(Formula.TSUR,
							ffV1.makeBinaryExpression(Formula.TSUR, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u21a3y", 
					ffV1.makeBinaryExpression(Formula.TINJ, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.TINJ, id_x, id_y) 					
			), new ExprTestPair(
					"x\u21a3y\u21a3z", 
					ffV1.makeBinaryExpression(Formula.TINJ,
							ffV1.makeBinaryExpression(Formula.TINJ, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			), new ExprTestPair(
					"x\u21f8y", 
					ffV1.makeBinaryExpression(Formula.PFUN, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.PFUN, id_x, id_y) 					
			), new ExprTestPair(
					"x\u21f8y\u21f8z", 
					ffV1.makeBinaryExpression(Formula.PFUN,
							ffV1.makeBinaryExpression(Formula.PFUN, id_x_V1, id_y_V1, null), id_z_V1, null
					) 					
					, null
			),
			
			// PairExpr
			new ExprTestPair(
					"x\u21a6y", 
					ffV1.makeBinaryExpression(Formula.MAPSTO, id_x_V1, id_y_V1, null),
					mBinaryExpression(Formula.MAPSTO, id_x, id_y) 					
			), new ExprTestPair(
					"x\u21a6y\u21a6z", 
					ffV1.makeBinaryExpression(Formula.MAPSTO,
							ffV1.makeBinaryExpression(Formula.MAPSTO, id_x_V1, id_y_V1, null), id_z_V1, null
					),
					mBinaryExpression(Formula.MAPSTO, 
							mBinaryExpression(Formula.MAPSTO, id_x, id_y), id_z
					) 					
			),
			
			// QuantifiedExpr & IdentPattern
			// UnBound
			new ExprTestPair(
					"\u03bb x\u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO, b0_V1, id_z_V1, null), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,  
							mList(bd_x), bfalse, 
							mBinaryExpression(Formula.MAPSTO, b0, id_z)
					)
			), new ExprTestPair(
					"\u03bb x\u21a6y\u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO,
									ffV1.makeBinaryExpression(Formula.MAPSTO, b1_V1, b0_V1, null),
									id_z_V1, null
							), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,
							mList(bd_x, bd_y), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, b1, b0), 
									id_z
							)
					)
			), new ExprTestPair(
					"\u03bb x\u21a6y\u21a6s\u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1, bd_s_V1),
							bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO,
									ffV1.makeBinaryExpression(Formula.MAPSTO,
											ffV1.makeBinaryExpression(Formula.MAPSTO, b2_V1, 	b1_V1, null),
											b0_V1, null
									), id_z_V1, null
							), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,
							mList(bd_x, bd_y, bd_s), 
							bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, 
											mBinaryExpression(Formula.MAPSTO, b2, 	b1), 
											b0
									), id_z
							)
					)
			), new ExprTestPair(
					"\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1, bd_s_V1),
							bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO,
									ffV1.makeBinaryExpression(Formula.MAPSTO,
											b2_V1,
											ffV1.makeBinaryExpression(Formula.MAPSTO, b1_V1, b0_V1, null), null
									), id_z_V1, null
							), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,
							mList(bd_x, bd_y, bd_s), 
							bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, 
											b2, 
											mBinaryExpression(Formula.MAPSTO, b1, b0)
									), id_z
							)
					)
			), 
			
			// Bound
			new ExprTestPair(
					"\u03bb x\u00b7\u22a5\u2223x", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO, b0_V1, b0_V1, null), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,
							mList(bd_x), bfalse, 
							mBinaryExpression(Formula.MAPSTO, b0, b0)
					)
			), new ExprTestPair(
					"\u03bb x\u21a6y\u00b7\u22a5\u2223y", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO,
									ffV1.makeBinaryExpression(Formula.MAPSTO, b1_V1, b0_V1, null),
									b0_V1, null
							), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,
							mList(bd_x, bd_y), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, b1, b0),
									b0
							)
					)
			), new ExprTestPair(
					"\u03bb x\u21a6y\u21a6s\u00b7\u22a5\u2223s", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1, bd_s_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO,
									ffV1.makeBinaryExpression(Formula.MAPSTO,
											ffV1.makeBinaryExpression(Formula.MAPSTO, b2_V1, b1_V1, null),
											b0_V1, null
									),
									b0_V1, null
							), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,
							mList(bd_x, bd_y, bd_s), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, 
											mBinaryExpression(Formula.MAPSTO, b2, b1), 
											b0
									), 
									b0
							)
					)
			), new ExprTestPair(
					"\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223s", 
					ffV1.makeQuantifiedExpression(Formula.CSET,
							mList(bd_x_V1, bd_y_V1, bd_s_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MAPSTO,
									ffV1.makeBinaryExpression(Formula.MAPSTO, b2_V1,
											ffV1.makeBinaryExpression(Formula.MAPSTO, b1_V1, b0_V1, null), null
									), b0_V1, null
							), null, Lambda
					),
					mQuantifiedExpression(Formula.CSET, Lambda,
							mList(bd_x, bd_y, bd_s), bfalse, 
							mBinaryExpression(Formula.MAPSTO, 
									mBinaryExpression(Formula.MAPSTO, b2, 
											mBinaryExpression(Formula.MAPSTO, b1, b0)
									), b0
							)
					)
			), 
			
			// UnBound
			new ExprTestPair(
					"\u22c3x\u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QUNION, Explicit,
							mList(bd_x), bfalse, id_z
					)
			), new ExprTestPair(
					"\u22c3 x, y \u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1, bd_y_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QUNION, Explicit,
							mList(bd_x, bd_y), bfalse, id_z
					)
			), new ExprTestPair(
					"\u22c3 x, y, s \u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1, bd_y_V1, bd_s_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QUNION, Explicit,
							mList(bd_x, bd_y, bd_s), bfalse, id_z
					)
			), 
			
			// Bound
			new ExprTestPair(
					"\u22c3x\u00b7\u22a5\u2223x", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1), bfalse_V1, b0_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QUNION, Explicit,
							mList(bd_x), bfalse, b0
					)
			), new ExprTestPair(
					"\u22c3 x, y \u00b7\u22a5\u2223y", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1, bd_y_V1), bfalse_V1, b0_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QUNION, Explicit,
							mList(bd_x, bd_y), bfalse, b0
					)
			), new ExprTestPair(
					"\u22c3 x, y, s \u00b7\u22a5\u2223s", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1, bd_y_V1, bd_s_V1), bfalse_V1, b0_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QUNION, Explicit,
							mList(bd_x, bd_y, bd_s), bfalse, b0
					)
			),
			
			// UnBound
			new ExprTestPair(
					"\u22c3x\u2223\u22a5", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1), bfalse_V1, b0_V1, null, Implicit
					),
					mQuantifiedExpression(Formula.QUNION, Implicit,
							mList(bd_x), bfalse, b0
					)
			), new ExprTestPair(
					"\u22c3 x−y \u2223\u22a5", 
					ffV1.makeQuantifiedExpression(Formula.QUNION,
							mList(bd_x_V1, bd_y_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MINUS, b1_V1, b0_V1, null), null, Implicit
					),
					mQuantifiedExpression(Formula.QUNION, Implicit,
							mList(bd_x, bd_y), bfalse, 
							mBinaryExpression(Formula.MINUS, b1, b0)
					)
			),
			
			// UnBound
			new ExprTestPair(
					"\u22c2x\u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_x_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QINTER, Explicit,
							mList(bd_x), bfalse, id_z
					)
			), new ExprTestPair(
					"\u22c2 x, y \u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_x_V1, bd_y_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QINTER, Explicit,
							mList(bd_x, bd_y), bfalse, id_z
					)
			), new ExprTestPair(
					"\u22c2 x, y, s \u00b7\u22a5\u2223z", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_x_V1, bd_y_V1, bd_s_V1), bfalse_V1, id_z_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QINTER, Explicit,
							mList(bd_x, bd_y, bd_s), bfalse, id_z
					)
			),
			
			// Bound
			new ExprTestPair(
					"\u22c2 x \u00b7\u22a5\u2223x", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_x_V1), bfalse_V1, b0_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QINTER, Explicit,
							mList(bd_x), bfalse, b0
					)
			), new ExprTestPair(
					"\u22c2 x, y \u00b7\u22a5\u2223y", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_x_V1, bd_y_V1), bfalse_V1, b0_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QINTER, Explicit,
							mList(bd_x, bd_y), bfalse, b0
					)
			), new ExprTestPair(
					"\u22c2 x, y, s \u00b7\u22a5\u2223s", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_x_V1, bd_y_V1, bd_s_V1), bfalse_V1, b0_V1, null, Explicit
					),
					mQuantifiedExpression(Formula.QINTER, Explicit,
							mList(bd_x, bd_y, bd_s), bfalse, b0
					)
			),
			
			// UnBound
			new ExprTestPair(
					"\u22c2x\u2223\u22a5", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_x_V1), bfalse_V1, b0_V1, null, Implicit
					),
					mQuantifiedExpression(Formula.QINTER, Implicit,
							mList(bd_x), bfalse, b0
					)
			), new ExprTestPair(
					"\u22c2y−x\u2223\u22a5", 
					ffV1.makeQuantifiedExpression(Formula.QINTER,
							mList(bd_y_V1, bd_x_V1), bfalse_V1,
							ffV1.makeBinaryExpression(Formula.MINUS, b1_V1, b0_V1, null), null, Implicit
					),
					mQuantifiedExpression(Formula.QINTER, Implicit,
							mList(bd_y, bd_x), bfalse, 
							mBinaryExpression(Formula.MINUS, b1, b0)
					)
			),

			// Typed empty set
			new ExprTestPair(
					"(\u2205\u2982\u2119(\u2124))", 
					mEmptySet(POW(ffV1.makeIntegerType())),
					mEmptySet(POW(ff.makeIntegerType()))
			), new ExprTestPair(
					"(\u2205\u2982\u2119(\u2119(\u2124)))", 
					mEmptySet(POW(POW(ffV1.makeIntegerType()))),
					mEmptySet(POW(POW(ff.makeIntegerType())))
			),
			
			// Misc.
			new ExprTestPair(
					"f∼(x)", 
					ffV1.makeBinaryExpression(Formula.FUNIMAGE,
							ffV1.makeUnaryExpression(Formula.CONVERSE,
									id_f_V1, null),
							id_x_V1, null),
					mBinaryExpression(Formula.FUNIMAGE,
							mUnaryExpression(Formula.CONVERSE,
									id_f),
							id_x)
			), new ExprTestPair(
					"f(x)∼",
					ffV1.makeUnaryExpression(Formula.CONVERSE,
							ffV1.makeBinaryExpression(Formula.FUNIMAGE,
									id_f_V1,
									id_x_V1, null), null),
					mUnaryExpression(Formula.CONVERSE,
							mBinaryExpression(Formula.FUNIMAGE,
									id_f,
									id_x))
			), new ExprTestPair(
					"f∼[x]", 
					ffV1.makeBinaryExpression(Formula.RELIMAGE,
							ffV1.makeUnaryExpression(Formula.CONVERSE,
									id_f_V1, null),
							id_x_V1, null),
					mBinaryExpression(Formula.RELIMAGE,
							mUnaryExpression(Formula.CONVERSE,
									id_f),
							id_x)
			), new ExprTestPair(
					"f(x)∼[y]", 
					ffV1.makeBinaryExpression(Formula.RELIMAGE,
							ffV1.makeUnaryExpression(Formula.CONVERSE,
									ffV1.makeBinaryExpression(Formula.FUNIMAGE,
											id_f_V1, id_x_V1, null), null),
							id_y_V1, null),
					mBinaryExpression(Formula.RELIMAGE,
							mUnaryExpression(Formula.CONVERSE,
									mBinaryExpression(Formula.FUNIMAGE,
											id_f, id_x)),
							id_y)
			),
			
	};
	
	AssignmentTestPair[] assigns = new AssignmentTestPair[] {
			new AssignmentTestPair(
					"x ≔ y",
					ffV1.makeBecomesEqualTo(mList(id_x_V1), mList(id_y_V1), null),
					mBecomesEqualTo(mList(id_x), mList(id_y))
			), new AssignmentTestPair(
					"x,y ≔ z,t",
					ffV1.makeBecomesEqualTo(mList(id_x_V1, id_y_V1), mList(id_z_V1, id_t_V1), null),
					mBecomesEqualTo(mList(id_x, id_y), mList(id_z, id_t))
			), new AssignmentTestPair(
					"x,y,z ≔ t,u,v",
					ffV1.makeBecomesEqualTo(mList(id_x_V1, id_y_V1, id_z_V1), mList(id_t_V1, id_u_V1, id_v_V1), null),
					mBecomesEqualTo(mList(id_x, id_y, id_z), mList(id_t, id_u, id_v))
			), new AssignmentTestPair(
					"x :∈ S",
					ffV1.makeBecomesMemberOf(id_x_V1, id_S_V1, null),
					mBecomesMemberOf(id_x, id_S)
			), new AssignmentTestPair(
					"x :\u2223 x' = x",
					ffV1.makeBecomesSuchThat(mList(id_x_V1), mList(bd_xp_V1),
							ffV1.makeRelationalPredicate(Formula.EQUAL, b0_V1, id_x_V1, null), null
					),
					mBecomesSuchThat(mList(id_x), mList(bd_xp),
							mRelationalPredicate(Formula.EQUAL, b0, id_x)
					)
			), new AssignmentTestPair(
					"x,y :\u2223 x' = y ∧ y' = x",
					ffV1.makeBecomesSuchThat(mList(id_x_V1, id_y_V1), mList(bd_xp_V1, bd_yp_V1),
							ffV1.makeAssociativePredicate(Formula.LAND,
									mList(ffV1.makeRelationalPredicate(Formula.EQUAL, b1_V1, id_y_V1, null),
									ffV1.makeRelationalPredicate(Formula.EQUAL, b0_V1, id_x_V1, null)), null
							), null),
					mBecomesSuchThat(mList(id_x, id_y), mList(bd_xp, bd_yp),
							mAssociativePredicate(Formula.LAND,
									mRelationalPredicate(Formula.EQUAL, b1, id_y),
									mRelationalPredicate(Formula.EQUAL, b0, id_x)
							))
			), new AssignmentTestPair(
					"x,y,z :\u2223 x' = y ∧ y' = z ∧ z' = x",
					ffV1.makeBecomesSuchThat(mList(id_x_V1, id_y_V1, id_z_V1), mList(bd_xp_V1, bd_yp_V1, bd_zp_V1),
							ffV1.makeAssociativePredicate(Formula.LAND,
									mList(ffV1.makeRelationalPredicate(Formula.EQUAL, b2_V1, id_y_V1, null),
									ffV1.makeRelationalPredicate(Formula.EQUAL, b1_V1, id_z_V1, null),
									ffV1.makeRelationalPredicate(Formula.EQUAL, b0_V1, id_x_V1, null)), null
							), null),
					mBecomesSuchThat(mList(id_x, id_y, id_z), mList(bd_xp, bd_yp, bd_zp),
							mAssociativePredicate(Formula.LAND,
									mRelationalPredicate(Formula.EQUAL, b2, id_y),
									mRelationalPredicate(Formula.EQUAL, b1, id_z),
									mRelationalPredicate(Formula.EQUAL, b0, id_x)
							))
			),
	};
	

	private void testList(TestPair[] list) {
		for (TestPair pair: list) {
			pair.verify();
		}
	}
	
	/**
	 * Main test routine.
	 */
	@Test 
	public void testParser() {
		testList(preds);
		testList(exprs);
		testList(assigns);
	}
	
	@Test 
	public void testInvalidExprs() throws Exception {
		doTestInvalidExpr("x/x/x");
		doTestInvalidExpr("x mod x mod x");
		doTestInvalidExpr("x domsub y + z");
		doTestInvalidExpr("x setminus y inter z");
		doTestInvalidExpr("x\u2225y\u2225z");
		doTestInvalidExpr("(\u2205\u2982x\u21a6y)");	// rhs is not a type
		doTestInvalidExpr("(\u2205\u2982\u2124)");		// type is not a set type
		// Duplicate idents in lambda pattern
		doTestInvalidExpr("\u03bb x\u21a6x\u00b7\u22a5\u2223x");
		doTestInvalidExpr("\u03bb x\u21a6y\u21a6x\u00b7\u22a5\u2223x+y");
		doTestInvalidExpr("\u03bb x\u21a6 (x \u2982 \u2124) \u00b7\u22a4\u2223x");
		doTestInvalidExpr("\u03bb(x \u2982 BOOL) \u21a6 x \u00b7\u22a4\u2223x");
		doTestInvalidExpr("\u03bb(x \u2982 BOOL) \u21a6 (x \u2982 \u2124) \u00b7\u22a4\u2223x");

		// ill-defined quantified expressions (nothing to bind)
		doTestInvalidExpr("{1\u2223\u22a5}");
		doTestInvalidExpr("\u03bb\u00b7\u22a5\u22231");
		doTestInvalidExpr("\u22c2{1}\u2223\u22a5");
		doTestInvalidExpr("\u22c3{1}\u2223\u22a5");
	}

	private void doTestInvalidExpr(String input) {
		new ExprTestPair(input, (Expression) null, (Expression) null).verify();
	}
}
