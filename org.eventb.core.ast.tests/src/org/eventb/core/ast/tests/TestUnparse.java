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
 *     Systerel - added tests for quantified expressions
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BCOMP;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FCOMP;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LEQV;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.MOD;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.OVR;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.Formula.RANRES;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesMemberOf;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMaplet;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.tests.Common.TagSupply;
import org.junit.Ignore;
import org.junit.Test;



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
public class TestUnparse extends AbstractTests {

	static FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	private static FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
	private static FreeIdentifier id_z = ff.makeFreeIdentifier("z", null);
	private static FreeIdentifier id_t = ff.makeFreeIdentifier("t", null);
	private static FreeIdentifier id_u = ff.makeFreeIdentifier("u", null);
	private static FreeIdentifier id_v = ff.makeFreeIdentifier("v", null);
	private static FreeIdentifier id_S = ff.makeFreeIdentifier("S", null);
	private static FreeIdentifier id_f = ff.makeFreeIdentifier("f", null);
	private static FreeIdentifier id_A = ff.makeFreeIdentifier("A", null);
	private static FreeIdentifier id_g = ff.makeFreeIdentifier("g", null);
	
	private static BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
	private static BoundIdentDecl bd_y = ff.makeBoundIdentDecl("y", null);
	private static BoundIdentDecl bd_z = ff.makeBoundIdentDecl("z", null);

	private static BoundIdentDecl bd_xp = ff.makeBoundIdentDecl("x'", null);
	private static BoundIdentDecl bd_yp = ff.makeBoundIdentDecl("y'", null);
	private static BoundIdentDecl bd_zp = ff.makeBoundIdentDecl("z'", null);

	private static BoundIdentifier b0 = ff.makeBoundIdentifier(0, null);
	private static BoundIdentifier b1 = ff.makeBoundIdentifier(1, null);
	private static BoundIdentifier b2 = ff.makeBoundIdentifier(2, null);

	private static PredicateVariable pv_P = ff.makePredicateVariable("$P", null);

	private static LiteralPredicate btrue = ff.makeLiteralPredicate(BTRUE, null);

	private static IntegerLiteral two = ff.makeIntegerLiteral(Common.TWO, null);
	
	static SourceLocationChecker slChecker = new SourceLocationChecker();

	private static abstract class TestPair {
		final String image;
		final LanguageVersion version;
		TestPair(String image) {
			this(image, LanguageVersion.V1);
		}
		TestPair(String image, LanguageVersion version) {
			this.image = image;
			this.version = version;
		}
		abstract Formula<?> getFormula();
		abstract Formula<?> parseAndCheck(String input);
		void verify(String input) {
			assertTrue(input, ParenChecker.check(input));
			Formula<?> actual = parseAndCheck(input);
			checkSourceLocations(input, actual);
		}
		void checkSourceLocations(String input, Formula<?> parsedFormula) {
			// Verify that source locations are properly nested
			parsedFormula.accept(slChecker);

			// also check that the source location reported corresponds to the
			// whole substring.
			final SourceLocation loc = parsedFormula.getSourceLocation();
			parseAndCheck(input.substring(loc.getStart(), loc.getEnd() + 1));
		}
	}
	
	private static class ExprTestPair extends TestPair {
		Expression formula;
		ExprTestPair(String image, Expression formula) {
			super(image);
			this.formula = formula;
		}
		ExprTestPair(String image, LanguageVersion version, Expression formula) {
			super(image, version);
			this.formula = formula;
		}
		@Override 
		Expression getFormula() {
			return formula;
		}
		@Override 
		Expression parseAndCheck(String input) {
			final Expression actual = parseExpression(input, version);
			assertEquals("Unexpected parser result", formula, actual);
			return actual;
		}
	}
	
	private static class PredTestPair extends TestPair {
		Predicate formula;
		PredTestPair(String image, Predicate formula) {
			super(image);
			this.formula = formula;
		}
		PredTestPair(String image, LanguageVersion version, Predicate formula) {
			super(image, version);
			this.formula = formula;
		}
		@Override 
		Predicate getFormula() {
			return formula;
		}
		@Override 
		Predicate parseAndCheck(String input) {
			final Predicate actual = parsePredicate(input, version);
			assertEquals("Unexpected parser result", formula, actual);
			return actual;
		}
	}

	private static class AssignTestPair extends TestPair {
		Assignment formula;
		AssignTestPair(String image, Assignment formula) {
			super(image);
			this.formula = formula;
		}
		@Override 
		Assignment getFormula() {
			return formula;
		}
		@Override 
		Assignment parseAndCheck(String input) {
			final Assignment actual = parseAssignment(input, version);
			assertEquals("Unexpected parser result", formula, actual);
			return actual;
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
			
	private PredTestPair[] predPatternTestPairs = new PredTestPair[] {
			new PredTestPair("$P", pv_P),
	};

	// Various special cases for expressions
	private ExprTestPair[] specialExprTestPairs = new ExprTestPair[] {
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
			), new ExprTestPair(
					"f;g ▷ A",
					mBinaryExpression(RANRES,
							mAssociativeExpression(FCOMP, id_f, id_g),
							id_A 
					)
			), new ExprTestPair(
					"λx0·⊤ ∣ x+x0",
					mQuantifiedExpression(CSET, Lambda,
							mList(mBoundIdentDecl("x")),
							mLiteralPredicate(BTRUE),
							mMaplet(mBoundIdentifier(0),
									mAssociativeExpression(PLUS,
											id_x,
											mBoundIdentifier(0)
									)
							)
					)
			), new ExprTestPair(
					"λx ↦ x0·⊤ ∣ x+x0",
					mQuantifiedExpression(CSET, Lambda,
							mList(bd_x, bd_x),
							btrue,
							mMaplet(b1, b0, mAssociativeExpression(PLUS, b1, b0)
							)
					)
			), new ExprTestPair(
					"\u2212(1)",
					mUnaryExpression(UNMINUS,
							mIntegerLiteral(1)
					)
			), new ExprTestPair(
					"\u22121",
					mIntegerLiteral(-1)
			), new ExprTestPair(
					"1 \u2212 (\u22121)",
					mBinaryExpression(MINUS,
							mIntegerLiteral(1),
							mIntegerLiteral(-1))
			), new ExprTestPair(
					"\u22121 \u2212 1",
					mBinaryExpression(MINUS,
							mIntegerLiteral(-1),
							mIntegerLiteral(1))
			), new ExprTestPair(
					"(\u22121)\u22171",
					mAssociativeExpression(MUL,
							mIntegerLiteral(-1),
							mIntegerLiteral(1))
			), new ExprTestPair(
					"\u22121\u22171",
					mUnaryExpression(UNMINUS,
							mAssociativeExpression(MUL,
									mIntegerLiteral(1),
									mIntegerLiteral(1)))
			), new ExprTestPair(
					"50000000000000000000",
					ff.makeIntegerLiteral(//
							new BigInteger("50000000000000000000"), null)
			), new ExprTestPair(
					"−50000000000000000000",
					ff.makeIntegerLiteral(//
							new BigInteger("-50000000000000000000"), null)
			), new ExprTestPair(
					"bool($P)",
					FastFactory.mBoolExpression(pv_P)
			),
	};
	
	private PredTestPair[] specialPredTestPairs = new PredTestPair[] {
			new PredTestPair(
					"∀x\u00b7∀y\u00b7∀z\u00b7finite(x∪y∪z∪t)",
					mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_y),
					mQuantifiedPredicate(FORALL, mList(bd_z),
							mSimplePredicate(
									mAssociativeExpression(BUNION, b2, b1, b0, id_t)
							)
					)))
			), new PredTestPair(
					"∀x\u00b7∀x0\u00b7finite(x∪x0)",
					mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_x),
							mSimplePredicate(
									mAssociativeExpression(BUNION, b1, b0)
							)
					))
			), new PredTestPair(
					"∀x\u00b7∀x0\u00b7∀x1\u00b7finite(x∪x0∪x1)",
					mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_x),
							mSimplePredicate(
									mAssociativeExpression(BUNION, b2, b1, b0)
							)
					)))
			), new PredTestPair(
					"∀x\u00b7∀y\u00b7∀y0\u00b7finite(x∪y∪y0)",
					mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_y),
					mQuantifiedPredicate(FORALL, mList(bd_y),
							mSimplePredicate(
									mAssociativeExpression(BUNION, b2, b1, b0)
							)
					)))
			), new PredTestPair(
					"∀prj0\u00b7∀prj3\u00b7finite(prj∪prj0∪prj3)",
					mQuantifiedPredicate(FORALL, mList(mBoundIdentDecl("prj")),
					mQuantifiedPredicate(FORALL, mList(mBoundIdentDecl("prj")),
							mSimplePredicate(
									mAssociativeExpression(BUNION,
											mFreeIdentifier("prj"),
											b1, b0
									)
							)
					))
			), new PredTestPair(
					"$P",
					pv_P
			),
	};

	AssignTestPair[] assignmentTestPairs = new AssignTestPair[] {
			new AssignTestPair(
					"x ≔ y",
					mBecomesEqualTo(mList(id_x), mList(id_y))
			), new AssignTestPair(
					"x,y ≔ z, t",
					mBecomesEqualTo(mList(id_x, id_y), mList(id_z, id_t))
			), new AssignTestPair(
					"x,y,z ≔ t, u, v",
					mBecomesEqualTo(mList(id_x, id_y, id_z), mList(id_t, id_u, id_v))
			), new AssignTestPair(
					"x :∈ S",
					mBecomesMemberOf(id_x, id_S)
			), new AssignTestPair(
					"x :\u2223 x'=x",
					mBecomesSuchThat(mList(id_x), mList(bd_xp),
							mRelationalPredicate(Formula.EQUAL, b0, id_x)
					)
			), new AssignTestPair(
					"x,y :\u2223 x'=y∧y'=x",
					mBecomesSuchThat(mList(id_x, id_y), mList(bd_xp, bd_yp),
							mAssociativePredicate(Formula.LAND,
									mRelationalPredicate(Formula.EQUAL, b1, id_y),
									mRelationalPredicate(Formula.EQUAL, b0, id_x)
							))
			), new AssignTestPair(
					"x,y,z :\u2223 x'=y∧y'=z∧z'=x",
					mBecomesSuchThat(mList(id_x, id_y, id_z), mList(bd_xp, bd_yp, bd_zp),
							mAssociativePredicate(Formula.LAND,
									mRelationalPredicate(Formula.EQUAL, b2, id_y),
									mRelationalPredicate(Formula.EQUAL, b1, id_z),
									mRelationalPredicate(Formula.EQUAL, b0, id_x)
							))
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
	
	private Expression[] constructBinaryBinaryTrees(TagSupply tagSupply) {
		// {MAPSTO,REL,TREL,SREL,STREL,PFUN,TFUN,PINJ,TINJ,PSUR,TSUR,TBIJ,SETMINUS,CPROD,DPROD,PPROD,DOMRES,DOMSUB,RANRES,RANSUB,UPTO,MINUS,DIV,MOD,EXPN}
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> binaryExpressions = tagSupply.binaryExpressionTags;
		int length = binaryExpressions.size();
		Expression[] formulae = new Expression[(length) * (length) * 2];
		int idx = 0;
		for (int binTag1 : binaryExpressions) {
			for (int binTag2 : binaryExpressions) {
				formulae[idx++] = factory
						.makeBinaryExpression(binTag2,
								factory.makeBinaryExpression(binTag1, factory.makeFreeIdentifier("x", null),
										factory.makeFreeIdentifier("y", null), null), factory.makeFreeIdentifier("z", null), null);
				formulae[idx++] = factory
						.makeBinaryExpression(binTag2, factory.makeFreeIdentifier("x", null),
								factory.makeBinaryExpression(binTag1, factory.makeFreeIdentifier("y", null),
										factory.makeFreeIdentifier("z", null), null), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Formula<?>[] constructAssociativeAssociativeTrees(TagSupply tagSupply) {
		// {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> associativeExpressions = tagSupply.associativeExpressionTags;
		final Expression[] id_y_z = {factory.makeFreeIdentifier("y", null), factory.makeFreeIdentifier("z", null)};
		int length = associativeExpressions.size();
		Expression[]  formulae = new Expression[3 * length * length];
		int idx = 0;
		for (int assocTag1 : associativeExpressions) {
			for (int assocTag2 : associativeExpressions) {
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag2,
						mList(factory.makeAssociativeExpression(assocTag1,
								id_y_z, null), factory.makeFreeIdentifier("x", null)), null);
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag2,
						mList(factory.makeFreeIdentifier("x", null), factory.makeAssociativeExpression(
								assocTag1, id_y_z, null), factory.makeFreeIdentifier("t", null)), null);
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag2,
						mList(factory.makeFreeIdentifier("x", null), factory.makeAssociativeExpression(
								assocTag1, id_y_z, null)), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructAssociativeBinaryTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> associativeExpressions = tagSupply.associativeExpressionTags;
		final int assocSize = associativeExpressions.size();
		final Set<Integer> binaryExpressions = tagSupply.binaryExpressionTags;
		final Expression[] id_y_z = {factory.makeFreeIdentifier("y", null), factory.makeFreeIdentifier("z", null)};
		final Expression[] id_x_y = {factory.makeFreeIdentifier("x", null), factory.makeFreeIdentifier("y", null)};
		final int binarySize = binaryExpressions.size();
		Expression[]  formulae = new Expression[5 * assocSize * binarySize];
		int idx = 0;
		for (int assocTag : associativeExpressions) {
			for (int binaryTag : binaryExpressions) {
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag,
						mList(factory.makeFreeIdentifier("x", null), factory.makeBinaryExpression(binaryTag,
								factory.makeFreeIdentifier("y", null), factory.makeFreeIdentifier("z", null), null)), null);
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag,
						mList(factory.makeFreeIdentifier("x", null), factory.makeBinaryExpression(binaryTag,
								factory.makeFreeIdentifier("y", null), factory.makeFreeIdentifier("z", null), null), factory.makeFreeIdentifier("t", null)), null);
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag,
						mList(factory.makeBinaryExpression(binaryTag, factory.makeFreeIdentifier("x", null),
								factory.makeFreeIdentifier("y", null), null), factory.makeFreeIdentifier("z", null)), null);
				formulae[idx++] = factory.makeBinaryExpression(binaryTag,
						factory.makeAssociativeExpression(assocTag, id_x_y,
								null), factory.makeFreeIdentifier("z", null), null);
				formulae[idx++] = factory.makeBinaryExpression(binaryTag,
						factory.makeFreeIdentifier("x", null), factory
								.makeAssociativeExpression(assocTag, id_y_z,
										null), null);
		}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructBinaryUnaryTrees(TagSupply tagSupply) {
		// {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID, KMIN, KMAX, CONVERSE}
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> unaryExpressions = tagSupply.unaryExpressionTags;
		final int unarySize = unaryExpressions.size();
		final Set<Integer> binaryExpressions = tagSupply.binaryExpressionTags;
		final int binarySize = binaryExpressions.size();
		final int length = (3 * unarySize * binarySize) + (2 * binarySize);
		final Expression[] formulae = new Expression[length];
		int idx = 0;
		for (int unaryTag : unaryExpressions) {
			for (int binaryTag : binaryExpressions) {
				formulae[idx++] = factory.makeUnaryExpression(unaryTag, factory
						.makeBinaryExpression(binaryTag, factory.makeFreeIdentifier("x", null), factory.makeFreeIdentifier("y", null), null),
						null);
				formulae[idx++] = factory.makeBinaryExpression(binaryTag,
						factory.makeUnaryExpression(unaryTag, factory.makeFreeIdentifier("x", null), null),
						factory.makeFreeIdentifier("y", null), null);
				formulae[idx++] = factory
						.makeBinaryExpression(binaryTag, factory.makeFreeIdentifier("x", null), factory
								.makeUnaryExpression(unaryTag, factory.makeFreeIdentifier("y", null), null),
								null);
			}
		}
		// Special case of negative integer literal
		for (int binaryTag : binaryExpressions) {
			formulae[idx++] = factory.makeBinaryExpression(binaryTag,
					factory.makeIntegerLiteral(BigInteger.valueOf(-1), null),
					factory.makeFreeIdentifier("y", null), null);
			formulae[idx++] = factory.makeBinaryExpression(binaryTag, factory.makeFreeIdentifier("x", null),
					factory.makeIntegerLiteral(BigInteger.valueOf(-1), null),
					null);
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructAssociativeUnaryTrees(TagSupply tagSupply) {
		// {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID, KMIN, KMAX, CONVERSE}
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> unaryExpressions = tagSupply.unaryExpressionTags;
		final int unarySize = unaryExpressions.size();
		final Set<Integer> associativeExpressions = tagSupply.associativeExpressionTags;
		final int assocSize = associativeExpressions.size();
		final int length = (4 * unarySize * assocSize)
				+ (3 * assocSize);
		final Expression[] formulae = new Expression[length];
		int idx = 0;
		for (int unaryTag: unaryExpressions) {
			for (int assocTag: associativeExpressions) {
				formulae[idx++] = factory.makeUnaryExpression(
						unaryTag,
						factory.makeAssociativeExpression(assocTag,
								mList(factory.makeFreeIdentifier("x", null), factory.makeFreeIdentifier("y", null)), null), null);
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag,
						mList(factory.makeFreeIdentifier("x", null), factory.makeUnaryExpression(unaryTag, factory.makeFreeIdentifier("y", null),
								null)), null);
				formulae[idx++] = factory.makeAssociativeExpression(
						assocTag,
						mList(factory.makeFreeIdentifier("x", null), factory.makeUnaryExpression(unaryTag, factory.makeFreeIdentifier("y", null),
								null), factory.makeFreeIdentifier("z", null)), null);
				formulae[idx++] = factory
						.makeAssociativeExpression(
								assocTag,
								mList(factory.makeUnaryExpression(unaryTag,
										factory.makeFreeIdentifier("x", null), null), factory.makeFreeIdentifier("y", null)), null);
			}
		}
		// Special case of negative integer literal
		for (int assocTag: associativeExpressions) {
			formulae[idx++] = factory.makeAssociativeExpression(
					assocTag,
					mList(factory.makeFreeIdentifier("x", null), factory.makeIntegerLiteral(
							BigInteger.valueOf(-1), null)), null);
			formulae[idx++] = factory.makeAssociativeExpression(
					assocTag,
					mList(factory.makeFreeIdentifier("x", null), factory.makeIntegerLiteral(
							BigInteger.valueOf(-1), null), factory.makeFreeIdentifier("z", null)), null);
			formulae[idx++] = factory.makeAssociativeExpression(
					assocTag,
					mList(factory.makeIntegerLiteral(BigInteger.valueOf(-1),
							null), factory.makeFreeIdentifier("y", null)), null);
		}
		assert idx == length;
		return formulae;
	}
	
	private Expression[] constructUnaryUnaryTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> unaryExpressions = tagSupply.unaryExpressionTags;
		final int unaryExpSize = unaryExpressions.size();
		
		final int length = (2 * unaryExpSize * unaryExpSize) + unaryExpSize;
		final Expression[] formulae = new Expression[length];
		int idx = 0;
		for (int tag1 : unaryExpressions) {
			for (int tag2 : unaryExpressions) {
				formulae[idx++] = factory.makeUnaryExpression(tag1,
						factory.makeUnaryExpression(tag2, factory.makeFreeIdentifier("x", null), null), null);
				formulae[idx++] = factory.makeUnaryExpression(tag2,
						factory.makeUnaryExpression(tag1, factory.makeFreeIdentifier("x", null), null), null);
			}
		}
		// Special case of negative integer literal
		for (int tag : unaryExpressions) {
			formulae[idx++] = factory.makeUnaryExpression(tag,
					factory.makeIntegerLiteral(BigInteger.valueOf(-1), null),
					null);
		}
		assert idx == formulae.length;
		return formulae;
	}
			
	private Expression mQExpr(FormulaFactory factory, int qtag,
			QuantifiedExpression.Form form, BoundIdentDecl bid, Expression expr) {
		if (form == Lambda) {
			return factory.makeQuantifiedExpression(qtag, mList(bid), factory.makeLiteralPredicate(BTRUE, null),
					factory.makeBinaryExpression(MAPSTO, factory.makeBoundIdentifier(0, null), expr, null), null, form);
		}
		return factory.makeQuantifiedExpression(qtag, mList(bid), factory.makeLiteralPredicate(BTRUE, null),
				factory.makeBinaryExpression(MAPSTO, factory.makeBoundIdentifier(0, null), expr, null), null, form);
	}
	
	private Expression[] constructQuantifiedBinaryTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedExpressions = tagSupply.quantifiedExpressionTags;
		final int quantSize = quantifiedExpressions.size();
		final Set<Integer> binaryExpressions = tagSupply.binaryExpressionTags;
		final int binarySize = binaryExpressions.size();
		final int length = 3 * binarySize * (2 * quantSize + 1);
		Expression[]  formulae = new Expression[length];
		int idx = 0;
		for (int quantTag : quantifiedExpressions) {
			for (int binaryTag : binaryExpressions) {
				for (QuantifiedExpression.Form form : QuantifiedExpression.Form
						.values()) {
					if (form == Lambda && quantTag != CSET)
						continue;
					formulae[idx++] = mQExpr(factory, quantTag, form, factory.makeBoundIdentDecl("x", null),
							factory.makeBinaryExpression(binaryTag, factory.makeFreeIdentifier("y", null), factory.makeFreeIdentifier("z", null),
									null));
					formulae[idx++] = factory.makeBinaryExpression(binaryTag,
							factory.makeFreeIdentifier("x", null), mQExpr(factory, quantTag, form, factory.makeBoundIdentDecl("y", null), factory.makeFreeIdentifier("z", null)),
							null);
					formulae[idx++] = factory.makeBinaryExpression(binaryTag,
							mQExpr(factory, quantTag, form, factory.makeBoundIdentDecl("x", null), factory.makeFreeIdentifier("y", null)), factory.makeFreeIdentifier("z", null),
							null);
				}
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Expression[] constructQuantifiedAssociativeTree(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedExpressions = tagSupply.quantifiedExpressionTags;
		final int quantSize = quantifiedExpressions.size();
		final Set<Integer> associativeExpressions = tagSupply.associativeExpressionTags;
		final int assocSize = associativeExpressions.size();
		final int length = 4 * assocSize * (2 * quantSize + 1);
		Expression[]  formulae = new Expression[length];
		int idx = 0;
		for (int quantTag:quantifiedExpressions) {
			for (int assocTag : associativeExpressions) {
				for (QuantifiedExpression.Form form: QuantifiedExpression.Form.values()) {
					if (form == Lambda &&  + quantTag != CSET)
						continue;
					formulae[idx++] = mQExpr(
							factory,
							quantTag,
							form,
							factory.makeBoundIdentDecl("x", null),
							factory.makeAssociativeExpression(assocTag,
									mList(factory.makeFreeIdentifier("y", null), factory.makeFreeIdentifier("z", null)), null));
					formulae[idx++] = factory
							.makeAssociativeExpression(
									assocTag,
									mList(factory.makeFreeIdentifier("x", null),
											mQExpr(factory, quantTag, form,
													factory.makeBoundIdentDecl("y", null), factory.makeFreeIdentifier("z", null))), null);
					formulae[idx++] = factory
							.makeAssociativeExpression(
									assocTag,
									mList(factory.makeFreeIdentifier("x", null),
											mQExpr(factory, quantTag, form,
													factory.makeBoundIdentDecl("y", null), factory.makeFreeIdentifier("z", null)), factory.makeFreeIdentifier("t", null)), null);
					formulae[idx++] = factory.makeAssociativeExpression(
							assocTag,
							mList(mQExpr(factory, quantTag, form, factory.makeBoundIdentDecl("x", null), factory.makeFreeIdentifier("y", null)),
									factory.makeFreeIdentifier("z", null)), null);
				}
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Expression[] constructQuantifiedUnaryTree(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedExpressions = tagSupply.quantifiedExpressionTags;
		final int quantSize = quantifiedExpressions.size();
		final Set<Integer> unaryExpressions = tagSupply.unaryExpressionTags;
		final int unarySize = unaryExpressions.size();
		final int length = (2 * unarySize + 1)
				* (2 * quantSize + 1);
		final Expression[]  formulae = new Expression[length];
		int idx = 0;
		for (int quantTag : quantifiedExpressions) {
			for (int unaryTag : unaryExpressions) {
				for (QuantifiedExpression.Form form : QuantifiedExpression.Form
						.values()) {
					if (form == Lambda && quantTag != CSET)
						continue;
					formulae[idx++] = mQExpr(factory, quantTag, form, factory.makeBoundIdentDecl("x", null),
							factory.makeUnaryExpression(unaryTag, factory.makeBoundIdentifier(0, null), null));
					formulae[idx++] = factory.makeUnaryExpression(unaryTag,
							mQExpr(factory, quantTag, form, factory.makeBoundIdentDecl("x", null), factory.makeBoundIdentifier(0, null)), null);
				}
			}
			// Special case of negative integer literal
			for (QuantifiedExpression.Form form : QuantifiedExpression.Form
					.values()) {
				if (form == Lambda && quantTag != CSET)
					continue;
				formulae[idx++] = mQExpr(
						factory,
						quantTag,
						form,
						factory.makeBoundIdentDecl("x", null),
						factory.makeIntegerLiteral(BigInteger.valueOf(-1), null));
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructAssociativeAssociativePredicateTree(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> associativePredicates = tagSupply.associativePredicateTags;
		final int length = associativePredicates.size();
		Predicate[] formulae = new Predicate[length * length * 3];
		int idx = 0;
		for (int assocTag1 : associativePredicates) {
			for (int assocTag2 : associativePredicates) {
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag2,
						mList(factory.makeAssociativePredicate(assocTag1,
								mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null)), null), factory.makeLiteralPredicate(BTRUE, null)), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag2,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeAssociativePredicate(
								assocTag1, mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null)), null), factory.makeLiteralPredicate(BTRUE, null)),
						null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag2,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeAssociativePredicate(
								assocTag1, mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null)), null)), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructAssociativePredicateVariableTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> associativePredicates = tagSupply.associativePredicateTags;
		final int length = associativePredicates.size();
		Predicate[]  formulae = new Predicate[length * 1 * 3];
		int idx = 0;
		for (int assocTag : associativePredicates) {
			formulae[idx++] = factory.makeAssociativePredicate(assocTag,
					mList(factory.makePredicateVariable("$P", null), factory.makeLiteralPredicate(BTRUE, null)), null);
			formulae[idx++] = factory.makeAssociativePredicate(assocTag,
					mList(factory.makeLiteralPredicate(BTRUE, null), factory.makePredicateVariable("$P", null), factory.makeLiteralPredicate(BTRUE, null)), null);
			formulae[idx++] = factory.makeAssociativePredicate(assocTag,
					mList(factory.makeLiteralPredicate(BTRUE, null), factory.makePredicateVariable("$P", null)), null);
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructBinaryBinaryPredicateTrees (TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> binaryPredicates = tagSupply.binaryPredicateTags;
		final int binarySize = binaryPredicates.size();
		int length = binarySize;
		Predicate[]  formulae = new Predicate[(length)*(length)*2];
		int idx = 0;
		for (int binaryTag1 : binaryPredicates) {
			for (int binaryTag2 : binaryPredicates) {
				formulae[idx++] = factory.makeBinaryPredicate(binaryTag2,
						factory.makeBinaryPredicate(binaryTag1, factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null),
								null), factory.makeLiteralPredicate(BTRUE, null), null);
				formulae[idx++] = factory.makeBinaryPredicate(binaryTag2,
						factory.makeLiteralPredicate(BTRUE, null), factory.makeBinaryPredicate(binaryTag1, factory.makeLiteralPredicate(BTRUE, null),
								factory.makeLiteralPredicate(BTRUE, null), null), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructBinaryPredicateVariableTrees (TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> binaryPredicates = tagSupply.binaryPredicateTags;
		final int binarySize = binaryPredicates.size();
		int length = binarySize;
		Predicate[]  formulae = new Predicate[length * 1 * 2];
		int idx = 0;
		for (int binaryTag : binaryPredicates) {
			formulae[idx++] = factory.makeBinaryPredicate(binaryTag, factory.makePredicateVariable("$P", null),
					factory.makeLiteralPredicate(BTRUE, null), null);
			formulae[idx++] = factory.makeBinaryPredicate(binaryTag, factory.makeLiteralPredicate(BTRUE, null),
					factory.makePredicateVariable("$P", null), null);
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructAssociativeBinaryPredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> associativePredicates = tagSupply.associativePredicateTags;
		final int assocSize = associativePredicates.size();
		final Set<Integer> binaryPredicates = tagSupply.binaryPredicateTags;
		final int binarySize = binaryPredicates.size();
		Predicate[]  formulae = new Predicate[(assocSize)*(binarySize)*5];
		int idx = 0;
		for (int assocTag : associativePredicates) {
			for (int binaryTag : binaryPredicates) {
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeBinaryPredicate(binaryTag,
								factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null), null)), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeBinaryPredicate(binaryTag,
								factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null), null), factory.makeLiteralPredicate(BTRUE, null)), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeBinaryPredicate(binaryTag, factory.makeLiteralPredicate(BTRUE, null),
								factory.makeLiteralPredicate(BTRUE, null), null), factory.makeLiteralPredicate(BTRUE, null)), null);
				formulae[idx++] = factory.makeBinaryPredicate(
						binaryTag,
						factory.makeAssociativePredicate(assocTag,
								mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null)), null), factory.makeLiteralPredicate(BTRUE, null), null);
				formulae[idx++] = factory.makeBinaryPredicate(
						binaryTag,
						factory.makeLiteralPredicate(BTRUE, null),
						factory.makeAssociativePredicate(assocTag,
								mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null)), null), null);

			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructUnaryUnaryPredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> unaryPredicates = tagSupply.unaryPredicateTags;
		final int unarySize = unaryPredicates.size();
		Predicate[] formulae = new Predicate[unarySize * unarySize];
		int idx = 0;
		for (int unaryTag1 : unaryPredicates) {
			for (int unaryTag2 : unaryPredicates) {
				formulae[idx++] = factory.makeUnaryPredicate(unaryTag1,
						factory.makeUnaryPredicate(unaryTag2, factory.makeLiteralPredicate(BTRUE, null), null),
						null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructUnaryPredicateVariableTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> unaryPredicates = tagSupply.unaryPredicateTags;
		final int unarySize = unaryPredicates.size();
		Predicate[] formulae = new Predicate[unarySize * 1];
		int idx = 0;
		for (int unaryTag : unaryPredicates) {
			formulae[idx++] = factory.makeUnaryPredicate(unaryTag, factory.makePredicateVariable("$P", null), null);
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructAssociativeUnaryPredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> associativePredicates = tagSupply.associativePredicateTags;
		final int assocSize = associativePredicates.size();
		final Set<Integer> unaryPredicates = tagSupply.unaryPredicateTags;
		final int unarySize = unaryPredicates.size();
		Predicate[]  formulae = new Predicate[(unarySize)*(assocSize)*4];
		int idx = 0;
		for (int unaryTag : unaryPredicates) {
			for (int assocTag : associativePredicates) {
				formulae[idx++] = factory.makeUnaryPredicate(
						unaryTag,
						factory.makeAssociativePredicate(assocTag,
								mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null)), null), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeUnaryPredicate(unaryTag,
								factory.makeLiteralPredicate(BTRUE, null), null)), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeUnaryPredicate(unaryTag,
								factory.makeLiteralPredicate(BTRUE, null), null), factory.makeLiteralPredicate(BTRUE, null)), null);
				formulae[idx++] = factory
						.makeAssociativePredicate(
								assocTag,
								mList(factory.makeUnaryPredicate(unaryTag,
										factory.makeLiteralPredicate(BTRUE, null), null), factory.makeLiteralPredicate(BTRUE, null)), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructBinaryUnaryPredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> binaryPredicates = tagSupply.binaryPredicateTags;
		final int binarySize = binaryPredicates.size();
		final Set<Integer> unaryPredicates = tagSupply.unaryPredicateTags;
		final int unarySize = unaryPredicates.size();
		Predicate[] formulae = new Predicate[(unarySize) * (binarySize) * 3];
		int idx = 0;
		for (int unaryTag : unaryPredicates) {
			for (int binaryTag : binaryPredicates) {
				formulae[idx++] = factory.makeUnaryPredicate(unaryTag, factory
						.makeBinaryPredicate(binaryTag, factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null), null),
						null);
				formulae[idx++] = factory.makeBinaryPredicate(binaryTag,
						factory.makeUnaryPredicate(unaryTag, factory.makeLiteralPredicate(BTRUE, null), null),
						factory.makeLiteralPredicate(BTRUE, null), null);
				formulae[idx++] = factory
						.makeBinaryPredicate(binaryTag, factory.makeLiteralPredicate(BTRUE, null), factory
								.makeUnaryPredicate(unaryTag, factory.makeLiteralPredicate(BTRUE, null), null),
								null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructQuantifiedQuantifiedPredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedPredicates = tagSupply.quantifiedPredicateTags;
		final int quantSize = quantifiedPredicates.size();
		Predicate[] formulae = new Predicate[quantSize * quantSize];
		int idx = 0;
		for (int quantTag1 : quantifiedPredicates) {
			for (int quantTag2 : quantifiedPredicates) {
				formulae[idx++] = factory.makeQuantifiedPredicate(quantTag2,
						mList(factory.makeBoundIdentDecl("x", null)), factory.makeQuantifiedPredicate(quantTag1,
								mList(factory.makeBoundIdentDecl("y", null)), factory.makeLiteralPredicate(BTRUE, null), null), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructQuantifiedBinaryPredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedPredicates = tagSupply.quantifiedPredicateTags;
		final int quantSize = quantifiedPredicates.size();
		final Set<Integer> binaryPredicates = tagSupply.binaryPredicateTags;
		final int binarySize = binaryPredicates.size();
		Predicate[] formulae = new Predicate[quantSize*binarySize*3];
		int idx = 0;
		for (int quantTag : quantifiedPredicates) {
			for (int binaryTag : binaryPredicates) {
				formulae[idx++] = factory.makeQuantifiedPredicate(quantTag,
						mList(factory.makeBoundIdentDecl("x", null)), factory.makeBinaryPredicate(binaryTag,
								factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null), null), null);
				formulae[idx++] = factory.makeBinaryPredicate(binaryTag,
						factory.makeQuantifiedPredicate(quantTag, mList(factory.makeBoundIdentDecl("x", null)),
								factory.makeLiteralPredicate(BTRUE, null), null), factory.makeLiteralPredicate(BTRUE, null), null);
				formulae[idx++] = factory.makeBinaryPredicate(binaryTag, factory.makeLiteralPredicate(BTRUE, null),
						factory.makeQuantifiedPredicate(quantTag, mList(factory.makeBoundIdentDecl("x", null)),
								factory.makeLiteralPredicate(BTRUE, null), null), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructQuantifiedAssociativePredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedPredicates = tagSupply.quantifiedPredicateTags;
		final int quantSize = quantifiedPredicates.size();
		final Set<Integer> associativePredicates = tagSupply.associativePredicateTags;
		final int assocSize = associativePredicates.size();
		Predicate[] formulae = new Predicate[quantSize*assocSize*4];
		int idx = 0;
		for (int quantTag : quantifiedPredicates) {
			for (int assocTag : associativePredicates) {
				formulae[idx++] = factory.makeQuantifiedPredicate(
						quantTag,
						mList(factory.makeBoundIdentDecl("x", null)),
						factory.makeAssociativePredicate(assocTag,
								mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeLiteralPredicate(BTRUE, null)), null), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeQuantifiedPredicate(quantTag,
								mList(factory.makeBoundIdentDecl("x", null)), factory.makeLiteralPredicate(BTRUE, null), null)), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeLiteralPredicate(BTRUE, null), factory.makeQuantifiedPredicate(quantTag,
								mList(factory.makeBoundIdentDecl("x", null)), factory.makeLiteralPredicate(BTRUE, null), null), factory.makeLiteralPredicate(BTRUE, null)), null);
				formulae[idx++] = factory.makeAssociativePredicate(
						assocTag,
						mList(factory.makeQuantifiedPredicate(quantTag,
								mList(factory.makeBoundIdentDecl("x", null)), factory.makeLiteralPredicate(BTRUE, null), null), factory.makeLiteralPredicate(BTRUE, null)), null);
		}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructQuantifiedUnaryPredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedPredicates = tagSupply.quantifiedPredicateTags;
		final int quantSize = quantifiedPredicates.size();
		final Set<Integer> unaryPredicates = tagSupply.unaryPredicateTags;
		final int unarySize = unaryPredicates.size();
		Predicate[] formulae = new Predicate[quantSize*unarySize*2];
		int idx = 0;
		for (int quantTag : quantifiedPredicates) {
			for (int unaryTag : unaryPredicates) {
				formulae[idx++] = factory
						.makeQuantifiedPredicate(quantTag, mList(factory.makeBoundIdentDecl("y", null)), factory
								.makeUnaryPredicate(unaryTag, factory.makeLiteralPredicate(BTRUE, null), null),
								null);
				formulae[idx++] = factory.makeUnaryPredicate(unaryTag, factory
						.makeQuantifiedPredicate(quantTag, mList(factory.makeBoundIdentDecl("y", null)), factory.makeLiteralPredicate(BTRUE, null),
								null), null);
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructQuantifiedPredicateVariableTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final Set<Integer> quantifiedPredicates = tagSupply.quantifiedPredicateTags;
		final int quantSize = quantifiedPredicates.size();
		Predicate[] formulae = new Predicate[quantSize * 1];
		int idx = 0;
		for (int quantTag : quantifiedPredicates) {
			formulae[idx++] = factory.makeQuantifiedPredicate(quantTag,
					mList(factory.makeBoundIdentDecl("y", null)), factory.makePredicateVariable("$P", null), null);
		}
		assert idx == formulae.length;
		return formulae;
	}

	private Predicate[] constructMultiplePredicateTrees(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		final List<Expression> exprs = Common.constructExpressions(tagSupply);
		final int exprsSize = exprs.size();
		final Set<Integer> multiplePredicates = tagSupply.multiplePredicateTags;
		final int multiSize = multiplePredicates.size();
		Predicate[]  formulae = new Predicate[multiSize * exprsSize * exprsSize];
		int idx = 0;
		for (int multiTag : multiplePredicates) {
			for (Expression exp1 : exprs) {
				for (Expression exp2 : exprs) {
					formulae[idx ++] = factory.makeMultiplePredicate(multiTag, mList(exp1, exp2), null);
				}
			}
		}
		assert idx == formulae.length;
		return formulae;
	}
	
	private Predicate[] constructRelop(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		List<Expression> exprs = Common.constructExpressions(tagSupply);
		final int exprsSize = exprs.size();
		final Set<Integer> relationalPredicates = tagSupply.relationalPredicateTags;
		final int relSize = relationalPredicates.size();
		Predicate[] formulae = new Predicate[2 * exprsSize * relSize];
		int idx = 0;
		for (Expression expr: exprs) {
			for (int relTag : relationalPredicates) {
				formulae[idx++] = factory.makeRelationalPredicate(relTag, expr,
						factory.makeFreeIdentifier("t", null), null);
				formulae[idx++] = factory.makeRelationalPredicate(relTag, factory.makeFreeIdentifier("t", null),
						expr, null);
			}
		}
		assert idx == formulae.length;
		return formulae; 
	}
	
	private Expression[] constructQuantifierWithPredicate(TagSupply tagSupply) {
		final FormulaFactory factory = tagSupply.factory;
		List<Predicate> preds = Common.constructPredicates(tagSupply);
		final Set<Integer> quantifiedExpressions = tagSupply.quantifiedExpressionTags;
		final int quantSize = quantifiedExpressions.size();
		Expression[] formulae = new Expression[2 * preds.size() * quantSize];
		int idx = 0;
		for (Predicate pred: preds) {
			for (int quantTag : quantifiedExpressions) {
				formulae[idx++] = factory.makeQuantifiedExpression(quantTag,
						mList(factory.makeBoundIdentDecl("x", null)), pred, factory.makeIntegerLiteral(Common.TWO, null), null, Explicit);
				formulae[idx++] = factory.makeQuantifiedExpression(quantTag,
						mList(factory.makeBoundIdentDecl("x", null)), pred, factory.makeBoundIdentifier(0, null), null, Implicit);
			}
		}
		assert idx == formulae.length;
		return formulae; 
	}
	
	private void routineTestStringFormula(TestPair[] pairs) {
		for (TestPair pair: pairs) {
			routineTestStringFormula(pair);
		}
	}

	private void routineTestStringFormula(TestPair pair) {
		String formula = pair.getFormula().toString();
		String formulaParenthesized = pair.getFormula().toStringFullyParenthesized();
		assertEquals("\nTest failed on original String: "+pair.image+"\nUnparser produced: "+formula+"\n",
				pair.image, formula);
		
		pair.verify(formula);
		pair.verify(formulaParenthesized);
	}

	private void routineTestStringExpression(String image, Expression expr) {
		routineTestStringFormula(new ExprTestPair(image, expr));
	}
	
	/**
	 * Test of hand-written formulae
	 */
	@Test 
	public void testStringFormula() {
		routineTestStringFormula(associativeExpressionTestPairs);
		routineTestStringFormula(associativePredicateTestPairs);
		routineTestStringFormula(predPatternTestPairs);
		routineTestStringFormula(specialExprTestPairs);
		routineTestStringFormula(specialPredTestPairs);
		routineTestStringFormula(assignmentTestPairs);
	}
	
	/**
	 * Test of automatically generated formulae
	 */
	@Test 
	public void testUnparse() {
		for (TagSupply tagSupply: TagSupply.getAllTagSupplies()) {
			routineUnparse(tagSupply);
		}
	}

	public void routineUnparse(TagSupply tagSupply) {
		final LanguageVersion ver = tagSupply.version;

		routineTest(constructAssociativeAssociativeTrees(tagSupply), ver);
		routineTest(constructBinaryBinaryTrees(tagSupply), ver);
		routineTest(constructUnaryUnaryTrees(tagSupply), ver);
		routineTest(constructQuantifiedQuantifiedTrees(), ver);
		routineTest(constructAssociativeBinaryTrees(tagSupply), ver);
		routineTest(constructAssociativeUnaryTrees(tagSupply), ver);
		routineTest(constructBinaryUnaryTrees(tagSupply), ver);
		routineTest(constructQuantifiedBinaryTrees(tagSupply), ver);
		routineTest(constructQuantifiedAssociativeTree(tagSupply), ver);
		routineTest(constructQuantifiedUnaryTree(tagSupply), ver);
		routineTest(constructAssociativeAssociativePredicateTree(tagSupply), ver);
		routineTest(constructAssociativePredicateVariableTrees(tagSupply), ver);
		routineTest(constructBinaryBinaryPredicateTrees(tagSupply), ver);
		routineTest(constructBinaryPredicateVariableTrees(tagSupply), ver);
		routineTest(constructUnaryUnaryPredicateTrees(tagSupply), ver);
		routineTest(constructUnaryPredicateVariableTrees(tagSupply), ver);
		routineTest(constructAssociativeBinaryPredicateTrees(tagSupply), ver);
		routineTest(constructAssociativeUnaryPredicateTrees(tagSupply), ver);
		routineTest(constructBinaryUnaryPredicateTrees(tagSupply), ver);
		routineTest(constructQuantifiedQuantifiedPredicateTrees(tagSupply), ver);
		routineTest(constructQuantifiedBinaryPredicateTrees(tagSupply), ver);
		routineTest(constructQuantifiedAssociativePredicateTrees(tagSupply), ver);
		routineTest(constructQuantifiedUnaryPredicateTrees(tagSupply), ver);
		routineTest(constructQuantifiedPredicateVariableTrees(tagSupply), ver);
		routineTest(constructMultiplePredicateTrees(tagSupply), ver);
		routineTest(constructRelop(tagSupply), ver);
		routineTest(constructQuantifierWithPredicate(tagSupply), ver);
	}
	
	
	private void routineTest(Formula<?>[] formulae, LanguageVersion version) {
		for (int i = 0; i < formulae.length; i++) {
			final TestPair pair;
			if (formulae[i] instanceof Expression) {
				pair = new ExprTestPair(null, version,
						(Expression) formulae[i]);
			} else {
				pair = new PredTestPair(null, version,
						(Predicate) formulae[i]);
			}
			
			final String formula = pair.getFormula().toString();
			final String formulaParenthesized = pair.getFormula().toStringFullyParenthesized();
			
			pair.verify(formula);
			pair.verify(formulaParenthesized);
		}
	}
	
	@Test 
	public void testNegativeIntegerLiteral() throws Exception {
		final ExprTestPair pairs[] = new ExprTestPair[] {
				// Alone
				new ExprTestPair("\u2212x", mUnaryExpression(UNMINUS, id_x)),
				// Unary minus
				new ExprTestPair("\u2212(\u2212x)", mUnaryExpression(UNMINUS,
						mUnaryExpression(UNMINUS, id_x))),
				// Addition
				new ExprTestPair("\u2212x+y", mAssociativeExpression(PLUS,
						mUnaryExpression(UNMINUS, id_x), id_y)),
				new ExprTestPair("y+(\u2212x)+z", mAssociativeExpression(PLUS,
						id_y, mUnaryExpression(UNMINUS, id_x), id_z)),
				new ExprTestPair("y+(\u2212x)", mAssociativeExpression(PLUS,
						id_y, mUnaryExpression(UNMINUS, id_x))),
				// Subtraction
				new ExprTestPair("\u2212x \u2212 y", mBinaryExpression(MINUS,
						mUnaryExpression(UNMINUS, id_x), id_y)),
				new ExprTestPair("y \u2212 (\u2212x)", mBinaryExpression(MINUS,
						id_y, mUnaryExpression(UNMINUS, id_x))),
				// Multiplication
				new ExprTestPair("(\u2212x)\u2217y", mAssociativeExpression(
						MUL, mUnaryExpression(UNMINUS, id_x), id_y)),
				new ExprTestPair("y\u2217(\u2212x)\u2217z",
						mAssociativeExpression(MUL, id_y, mUnaryExpression(
								UNMINUS, id_x), id_z)),
				new ExprTestPair("y\u2217(\u2212x)", mAssociativeExpression(
						MUL, id_y, mUnaryExpression(UNMINUS, id_x))),
				// Division
				new ExprTestPair("(\u2212x) \u00f7 y", mBinaryExpression(DIV,
						mUnaryExpression(UNMINUS, id_x), id_y)),
				new ExprTestPair("y \u00f7 (\u2212x)", mBinaryExpression(DIV,
						id_y, mUnaryExpression(UNMINUS, id_x))),
				// Modulo
				new ExprTestPair("(\u2212x) mod y", mBinaryExpression(MOD,
						mUnaryExpression(UNMINUS, id_x), id_y)),
				new ExprTestPair("y mod (\u2212x)", mBinaryExpression(MOD,
						id_y, mUnaryExpression(UNMINUS, id_x))),
				// Exponentiation
				new ExprTestPair("(\u2212x) ^ y", mBinaryExpression(EXPN,
						mUnaryExpression(UNMINUS, id_x), id_y)),
				new ExprTestPair("y ^ (\u2212x)", mBinaryExpression(EXPN, id_y,
						mUnaryExpression(UNMINUS, id_x))), };
		
		routineTestStringFormula(pairs);
		
		// Same test with a positive integer literal
		final int length = pairs.length;
		final ExprTestPair pairsPos[] = new ExprTestPair[length];
		final Pattern pattern = Pattern.compile(id_x.getName());
		final IntegerLiteral il_1 = mIntegerLiteral(1);
		final IFormulaRewriter rewriterPos = new DefaultRewriter(false, ff) {
			@Override
			public Expression rewrite(FreeIdentifier identifier) {
				return identifier == id_x ? il_1 : identifier;
			}
		};
		for (int i = 0; i < length; i++) {
			final String image = pairs[i].image;
			final Expression expr = pairs[i].formula;
			final String newImage = pattern.matcher(image).replaceAll("(1)");
			final Expression newExpr = expr.rewrite(rewriterPos);
			pairsPos[i] = new ExprTestPair(newImage, newExpr);
		}
		routineTestStringFormula(pairsPos);

		// Same test with a negative integer literal
		final ExprTestPair pairsNeg[] = new ExprTestPair[length];
		final IntegerLiteral il_m1 = mIntegerLiteral(-1);
		final IFormulaRewriter rewriterNeg = new DefaultRewriter(false, ff) {
			@Override
			public Expression rewrite(UnaryExpression expr) {
				return expr.getChild() == id_x ? il_m1 : expr;
			}
		};
		for (int i = 0; i < length; i++) {
			final String image = pairs[i].image;
			final Expression expr = pairs[i].formula;
			final String newImage = pattern.matcher(image).replaceAll("1");
			final Expression newExpr = expr.rewrite(rewriterNeg);
			pairsNeg[i] = new ExprTestPair(newImage, newExpr);
		}
		routineTestStringFormula(pairsNeg);
	}

	/**
	 * Ensures that quantified expressions built programmatically have their
	 * form automatically changed so that they can be unparsed and parsed back
	 * without error.
	 */
	@Test 
	public void testQuantifiedExpressionForm() throws Exception {
		// Lambda with duplicate pattern identifier (simple)
		routineTestStringExpression(
				"{x ↦ x ↦ x ∣ ⊤}", 
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_x), btrue, 
						mMaplet(b0, b0, b0)
				)
		);

		// Lambda with duplicate pattern identifier (complex)
		routineTestStringExpression(
				"{x ↦ y ↦ x ↦ x+y ∣ ⊤}", 
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_x, bd_y), btrue, 
						mMaplet(b1, b0, b1,
								mAssociativeExpression(PLUS, b1, b0)
						)
				)
		);

		// Lambda with duplicate pattern identifier (--> Explicit)
		routineTestStringExpression(
				"{x·⊤ ∣ x ↦ x ↦ t}", 
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_x), btrue, 
						mMaplet(b0, b0, id_t)
				)
		);

		// Lambda with literal in pattern
		routineTestStringExpression(
				"{x ↦ 2 ↦ x ∣ ⊤}",
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_x), btrue, 
						mMaplet(b0, two, b0)
				)
		);

		// Lambda with free identifier in pattern
		routineTestStringExpression(
				"{x,y·⊤ ∣ x ↦ t ↦ y ↦ x}",
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_x, bd_y), btrue, 
						mMaplet(b1, id_t, b0, b1)
				)
		);

		// Lambda with outer bound identifier in pattern
		routineTestStringExpression(
				"{x·⊤ ∣ {y·⊤ ∣ y ↦ x ↦ y}}",
				mQuantifiedExpression(CSET, Explicit,
						mList(bd_x), btrue,
						mQuantifiedExpression(CSET, Lambda,
								mList(bd_y), btrue, 
								mMaplet(b0, b1, b0)
						)
				)
		);

		// Implicit formulas with free identifier in expression
		routineTestStringExpression(
				"{x·⊤ ∣ t}",
				mQuantifiedExpression(CSET, Implicit,
						mList(bd_x), btrue,
						id_t
				)
		);
		routineTestStringExpression(
				"⋃x·⊤ ∣ t",
				mQuantifiedExpression(QUNION, Implicit,
						mList(bd_x), btrue,
						id_t
				)
		);
		routineTestStringExpression(
				"⋂x·⊤ ∣ t",
				mQuantifiedExpression(QINTER, Implicit,
						mList(bd_x), btrue,
						id_t
				)
		);

		// Implicit formulas with outer bound identifier in expression
		routineTestStringExpression(
				"{x·⊤ ∣ {y·⊤ ∣ x}}",
				mQuantifiedExpression(CSET, Explicit,
						mList(bd_x), btrue,
						mQuantifiedExpression(CSET, Implicit,
								mList(bd_y), btrue,
								b1
						)
				)
		);
		routineTestStringExpression(
				"{x·⊤ ∣ ⋃y·⊤ ∣ x}",
				mQuantifiedExpression(CSET, Explicit,
						mList(bd_x), btrue,
						mQuantifiedExpression(QUNION, Implicit,
								mList(bd_y), btrue,
								b1
						)
				)
		);
		routineTestStringExpression(
				"{x·⊤ ∣ ⋂y·⊤ ∣ x}",
				mQuantifiedExpression(CSET, Explicit,
						mList(bd_x), btrue,
						mQuantifiedExpression(QINTER, Implicit,
								mList(bd_y), btrue,
								b1
						)
				)
		);
	}

	/**
	 * Ensures that a quantified expression which cannot be parsed back as
	 * Implicit is unparsed in Explicit form.
	 */
	@Ignore("Known limitation of the pretty-print.")
	@Test
	public void testQuantifiedExpressionOrder() throws Exception {
		// Lambda with inverted pattern
		routineTestStringExpression(
				"{x ↦ y ↦ x+y ∣ ⊤}", 
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_y, bd_x), btrue, 
						mMaplet(b0, b1,
								mAssociativeExpression(PLUS, b0, b1)
						)
				)
		);

		// Lambda with incomplete pattern
		routineTestStringExpression(
				"{x,y·⊤ ∣ x ↦ 2}", 
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_x, bd_y), btrue, 
						mMaplet(b1, two)
				)
		);
		routineTestStringExpression(
				"{x,y·⊤ ∣ y ↦ 2}", 
				mQuantifiedExpression(CSET, Lambda,
						mList(bd_x, bd_y), btrue, 
						mMaplet(b0, two)
				)
		);

		// Inverted order of variable declarations
		routineTestStringExpression(
				"{y,x·x ↦ y ∣ ⊤}", 
				mQuantifiedExpression(CSET, Implicit,
						mList(bd_y, bd_x), btrue, 
						mMaplet(b0, b1)
				)
		);
	}

}
