/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.tests.FastFactory.mList;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.ISimpleVisitor2;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

public class TestSimpleVisitor extends TestCase {

	static class TestVisitor implements ISimpleVisitor {
		
		StringBuilder b = new StringBuilder();

		String getTrace() {
			return b.toString();
		}
		
		@Override
		public void visitAssociativeExpression(AssociativeExpression expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitAssociativePredicate(AssociativePredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitAtomicExpression(AtomicExpression expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitBecomesEqualTo(BecomesEqualTo assignment) {
			b.append(assignment.getClass());
		}

		@Override
		public void visitBecomesMemberOf(BecomesMemberOf assignment) {
			b.append(assignment.getClass());
		}

		@Override
		public void visitBecomesSuchThat(BecomesSuchThat assignment) {
			b.append(assignment.getClass());
		}

		@Override
		public void visitBinaryExpression(BinaryExpression expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitBinaryPredicate(BinaryPredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitBoolExpression(BoolExpression expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
			b.append(boundIdentDecl.getClass());
		}

		@Override
		public void visitBoundIdentifier(BoundIdentifier expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitFreeIdentifier(FreeIdentifier expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitIntegerLiteral(IntegerLiteral expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitLiteralPredicate(LiteralPredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitQuantifiedExpression(QuantifiedExpression expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitRelationalPredicate(RelationalPredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitSetExtension(SetExtension expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitSimplePredicate(SimplePredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitUnaryExpression(UnaryExpression expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitUnaryPredicate(UnaryPredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitMultiplePredicate(MultiplePredicate predicate) {
			b.append(predicate.getClass());
		}

		@Override
		public void visitExtendedExpression(ExtendedExpression expression) {
			b.append(expression.getClass());
		}

		@Override
		public void visitExtendedPredicate(ExtendedPredicate predicate) {
			b.append(predicate.getClass());
		}
	}

	static class TestVisitor2 extends TestVisitor implements ISimpleVisitor2 {

		@Override
		public void visitPredicateVariable(PredicateVariable predVar) {
			b.append(predVar.getClass());
		}
	}
	
	private final FormulaFactory ff = FormulaFactory.getDefault();

	private final FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	private final FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);

	private Expression e1 = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	private Expression e2 = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	private Expression e3 = ff.makeIntegerLiteral(BigInteger.ZERO, null);

	private Predicate p1 = ff.makeLiteralPredicate(Formula.BTRUE, null);
	private Predicate p2 = ff.makeLiteralPredicate(Formula.BTRUE, null);
	private Predicate p3 = ff.makeLiteralPredicate(Formula.BTRUE, null);

	private BoundIdentDecl bid = ff.makeBoundIdentDecl("x", null);

	private static final String assertMessage = "visit method was not called properly";

	private static <T extends Formula<?>> void assertVisit(T formula) {
		assertVisit(formula, new TestVisitor());
	}
	
	private static <T extends Formula<?>> void assertVisit(T formula,
			TestVisitor visitor) {
		final String expected = formula.getClass().toString();
		formula.accept(visitor);
		assertEquals(assertMessage, expected, visitor.getTrace());
	}

	public void testAssociativeExpression() {
		assertVisit(ff.makeAssociativeExpression(
				Formula.PLUS, mList(id_x, id_y), null));
	}

	public void testAssociativePredicate() {
		assertVisit(ff.makeAssociativePredicate(Formula.LOR, mList(p1,
				p2, p3), null));
	}

	public void testAtomicExpression() {
		assertVisit(ff.makeAtomicExpression(Formula.INTEGER, null));
	}

	public void testBecomesEqual() {
		assertVisit(ff.makeBecomesEqualTo(id_x, e1, null));
	}

	public void testToBecomesMemberOf() {
		assertVisit(ff.makeBecomesMemberOf(id_x, e1, null));
	}

	public void testBecomesSuchThat() {
		assertVisit(ff.makeBecomesSuchThat(id_x, bid, p1, null));
	}

	public void testBinaryExpression() {
		assertVisit(ff.makeBinaryExpression(Formula.MAPSTO, e1, e2, null));
	}

	public void testBinaryPredicate() {
		assertVisit(ff.makeBinaryPredicate(Formula.LIMP, p1, p2, null));
	}

	public void testBoolExpression() {
		assertVisit(ff.makeBoolExpression(p1, null));
	}

	public void testBoundIdentDecl() {
		assertVisit(ff.makeBoundIdentDecl("x", null));
	}

	public void testBoundIdentifier() {
		assertVisit(ff.makeBoundIdentifier(0, null));
	}

	public void testFreeIdentifier() {
		assertVisit(ff.makeFreeIdentifier("t", null));
	}

	public void testIntegerLiteral() {
		assertVisit(ff.makeIntegerLiteral(BigInteger.ZERO, null));
	}

	public void testLiteralPredicate() {
		assertVisit(ff.makeLiteralPredicate(Formula.BTRUE, null));
	}

	public void testPredicateVariable() {
		final PredicateVariable pvP = ff.makePredicateVariable("$P", null);
		try {
			pvP.accept(new TestVisitor());
			fail("IllegalArgumentException expected on predicate variables");
		} catch (IllegalArgumentException e) {
			// as expected
		}
		assertVisit(pvP, new TestVisitor2());
	}

	public void testQuantifiedExpression() {
		assertVisit(ff.makeQuantifiedExpression(Formula.CSET, mList(bid),
				p1, e1, null, Explicit));
	}

	public void testQuantifiedPredicate() {
		assertVisit(ff.makeQuantifiedPredicate(Formula.FORALL,
				mList(bid), p2, null));
	}

	public void testRelationalPredicate() {
		assertVisit(ff.makeRelationalPredicate(Formula.NOTSUBSETEQ, e1,
				e2, null));
	}

	public void testSetExtension() {
		assertVisit(ff.makeSetExtension(mList(e1, e2, e3), null));
	}

	public void testSimplePredicate() {
		assertVisit(ff.makeSimplePredicate(Formula.KFINITE, e1, null));
	}

	public void testUnaryExpression() {
		assertVisit(ff.makeUnaryExpression(Formula.POW, e1, null));
	}

	public void testUnaryPredicate() {
		assertVisit(ff.makeUnaryPredicate(Formula.NOT, p1, null));
	}

	public void testMultiplePredicate() throws Exception {
		assertVisit(ff.makeMultiplePredicate(Formula.KPARTITION, mList(e1, e2),
				null));
	}
}
