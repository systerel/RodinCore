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

import java.util.List;
import java.util.Map;

import org.eventb.core.ast.ExtendedExpression;
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
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 *
 */
public class ConstructorMediator extends DatatypeMediator implements IConstructorMediator {

	// FIXME we may wish to have priorities and custom type check methods, thus
	// fully implementing methods from IExpressionExtension
	
	public ConstructorMediator(String typeName,
			Map<String, ITypeParameter> typeParams) {
		super(typeName, typeParams);
	}

	public void addConstructor(final String name, final String id, final String groupId) {
		final IExpressionExtension constructor = new IExpressionExtension() {
			
			public Predicate getWDPredicate(IWDMediator wdMediator,
					IExtendedFormula formula) {
				return wdMediator.makeTrueWD();
			}
			
			public String getSyntaxSymbol() {
				return name;
			}
			
			public IExtensionKind getKind() {
				return ATOMIC_EXPRESSION;
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
				// no compatibility				
			}
			
			public Type typeCheck(ITypeCheckMediator tcMediator,
					ExtendedExpression expression) {
				// TODO unification and return the datatype type
				return null;
			}
			
			public Type getType(ITypeMediator mediator, ExtendedExpression expression) {
				// TODO return the datatype type
				return null;
			}
		};
		extensions.add(constructor);
	}

	public void addConstructor(final String name, final String id, final String groupId, List<ITypeParameter> argumentTypes) {
		if (argumentTypes.isEmpty()) {
			addConstructor(name, id, groupId);
			return;
		}
		final IExpressionExtension constructor = new IExpressionExtension() {
			
			public Predicate getWDPredicate(IWDMediator wdMediator,
					IExtendedFormula formula) {
				return wdMediator.makeTrueWD();
			}
			
			public String getSyntaxSymbol() {
				return name;
			}
			
			public IExtensionKind getKind() {
				return ExtensionKind.PARENTHESIZED_EXPRESSION_1;
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
				// no compatibility				
			}
			
			public Type typeCheck(ITypeCheckMediator tcMediator,
					ExtendedExpression expression) {
				// TODO unification and return the datatype type
				return null;
			}
			
			public Type getType(ITypeMediator mediator, ExtendedExpression expression) {
				// TODO return the datatype type
				return null;
			}
		};
		extensions.add(constructor);
	}

}
