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
package org.eventb.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IFormulaExtension {

	public static enum ExtensionKind {
		BINARY_INFIX_EXPRESSION, // a op b
		ASSOCIATIVE_INFIX_EXPRESSION, // a op b op ... op c 
		PARENTHESIZED_PREFIX_EXPRESSION // op(a, b, ..., c)
	}
	
	public static enum Associativity {
		LEFT,
		RIGHT
	}
	
	boolean checkPreconditions(Expression[] expressions, Predicate[] predicates);

	String getSyntaxSymbol();

	void toString(IToStringMediator mediator, IExtendedFormula formula);

	Predicate getWDPredicate(IWDMediator wdMediator, IExtendedFormula formula);

	boolean isFlattenable();

	String getId();

	String getGroupId();

	ExtensionKind getKind();

	void addCompatibilities(ICompatibilityMediator mediator);

	void addPriorities(IPriorityMediator mediator);

	Associativity getAssociativity();
}
