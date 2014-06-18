/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover.tests;

import static junit.framework.Assert.assertFalse;
import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.ast.extension.ExtensionFactory.TWO_OR_MORE_EXPRS;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.junit.Test;

/**
 * Tests the default operator position computed by
 * {@link DefaultPositionApplication}.
 * 
 * @author beauger
 */
@SuppressWarnings("restriction")
public class DefaultPositionApplicationTests {

	private static class ExpressionExtension implements IExpressionExtension {

		private final String symbol;
		private final IExtensionKind kind;

		public ExpressionExtension(String symbol, IExtensionKind kind) {
			this.symbol = symbol;
			this.kind = kind;
		}

		@Override
		public String getSyntaxSymbol() {
			return symbol;
		}

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public String getId() {
			return symbol;
		}

		@Override
		public String getGroupId() {
			return StandardGroup.BINOP.getId();
		}

		@Override
		public IExtensionKind getKind() {
			return kind;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			mediator.addAssociativity(getId());
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// none
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return null;
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			return true;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			return null;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	private static final ExpressionExtension ATOMIC = new ExpressionExtension(
			"atomic", IFormulaExtension.ATOMIC_EXPRESSION);
	private static final ExpressionExtension PREFIX = new ExpressionExtension(
			"prefix", IFormulaExtension.PARENTHESIZED_BINARY_EXPRESSION);
	private static final ExpressionExtension INFIX = new ExpressionExtension(
			"infix", IFormulaExtension.ASSOCIATIVE_INFIX_EXPRESSION);

	// Note: postfix extensions are not parseable at the time these tests are
	// being written, so we declare it as a parenthesized prefix operator to put
	// it into a formula factory, then change its kind afterwards and simulate a
	// parsed formula with postfix source location
	private static class PostfixExpressionExtension extends ExpressionExtension {

		private boolean postfix = false;
		private final IExtensionKind postfixKind = new ExtensionKind(
				Notation.POSTFIX, FormulaType.EXPRESSION, TWO_OR_MORE_EXPRS,
				false);

		public PostfixExpressionExtension(String symbol) {
			super(symbol, IFormulaExtension.ASSOCIATIVE_INFIX_EXPRESSION);
		}

		public void triggerPostfix() {
			postfix = true;
		}

		@Override
		public IExtensionKind getKind() {
			if (postfix) {
				return postfixKind;
			}
			return super.getKind();
		}

	}

	private static final PostfixExpressionExtension POSTFIX = new PostfixExpressionExtension(
			"postfix");

	private final FormulaFactory factory = FormulaFactory.getInstance(ATOMIC,
			PREFIX, INFIX, POSTFIX);

	private void doPositionTest(String predStr, String pos, int expStart,
			int expEnd) {
		final IParseResult result = factory.parsePredicate(predStr, null);
		assertFalse(result.hasProblem());
		final Predicate pred = result.getParsedPredicate();

		doPositionTest(pred, predStr, pos, expStart, expEnd);
	}

	private void doPositionTest(Predicate pred, String predStr, String pos,
			int expStart, int expEnd) {
		final DefaultPositionApplication posAppli = new DefaultPositionApplication(
				null, makePosition(pos));
		final Point expected = new Point(expStart, expEnd);
		final Point actual = posAppli.getOperatorPosition(pred, predStr);

		assertEquals(expected, actual);
	}

	@Test
	public void associativePred() throws Exception {
		doPositionTest("⊤ ∧ ⊥", "", 2, 3);
		doPositionTest("⊤∧⊥∧⊥", "", 1, 2);
	}

	@Test
	public void binaryPred() throws Exception {
		doPositionTest("⊤\t⇒\t⊥", "", 2, 3);
		doPositionTest("⊤ ∧(⊤⇒⊥)", "1", 5, 6);
	}

	@Test
	public void literalPred() throws Exception {
		doPositionTest("⊤", "", 0, 1);
		doPositionTest("⊤ ∧ ⊥", "0", 0, 1);
		doPositionTest("⊤ ∧ ⊥", "1", 4, 5);
	}

	@Test
	public void multiplePred() throws Exception {
		doPositionTest("partition(S,{0})", "", 0, 9);
		doPositionTest("⊤ ∧ partition(S,{0})", "1", 4, 13);
	}

	@Test
	public void quantifiedPred() throws Exception {
		doPositionTest("∀x·∃y·y>x", "", 0, 1);
		doPositionTest("∀x·∃y·y>x", "1", 3, 4);
		doPositionTest("⊤ ∧ (∀x·∃y·y>x)", "1", 5, 6);
		doPositionTest("⊤ ∧ (∀x·∃y·y>x)", "1.1", 8, 9);
	}

	@Test
	public void relationalPred() throws Exception {
		doPositionTest("0<1", "", 1, 2);
		doPositionTest("⊤ ∧ 0<1", "1", 5, 6);
	}

	@Test
	public void simplePred() throws Exception {
		doPositionTest("finite({1})", "", 0, 6);
		doPositionTest("⊤ ∧ finite({1})", "1", 4, 10);
	}

	@Test
	public void unaryPred() throws Exception {
		doPositionTest("¬⊤", "", 0, 1);
		doPositionTest("⊤ ∧ ¬⊥", "1", 4, 5);
	}

	@Test
	public void associativeExpr() throws Exception {
		doPositionTest("S= {1} ∪ {2}", "1", 7, 8);
		doPositionTest("S= {1} ∪ {2} ∪ {3}", "1", 7, 8);
	}

	@Test
	public void atomicExpr() throws Exception {
		doPositionTest("S= ℕ", "1", 3, 4);
	}

	@Test
	public void binaryExpr() throws Exception {
		doPositionTest("x= 0↦1", "1", 4, 5);
	}

	@Test
	public void boolExpr() throws Exception {
		doPositionTest("x= bool(⊤)", "1", 3, 7);
		doPositionTest("x= bool ( ⊤ )", "1", 3, 7);
	}

	@Test
	public void boundIdent() throws Exception {
		doPositionTest("∀x·∃y·y>x", "0", 1, 2);
		doPositionTest("∀x·∃y·y>x", "1.0", 4, 5);
	}

	@Test
	public void freeIdent() throws Exception {
		doPositionTest("x=y+1", "0", 0, 1);
		doPositionTest("x=y+1", "1.0", 2, 3);
	}

	@Test
	public void integerLiteral() throws Exception {
		doPositionTest("0=0+0", "0", 0, 1);
		doPositionTest("0=0+0", "1.0", 2, 3);
	}

	@Test
	public void quantifiedExpr() throws Exception {
		doPositionTest("(⋃x∣x⊆ℕ)=∅", "0", 1, 2);
		doPositionTest("(⋃x·x⊆ℕ∣x)=∅", "0", 1, 2);
		doPositionTest("(λx·x∈ℕ∣x+1)=∅", "0", 1, 2);
	}

	@Test
	public void setExtension() throws Exception {
		doPositionTest("{1}=∅", "0", 0, 1);
		doPositionTest("⊤ ∧ {1}=∅", "1.0", 4, 5);
	}

	@Test
	public void unaryExpr() throws Exception {
		doPositionTest("card({1})=0", "0", 0, 4);
		doPositionTest("⊤ ∧ card({1})=0", "1.0", 4, 8);
		doPositionTest("ℙ({1}) = ∅", "0", 0, 1);
		doPositionTest("⊤ ∧ ℙ({1}) = ∅", "1.0", 4, 5);
	}

	@Test
	public void extendedFormula() throws Exception {
		doPositionTest("atomic=0", "0", 0, 6);
		doPositionTest("⊤ ∧ atomic=0", "1.0", 4, 10);
		doPositionTest("prefix(11,22)=0", "0", 0, 6);
		doPositionTest("⊤ ∧ prefix(11,22)=0", "1.0", 4, 10);
		doPositionTest("11 infix 22 infix 33 =0", "0", 3, 8);
	}

	@Test
	public void extendedPostfix() throws Exception {
		POSTFIX.triggerPostfix();
		
		// "(10,10)postfix=0"
		final Predicate pred = factory.makeRelationalPredicate(Formula.EQUAL,
				factory.makeExtendedExpression(POSTFIX, Arrays
						.<Expression> asList(factory.makeIntegerLiteral(
								BigInteger.TEN, new SourceLocation(1, 2)),
								factory.makeIntegerLiteral(BigInteger.TEN,
										new SourceLocation(4, 5))), Arrays
						.<Predicate> asList(), new SourceLocation(0, 13)),
				factory.makeIntegerLiteral(BigInteger.ZERO, null), null);

		doPositionTest(pred, "(10,10)postfix=0", "0", 7, 14);
	}
}
