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
package org.eventb.core.tests.extension;

import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_PRED;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * An extension for tests.
 */
public class Prime implements IPredicateExtension {
	
	
	private static final Prime INSTANCE = new Prime();
	
	private static final String SYMBOL = "prime";
	private static final String ID = "Ext Prime";
	
	public static IFormulaExtension getPrime() {
		return INSTANCE; 
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return wdMediator.makeTrueWD();
	}
	
	@Override
	public String getSyntaxSymbol() {
		return SYMBOL;
	}
	
	@Override
	public IExtensionKind getKind() {
		return PARENTHESIZED_UNARY_PREDICATE;
	}
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getGroupId() {
		return ATOMIC_PRED.getId();
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
	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator tcMediator) {
		final Expression child = predicate.getChildExpressions()[0];
		final Type childType = tcMediator.makePowerSetType(tcMediator.makeIntegerType());
		tcMediator.sameType(child.getType(), childType);
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public Object getOrigin() {
		return null;
	}

}
