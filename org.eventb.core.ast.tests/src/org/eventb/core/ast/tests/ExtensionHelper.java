/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.extension.ExtensionFactory.NO_CHILD;
import static org.eventb.core.ast.extension.ExtensionFactory.makeChildTypes;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;

import java.math.BigInteger;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * A class to help building extended formulae.
 * 
 * @autor Thomas Muller
 */
public class ExtensionHelper {

	/*
	 * Abstract implementation of basic formula extension for tests
	 */
	private static abstract class BasicFormulaExtension implements
			IFormulaExtension {

		private final String symbol;
		private final boolean wdStrict;
		private final IExtensionKind kind;

		public BasicFormulaExtension(String symbol, boolean wdStrict,
				IExtensionKind extensionKind) {
			this.symbol = symbol;
			this.wdStrict = wdStrict;
			this.kind = extensionKind;
		}

		public abstract ITypeDistribution getTypeDistribution();

		@Override
		public String getSyntaxSymbol() {
			return symbol;
		}

		/*
		 * Return silly predicates that are easy to check from the outside:
		 * finite({1}) if WD strict, finite({0}) otherwise.
		 */
		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			final FormulaFactory ff = wdMediator.getFormulaFactory();
			return makeFiniteSingleton(wdStrict ? ONE : ZERO, ff);
		}

		private Predicate makeFiniteSingleton(BigInteger value,
				FormulaFactory ff) {
			return ff.makeSimplePredicate(KFINITE, ff.makeSetExtension(
					ff.makeIntegerLiteral(value, null), null), null);
		}

		@Override
		public boolean conjoinChildrenWD() {
			return wdStrict;
		}

		@Override
		public String getId() {
			return symbol;
		}

		@Override
		public String getGroupId() {
			return symbol;
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
			// None to add
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// None to add
		}

	}

	/**
	 * Implementation of a predicate operator α carrying two children: one
	 * predicate, and one expression of an arbitrary type.
	 * <p>
	 * It can be used to build predicates of the form "α(a ∈ A, a)".
	 * </p>
	 */
	public static class AlphaPredicateExtension extends BasicFormulaExtension
			implements IPredicateExtension {

		private static ITypeDistribution CHILD_SIGNATURE = makeChildTypes(
				PREDICATE, EXPRESSION);
		private static final IExtensionKind EXTENSION_KIND = makePrefixKind(
				PREDICATE, CHILD_SIGNATURE);

		public AlphaPredicateExtension() {
			super("α", true, EXTENSION_KIND);
		}

		@Override
		public ITypeDistribution getTypeDistribution() {
			return CHILD_SIGNATURE;
		}

		private Type typeCheckChildExprs(Expression[] childExprs,
				ITypeCheckMediator tcMediator) {
			final Type alpha = tcMediator.newTypeVariable();
			tcMediator.sameType(alpha, childExprs[0].getType());
			return alpha;
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			typeCheckChildExprs(predicate.getChildExpressions(), tcMediator);
		}

	}

	/**
	 * Returns an extension defining a predicate operator α carrying two
	 * children: one predicate, and one expression of an arbitrary type.
	 * <p>
	 * This operator can be used to build predicates of the form "α(a ∈ A, a)".
	 * </p>
	 */
	public static IPredicateExtension getAlphaExtension() {
		return new AlphaPredicateExtension();
	}

	/**
	 * Implementation of a generic operator with the same type profile as the
	 * generic identity operator.
	 */
	public static class GenericOperatorExtension extends BasicFormulaExtension
			implements IExpressionExtension {

		public GenericOperatorExtension() {
			super("▲", true, ATOMIC_EXPRESSION);
		}

		@Override
		public ITypeDistribution getTypeDistribution() {
			return NO_CHILD;
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
			final Type alpha = tcMediator.newTypeVariable();
			return tcMediator.makeRelationalType(alpha, alpha);
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	/**
	 * Returns a generic operator with the same type profile as the generic
	 * identity operator.
	 * <p>
	 * Example of usage: "f ◁ ▲".
	 * </p>
	 */
	public static IExpressionExtension getGenericOperatorExtension() {
		return new GenericOperatorExtension();
	}

}
