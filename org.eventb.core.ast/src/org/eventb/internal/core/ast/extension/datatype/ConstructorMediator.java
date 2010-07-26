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
package org.eventb.internal.core.ast.extension.datatype;

import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.GenericType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.parser.BMath;

/**
 * @author Nicolas Beauger
 * 
 */
public class ConstructorMediator extends DatatypeMediator implements
		IConstructorMediator {

	private static class ConstructorExtension implements IExpressionExtension {

		private final String name;
		private final String id;
		private final IExpressionExtension typeCons;
		private final List<IArgumentType> argumentTypes;
		private final List<ITypeParameter> typeParams;

		public ConstructorExtension(String name, String id,
				List<IArgumentType> argumentTypes,
				IExpressionExtension typeCons, List<ITypeParameter> typeParams) {
			this.name = name;
			this.id = id;
			this.typeCons = typeCons;
			this.argumentTypes = argumentTypes;
			this.typeParams = typeParams;
		}

		@Override
		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public String getSyntaxSymbol() {
			return name;
		}

		@Override
		public IExtensionKind getKind() {
			return new IFormulaExtension.PrefixKind(EXPRESSION,
					argumentTypes.size(), EXPRESSION);
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getGroupId() {
			return BMath.BOUND_UNARY;
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
		public Type typeCheck(ITypeCheckMediator tcMediator,
				ExtendedExpression expression) {
			final TypeParamInst instantiation = new TypeParamInst();

			final List<Type> typeParamVars = new ArrayList<Type>();
			for (ITypeParameter prm : typeParams) {
				final Type alpha = tcMediator.newTypeVariable();
				typeParamVars.add(alpha);
				instantiation.put(prm, alpha);
			}
			final Expression[] children = expression.getChildExpressions();
			assert children.length == argumentTypes.size();
			for (int i = 0; i < children.length; i++) {
				final Type childType = children[i].getType();
				final IArgumentType argType = argumentTypes.get(i);
				final Type type = argType.toType(tcMediator, typeCons,
						instantiation);
				tcMediator.sameType(childType, type);
			}
			return tcMediator.makeGenericType(typeParamVars, typeCons);
		}

		@Override
		public Type getType(ExtendedExpression expression, Type proposedType,
				ITypeMediator mediator) {
			if (proposedType == null) {
				return null;
			}
			assert proposedType instanceof GenericType;
			final GenericType genType = (GenericType) proposedType;
			assert genType.getExprExtension() == typeCons;

			final TypeParamInst instantiation = new TypeParamInst();

			// instantiate type parameters with those of proposed type
			final Type[] actualTypePrms = genType.getTypeParameters();
			assert actualTypePrms.length == typeParams.size();
			for (int i = 0; i < typeParams.size(); i++) {
				instantiation.put(typeParams.get(i), actualTypePrms[i]);
			}

			// verify child type compatibility
			final Expression[] children = expression.getChildExpressions();
			assert children.length == argumentTypes.size();
			for (int i = 0; i < children.length; i++) {
				final Type childType = children[i].getType();
				final IArgumentType argType = argumentTypes.get(i);
				final Type instType = argType.toType(mediator, typeCons,
						instantiation);
				if (!instType.equals(childType)) {
					return null;
				}
			}
			return proposedType;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}
	}

	private final IExpressionExtension typeConstructor;

	// FIXME we may wish to have priorities and custom type check methods, thus
	// fully implementing methods from IExpressionExtension

	public ConstructorMediator(String typeName,
			Map<String, ITypeParameter> typeParams,
			IExpressionExtension typeConstructor) {
		super(typeParams);
		this.typeConstructor = typeConstructor;
	}

	@Override
	public void addConstructor(final String name, final String id) {
		addConstructor(name, id, Collections.<IArgumentType> emptyList());
	}

	@Override
	public void addConstructor(final String name, final String id,
			final List<IArgumentType> argumentTypes) {
		final List<ITypeParameter> typePrmsList = new ArrayList<ITypeParameter>(
				typeParams.values());
		final IExpressionExtension constructor = new ConstructorExtension(name,
				id, argumentTypes, typeConstructor, typePrmsList);
		extensions.add(constructor);
	}

}
