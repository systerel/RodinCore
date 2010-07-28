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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ParametricType;
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
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
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
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			final TypeInstantiation instantiation = checkAndGetInst(childExprs);
			if(instantiation == null) {
				return null;
			}
			return returnType.toType(mediator, instantiation);
		}

		@Override
		public boolean verifyType(Type proposedType,
				Expression[] childExprs, Predicate[] childPreds) {
			final TypeInstantiation instantiation = checkAndGetInst(childExprs);
			if (instantiation == null) {
				return false;
			}
			return returnType.verifyType(proposedType, instantiation);
		}

		private TypeInstantiation checkAndGetInst(Expression[] childExprs) {
			final Expression[] children = childExprs;
			assert children.length == 1;
			final Type childType = children[0].getType();
			if (!(childType instanceof ParametricType)) {
				return null;
			}
			final ParametricType genChildType = (ParametricType) childType;
			if (genChildType.getExprExtension() != typeConstructor) {
				return null;
			}

			final TypeInstantiation instantiation = new TypeInstantiation(typeConstructor);
			final Type[] actualParams = genChildType.getTypeParameters();

			assert actualParams.length == typeParams.size();

			for (int i = 0; i < actualParams.length; i++) {
				instantiation.put(typeParams.get(i), actualParams[i]);
			}
			return instantiation;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final List<Type> typePrmVars = new ArrayList<Type>();
			final TypeInstantiation instantiation = new TypeInstantiation(typeConstructor);
			for (ITypeParameter prm : typeParams) {
				final Type alpha = tcMediator.newTypeVariable();
				instantiation.put(prm, alpha);
				typePrmVars.add(alpha);
			}
			final Type argType = tcMediator.makeParametricType(typePrmVars,
					typeConstructor);
			final Expression[] children = expression.getChildExpressions();
			assert children.length == 1;
			tcMediator.sameType(argType, children[0].getType());
			return returnType.toType(tcMediator, instantiation);
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

	public DestructorMediator(IExpressionExtension typeConstructor,
			List<ITypeParameter> typeParams) {
		super(typeParams);
		this.typeConstructor = typeConstructor;
	}

	@Override
	public void addDestructor(final String name, final String id,
			final IArgumentType returnType) {
		final IExpressionExtension destructor = new DestructorExtension(name,
				id, returnType, typeConstructor, typeParams);
		extensions.add(destructor);
	}

}
