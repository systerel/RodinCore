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

import static org.eventb.core.ast.extension.IFormulaExtension.ATOMIC_EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension.PrefixKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 * 
 */
public class TypeConstrMediator implements ITypeConstructorMediator {

	private final List<ITypeParameter> typeParams = new ArrayList<ITypeParameter>();
	private final IDatatypeExtension datatype;

	public TypeConstrMediator(IDatatypeExtension datatype) {
		this.datatype = datatype;
	}

	@Override
	public void addTypeParam(String name) {
		typeParams.add(new TypeParam(name));
	}

	public List<ITypeParameter> getTypeParams() {
		return Collections.unmodifiableList(typeParams);
	}

	public ITypeExpressionExtension getTypeConstructor() {
		final String typeName = datatype.getTypeName();
		final String id = datatype.getId();
		final String groupId = datatype.getGroupId();
		final IExtensionKind kind = computeKind();
		
		return new ITypeExpressionExtension() {
			
			@Override
			public Predicate getWDPredicate(IWDMediator wdMediator,
					IExtendedFormula formula) {
				return wdMediator.makeTrueWD();
			}
			
			@Override
			public String getSyntaxSymbol() {
				return typeName;
			}
			
			@Override
			public IExtensionKind getKind() {
				return kind;
			}
			
			@Override
			public String getId() {
				return id;
			}
			
			@Override
			public String getGroupId() {
				return groupId;
			}
			
			@Override
			public void addPriorities(IPriorityMediator mediator) {
				// no priority
			}
			
			@Override
			public void addCompatibilities(ICompatibilityMediator mediator) {
				// no priority
			}
			
			@Override
			public Type typeCheck(ITypeCheckMediator tcMediator,
					ExtendedExpression expression) {
				final List<Type> typePrms = new ArrayList<Type>();
				for (Expression child:expression.getChildExpressions()) {
					final Type alpha = tcMediator.newTypeVariable();
					tcMediator.sameType(child.getType(), tcMediator.makePowerSetType(alpha));
					typePrms.add(alpha);
				}
				return tcMediator.makePowerSetType(tcMediator.makeGenericType(typePrms, this));
			}
			
			@Override
			public Type getType(ITypeMediator mediator, ExtendedExpression expression) {
				final List<Type> typePrms = new ArrayList<Type>();
				for (Expression child:expression.getChildExpressions()) {
					if (!child.isTypeChecked()) {
						return null;
					}
					final Type alpha = child.getType().getBaseType();
					if (alpha == null) {
						return null;
					}
					typePrms.add(alpha);
				}
				return mediator.makePowerSetType(mediator.makeGenericType(typePrms, this));
			}

			@Override
			public boolean conjoinChildrenWD() {
				return true;
			}
		};
	}
	
	private IExtensionKind computeKind() {
		if (typeParams.isEmpty()) {
			return ATOMIC_EXPRESSION;
		}
		return new PrefixKind(EXPRESSION, typeParams.size(), EXPRESSION);
	}
}
