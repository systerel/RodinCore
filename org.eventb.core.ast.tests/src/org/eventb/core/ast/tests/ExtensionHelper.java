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
import static org.eventb.core.ast.extension.ExtensionFactory.makeChildTypes;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;

import java.math.BigInteger;
import java.util.Collections;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * A class to help building extended formulae.
 */
public class ExtensionHelper {

	public static final IDatatypeExtension FOOBARTYPE = new IDatatypeExtension() {

		private static final String TYPE_NAME = "FooBar";
		private static final String TYPE_IDENTIFIER = "org.eventb.core.ast.tests.FooBar";

		@Override
		public String getTypeName() {
			return TYPE_NAME;
		}

		@Override
		public String getId() {
			return TYPE_IDENTIFIER;
		}

		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			mediator.addTypeParam("S");
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			final ITypeParameter typeS = mediator.getTypeParameter("S");
			final IArgumentType refS = mediator.newArgumentType(typeS);
			final IArgument bar = mediator.newArgument("bar", refS);
			mediator.addConstructor("foo", "FOO",
					Collections.singletonList(bar));
		}

	};

	private static abstract class BasicFormulaExtension implements
			IFormulaExtension {

		private static final ITypeDistribution CHILD_SIGNATURE = makeChildTypes(
				PREDICATE, EXPRESSION);

		private final String symbol;
		private final boolean wdStrict;
		private final IExtensionKind kind;

		public BasicFormulaExtension(String symbol, boolean wdStrict,
				FormulaType ftype) {
			this.symbol = symbol;
			this.wdStrict = wdStrict;
			this.kind = makePrefixKind(ftype, CHILD_SIGNATURE);
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

		protected abstract Type typeCheckChildExprs(Expression[] childExprs,
				ITypeCheckMediator tcMediator);

	}

	public static class AlphaPredicateExtension extends BasicFormulaExtension
			implements IPredicateExtension {

		public AlphaPredicateExtension() {
			super("α", true, PREDICATE);
		}

		@Override
		public ITypeDistribution getTypeDistribution() {
			return ExtensionFactory.makeChildTypes(PREDICATE, EXPRESSION);
		}

		@Override
		protected Type typeCheckChildExprs(Expression[] childExprs,
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
	
	public static IPredicateExtension getAlphaExtension() {
		return new AlphaPredicateExtension();
	}

}
