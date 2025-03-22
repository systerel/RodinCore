/*******************************************************************************
 * Copyright (c) 2014, 2025 Systerel and others.
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
import static org.eventb.core.ast.extension.StandardGroup.CLOSED;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IArity;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension2;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.tests.DatatypeParser;

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
 * In addition, this class defines the "either" datatype and some operators
 * whose result type is not completely defined by their arguments, and thus that
 * would need oftype annotations to get properly type-checked in all cases.
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
			IPredicateExtension2 {

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

		@Override
		public boolean verifyType(Expression[] childExprs, Predicate[] childPreds) {
			return true;
		}

	}

	private static class Belongs extends AbstractExtension implements
			IPredicateExtension2 {

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

		@Override
		public boolean verifyType(Expression[] childExprs, Predicate[] childPreds) {
			return childExprs[1].getType().getBaseType().equals(childExprs[0].getType());
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
	public static class RealPlus extends AbstractRealExtension
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
	 * defined by the Theory plug-in. The purpose of this operator is to have a
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
			var typeParam = extractTypeParameter(childExprs[0]);
			if (typeParam == null) {
				return null;
			}
			var ptype = mediator.makeParametricType(this, asList(typeParam));
			return mediator.makePowerSetType(ptype);
		}

		/* Extracts the type parameter from the type of the child if possible. */
		private Type extractTypeParameter(Expression childExpr) {
			var childType = childExpr.getType();
			if (childType == null) {
				return null;
			}
			return childType.getBaseType();
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			assert (childExprs.length == 1 && childPreds.length == 0);
			final Type baseType = proposedType.getBaseType();
			if (!(baseType instanceof ParametricType pType)) {
				return false;
			}
			if (pType.getExprExtension() != this) {
				return false;
			}
			final Type[] typeParameters = pType.getTypeParameters();
			assert typeParameters.length == 1;
			return typeParameters[0].equals(extractTypeParameter(childExprs[0]));
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Expression[] childExprs = expression.getChildExpressions();
			// Child must be a set
			final Type alpha = tcMediator.newTypeVariable();
			final Type powType = tcMediator.makePowerSetType(alpha);
			tcMediator.sameType(powType, childExprs[0].getType());

			var ptype = tcMediator.makeParametricType(this, asList(alpha));
			return tcMediator.makePowerSetType(ptype);
		}

		@Override
		public boolean isATypeConstructor() {
			return true;
		}

	}

	/**
	 * An extension representing a Cartesian product, simulating an axiomatic
	 * type defined by the Theory plug-in. The purpose of this operator is to
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

			// Put children constraints and collect type parameters
			final Type[] typeParams = new Type[childExprs.length];
			for (int i = 0; i < childExprs.length; i++) {
				final Type alpha = tcMediator.newTypeVariable();
				final Type powType = tcMediator.makePowerSetType(alpha);
				tcMediator.sameType(powType, childExprs[i].getType());
				typeParams[i] = alpha;
			}

			final Type ptype = tcMediator.makeParametricType(this, asList(typeParams));
			return tcMediator.makePowerSetType(ptype);
		}

		@Override
		public boolean isATypeConstructor() {
			return true;
		}

	}

	/**
	 * A formula factory containing the "either" datatype, together with the
	 * "return" and "++" operators.
	 */
	public static final FormulaFactory EITHER_FAC;

	public static final IDatatype EITHER_DT = DatatypeParser.parse(FormulaFactory.getDefault(), //
			"either[A,B] ::= Left[getLeft: A] || Right[getRight: B]");

	static {
		var extns = new LinkedHashSet<>(EITHER_DT.getExtensions());
		extns.add(Return.EXT);
		extns.add(LeftPlus.EXT);
		EITHER_FAC = FormulaFactory.getInstance(extns);
	}

	/**
	 * An alternative to the Right constructor which is typically used in the either
	 * Monad of Haskell: "return :: a -> Either e a".
	 */
	public static class Return extends AbstractExtension implements IExpressionExtension {

		public static Return EXT = new Return();

		private Return() {
			super("return");
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_EXPRESSION;
		}

		@Override
		public String getGroupId() {
			return CLOSED.getId();
		}

		@Override
		public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 1 && childPreds.length == 0);
			// Cannot invent the first type parameter.
			return null;
		}

		/*
		 * The proposed type must be of the form "either(A, B)" where "B" is the type of
		 * the child.
		 */
		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs, Predicate[] childPreds) {
			assert (childExprs.length == 1 && childPreds.length == 0);
			if (!(proposedType instanceof ParametricType pType)) {
				return false;
			}
			if (pType.getExprExtension() != EITHER_DT.getTypeConstructor()) {
				return false;
			}
			final Type[] params = pType.getTypeParameters();
			final Type childType = childExprs[0].getType();
			return params[1].equals(childType);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression, ITypeCheckMediator tcMediator) {
			final Type alpha = tcMediator.newTypeVariable();
			final Type beta = tcMediator.newTypeVariable();

			final Expression[] childExprs = expression.getChildExpressions();
			tcMediator.sameType(beta, childExprs[0].getType());

			return tcMediator.makeParametricType(EITHER_DT.getTypeConstructor(), //
					asList(alpha, beta));
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}
	}

	/**
	 * A strange binary operator that is equivalent to "Left(x + y)". The purpose is
	 * to test oftype annotation on infix operators.
	 */
	public static class LeftPlus extends AbstractExtension implements IExpressionExtension {

		public static LeftPlus EXT = new LeftPlus();

		private LeftPlus() {
			super("++");
		}

		@Override
		public IExtensionKind getKind() {
			return BINARY_INFIX_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds, ITypeMediator mediator) {
			assert (childExprs.length == 2 && childPreds.length == 0);
			// Cannot invent the second type parameter.
			return null;
		}

		/*
		 * The proposed type must be of the form "either(ℤ, B)".
		 */
		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs, Predicate[] childPreds) {
			assert (childExprs.length == 2 && childPreds.length == 0);
			for (final Expression childExpr : childExprs) {
				if (!(childExpr.getType() instanceof IntegerType)) {
					return false;
				}
			}

			if (!(proposedType instanceof ParametricType pType)) {
				return false;
			}
			if (pType.getExprExtension() != EITHER_DT.getTypeConstructor()) {
				return false;
			}
			final Type[] params = pType.getTypeParameters();
			return params[0] instanceof IntegerType;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression, ITypeCheckMediator tcMediator) {
			final Expression[] childExprs = expression.getChildExpressions();
			final Type intType = tcMediator.makeIntegerType();
			for (final Expression childExpr : childExprs) {
				tcMediator.sameType(intType, childExpr.getType());
			}

			final Type alpha = tcMediator.newTypeVariable();
			return tcMediator.makeParametricType(EITHER_DT.getTypeConstructor(), //
					asList(intType, alpha));
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}
	}

}
