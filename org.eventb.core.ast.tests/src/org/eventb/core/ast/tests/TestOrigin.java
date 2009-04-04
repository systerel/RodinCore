/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.RELIMAGE;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;

public class TestOrigin extends AbstractTests {

	private static FreeIdentifier id_x = mFreeIdentifier("x");
	private static FreeIdentifier id_y = mFreeIdentifier("y");
	private static FreeIdentifier id_z = mFreeIdentifier("z");
	private static FreeIdentifier id_S = mFreeIdentifier("S");
	private static FreeIdentifier id_T = mFreeIdentifier("T");
	private static FreeIdentifier id_f = mFreeIdentifier("f");
	
	private static BoundIdentDecl bd_x = mBoundIdentDecl("x");
	private static BoundIdentDecl bd_xp = mBoundIdentDecl("x'");
	private static BoundIdentDecl bd_yp = mBoundIdentDecl("y'");
	private static BoundIdentDecl bd_zp = mBoundIdentDecl("z'");

	private static BoundIdentifier b0 = mBoundIdentifier(0);
	private static BoundIdentifier b1 = mBoundIdentifier(1);
	private static BoundIdentifier b2 = mBoundIdentifier(2);
	
	private static LiteralPredicate bfalse = mLiteralPredicate(BFALSE);

	private static abstract class TestAllOrigins {
		private String image;

		TestAllOrigins(String image) {
			this.image = image;
		}

		final void verify() {
			final Formula<?> parsedFormula = parseAndCheck(image);
			parsedFormula.accept(new SourceLocationOriginChecker(this));
		}

		abstract Formula<?> parseAndCheck(String stringToParse);
	}

	private static class ExprTestOrigin extends TestAllOrigins {
		Expression formula;

		ExprTestOrigin(String image, Expression formula) {
			super(image);
			this.formula = formula;
		}

		@Override
		Formula<?> parseAndCheck(String image) {
			IParseResult result = ff.parseExpression(image, LATEST, this);
			assertSuccess("Parse failed for " + image, result);
			final Expression actual = result.getParsedExpression();
			assertEquals("Unexpected parser result origin", this,
					actual.getSourceLocation().getOrigin());
			return actual;
		}
	}

	private static class PredTestOrigin extends TestAllOrigins {
		Predicate formula;

		PredTestOrigin(String image, Predicate formula) {
			super(image);
			this.formula = formula;
		}

		@Override
		Formula<?> parseAndCheck(String image) {
			IParseResult result = ff.parsePredicate(image, LATEST, this);
			assertSuccess("Parse failed for " + image, result);
			final Predicate actual = result.getParsedPredicate();
			assertEquals("Unexpected parser result", this, actual
					.getSourceLocation().getOrigin());
			return actual;
		}
	}

	private static class AssignmentTestOrigin extends TestAllOrigins {
		Assignment formula;

		AssignmentTestOrigin(String image, Assignment formula) {
			super(image);
			this.formula = formula;
		}

		@Override
		Formula<?> parseAndCheck(String image) {
			IParseResult result = ff.parseAssignment(image, LATEST, this);
			assertSuccess("Parse failed for " + image, result);
			final Assignment actual = result.getParsedAssignment();
			assertSame("Unexpected parser result", this, actual
					.getSourceLocation().getOrigin());
			return actual;
		}
	}

	PredTestOrigin[] predsOrigin = new PredTestOrigin[]{
			// AtomicPredicate
			new PredTestOrigin(
					"\u22a5", 
					bfalse
			), new PredTestOrigin(
					"\u00ac\u00ac\u22a5", 
					mUnaryPredicate(NOT, 
							mUnaryPredicate(NOT, bfalse)
					)
			), new PredTestOrigin(
					"∀x·x ∈ S ∧ (∀x·x ∈ T)",
					mQuantifiedPredicate(FORALL,
							mList(bd_x),
							mAssociativePredicate(LAND,
									mRelationalPredicate(IN, b0, id_S),
									mQuantifiedPredicate(FORALL,
											mList(bd_x),
											mRelationalPredicate(IN, b0, id_T)
									)
							)
					)
			),
	};

	
	ExprTestOrigin[] exprsOrigin = new ExprTestOrigin[] {
			// SimpleExpression
			new ExprTestOrigin(
					"bool(\u22a5)", 
					mBoolExpression(bfalse)
			), new ExprTestOrigin(
					"−x+y+z", 
					mAssociativeExpression(PLUS, 
							mUnaryExpression(UNMINUS, id_x), 
							id_y, 
							id_z
					) 
			), new ExprTestOrigin(
					"(f(x))∼[y]", 
					mBinaryExpression(RELIMAGE,
							mUnaryExpression(CONVERSE,
									mBinaryExpression(FUNIMAGE,
											id_f, id_x)),
							id_y)
			),
	};

	AssignmentTestOrigin[] assignsOrigin = new AssignmentTestOrigin[] {
			new AssignmentTestOrigin(
					"x ≔ y",
					mBecomesEqualTo(mList(id_x), mList(id_y))
			), new AssignmentTestOrigin(
					"x,y,z :\u2223 x' = y ∧ y' = z ∧ z' = x",
					mBecomesSuchThat(mList(id_x, id_y, id_z), mList(bd_xp, bd_yp, bd_zp),
							mAssociativePredicate(LAND,
									mRelationalPredicate(EQUAL, b2, id_y),
									mRelationalPredicate(EQUAL, b1, id_z),
									mRelationalPredicate(EQUAL, b0, id_x)
							))
			),
	};

	
	
	private void testList(TestAllOrigins[] list) {
		for (TestAllOrigins pair : list) {
			pair.verify();
		}
	}

	/**
	 * Main test routine.
	 */
	public void testParserOrigin() {
		 testList(predsOrigin);
		 testList(exprsOrigin);
		 testList(assignsOrigin);
	}

}
