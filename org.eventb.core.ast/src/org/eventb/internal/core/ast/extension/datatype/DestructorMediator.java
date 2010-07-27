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

import java.util.ArrayList;
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
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IDestructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.parser.BMath;

/**
 * @author Nicolas Beauger
 * 
 */
public class DestructorMediator extends DatatypeMediator implements
		IDestructorMediator {

	private static class DestructorExtension implements IExpressionExtension {

		private final String name;
		private final String id;
		private final IExpressionExtension typeConstructor;
		private final IArgumentType returnType;
		private final List<ITypeParameter> typeParams;

		public DestructorExtension(String name, String id,
				IArgumentType returnType, IExpressionExtension typeConstructor,
				List<ITypeParameter> typeParams) {
			this.name = name;
			this.id = id;
			this.typeConstructor = typeConstructor;
			this.returnType = returnType;
			this.typeParams = typeParams;
		}

		@Override
		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public String getSyntaxSymbol() {
			return name;
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_EXPRESSION;
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
			final List<Type> typePrmVars = new ArrayList<Type>();
			final TypeParamInst instantiation = new TypeParamInst();
			for (ITypeParameter prm : typeParams) {
				final Type alpha = tcMediator.newTypeVariable();
				instantiation.put(prm, alpha);
				typePrmVars.add(alpha);
			}
			final Type argType = tcMediator.makeGenericType(typePrmVars,
					typeConstructor);
			final Expression[] children = expression.getChildExpressions();
			assert children.length == 1;
			tcMediator.sameType(argType, children[0].getType());
			return returnType
					.toType(tcMediator, typeConstructor, instantiation);
		}

		@Override
		public Type synthesizeType(ExtendedExpression expression, Type proposedType,
				ITypeMediator mediator) {
			final Expression[] children = expression.getChildExpressions();
			assert children.length == 1;
			final Type childType = children[0].getType();
			if (!(childType instanceof GenericType)) {
				return null;
			}
			final GenericType genChildType = (GenericType) childType;
			if (genChildType.getExprExtension() != typeConstructor) {
				return null;
			}

			final TypeParamInst instantiation = new TypeParamInst();
			final Type[] actualParams = genChildType.getTypeParameters();

			assert actualParams.length == typeParams.size();

			for (int i = 0; i < actualParams.length; i++) {
				instantiation.put(typeParams.get(i), actualParams[i]);
			}
			final Type resultType = returnType.toType(mediator,
					typeConstructor, instantiation);
			if (proposedType != null && !resultType.equals(proposedType)) {
				return null;
			}
			return proposedType;
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	private final IExpressionExtension typeConstructor;

	public DestructorMediator(String typeName,
			Map<String, ITypeParameter> typeParams,
			IExpressionExtension typeConstructor) {
		super(typeParams);
		this.typeConstructor = typeConstructor;
	}

	@Override
	public void addDestructor(final String name, final String id,
			final IArgumentType returnType) {
		final List<ITypeParameter> typePrmsList = new ArrayList<ITypeParameter>(
				typeParams.values());
		final IExpressionExtension destructor = new DestructorExtension(name,
				id, returnType, typeConstructor, typePrmsList);
		extensions.add(destructor);
	}

}
