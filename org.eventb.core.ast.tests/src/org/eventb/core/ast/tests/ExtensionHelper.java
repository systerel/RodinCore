/*******************************************************************************
 * Copyright (c) 2012, 2016 Systerel and others.
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
import static org.eventb.core.ast.extension.ExtensionFactory.TWO_EXPRS;
import static org.eventb.core.ast.extension.ExtensionFactory.makeAllExpr;
import static org.eventb.core.ast.extension.ExtensionFactory.makeChildTypes;
import static org.eventb.core.ast.extension.ExtensionFactory.makeFixedArity;
import static org.eventb.core.ast.extension.ExtensionFactory.makeInfixKind;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;
import static org.eventb.core.ast.extension.StandardGroup.ARITHMETIC;
import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_PRED;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.CycleError;
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

	public static final IExpressionExtension DIRECT_PRODUCT = new IExpressionExtension() {

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public String getSyntaxSymbol() {
			return "§";
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			final Type leftType = childExprs[0].getType();
			final Type rightType = childExprs[1].getType();
			final Type alphaLeft = leftType.getSource();
			final Type alphaRight = rightType.getSource();
			if (alphaLeft == null || !alphaLeft.equals(alphaRight)) {
				return null; // incompatible types
			}
			final Type beta = leftType.getTarget();
			final Type gamma = rightType.getTarget();
			if (beta == null || gamma == null) {
				return null;
			}
			return mediator.getFactory().makeRelationalType(alphaLeft,
					mediator.getFactory().makeProductType(beta, gamma));
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			final FormulaFactory fac = proposedType.getFactory();
			final Type alpha = proposedType.getSource();
			if (alpha == null) {
				return false;
			}
			final Type target = proposedType.getTarget();
			if (!(target instanceof ProductType)) {
				return false;
			}
			final ProductType ptarget = (ProductType) target;
			final Type beta = ptarget.getLeft();
			final Type gamma = ptarget.getRight();
			final Expression left = childExprs[0];
			final Expression right = childExprs[1];
			return verifyType(left, fac.makeRelationalType(alpha, beta))
					&& verifyType(right, fac.makeRelationalType(alpha, gamma));
		}

		private boolean verifyType(Expression expr, Type proposedType) {
			final Type type = expr.getType();
			return type == null || type.equals(proposedType);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Type alpha = tcMediator.newTypeVariable();
			final Type beta = tcMediator.newTypeVariable();
			final Type gamma = tcMediator.newTypeVariable();
			final Type leftType = tcMediator.makeRelationalType(alpha, beta);
			final Type rightType = tcMediator.makeRelationalType(alpha, gamma);

			final Expression[] children = expression.getChildExpressions();
			tcMediator.sameType(children[0].getType(), leftType);
			tcMediator.sameType(children[1].getType(), rightType);

			final Type resultType = tcMediator.makeRelationalType(alpha,
					tcMediator.makeProductType(beta, gamma));
			return resultType;
		}

		@Override
		public String getGroupId() {
			return "My own group";
		}

		@Override
		public String getId() {
			return "direct product extension";
		}

		@Override
		public IExtensionKind getKind() {
			return BINARY_INFIX_EXPRESSION;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

	};
	public static final IExpressionExtension EMAX = new IExpressionExtension() {
		private static final String SYNTAX_SYMBOL = "emax";
		private static final String OPERATOR_ID = "Extension Maximum";

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return childExprs[0].getType();
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
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
			return ARITHMETIC.getId();
		}

		@Override
		public String getId() {
			return OPERATOR_ID;
		}

		@Override
		public IExtensionKind getKind() {
			return makePrefixKind(EXPRESSION, makeAllExpr(makeFixedArity(3)));
		}

		@Override
		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
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
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

	};
	public static final IPredicateExtension EXT_PRIME = new IPredicateExtension() {
		private static final String SYMBOL = "prime";
		private static final String ID = "Ext Prime";

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public String getSyntaxSymbol() {
			return SYMBOL;
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_PREDICATE;
		}

		@Override
		public String getId() {
			return ID;
		}

		@Override
		public String getGroupId() {
			return ATOMIC_PRED.getId();
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			final Expression child = predicate.getChildExpressions()[0];
			final Type childType = tcMediator.makePowerSetType(tcMediator
					.makeIntegerType());
			tcMediator.sameType(child.getType(), childType);
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public Object getOrigin() {
			return null;
		}
	};

	public static class Money implements IExpressionExtension {
		private static final String SYNTAX_SYMBOL = "€";
		private static final String OPERATOR_ID = "Money";

		private final boolean arithmetic;

		public Money(boolean arithmetic) {
			this.arithmetic = arithmetic;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return childExprs[0].getType();
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
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
			mediator.addAssociativity(getId());
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			if (!arithmetic) {
				return;
			}
			try {
				mediator.addPriority(getId(), "plus");
			} catch (CycleError e) {
				fail("A cycle error was detected"
						+ " when adding priorities for plus " + e);
			}
		}

		@Override
		public String getGroupId() {
			return arithmetic ? ARITHMETIC.getId() : OPERATOR_ID;
		}

		@Override
		public String getId() {
			return OPERATOR_ID;
		}

		@Override
		public IExtensionKind getKind() {
			return ASSOCIATIVE_INFIX_EXPRESSION;
		}

		@Override
		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
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
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

	}

	public static final IExpressionExtension MONEY = new Money(true);

	
	public static final IPredicateExtension DIFFERENT = new IPredicateExtension() {

		@Override
		public IExtensionKind getKind() {
			return makeInfixKind(PREDICATE, TWO_EXPRS, false);
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate, ITypeCheckMediator tcMediator) {
			final Expression[] childExprs = predicate.getChildExpressions();
			final Type alpha = tcMediator.newTypeVariable();
			tcMediator.sameType(alpha, childExprs[0].getType());
			tcMediator.sameType(alpha, childExprs[1].getType());
		}

		@Override
		public String getSyntaxSymbol() {
			return "<>";
		}

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula, IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public String getId() {
			return "different";
		}

		@Override
		public String getGroupId() {
			return getId();
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// none
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// none
		}

	};

}
