/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * Test class for verifying extension WD computation through a WDMediator.
 * <p>
 * This class is NOT intended to be committed to the trunk, but rather merged
 * with other WD tests after integrating external WD computation.
 * </p>
 * <p>As such, it is not referenced in the AST test suite.</p>
 * 
 * @author Nicolas Beauger
 * 
 */
public class TestExtensionWD extends AbstractTests {

	private static final Set<Predicate> NO_PREDICATE = Collections.emptySet();
	private static final LiteralPredicate LIT_BFALSE = ff.makeLiteralPredicate(
			Formula.BFALSE, null);
	private static final PowerSetType POW_S_TYPE = ff.makePowerSetType(ff.makeGivenType("S"));
	private static final FreeIdentifier FRID_S = ff.makeFreeIdentifier("S", null, POW_S_TYPE);
	private static final UnaryExpression CARD_S = ff.makeUnaryExpression(Formula.KCARD, FRID_S, null);
	private static final SimplePredicate FINITE_S = ff.makeSimplePredicate(Formula.KFINITE, FRID_S, null);
	protected static final IntegerLiteral ZERO = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	private static final IntegerType INT_TYPE = ff.makeIntegerType();
	private static final FreeIdentifier FRID_B = ff.makeFreeIdentifier("B", null, INT_TYPE);
	private static final FreeIdentifier FRID_A = ff.makeFreeIdentifier("A", null,	INT_TYPE);
	
	private static class Emax implements IExpressionExtension {
		private static final String SYNTAX_SYMBOL = "emax";
		private static final String OPERATOR_ID = "Extension Maximum";
		private final boolean conjoinChildrenWD;

		public Emax(boolean conjoinChildrenWD) {
			this.conjoinChildrenWD = conjoinChildrenWD;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return childExprs[0].getType();
		}

		@Override
		public boolean verifyType(Type proposedType,
				Expression[] childExprs, Predicate[] childPreds) {
			for (Expression child : childExprs) {
				final Type childType = child.getType();
				if (!(childType instanceof IntegerType)) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Expression[] children = expression.getChildExpressions();
			final Type resultType = tcMediator.makeIntegerType();
			for (int i = 0; i < children.length; i++) {
				tcMediator.sameType(children[i].getType(), resultType);
			}
			return resultType;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			mediator.addCompatibility(getId(), getId());
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority to add
		}

		@Override
		public String getGroupId() {
			return "Arithmetic";
		}

		@Override
		public String getId() {
			return OPERATOR_ID;
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_BINARY_EXPRESSION;
		}

		@Override
		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
		}

		// BTRUE if the first child is an integer literal
		// else BFALSE 
		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			final Expression firstChild = formula.getChildExpressions()[0];
			
			final FormulaFactory factory = wdMediator.getFormulaFactory();
			if (firstChild.getTag() == Formula.INTLIT) {
				return factory.makeLiteralPredicate(Formula.BTRUE, null);
			} else {
				return factory.makeLiteralPredicate(Formula.BFALSE, null);
			}
		}

		@Override
		public boolean conjoinChildrenWD() {
			return conjoinChildrenWD;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

	}

	private static final IExpressionExtension EMAX = new Emax(true);
	
	public void testSimpleWD() throws Exception {
		final Predicate expectedWD = LIT_BFALSE;

		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(EMAX));
		final Expression emax = extFac.makeExtendedExpression(EMAX, Arrays
				.<Expression> asList(
						// first child is an identifier => WD = false
						FRID_A,
						FRID_B),
				NO_PREDICATE, null);

		final Predicate actualWD = emax.getWDPredicate(extFac);
		assertEquals("unexpected WD predicate", expectedWD, actualWD);
	}

	public void testWithChildWD() throws Exception {
		final Predicate expectedWD = ff.makeAssociativePredicate(Formula.LAND,
				Arrays.asList(LIT_BFALSE, FINITE_S), null);

		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(EMAX));
		final Expression emax = extFac.makeExtendedExpression(EMAX, Arrays
				.<Expression> asList(
						// first child is an identifier => WD = false
						FRID_A,
						CARD_S),
				NO_PREDICATE, null);

		final Predicate actualWD = emax.getWDPredicate(extFac);
		assertEquals("unexpected WD predicate", expectedWD, actualWD);
	}
	
	public void testWithChildWDAndSimplification() throws Exception {
		final Predicate expectedWD = FINITE_S;

		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(EMAX));
		final Expression emax = extFac.makeExtendedExpression(EMAX, Arrays
				.<Expression> asList(
						// first child is an integer literal => WD = true
						ZERO,
						CARD_S),
				NO_PREDICATE, null);

		final Predicate actualWD = emax.getWDPredicate(extFac);
		assertEquals("unexpected WD predicate", expectedWD, actualWD);
	}
	
	private static final IExpressionExtension EMAX_NO_CONJ = new Emax(false);

	public void testNoConjChildrenWD() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(EMAX_NO_CONJ));
		final Expression emax = extFac.makeExtendedExpression(EMAX_NO_CONJ, Arrays
				.<Expression> asList(
						// first child is an identifier => WD = false
						FRID_A,
						CARD_S),
				NO_PREDICATE, null);

		final Predicate actualWD = emax.getWDPredicate(extFac);
		// conjoin children WD is disabled => no finite(S)
		assertEquals("unexpected WD predicate", LIT_BFALSE, actualWD);
	}
}
