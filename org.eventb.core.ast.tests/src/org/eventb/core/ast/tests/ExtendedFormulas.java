/*******************************************************************************
 * Copyright (c) 2011, 2022 Systerel and others.
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
import static org.eventb.core.ast.FormulaFactory.getInstance;
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
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPredicateExtension2;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * Common implementation of some formula extensions for use in tests.
 * 
 * @author Laurent Voisin
 */
public class ExtendedFormulas {

	// Code common to both predicate and expression extensions
	private static class CommonExtension implements IFormulaExtension {

		private static final ITypeDistribution CHILD_SIGNATURE = makeChildTypes(
				PREDICATE, EXPRESSION, PREDICATE, EXPRESSION);

		private final String symbol;
		private final boolean wdStrict;
		private final IExtensionKind kind;

		public CommonExtension(String symbol, boolean wdStrict,
				FormulaType ftype) {
			this(symbol, wdStrict, ftype, makePrefixKind(ftype, CHILD_SIGNATURE));
		}

		public CommonExtension(String symbol, boolean wdStrict,
				FormulaType ftype, IExtensionKind kind) {
			this.symbol = symbol;
			this.wdStrict = wdStrict;
			this.kind = kind;
		}
		
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
		
		protected boolean verifyChildTypes(Type proposedType,
				Expression[] childExprs) {
			if (proposedType == null) {
				return false;
			}
			for (Expression child : childExprs) {
				if (!proposedType.equals(child.getType())) {
					return false;
				}
			}
			return true;
		}

		protected Type typeCheckChildExprs(Expression[] childExprs,
				ITypeCheckMediator tcMediator) {
			final Type alpha = tcMediator.newTypeVariable();
			for (Expression child : childExprs) {
				tcMediator.sameType(alpha, child.getType());
			}
			return alpha;
		}

	}

	/**
	 * Implementation of a predicate extension which takes the form of a prefix
	 * symbol followed by two predicates and two expressions within parentheses.
	 * The children are alternated following the pattern predicate, expression,
	 * predicate, expression. Both expressions must bear the same type.
	 * 
	 * The symbol and WD strictness are specified when constructing instances.
	 */
	public static class PredicateExtension extends CommonExtension implements
			IPredicateExtension {

		public PredicateExtension(String symbol, boolean wdStrict) {
			super(symbol, wdStrict, PREDICATE);
		}

		public PredicateExtension(String symbol, boolean wdStrict, IExtensionKind kind) {
			super(symbol, wdStrict, PREDICATE, kind);
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			typeCheckChildExprs(predicate.getChildExpressions(), tcMediator);
		}
	}

	public static class PredicateExtension2 extends PredicateExtension implements
			IPredicateExtension2 {

		public PredicateExtension2(String symbol, boolean wdStrict) {
			super(symbol, wdStrict);
		}

		public PredicateExtension2(String symbol, boolean wdStrict, IExtensionKind kind) {
			super(symbol, wdStrict, kind);
		}

		// All children must bear the same type.
		@Override
		public boolean verifyType(Expression[] childExprs, Predicate[] childPreds) {
			return childExprs[0].getType().equals(childExprs[1].getType());
		}
	}

	/**
	 * Implementation of a predicate extension which takes the form of a prefix
	 * symbol followed by two predicates and two expressions within parentheses.
	 * The children are alternated following the pattern predicate, expression,
	 * predicate, expression. Both expressions must bear the same type which is
	 * also the resulting type.
	 * 
	 * The symbol and WD strictness are specified when constructing instances.
	 */
	public static class ExpressionExtension extends CommonExtension implements
			IExpressionExtension {
		
		public ExpressionExtension(String symbol, boolean wdStrict) {
			super(symbol, wdStrict, EXPRESSION);
		}

		public ExpressionExtension(String symbol, boolean wdStrict, IExtensionKind kind) {
			super(symbol, wdStrict, EXPRESSION, kind);
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return childExprs[0].getType();
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			return verifyChildTypes(proposedType, childExprs);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			return typeCheckChildExprs(expression.getChildExpressions(),
					tcMediator);
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	// associative infix expression in independent parse group
	public static class AssociativeExpressionExtension extends ExpressionExtension {

		public AssociativeExpressionExtension(String symbol, boolean wdStrict) {
			super(symbol, wdStrict, ASSOCIATIVE_INFIX_EXPRESSION);
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			super.addCompatibilities(mediator);
			mediator.addAssociativity(getId());
		}

	}
	
	/**
	 * Old version of fooS with obsolescent interface.
	 */
	public static final IPredicateExtension old_fooS = new PredicateExtension(
			"old_fooS", true);

	/**
	 * WD strict predicate extension with four children.
	 * <p>
	 * Example: <code>fooS(⊤, 1, ⊥, 2)</code>
	 * </p>
	 */
	public static final IPredicateExtension fooS = new PredicateExtension(
			"fooS", true);

	/**
	 * Non WD strict (lazy) predicate extension with four children.
	 * <p>
	 * Example: <code>fooL(⊤, 1, ⊥, 2)</code>
	 * </p>
	 */
	public static final IPredicateExtension fooL = new PredicateExtension(
			"fooL", false);

	/**
	 * WD strict expression extension with four children.
	 * <p>
	 * Example: <code>barS(⊤, 1, ⊥, 2)</code>
	 * </p>
	 */
	public static final IExpressionExtension barS = new ExpressionExtension(
			"barS", true);

	/**
	 * Non WD strict (lazy) expression extension with four children.
	 * <p>
	 * Example: <code>barL(⊤, 1, ⊥, 2)</code>
	 * </p>
	 */
	public static final IExpressionExtension barL = new ExpressionExtension(
			"barL", false);

	public static final IExpressionExtension asso = 
			new AssociativeExpressionExtension("asso", true);

	/**
	 * Formula factory with the extensions described above.
	 */
	public static final FormulaFactory EFF = getInstance(old_fooS, fooS, fooL, barS,
			barL, asso);

}
