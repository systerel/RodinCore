/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.extension;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.eventb.core.ast.FormulaFactory.getCond;
import static org.eventb.core.ast.extension.ExtensionFactory.TWO_OR_MORE_EXPRS;
import static org.eventb.core.ast.extension.ExtensionFactory.makeAllPred;
import static org.eventb.core.ast.extension.ExtensionFactory.makeArity;
import static org.eventb.core.ast.extension.ExtensionFactory.makeChildTypes;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IArity.MAX_ARITY;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IArity;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * This class provides a formula factory to be used for testing extension
 * translations. The set of extensions of the factory has been designed to cover
 * all common cases of extension kinds:
 * <ul>
 * <li>and: takes a variable number of predicates and returns a predicate</li>
 * <li>belongs: takes a mix of expressions and predicate and returns a predicate
 * </li>
 * <li>union2: takes a variable number of expressions and returns an expression</li>
 * <li>union3: same kind as union2</li>
 * <li>empty: polymorphic atomic expression</li>
 * <li>cond: not WD-strict</li>
 * </ul>
 * 
 * @author Laurent Voisin
 * @author Thomas Muller
 */
public class Extensions {

	public static final FormulaFactory EXTS_FAC = FormulaFactory.getInstance(
			new And(), new Belongs(), new Union2(), new Union3(), new Empty(),
			getCond(), Real.EXT, RealZero.EXT, RealPlus.EXT, RealEmpty.EXT,
			FSet.EXT, CProd.EXT);

	/*
	 * Common implementation for extensions in this class.
	 */
	private static abstract class AbstractExtension implements
			IFormulaExtension {

		protected static final List<Type> NO_PARAMS = emptyList();

		private final String symbol;

		public AbstractExtension(String symbol) {
			this.symbol = symbol;
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
			return symbol;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}

	}

	private static class And extends AbstractExtension implements
			IPredicateExtension {

		public And() {
			super("∧∧");
		}

		@Override
		public IExtensionKind getKind() {
			final IArity arity = makeArity(0, MAX_ARITY);
			return makePrefixKind(PREDICATE, makeAllPred(arity));
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			// nothing to do
		}

	}

	private static class Belongs extends AbstractExtension implements
			IPredicateExtension {

		public Belongs() {
			super("belongs");
		}

		@Override
		public IExtensionKind getKind() {
			return makePrefixKind(PREDICATE,
					makeChildTypes(EXPRESSION, PREDICATE, EXPRESSION));
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			final Expression[] childExprs = predicate.getChildExpressions();
			final Type alpha = tcMediator.newTypeVariable();
			final Type powType = tcMediator.makePowerSetType(alpha);
			tcMediator.sameType(alpha, childExprs[0].getType());
			tcMediator.sameType(powType, childExprs[1].getType());
		}

	}

	private static class Union2 extends AbstractExtension implements
			IExpressionExtension {

		public Union2() {
			super("union2");
		}

		protected Union2(String symbol) {
			super(symbol);
		}

		@Override
		public IExtensionKind getKind() {
			return makePrefixKind(EXPRESSION, TWO_OR_MORE_EXPRS);
		}

		@Override
		public Type typeCheck(ExtendedExpression expr,
				ITypeCheckMediator tcMediator) {
			final Type alpha = tcMediator.newTypeVariable();
			final Type powType = tcMediator.makePowerSetType(alpha);
			for (final Expression child : expr.getChildExpressions()) {
				tcMediator.sameType(powType, child.getType());
			}
			return powType;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			final Type resultType = childExprs[0].getType();
			if (!(resultType instanceof PowerSetType)) {
				return null;
			}
			for (int i = 1; i < childExprs.length; i++) {
				if (!resultType.equals(childExprs[i].getType())) {
					return null;
				}
			}
			return resultType;
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			return proposedType.equals(synthesizeType(childExprs, childPreds,
					null));
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	private static class Union3 extends Union2 {

		public Union3() {
			super("union3");
		}

	}

	private static class Empty extends AbstractExtension implements
			IExpressionExtension {

		public Empty() {
			super("empty");
		}

		@Override
		public IExtensionKind getKind() {
			return ATOMIC_EXPRESSION;
		}

		@Override
		public Type typeCheck(ExtendedExpression expr,
				ITypeCheckMediator tcMediator) {
			final Type alpha = tcMediator.newTypeVariable();
			return tcMediator.makePowerSetType(alpha);
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return null;
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			return proposedType instanceof PowerSetType;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	/**
	 * Common abstraction for extensions about real numbers.
	 */
	private abstract static class AbstractRealExtension
			extends AbstractExtension {

		protected AbstractRealExtension(String symbol) {
			super(symbol);
		}

		protected Type realType(ITypeMediator mediator) {
			return mediator.makeParametricType(Real.EXT, NO_PARAMS);
		}

		protected Type realType(ITypeCheckMediator tcMediator) {
			return tcMediator.makeParametricType(Real.EXT, NO_PARAMS);
		}

		protected boolean isRealType(Type type) {
			if (!(type instanceof ParametricType)) {
				return false;
			}
			final ParametricType paramType = (ParametricType) type;
			return paramType.getExprExtension() == Real.EXT;
		}

	}

	/**
	 * An extension representing the set of all real numbers, simulating an
	 * axiomatic type defined by the Theory plug-in.
	 */
	public static class Real extends AbstractRealExtension
			implements IExpressionExtension {

		public static Real EXT = new Real();

		private Real() {
			super("ℝ");
		}

		@Override
		public IExtensionKind getKind() {
			return ATOMIC_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 0 && childPreds.length == 0);
			return mediator.makePowerSetType(realType(mediator));
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			assert (childExprs.length == 0 && childPreds.length == 0);
			final Type baseType = proposedType.getBaseType();
			return isRealType(baseType);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			return tcMediator.makePowerSetType(realType(tcMediator));
		}

		@Override
		public boolean isATypeConstructor() {
			return true;
		}

	}

	/**
	 * An extension representing the real number zero, simulating an
	 * axiomatic atomic operator defined by the Theory plug-in.
	 */
	public static class RealZero extends AbstractRealExtension
			implements IExpressionExtension {

		public static RealZero EXT = new RealZero();

		private RealZero() {
			super("zero");
		}

		@Override
		public IExtensionKind getKind() {
			return ATOMIC_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 0 && childPreds.length == 0);
			return realType(mediator);
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			assert (childExprs.length == 0 && childPreds.length == 0);
			return isRealType(proposedType);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			return realType(tcMediator);
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	/**
	 * An extension representing addition on real numbers, simulating an
	 * axiomatic operator defined by the Theory plug-in.
	 */
	private static class RealPlus extends AbstractRealExtension
			implements IExpressionExtension {

		public static RealPlus EXT = new RealPlus();

		private RealPlus() {
			super("+.");
		}

		@Override
		public IExtensionKind getKind() {
			return BINARY_INFIX_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 2 && childPreds.length == 0);
			return realType(mediator);
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			assert (childExprs.length == 2 && childPreds.length == 0);
			for (final Expression childExpr : childExprs) {
				if (!isRealType(childExpr.getType())) {
					return false;
				}
			}
			return isRealType(proposedType);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Type result = realType(tcMediator);
			final Expression[] childExprs = expression.getChildExpressions();
			for (final Expression childExpr : childExprs) {
				tcMediator.sameType(childExpr.getType(), result);
			}
			return result;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	/**
	 * An extension representing the empty set of real numbers, simulating an
	 * axiomatic operator defined by the Theory plug-in whose type is not
	 * directly an axiomatic type.
	 */
	private static class RealEmpty extends AbstractRealExtension
			implements IExpressionExtension {

		public static RealEmpty EXT = new RealEmpty();

		private RealEmpty() {
			super("emptyR");
		}

		@Override
		public IExtensionKind getKind() {
			return ATOMIC_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 0 && childPreds.length == 0);
			return mediator.makePowerSetType(realType(mediator));
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			assert (childExprs.length == 0 && childPreds.length == 0);
			return isRealType(proposedType.getBaseType());
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			return tcMediator.makePowerSetType(realType(tcMediator));
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	/**
	 * An extension representing finite sets, simulating an axiomatic type
	 * defined by the Theory plug-in. The purpose of this operator is two have a
	 * type constructor taking one type parameter.
	 */
	public static class FSet extends AbstractExtension
			implements IExpressionExtension {

		public static FSet EXT = new FSet();

		private FSet() {
			super("FIN");
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 1 && childPreds.length == 0);
			final List<Type> params = getParamTypes(childExprs);
			final Type ptype = mediator.makeParametricType(this, params);
			return mediator.makePowerSetType(ptype);
		}

		private List<Type> getParamTypes(Expression[] childExprs) {
			final List<Type> params = new ArrayList<Type>(childExprs.length);
			for (final Expression child : childExprs) {
				final Type childType = child.getType();
				if (childType == null) {
					return null;
				}
				final Type paramType = childType.getBaseType();
				if (paramType == null) {
					return null;
				}
				params.add(paramType);
			}
			return params;
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			assert (childExprs.length == 1 && childPreds.length == 0);
			final Type baseType = proposedType.getBaseType();
			if (!(baseType instanceof ParametricType)) {
				return false;
			}
			final ParametricType pType = (ParametricType) baseType;
			if (pType.getExprExtension() != this) {
				return false;
			}
			final Type[] typeParameters = pType.getTypeParameters();
			final List<Type> expectedParams = getParamTypes(childExprs);
			return asList(typeParameters).equals(expectedParams);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Expression[] childExprs = expression.getChildExpressions();
			// Children must be sets
			for (final Expression child : childExprs) {
				final Type alpha = tcMediator.newTypeVariable();
				final Type powType = tcMediator.makePowerSetType(alpha);
				tcMediator.sameType(powType, child.getType());
			}
			final List<Type> params = getParamTypes(childExprs);
			final Type ptype = tcMediator.makeParametricType(this, params);
			return tcMediator.makePowerSetType(ptype);
		}

		@Override
		public boolean isATypeConstructor() {
			return true;
		}

	}

	/**
	 * An extension representing a Cartesian product, simulating an axiomatic
	 * type defined by the Theory plug-in. The purpose of this operator is two
	 * have a type constructor taking two type parameters.
	 */
	public static class CProd extends AbstractExtension
			implements IExpressionExtension {

		public static CProd EXT = new CProd();

		private CProd() {
			super("**");
		}

		@Override
		public IExtensionKind getKind() {
			return BINARY_INFIX_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 2 && childPreds.length == 0);
			final List<Type> params = getParamTypes(childExprs);
			final Type ptype = mediator.makeParametricType(this, params);
			return mediator.makePowerSetType(ptype);
		}

		private List<Type> getParamTypes(Expression[] childExprs) {
			final List<Type> params = new ArrayList<Type>(childExprs.length);
			for (final Expression child : childExprs) {
				final Type childType = child.getType();
				if (childType == null) {
					return null;
				}
				final Type paramType = childType.getBaseType();
				if (paramType == null) {
					return null;
				}
				params.add(paramType);
			}
			return params;
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			assert (childExprs.length == 2 && childPreds.length == 0);
			final Type baseType = proposedType.getBaseType();
			if (!(baseType instanceof ParametricType)) {
				return false;
			}
			final ParametricType pType = (ParametricType) baseType;
			if (pType.getExprExtension() != this) {
				return false;
			}
			final Type[] typeParameters = pType.getTypeParameters();
			final List<Type> expectedParams = getParamTypes(childExprs);
			return asList(typeParameters).equals(expectedParams);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Expression[] childExprs = expression.getChildExpressions();
			// Children must be sets
			for (final Expression child : childExprs) {
				final Type alpha = tcMediator.newTypeVariable();
				final Type powType = tcMediator.makePowerSetType(alpha);
				tcMediator.sameType(powType, child.getType());
			}
			final List<Type> params = getParamTypes(childExprs);
			final Type ptype = tcMediator.makeParametricType(this, params);
			return tcMediator.makePowerSetType(ptype);
		}

		@Override
		public boolean isATypeConstructor() {
			return true;
		}

	}

}
