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

import static org.eventb.core.ast.extension.IFormulaExtension.ExtensionKind.ATOMIC_EXPRESSION;
import static org.eventb.core.ast.extension.IFormulaExtension.ExtensionKind.PARENTHESIZED_EXPRESSION_1;

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
			// FIXME specify precondition of number of arguments = typeParams.size()
			
			public Predicate getWDPredicate(IWDMediator wdMediator,
					IExtendedFormula formula) {
				return wdMediator.makeTrueWD();
			}
			
			public String getSyntaxSymbol() {
				return typeName;
			}
			
			public IExtensionKind getKind() {
				return kind;
			}
			
			public String getId() {
				return id;
			}
			
			public String getGroupId() {
				return groupId;
			}
			
			public void addPriorities(IPriorityMediator mediator) {
				// no priority
			}
			
			public void addCompatibilities(ICompatibilityMediator mediator) {
				// no priority
			}
			
			public Type typeCheck(ITypeCheckMediator tcMediator,
					ExtendedExpression expression) {
				assert typeParams.size() == expression.getChildExpressions().length;
				final List<Type> typePrms = new ArrayList<Type>();
				for (Expression child:expression.getChildExpressions()) {
					final Type alpha = tcMediator.newTypeVariable();
					tcMediator.sameType(child.getType(), tcMediator.makePowerSetType(alpha));
					typePrms.add(alpha);
				}
				return tcMediator.makePowerSetType(tcMediator.makeGenericType(typePrms, this));
			}
			
			public Type getType(ITypeMediator mediator, ExtendedExpression expression) {
				assert typeParams.size() == expression.getChildExpressions().length;
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
		};
	}
	
	private IExtensionKind computeKind() {
		if (typeParams.isEmpty()) {
			return ATOMIC_EXPRESSION;
		}
		// FIXME N_ARY
		return PARENTHESIZED_EXPRESSION_1;
	}
}
